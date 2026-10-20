package com.sam.marketplace.warehouse;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.AuditAction;
import com.sam.marketplace.common.enums.NotificationType;
import com.sam.marketplace.common.enums.WarehouseBookingStatus;
import com.sam.marketplace.common.exception.BadRequestException;
import com.sam.marketplace.common.exception.BusinessRuleException;
import com.sam.marketplace.common.exception.ResourceNotFoundException;
import com.sam.marketplace.common.exception.UnauthorizedException;
import com.sam.marketplace.common.response.PageResponse;
import com.sam.marketplace.notification.NotificationService;
import com.sam.marketplace.user.User;
import com.sam.marketplace.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.sam.marketplace.merit.MeritService;

@Service
public class WarehouseService {

    private static final Logger log = LoggerFactory.getLogger(WarehouseService.class);

    private final WarehouseRepository warehouseRepository;
    private final WarehouseBookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final MeritService meritService;

    public WarehouseService(WarehouseRepository warehouseRepository,
                            WarehouseBookingRepository bookingRepository,
                            UserRepository userRepository,
                            NotificationService notificationService,
                            AuditService auditService,
                            MeritService meritService) {
        this.warehouseRepository = warehouseRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.meritService = meritService;
    }

    @Transactional
    public WarehouseResponseDto createWarehouse(UUID operatorId, WarehouseCreateDto dto) {
        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Operator not found with id: " + operatorId));

        Warehouse warehouse = Warehouse.builder()
                .operator(operator)
                .warehouseName(dto.getWarehouseName())
                .ownerName(dto.getOwnerName())
                .contactNumber(dto.getContactNumber())
                .state(dto.getState())
                .district(dto.getDistrict())
                .address(dto.getAddress())
                .totalCapacityQuintals(dto.getTotalCapacityQuintals())
                .availableCapacityQuintals(dto.getTotalCapacityQuintals())
                .supportedCrops(dto.getSupportedCrops())
                .storageChargePerQuintalMonth(dto.getStorageChargePerQuintalMonth())
                .isAccredited(dto.isAccredited())
                .description(dto.getDescription())
                .build();

        Warehouse saved = warehouseRepository.save(warehouse);

        auditService.logAction(operatorId, AuditAction.WAREHOUSE_CREATED, "WAREHOUSE",
                saved.getId().toString(), "Warehouse created: " + saved.getWarehouseName(), null);

        return WarehouseResponseDto.from(saved);
    }

    @Transactional(readOnly = true)
    public WarehouseResponseDto getWarehouseById(UUID id) {
        Warehouse w = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        return WarehouseResponseDto.from(w);
    }

    @Transactional(readOnly = true)
    public PageResponse<WarehouseResponseDto> searchWarehouses(String district, String crop, Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<Warehouse> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            if (district != null && !district.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("district")), district.toLowerCase().trim()));
            }
            if (crop != null && !crop.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("supportedCrops")), "%" + crop.toLowerCase().trim() + "%"));
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        Page<Warehouse> page = warehouseRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(WarehouseResponseDto::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<WarehouseResponseDto> getWarehousesByOperator(UUID operatorId, Pageable pageable) {
        Page<Warehouse> page = warehouseRepository.findByOperatorIdOrderByCreatedAtDesc(operatorId, pageable);
        return PageResponse.from(page.map(WarehouseResponseDto::from));
    }

    @Transactional
    public WarehouseResponseDto updateWarehouse(UUID operatorId, UUID warehouseId, WarehouseCreateDto dto) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + warehouseId));

        if (!warehouse.getOperator().getId().equals(operatorId)) {
            throw new UnauthorizedException("You do not own this warehouse profile");
        }

        warehouse.setWarehouseName(dto.getWarehouseName());
        warehouse.setOwnerName(dto.getOwnerName());
        warehouse.setContactNumber(dto.getContactNumber());
        warehouse.setState(dto.getState());
        warehouse.setDistrict(dto.getDistrict());
        warehouse.setAddress(dto.getAddress());
        warehouse.setTotalCapacityQuintals(dto.getTotalCapacityQuintals());
        warehouse.setSupportedCrops(dto.getSupportedCrops());
        warehouse.setStorageChargePerQuintalMonth(dto.getStorageChargePerQuintalMonth());
        warehouse.setAccredited(dto.isAccredited());
        warehouse.setDescription(dto.getDescription());

        Warehouse saved = warehouseRepository.save(warehouse);
        return WarehouseResponseDto.from(saved);
    }

    @Transactional
    public BookingResponseDto createBookingRequest(UUID farmerId, UUID warehouseId, BookingCreateDto dto) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found: " + farmerId));

        if (farmer.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessRuleException("Only ACTIVE verified farmers can request warehouse bookings");
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + warehouseId));

        if (dto.getQuantityQuintals().compareTo(warehouse.getAvailableCapacityQuintals()) > 0) {
            throw new BusinessRuleException("Requested quantity (" + dto.getQuantityQuintals() + " qtl) exceeds available warehouse capacity (" + warehouse.getAvailableCapacityQuintals() + " qtl)");
        }

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BadRequestException("Booking end date must be after start date");
        }

        long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate());
        BigDecimal months = BigDecimal.valueOf(Math.max(1.0, (double) days / 30.0));
        BigDecimal estimatedCost = warehouse.getStorageChargePerQuintalMonth()
                .multiply(dto.getQuantityQuintals())
                .multiply(months)
                .setScale(2, RoundingMode.HALF_UP);

        WarehouseBooking booking = WarehouseBooking.builder()
                .warehouse(warehouse)
                .farmer(farmer)
                .crop(dto.getCrop())
                .quantityQuintals(dto.getQuantityQuintals())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .totalEstimatedCost(estimatedCost)
                .status(WarehouseBookingStatus.REQUESTED)
                .remarks(dto.getRemarks())
                .build();

        WarehouseBooking saved = bookingRepository.save(booking);

        // Notify Operator
        notificationService.createNotification(
                warehouse.getOperator().getId(),
                "New Warehouse Storage Booking Request",
                "Farmer " + farmer.getFullName() + " requested " + dto.getQuantityQuintals() + " qtl storage for " + dto.getCrop() + " at " + warehouse.getWarehouseName(),
                NotificationType.WAREHOUSE_BOOKING_REQUESTED,
                saved.getId().toString(),
                "WAREHOUSE_BOOKING"
        );

        auditService.logAction(farmerId, AuditAction.WAREHOUSE_BOOKED, "WAREHOUSE_BOOKING",
                saved.getId().toString(), "Booking requested: " + dto.getQuantityQuintals() + " qtl at " + warehouse.getWarehouseName(), null);

        return BookingResponseDto.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookingResponseDto> getFarmerBookings(UUID farmerId, Pageable pageable) {
        Page<WarehouseBooking> page = bookingRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId, pageable);
        return PageResponse.from(page.map(BookingResponseDto::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<BookingResponseDto> getOperatorBookings(UUID operatorId, Pageable pageable) {
        Page<WarehouseBooking> page = bookingRepository.findByOperatorIdOrderByCreatedAtDesc(operatorId, pageable);
        return PageResponse.from(page.map(BookingResponseDto::from));
    }

    @Transactional
    public BookingResponseDto approveBooking(UUID operatorId, UUID bookingId) {
        WarehouseBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        Warehouse warehouse = booking.getWarehouse();

        if (!warehouse.getOperator().getId().equals(operatorId)) {
            throw new UnauthorizedException("You do not have permission to approve bookings for this warehouse");
        }

        if (booking.getStatus() != WarehouseBookingStatus.REQUESTED) {
            throw new BusinessRuleException("Only REQUESTED bookings can be approved");
        }

        if (booking.getQuantityQuintals().compareTo(warehouse.getAvailableCapacityQuintals()) > 0) {
            throw new BusinessRuleException("Insufficient available capacity to approve booking");
        }

        // Deduct available capacity
        warehouse.setAvailableCapacityQuintals(warehouse.getAvailableCapacityQuintals().subtract(booking.getQuantityQuintals()));
        warehouseRepository.save(warehouse);

        booking.setStatus(WarehouseBookingStatus.APPROVED);
        WarehouseBooking saved = bookingRepository.save(booking);

        try {
            meritService.awardMeritPoints(
                    operatorId,
                    5,
                    "Condition Met: Storage capacity fulfilled at " + warehouse.getWarehouseName(),
                    "WAREHOUSE #" + warehouse.getId()
            );
        } catch (Exception e) {
            log.warn("Failed to award merit points to operator: {}", e.getMessage());
        }

        notificationService.createNotification(
                booking.getFarmer().getId(),
                "Warehouse Booking Approved!",
                "Your storage booking for " + booking.getQuantityQuintals() + " qtl of " + booking.getCrop() + " at " + warehouse.getWarehouseName() + " has been approved.",
                NotificationType.WAREHOUSE_BOOKING_APPROVED,
                saved.getId().toString(),
                "WAREHOUSE_BOOKING"
        );

        auditService.logAction(operatorId, AuditAction.WAREHOUSE_APPROVED, "WAREHOUSE_BOOKING",
                saved.getId().toString(), "Booking approved: " + booking.getQuantityQuintals() + " qtl at " + warehouse.getWarehouseName(), null);

        return BookingResponseDto.from(saved);
    }

    @Transactional
    public BookingResponseDto rejectBooking(UUID operatorId, UUID bookingId, String reason) {
        WarehouseBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        Warehouse warehouse = booking.getWarehouse();

        if (!warehouse.getOperator().getId().equals(operatorId)) {
            throw new UnauthorizedException("You do not have permission to reject bookings for this warehouse");
        }

        if (booking.getStatus() != WarehouseBookingStatus.REQUESTED) {
            throw new BusinessRuleException("Only REQUESTED bookings can be rejected");
        }

        booking.setStatus(WarehouseBookingStatus.REJECTED);
        if (reason != null && !reason.isBlank()) {
            booking.setRemarks("Rejected: " + reason);
        }
        WarehouseBooking saved = bookingRepository.save(booking);

        try {
            meritService.deductMeritPoints(
                    operatorId,
                    10,
                    "Condition Not Met: Warehouse booking rejected/unfulfilled (" + (reason != null ? reason : "Capacity constraints") + ")",
                    "WAREHOUSE #" + warehouse.getId()
            );
        } catch (Exception e) {
            log.warn("Failed to deduct merit points from operator: {}", e.getMessage());
        }

        notificationService.createNotification(
                booking.getFarmer().getId(),
                "Warehouse Booking Declined",
                "Your storage booking request at " + warehouse.getWarehouseName() + " was declined. Reason: " + (reason != null ? reason : "Capacity constraints"),
                NotificationType.WAREHOUSE_BOOKING_REJECTED,
                saved.getId().toString(),
                "WAREHOUSE_BOOKING"
        );

        auditService.logAction(operatorId, AuditAction.WAREHOUSE_REJECTED, "WAREHOUSE_BOOKING",
                saved.getId().toString(), "Booking rejected for: " + warehouse.getWarehouseName(), null);

        return BookingResponseDto.from(saved);
    }
}
