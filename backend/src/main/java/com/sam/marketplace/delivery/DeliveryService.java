package com.sam.marketplace.delivery;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.common.enums.*;
import com.sam.marketplace.common.exception.BusinessRuleException;
import com.sam.marketplace.common.exception.ResourceNotFoundException;
import com.sam.marketplace.common.exception.UnauthorizedException;
import com.sam.marketplace.contract.SaleContract;
import com.sam.marketplace.contract.SaleContractRepository;
import com.sam.marketplace.dispute.Dispute;
import com.sam.marketplace.dispute.DisputeRepository;
import com.sam.marketplace.notification.NotificationService;
import com.sam.marketplace.payment.PaymentRepository;
import com.sam.marketplace.payment.PaymentService;
import com.sam.marketplace.payment.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sam.marketplace.merit.MeritService;
import java.util.List;
import java.util.UUID;

@Service
public class DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);

    private final DeliveryRepository deliveryRepository;
    private final SaleContractRepository saleContractRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final DisputeRepository disputeRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final MeritService meritService;

    public DeliveryService(DeliveryRepository deliveryRepository,
                           SaleContractRepository saleContractRepository,
                           PaymentRepository paymentRepository,
                           PaymentService paymentService,
                           DisputeRepository disputeRepository,
                           NotificationService notificationService,
                           AuditService auditService,
                           MeritService meritService) {
        this.deliveryRepository = deliveryRepository;
        this.saleContractRepository = saleContractRepository;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
        this.disputeRepository = disputeRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.meritService = meritService;
    }

    @Transactional
    public DeliveryResponseDto recordDelivery(UUID actorId, UUID contractId, DeliveryCreateDto dto) {
        SaleContract contract = saleContractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale contract not found: " + contractId));

        boolean isFarmer = contract.getFarmer().getId().equals(actorId);
        boolean isBuyer = contract.getBuyer().getId().equals(actorId);

        if (!isFarmer && !isBuyer) {
            throw new UnauthorizedException("You are not a party to this sale contract");
        }

        if (contract.getStatus() != ContractStatus.ESCROW_FUNDED &&
            contract.getStatus() != ContractStatus.READY_FOR_DISPATCH &&
            contract.getStatus() != ContractStatus.DISPATCHED) {
            throw new BusinessRuleException("Cannot record delivery: Escrow must be funded first. Current contract status: " + contract.getStatus());
        }

        DeliveryRecord delivery = deliveryRepository.findByContractId(contractId)
                .orElseGet(() -> DeliveryRecord.builder().contract(contract).build());

        delivery.setQuantityDeliveredQuintals(dto.getQuantityDeliveredQuintals());
        delivery.setDeliveryDate(dto.getDeliveryDate());
        delivery.setQualityRemarks(dto.getQualityRemarks());
        delivery.setWeightSlipUrl(dto.getWeightSlipUrl());
        delivery.setDeliveryPhotosMetadata(dto.getDeliveryPhotosMetadata());
        delivery.setStatus("DISPATCHED");
        delivery.setFarmerConfirmed(true);

        contract.setStatus(ContractStatus.DISPATCHED);
        saleContractRepository.save(contract);

        DeliveryRecord saved = deliveryRepository.save(delivery);

        // Notify Buyer
        notificationService.createNotification(
                contract.getBuyer().getId(),
                "Produce Dispatched",
                "Farmer has dispatched " + dto.getQuantityDeliveredQuintals() + " qtl for Contract #" + contract.getContractNumber() + ". Please inspect and confirm upon delivery.",
                NotificationType.DISPATCH_REQUIRED,
                saved.getId().toString(),
                "DELIVERY"
        );

        auditService.logAction(actorId, AuditAction.DISPATCH_CONFIRMED, "DELIVERY",
                saved.getId().toString(), "Dispatch recorded: " + dto.getQuantityDeliveredQuintals() + " qtl", null);

        return DeliveryResponseDto.from(saved);
    }

    @Transactional(readOnly = true)
    public DeliveryResponseDto getDeliveryByContract(UUID contractId) {
        return deliveryRepository.findByContractId(contractId)
                .map(DeliveryResponseDto::from)
                .orElseThrow(() -> new ResourceNotFoundException("No delivery record found for contract: " + contractId));
    }

    @Transactional
    public DeliveryResponseDto confirmDelivery(UUID buyerId, UUID deliveryId, DeliveryConfirmDto dto) {
        DeliveryRecord delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery record not found: " + deliveryId));

        SaleContract contract = delivery.getContract();

        if (!contract.getBuyer().getId().equals(buyerId)) {
            throw new UnauthorizedException("Only the designated buyer can confirm delivery");
        }

        if (dto.isSatisfied()) {
            delivery.setBuyerConfirmed(true);
            delivery.setStatus("CONFIRMED");
            delivery.setQualityRemarks(dto.getQualityRemarks() != null ? dto.getQualityRemarks() : "Accepted in good condition");
            contract.setStatus(ContractStatus.DELIVERED);
            saleContractRepository.save(contract);

            DeliveryRecord saved = deliveryRepository.save(delivery);

            // Auto-release payment from escrow to farmer
            List<PaymentTransaction> payments = paymentRepository.findByContractId(contract.getId());
            for (PaymentTransaction pt : payments) {
                if (pt.getStatus() == PaymentStatus.HELD_IN_ESCROW) {
                    paymentService.releasePayment(pt.getId(), buyerId);
                    break;
                }
            }

            // Award +5 Merit Points for verified quality delivery
            try {
                meritService.awardMeritPoints(
                        contract.getFarmer().getId(),
                        5,
                        "Condition Met: Quality delivery confirmed for Contract #" + contract.getContractNumber(),
                        "CONTRACT #" + contract.getContractNumber()
                );
            } catch (Exception e) {
                log.warn("Failed to award merit points: {}", e.getMessage());
            }

            notificationService.createNotification(
                    contract.getFarmer().getId(),
                    "Delivery Confirmed & Payment Released!",
                    "Buyer confirmed delivery for Contract #" + contract.getContractNumber() + ". Escrow funds have been credited to your bank account (+5 Merit Points).",
                    NotificationType.DELIVERY_CONFIRMED,
                    saved.getId().toString(),
                    "DELIVERY"
            );

            auditService.logAction(buyerId, AuditAction.DELIVERY_CONFIRMED, "DELIVERY",
                    saved.getId().toString(), "Buyer confirmed delivery and released escrow payment", null);

            return DeliveryResponseDto.from(saved);
        } else {
            // Quality issue -> hold payment and raise dispute
            delivery.setBuyerConfirmed(false);
            delivery.setStatus("DISPUTED");
            delivery.setQualityRemarks(dto.getQualityRemarks() != null ? dto.getQualityRemarks() : "Quality issue reported by buyer");
            contract.setStatus(ContractStatus.DISPUTED);
            saleContractRepository.save(contract);

            DeliveryRecord saved = deliveryRepository.save(delivery);

            // Deduct -10 Merit Points from farmer for condition violation / quality dispute
            try {
                meritService.deductMeritPoints(
                        contract.getFarmer().getId(),
                        10,
                        "Condition Violation: Quality / Delivery defect reported on Contract #" + contract.getContractNumber(),
                        "CONTRACT #" + contract.getContractNumber()
                );
            } catch (Exception e) {
                log.warn("Failed to deduct merit points: {}", e.getMessage());
            }

            // Auto-create dispute record
            Dispute dispute = Dispute.builder()
                    .contract(contract)
                    .raisedByUser(contract.getBuyer())
                    .reason(dto.getDisputeReason() != null ? dto.getDisputeReason() : "Quality / Quantity Mismatch on Delivery")
                    .description(dto.getDisputeDescription() != null ? dto.getDisputeDescription() : dto.getQualityRemarks())
                    .requestedResolution("REFUND_OR_REMEDIATION")
                    .status(DisputeStatus.OPEN)
                    .build();
            disputeRepository.save(dispute);

            notificationService.createNotification(
                    contract.getFarmer().getId(),
                    "Delivery Issue Raised - Dispute Opened (-10 Merit Points)",
                    "Buyer reported a delivery issue for Contract #" + contract.getContractNumber() + ". Escrow funds are held until dispute review.",
                    NotificationType.DISPUTE_OPENED,
                    dispute.getId().toString(),
                    "DISPUTE"
            );

            auditService.logAction(buyerId, AuditAction.DISPUTE_RAISED, "DISPUTE",
                    dispute.getId().toString(), "Dispute raised during delivery confirmation: " + dispute.getReason(), null);

            return DeliveryResponseDto.from(saved);
        }
    }
}
