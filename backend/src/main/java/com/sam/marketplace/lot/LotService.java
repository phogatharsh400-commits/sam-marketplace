package com.sam.marketplace.lot;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.AuditAction;
import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.enums.LotStatus;
import com.sam.marketplace.common.exception.BadRequestException;
import com.sam.marketplace.common.exception.BusinessRuleException;
import com.sam.marketplace.common.exception.ResourceNotFoundException;
import com.sam.marketplace.common.exception.UnauthorizedException;
import com.sam.marketplace.common.response.PageResponse;
import com.sam.marketplace.user.User;
import com.sam.marketplace.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class LotService {

    private static final Logger log = LoggerFactory.getLogger(LotService.class);

    private final LotRepository lotRepository;
    private final LotImageRepository lotImageRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public LotService(LotRepository lotRepository,
                      LotImageRepository lotImageRepository,
                      UserRepository userRepository,
                      AuditService auditService) {
        this.lotRepository = lotRepository;
        this.lotImageRepository = lotImageRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public LotResponseDto createLot(UUID farmerId, LotCreateDto dto) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with id: " + farmerId));

        if (farmer.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessRuleException("Only ACTIVE and verified farmers can list produce lots. Current status: " + farmer.getStatus());
        }

        if (dto.getAuctionClosingDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Auction closing date cannot be in the past");
        }
        if (dto.getExpectedDispatchDate().isBefore(dto.getAuctionClosingDate())) {
            throw new BadRequestException("Expected dispatch date must be on or after the auction closing date");
        }

        String lotNumber = generateLotNumber();

        ProduceLot lot = ProduceLot.builder()
                .lotNumber(lotNumber)
                .farmer(farmer)
                .crop(dto.getCrop())
                .variety(dto.getVariety())
                .quantityQuintals(dto.getQuantityQuintals())
                .unit(dto.getUnit() != null ? dto.getUnit() : "QUINTAL")
                .grade(dto.getGrade())
                .moisturePercentage(dto.getMoisturePercentage())
                .state(dto.getState())
                .district(dto.getDistrict())
                .village(dto.getVillage())
                .pickupLocation(dto.getPickupLocation())
                .minPricePerQuintal(dto.getMinPricePerQuintal())
                .expectedDispatchDate(dto.getExpectedDispatchDate())
                .auctionClosingDate(dto.getAuctionClosingDate())
                .description(dto.getDescription())
                .storageRequirement(dto.getStorageRequirement() != null ? dto.getStorageRequirement() : "DRY_COVERED")
                .status(dto.isPublishImmediately() ? LotStatus.OPEN : LotStatus.DRAFT)
                .poolingEnabled(dto.isPoolingEnabled())
                .minOrderQuantity(dto.getMinOrderQuantity() != null ? dto.getMinOrderQuantity() : dto.getQuantityQuintals())
                .sampleApproved(dto.isSampleApproved())
                .sampleStatus(dto.isSampleApproved() ? "QUALITY_VERIFIED_AND_APPROVED" : "PENDING_SAMPLE")
                .foreignMatterPercent(dto.getForeignMatterPercent() != null ? dto.getForeignMatterPercent() : BigDecimal.valueOf(0.80))
                .brokenGrainsPercent(dto.getBrokenGrainsPercent() != null ? dto.getBrokenGrainsPercent() : BigDecimal.valueOf(1.20))
                .qualityCertificateNumber("QC-HAR-2026-" + lotNumber.replaceAll("\\D+", ""))
                .images(new ArrayList<>())
                .build();

        ProduceLot savedLot = lotRepository.save(lot);

        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            boolean first = true;
            for (String url : dto.getImageUrls()) {
                if (url != null && !url.isBlank()) {
                    LotImage img = LotImage.builder()
                            .lot(savedLot)
                            .imageUrl(url)
                            .isPrimary(first)
                            .build();
                    lotImageRepository.save(img);
                    savedLot.getImages().add(img);
                    first = false;
                }
            }
        }

        auditService.logAction(farmerId, AuditAction.LOT_CREATED, "LOT",
                savedLot.getId().toString(), "Produce lot created: " + lotNumber + " (" + dto.getCrop() + ")", null);

        return LotResponseDto.from(savedLot);
    }

    @Transactional(readOnly = true)
    public LotResponseDto getLotById(UUID id) {
        ProduceLot lot = lotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produce lot not found with id: " + id));
        return LotResponseDto.from(lot);
    }

    @Transactional(readOnly = true)
    public PageResponse<LotResponseDto> getOpenLots(CropType crop, String district, Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<ProduceLot> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            predicates.add(cb.equal(root.get("status"), LotStatus.OPEN));
            if (crop != null) {
                predicates.add(cb.equal(root.get("crop"), crop));
            }
            if (district != null && !district.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("district")), district.toLowerCase().trim()));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        Page<ProduceLot> page = lotRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(LotResponseDto::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<LotResponseDto> getAllLots(LotStatus status, Pageable pageable) {
        Page<ProduceLot> page = (status != null)
                ? lotRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                : lotRepository.findAll(pageable);
        return PageResponse.from(page.map(LotResponseDto::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<LotResponseDto> getLotsByFarmer(UUID farmerId, Pageable pageable) {
        Page<ProduceLot> page = lotRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId, pageable);
        return PageResponse.from(page.map(LotResponseDto::from));
    }

    @Transactional
    public LotResponseDto publishLot(UUID farmerId, UUID lotId) {
        ProduceLot lot = getFarmerOwnedLot(farmerId, lotId);
        if (lot.getStatus() != LotStatus.DRAFT) {
            throw new BusinessRuleException("Only DRAFT lots can be published. Current status: " + lot.getStatus());
        }
        lot.setStatus(LotStatus.OPEN);
        ProduceLot saved = lotRepository.save(lot);

        auditService.logAction(farmerId, AuditAction.LOT_PUBLISHED, "LOT",
                saved.getId().toString(), "Lot published to marketplace: " + saved.getLotNumber(), null);

        return LotResponseDto.from(saved);
    }

    @Transactional
    public LotResponseDto withdrawLot(UUID farmerId, UUID lotId) {
        ProduceLot lot = getFarmerOwnedLot(farmerId, lotId);
        if (lot.getStatus() != LotStatus.OPEN && lot.getStatus() != LotStatus.DRAFT) {
            throw new BusinessRuleException("Cannot withdraw lot in current status: " + lot.getStatus());
        }
        lot.setStatus(LotStatus.CANCELLED);
        ProduceLot saved = lotRepository.save(lot);

        auditService.logAction(farmerId, AuditAction.LOT_WITHDRAWN, "LOT",
                saved.getId().toString(), "Lot withdrawn: " + saved.getLotNumber(), null);

        return LotResponseDto.from(saved);
    }

    @Transactional
    public LotResponseDto updateLot(UUID farmerId, UUID lotId, LotUpdateDto dto) {
        ProduceLot lot = getFarmerOwnedLot(farmerId, lotId);
        if (lot.getStatus() != LotStatus.DRAFT && lot.getStatus() != LotStatus.OPEN) {
            throw new BusinessRuleException("Cannot edit lot in status: " + lot.getStatus());
        }

        if (dto.getCrop() != null) lot.setCrop(dto.getCrop());
        if (dto.getVariety() != null) lot.setVariety(dto.getVariety());
        if (dto.getQuantityQuintals() != null) lot.setQuantityQuintals(dto.getQuantityQuintals());
        if (dto.getGrade() != null) lot.setGrade(dto.getGrade());
        if (dto.getMoisturePercentage() != null) lot.setMoisturePercentage(dto.getMoisturePercentage());
        if (dto.getState() != null) lot.setState(dto.getState());
        if (dto.getDistrict() != null) lot.setDistrict(dto.getDistrict());
        if (dto.getVillage() != null) lot.setVillage(dto.getVillage());
        if (dto.getPickupLocation() != null) lot.setPickupLocation(dto.getPickupLocation());
        if (dto.getMinPricePerQuintal() != null) lot.setMinPricePerQuintal(dto.getMinPricePerQuintal());
        if (dto.getExpectedDispatchDate() != null) lot.setExpectedDispatchDate(dto.getExpectedDispatchDate());
        if (dto.getAuctionClosingDate() != null) lot.setAuctionClosingDate(dto.getAuctionClosingDate());
        if (dto.getDescription() != null) lot.setDescription(dto.getDescription());
        if (dto.getStorageRequirement() != null) lot.setStorageRequirement(dto.getStorageRequirement());

        ProduceLot saved = lotRepository.save(lot);
        return LotResponseDto.from(saved);
    }

    @Transactional
    public void deleteLot(UUID farmerId, UUID lotId) {
        ProduceLot lot = getFarmerOwnedLot(farmerId, lotId);
        if (lot.getStatus() != LotStatus.DRAFT) {
            throw new BusinessRuleException("Only DRAFT lots can be deleted");
        }
        lotRepository.delete(lot);
    }

    private ProduceLot getFarmerOwnedLot(UUID farmerId, UUID lotId) {
        ProduceLot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Produce lot not found with id: " + lotId));
        if (!lot.getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedException("You do not own this produce lot");
        }
        return lot;
    }

    private String generateLotNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "LOT-" + dateStr + "-" + rand;
    }
}
