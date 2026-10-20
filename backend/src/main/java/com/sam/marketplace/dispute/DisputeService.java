package com.sam.marketplace.dispute;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.common.enums.*;
import com.sam.marketplace.common.exception.BadRequestException;
import com.sam.marketplace.common.exception.BusinessRuleException;
import com.sam.marketplace.common.exception.ResourceNotFoundException;
import com.sam.marketplace.common.exception.UnauthorizedException;
import com.sam.marketplace.common.response.PageResponse;
import com.sam.marketplace.contract.SaleContract;
import com.sam.marketplace.contract.SaleContractRepository;
import com.sam.marketplace.notification.NotificationService;
import com.sam.marketplace.payment.PaymentRepository;
import com.sam.marketplace.payment.PaymentService;
import com.sam.marketplace.merit.MeritService;
import com.sam.marketplace.payment.PaymentTransaction;
import com.sam.marketplace.security.SecurityUtils;
import com.sam.marketplace.user.User;
import com.sam.marketplace.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DisputeService {

    private static final Logger log = LoggerFactory.getLogger(DisputeService.class);

    private final DisputeRepository disputeRepository;
    private final DisputeEvidenceRepository evidenceRepository;
    private final SaleContractRepository saleContractRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final MeritService meritService;

    public DisputeService(DisputeRepository disputeRepository,
                          DisputeEvidenceRepository evidenceRepository,
                          SaleContractRepository saleContractRepository,
                          UserRepository userRepository,
                          PaymentRepository paymentRepository,
                          PaymentService paymentService,
                          NotificationService notificationService,
                          AuditService auditService,
                          MeritService meritService) {
        this.disputeRepository = disputeRepository;
        this.evidenceRepository = evidenceRepository;
        this.saleContractRepository = saleContractRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.meritService = meritService;
    }

    @Transactional
    public DisputeResponseDto raiseDispute(UUID userId, UUID contractId, DisputeCreateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        SaleContract contract = saleContractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale contract not found: " + contractId));

        boolean isFarmer = contract.getFarmer().getId().equals(userId);
        boolean isBuyer = contract.getBuyer().getId().equals(userId);

        if (!isFarmer && !isBuyer) {
            throw new UnauthorizedException("You must be a party to the contract to raise a dispute");
        }

        contract.setStatus(ContractStatus.DISPUTED);
        saleContractRepository.save(contract);

        Dispute dispute = Dispute.builder()
                .contract(contract)
                .raisedByUser(user)
                .reason(dto.getReason())
                .description(dto.getDescription())
                .evidenceFileUrl(dto.getEvidenceFileUrl())
                .requestedResolution(dto.getRequestedResolution())
                .status(DisputeStatus.OPEN)
                .evidenceList(new ArrayList<>())
                .build();

        Dispute savedDispute = disputeRepository.save(dispute);

        if (dto.getEvidenceFileUrls() != null && !dto.getEvidenceFileUrls().isEmpty()) {
            for (String url : dto.getEvidenceFileUrls()) {
                if (url != null && !url.isBlank()) {
                    DisputeEvidence evidence = DisputeEvidence.builder()
                            .dispute(savedDispute)
                            .fileUrl(url)
                            .fileName("Evidence Attachment")
                            .fileType("DOCUMENT")
                            .uploadedByUser(user)
                            .build();
                    evidenceRepository.save(evidence);
                    savedDispute.getEvidenceList().add(evidence);
                }
            }
        }

        UUID otherPartyId = isFarmer ? contract.getBuyer().getId() : contract.getFarmer().getId();

        notificationService.createNotification(
                otherPartyId,
                "Dispute Raised on Contract #" + contract.getContractNumber(),
                user.getFullName() + " raised a dispute: " + dto.getReason() + ". Funds are held in escrow.",
                NotificationType.DISPUTE_OPENED,
                savedDispute.getId().toString(),
                "DISPUTE"
        );

        auditService.logAction(userId, AuditAction.DISPUTE_RAISED, "DISPUTE",
                savedDispute.getId().toString(), "Dispute raised: " + dto.getReason(), null);

        return DisputeResponseDto.from(savedDispute);
    }

    @Transactional(readOnly = true)
    public DisputeResponseDto getDisputeById(UUID disputeId) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found: " + disputeId));
        return DisputeResponseDto.from(dispute);
    }

    @Transactional(readOnly = true)
    public List<DisputeResponseDto> getDisputesForContract(UUID contractId) {
        return disputeRepository.findByContractId(contractId).stream()
                .map(DisputeResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<DisputeResponseDto> getDisputesByUser(UUID userId, Pageable pageable) {
        Page<Dispute> page = disputeRepository.findByRaisedByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(page.map(DisputeResponseDto::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<DisputeResponseDto> getAllDisputes(DisputeStatus status, Pageable pageable) {
        Page<Dispute> page = (status != null)
                ? disputeRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                : disputeRepository.findAll(pageable);
        return PageResponse.from(page.map(DisputeResponseDto::from));
    }

    @Transactional
    public DisputeResponseDto startReview(UUID adminId, UUID disputeId) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found: " + disputeId));

        dispute.setStatus(DisputeStatus.UNDER_REVIEW);
        Dispute saved = disputeRepository.save(dispute);

        auditService.logAction(adminId, AuditAction.DISPUTE_UNDER_REVIEW, "DISPUTE",
                saved.getId().toString(), "Admin commenced review on dispute #" + disputeId, null);

        return DisputeResponseDto.from(saved);
    }

    @Transactional
    public DisputeResponseDto resolveDispute(UUID adminId, UUID disputeId, DisputeResolveDto dto) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found: " + disputeId));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + adminId));

        SaleContract contract = dispute.getContract();
        List<PaymentTransaction> payments = paymentRepository.findByContractId(contract.getId());

        PaymentTransaction escrowPayment = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.HELD_IN_ESCROW || p.getStatus() == PaymentStatus.DISPUTED)
                .findFirst()
                .orElse(null);

        switch (dto.getResolution()) {
            case RELEASE_TO_FARMER -> {
                dispute.setStatus(DisputeStatus.RESOLVED_FOR_FARMER);
                if (escrowPayment != null) {
                    paymentService.releasePayment(escrowPayment.getId(), adminId);
                }
                try {
                    meritService.awardMeritPoints(
                            contract.getFarmer().getId(),
                            5,
                            "Dispute Resolved in Farmer's Favor: " + dto.getResolutionNotes(),
                            "CONTRACT #" + contract.getContractNumber()
                    );
                } catch (Exception e) {
                    log.warn("Failed to award merit points: {}", e.getMessage());
                }
            }
            case REFUND_BUYER -> {
                dispute.setStatus(DisputeStatus.RESOLVED_FOR_BUYER);
                if (escrowPayment != null) {
                    paymentService.refundPayment(escrowPayment.getId(), adminId, dto.getResolutionNotes());
                }
                try {
                    meritService.deductMeritPoints(
                            contract.getFarmer().getId(),
                            15,
                            "Condition Breach: Dispute resolved in favor of Buyer (" + dto.getResolutionNotes() + ")",
                            "CONTRACT #" + contract.getContractNumber()
                    );
                } catch (Exception e) {
                    log.warn("Failed to deduct merit points: {}", e.getMessage());
                }
            }
            case PARTIALLY_RESOLVED -> {
                dispute.setStatus(DisputeStatus.PARTIALLY_RESOLVED);
                if (escrowPayment != null) {
                    escrowPayment.setStatus(PaymentStatus.RELEASED);
                    escrowPayment.setNotes("Partially resolved by admin. Split notes: " + dto.getResolutionNotes());
                    paymentRepository.save(escrowPayment);
                }
            }
            case DISMISSED -> dispute.setStatus(DisputeStatus.CLOSED);
        }

        dispute.setResolutionNotes(dto.getResolutionNotes());
        dispute.setResolvedAt(Instant.now());
        dispute.setResolvedByAdmin(admin);

        Dispute saved = disputeRepository.save(dispute);

        // Notify both parties
        String msg = "Dispute for Contract #" + contract.getContractNumber() + " was resolved by Admin (" + dto.getResolution() + "): " + dto.getResolutionNotes();
        notificationService.createNotification(contract.getFarmer().getId(), "Dispute Resolved", msg, NotificationType.DISPUTE_RESOLVED, saved.getId().toString(), "DISPUTE");
        notificationService.createNotification(contract.getBuyer().getId(), "Dispute Resolved", msg, NotificationType.DISPUTE_RESOLVED, saved.getId().toString(), "DISPUTE");

        auditService.logAction(adminId, AuditAction.DISPUTE_RESOLVED, "DISPUTE",
                saved.getId().toString(), "Dispute resolved with action: " + dto.getResolution(), null);

        return DisputeResponseDto.from(saved);
    }
}
