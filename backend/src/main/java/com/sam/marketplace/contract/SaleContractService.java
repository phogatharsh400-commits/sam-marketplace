package com.sam.marketplace.contract;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.common.enums.AuditAction;
import com.sam.marketplace.common.enums.ContractStatus;
import com.sam.marketplace.common.enums.LotStatus;
import com.sam.marketplace.common.enums.NotificationType;
import com.sam.marketplace.common.exception.BusinessRuleException;
import com.sam.marketplace.common.exception.ResourceNotFoundException;
import com.sam.marketplace.common.exception.UnauthorizedException;
import com.sam.marketplace.common.response.PageResponse;
import com.sam.marketplace.delivery.DeliveryRecord;
import com.sam.marketplace.delivery.DeliveryRepository;
import com.sam.marketplace.lot.LotRepository;
import com.sam.marketplace.lot.ProduceLot;
import com.sam.marketplace.merit.MeritService;
import com.sam.marketplace.notification.NotificationService;
import com.sam.marketplace.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class SaleContractService {

    private static final Logger log = LoggerFactory.getLogger(SaleContractService.class);

    private final SaleContractRepository saleContractRepository;
    private final LotRepository lotRepository;
    private final DeliveryRepository deliveryRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final MeritService meritService;

    public SaleContractService(SaleContractRepository saleContractRepository,
                               LotRepository lotRepository,
                               DeliveryRepository deliveryRepository,
                               NotificationService notificationService,
                               AuditService auditService,
                               MeritService meritService) {
        this.saleContractRepository = saleContractRepository;
        this.lotRepository = lotRepository;
        this.deliveryRepository = deliveryRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.meritService = meritService;
    }

    @Transactional(readOnly = true)
    public ContractResponseDto getContractById(UUID contractId, UUID currentUserId) {
        SaleContract contract = saleContractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale contract not found with id: " + contractId));

        boolean isAuthorized = SecurityUtils.hasRole(com.sam.marketplace.common.enums.RoleType.ADMIN)
                || contract.getFarmer().getId().equals(currentUserId)
                || contract.getBuyer().getId().equals(currentUserId);

        if (!isAuthorized) {
            throw new UnauthorizedException("You are not authorized to view this sale contract");
        }

        return ContractResponseDto.from(contract);
    }

    @Transactional(readOnly = true)
    public PageResponse<ContractResponseDto> getAllContracts(ContractStatus status, Pageable pageable) {
        Page<SaleContract> page = (status != null)
                ? saleContractRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                : saleContractRepository.findAll(pageable);
        return PageResponse.from(page.map(ContractResponseDto::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<ContractResponseDto> getContractsByFarmer(UUID farmerId, Pageable pageable) {
        Page<SaleContract> page = saleContractRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId, pageable);
        return PageResponse.from(page.map(ContractResponseDto::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<ContractResponseDto> getContractsByBuyer(UUID buyerId, Pageable pageable) {
        Page<SaleContract> page = saleContractRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId, pageable);
        return PageResponse.from(page.map(ContractResponseDto::from));
    }

    @Transactional
    public ContractResponseDto confirmDispatch(UUID contractId, UUID callerId) {
        SaleContract contract = saleContractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale contract not found: " + contractId));

        if (!contract.getFarmer().getId().equals(callerId) && !SecurityUtils.hasRole(com.sam.marketplace.common.enums.RoleType.ADMIN)) {
            throw new UnauthorizedException("Only the designated farmer can confirm produce dispatch");
        }

        // Update Contract status to DISPATCHED
        contract.setStatus(ContractStatus.DISPATCHED);
        SaleContract savedContract = saleContractRepository.save(contract);

        // Update ProduceLot status
        ProduceLot lot = contract.getLot();
        if (lot != null) {
            lot.setStatus(LotStatus.IN_TRANSIT);
            lotRepository.save(lot);
        }

        // Create or update DeliveryRecord
        DeliveryRecord delivery = deliveryRepository.findByContractId(contractId)
                .orElseGet(() -> DeliveryRecord.builder().contract(contract).build());

        delivery.setQuantityDeliveredQuintals(contract.getQuantityQuintals());
        delivery.setDeliveryDate(LocalDate.now());
        delivery.setQualityRemarks("Dispatched by farmer with pre-dispatch quality verification");
        delivery.setStatus("DISPATCHED");
        delivery.setFarmerConfirmed(true);
        deliveryRepository.save(delivery);

        // Notify Buyer
        notificationService.createNotification(
                contract.getBuyer().getId(),
                "Produce Dispatched - Delivery In Transit",
                "Farmer " + contract.getFarmer().getFullName() + " has dispatched " + contract.getQuantityQuintals() + " qtl of " + contract.getLot().getCrop() + " for Contract #" + contract.getContractNumber() + ". Please inspect goods upon arrival.",
                NotificationType.DISPATCH_REQUIRED,
                savedContract.getId().toString(),
                "CONTRACT"
        );

        auditService.logAction(callerId, AuditAction.DISPATCH_CONFIRMED, "CONTRACT",
                savedContract.getId().toString(), "Farmer confirmed produce dispatch for Contract #" + contract.getContractNumber(), null);

        log.info("[SaleContractService] Dispatch confirmed for contract: {} by user: {}", contract.getContractNumber(), callerId);

        return ContractResponseDto.from(savedContract);
    }
}
