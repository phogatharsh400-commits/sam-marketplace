package com.sam.marketplace.service;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.RoleType;
import com.sam.marketplace.common.enums.WarehouseBookingStatus;
import com.sam.marketplace.common.exception.BusinessRuleException;
import com.sam.marketplace.notification.NotificationService;
import com.sam.marketplace.user.User;
import com.sam.marketplace.user.UserRepository;
import com.sam.marketplace.warehouse.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private WarehouseBookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private WarehouseService warehouseService;

    private User operator;
    private User farmer;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        operator = User.builder()
                .id(UUID.randomUUID())
                .fullName("Operator Sukhvinder")
                .role(RoleType.WAREHOUSE_OPERATOR)
                .status(AccountStatus.ACTIVE)
                .build();

        farmer = User.builder()
                .id(UUID.randomUUID())
                .fullName("Farmer Ramesh")
                .role(RoleType.FARMER)
                .status(AccountStatus.ACTIVE)
                .build();

        warehouse = Warehouse.builder()
                .id(UUID.randomUUID())
                .operator(operator)
                .warehouseName("Karnal Agro Silos")
                .ownerName("Sukhvinder Mann")
                .contactNumber("9812345678")
                .state("Haryana")
                .district("Karnal")
                .address("GT Road, Karnal")
                .totalCapacityQuintals(BigDecimal.valueOf(1000.0))
                .availableCapacityQuintals(BigDecimal.valueOf(500.0))
                .supportedCrops("WHEAT, PADDY")
                .storageChargePerQuintalMonth(BigDecimal.valueOf(18.0))
                .isAccredited(true)
                .build();
    }

    @Test
    @DisplayName("Farmer requests storage booking within available capacity")
    void testCreateBookingRequest_Success() {
        when(userRepository.findById(farmer.getId())).thenReturn(Optional.of(farmer));
        when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));
        when(bookingRepository.save(any(WarehouseBooking.class))).thenAnswer(inv -> {
            WarehouseBooking wb = inv.getArgument(0);
            wb.setId(UUID.randomUUID());
            return wb;
        });

        BookingCreateDto dto = new BookingCreateDto();
        dto.setCrop("WHEAT");
        dto.setQuantityQuintals(BigDecimal.valueOf(100.0));
        dto.setStartDate(LocalDate.now().plusDays(2));
        dto.setEndDate(LocalDate.now().plusDays(32));

        BookingResponseDto response = warehouseService.createBookingRequest(farmer.getId(), warehouse.getId(), dto);

        assertNotNull(response);
        assertEquals(WarehouseBookingStatus.REQUESTED, response.getStatus());
        assertEquals(BigDecimal.valueOf(100.0), response.getQuantityQuintals());
    }

    @Test
    @DisplayName("Fail booking if requested quantity exceeds available capacity")
    void testCreateBookingRequest_FailsWhenExceedsCapacity() {
        when(userRepository.findById(farmer.getId())).thenReturn(Optional.of(farmer));
        when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));

        BookingCreateDto dto = new BookingCreateDto();
        dto.setCrop("WHEAT");
        dto.setQuantityQuintals(BigDecimal.valueOf(800.0)); // 800 > 500
        dto.setStartDate(LocalDate.now().plusDays(2));
        dto.setEndDate(LocalDate.now().plusDays(32));

        assertThrows(BusinessRuleException.class, () ->
                warehouseService.createBookingRequest(farmer.getId(), warehouse.getId(), dto));
    }

    @Test
    @DisplayName("Approve booking deducts available warehouse capacity")
    void testApproveBooking_Success() {
        WarehouseBooking booking = WarehouseBooking.builder()
                .id(UUID.randomUUID())
                .warehouse(warehouse)
                .farmer(farmer)
                .crop("WHEAT")
                .quantityQuintals(BigDecimal.valueOf(100.0))
                .status(WarehouseBookingStatus.REQUESTED)
                .build();

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(WarehouseBooking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingResponseDto response = warehouseService.approveBooking(operator.getId(), booking.getId());

        assertNotNull(response);
        assertEquals(WarehouseBookingStatus.APPROVED, response.getStatus());
        assertEquals(BigDecimal.valueOf(400.0), warehouse.getAvailableCapacityQuintals()); // 500 - 100
        verify(warehouseRepository, times(1)).save(warehouse);
    }
}
