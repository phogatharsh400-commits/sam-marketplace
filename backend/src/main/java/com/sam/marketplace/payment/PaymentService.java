package com.sam.marketplace.payment;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.common.enums.*;
import com.sam.marketplace.common.exception.BadRequestException;
import com.sam.marketplace.common.exception.BusinessRuleException;
import com.sam.marketplace.common.exception.ResourceNotFoundException;
import com.sam.marketplace.common.exception.UnauthorizedException;
import com.sam.marketplace.contract.SaleContract;
import com.sam.marketplace.contract.SaleContractRepository;
import com.sam.marketplace.lot.ProduceLot;
import com.sam.marketplace.lot.LotRepository;
import com.sam.marketplace.notification.NotificationService;
import com.sam.marketplace.wallet.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final SaleContractRepository saleContractRepository;
    private final LotRepository lotRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final WalletService walletService;

    public PaymentService(PaymentRepository paymentRepository,
                          SaleContractRepository saleContractRepository,
                          LotRepository lotRepository,
                          NotificationService notificationService,
                          AuditService auditService,
                          WalletService walletService) {
        this.paymentRepository = paymentRepository;
        this.saleContractRepository = saleContractRepository;
        this.lotRepository = lotRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.walletService = walletService;
    }

    @Transactional
    public PaymentResponseDto fundEscrow(UUID buyerId, UUID contractId, PaymentInitiateDto dto) {
        if (dto.getIdempotencyKey() != null && !dto.getIdempotencyKey().isBlank()) {
            Optional<PaymentTransaction> existing = paymentRepository.findByIdempotencyKey(dto.getIdempotencyKey());
            if (existing.isPresent()) {
                log.info("Idempotent request match for key: {}", dto.getIdempotencyKey());
                return PaymentResponseDto.from(existing.get());
            }
        }

        SaleContract contract = saleContractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale contract not found with id: " + contractId));

        if (!contract.getBuyer().getId().equals(buyerId)) {
            throw new UnauthorizedException("You are not authorized to fund escrow for this contract");
        }

        if (contract.getStatus() != ContractStatus.AWAITING_ESCROW && contract.getStatus() != ContractStatus.CREATED) {
            throw new BusinessRuleException("Cannot fund escrow: Contract is currently in status " + contract.getStatus());
        }

        String txnRef = generateTxnReference();
        String tokenCode = "ESC-2026-" + ThreadLocalRandom.current().nextInt(100000, 999999);
        String bankRef = "RBI-NODAL-ESC-" + ThreadLocalRandom.current().nextInt(10000000, 99999999);
        String tokenHash = "SHA256:" + UUID.randomUUID().toString().replace("-", "").substring(0, 32);

        PaymentTransaction payment = PaymentTransaction.builder()
                .contract(contract)
                .payer(contract.getBuyer())
                .payee(contract.getFarmer())
                .amount(contract.getTotalAmount())
                .paymentMethod(dto.getPaymentMethod())
                .status(PaymentStatus.INITIATED)
                .transactionReference(txnRef)
                .idempotencyKey(dto.getIdempotencyKey())
                .simulationMode(true)
                .notes(dto.getNotes() != null ? dto.getNotes() : "Simulated escrow funding via " + dto.getPaymentMethod())
                .escrowTokenCode(tokenCode)
                .escrowTokenHash(tokenHash)
                .bankGuaranteeRef(bankRef)
                .build();

        if (dto.isAutoSimulateSuccess()) {
            payment.setStatus(PaymentStatus.HELD_IN_ESCROW);
            contract.setStatus(ContractStatus.ESCROW_FUNDED);
            
            ProduceLot lot = contract.getLot();
            lot.setStatus(LotStatus.READY_FOR_DISPATCH);
            lotRepository.save(lot);
            saleContractRepository.save(contract);
        }

        PaymentTransaction savedPayment = paymentRepository.save(payment);

        if (dto.isAutoSimulateSuccess()) {
            notificationService.createNotification(
                    contract.getFarmer().getId(),
                    "Escrow Funded - Dispatch Required",
                    "Buyer has funded ₹" + contract.getTotalAmount() + " in Escrow for Contract #" + contract.getContractNumber() + ". You may now safely dispatch the produce lot.",
                    NotificationType.ESCROW_FUNDED,
                    savedPayment.getId().toString(),
                    "PAYMENT"
            );

            notificationService.createNotification(
                    buyerId,
                    "Escrow Successfully Funded",
                    "Your payment of ₹" + contract.getTotalAmount() + " is securely held in Escrow for Contract #" + contract.getContractNumber() + ".",
                    NotificationType.ESCROW_FUNDED,
                    savedPayment.getId().toString(),
                    "PAYMENT"
            );

            auditService.logAction(buyerId, AuditAction.ESCROW_FUNDED, "PAYMENT",
                    savedPayment.getId().toString(), "Escrow funded: ₹" + contract.getTotalAmount() + " (Simulated)", null);
        }

        return PaymentResponseDto.from(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(UUID id) {
        PaymentTransaction payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found with id: " + id));
        return PaymentResponseDto.from(payment);
    }

    @Transactional
    public PaymentResponseDto simulateSuccess(UUID paymentId) {
        PaymentTransaction payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.INITIATED) {
            throw new BusinessRuleException("Payment cannot be transitioned to SUCCESS from status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.HELD_IN_ESCROW);
        SaleContract contract = payment.getContract();
        contract.setStatus(ContractStatus.ESCROW_FUNDED);

        ProduceLot lot = contract.getLot();
        lot.setStatus(LotStatus.READY_FOR_DISPATCH);
        lotRepository.save(lot);
        saleContractRepository.save(contract);

        PaymentTransaction saved = paymentRepository.save(payment);

        notificationService.createNotification(
                contract.getFarmer().getId(),
                "Escrow Funded - Dispatch Required",
                "Buyer funded ₹" + contract.getTotalAmount() + " in Escrow for Contract #" + contract.getContractNumber() + ".",
                NotificationType.ESCROW_FUNDED,
                saved.getId().toString(),
                "PAYMENT"
        );

        auditService.logAction(payment.getPayer().getId(), AuditAction.ESCROW_FUNDED, "PAYMENT",
                saved.getId().toString(), "Simulated escrow payment confirmed", null);

        return PaymentResponseDto.from(saved);
    }

    @Transactional
    public PaymentResponseDto simulateFailure(UUID paymentId) {
        PaymentTransaction payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.INITIATED) {
            throw new BusinessRuleException("Payment cannot be failed from status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.FAILED);
        PaymentTransaction saved = paymentRepository.save(payment);

        return PaymentResponseDto.from(saved);
    }

    @Transactional
    public PaymentResponseDto releasePayment(UUID paymentId, UUID actorId) {
        PaymentTransaction payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.HELD_IN_ESCROW && payment.getStatus() != PaymentStatus.DISPUTED) {
            throw new BusinessRuleException("Cannot release payment in current status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.RELEASED);
        SaleContract contract = payment.getContract();
        contract.setStatus(ContractStatus.PAYMENT_RELEASED);

        ProduceLot lot = contract.getLot();
        lot.setStatus(LotStatus.PAID);
        lotRepository.save(lot);
        saleContractRepository.save(contract);

        PaymentTransaction saved = paymentRepository.save(payment);

        // Credit Farmer Wallet Payout so farmer can withdraw
        try {
            walletService.creditFarmerEscrowPayout(contract.getFarmer().getId(), contract.getTotalAmount(), contract.getContractNumber());
        } catch (Exception e) {
            log.warn("Farmer wallet credit handled gracefully: {}", e.getMessage());
        }

        // Notify Farmer
        notificationService.createNotification(
                contract.getFarmer().getId(),
                "Payment Released to Your Account",
                "₹" + contract.getTotalAmount() + " has been successfully released from Escrow to your SAM wallet for Contract #" + contract.getContractNumber() + ". You can withdraw to bank anytime!",
                NotificationType.PAYMENT_RELEASED,
                saved.getId().toString(),
                "PAYMENT"
        );

        // Notify Buyer
        notificationService.createNotification(
                contract.getBuyer().getId(),
                "Escrow Payment Completed",
                "Escrow payment of ₹" + contract.getTotalAmount() + " released to farmer for Contract #" + contract.getContractNumber() + ".",
                NotificationType.PAYMENT_RELEASED,
                saved.getId().toString(),
                "PAYMENT"
        );

        auditService.logAction(actorId, AuditAction.PAYMENT_RELEASED, "PAYMENT",
                saved.getId().toString(), "Escrow payment released: ₹" + contract.getTotalAmount(), null);

        return PaymentResponseDto.from(saved);
    }

    @Transactional
    public PaymentResponseDto refundPayment(UUID paymentId, UUID actorId, String reason) {
        PaymentTransaction payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.HELD_IN_ESCROW && payment.getStatus() != PaymentStatus.DISPUTED) {
            throw new BusinessRuleException("Cannot refund payment in current status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setNotes((payment.getNotes() != null ? payment.getNotes() + " | " : "") + "Refunded: " + reason);

        SaleContract contract = payment.getContract();
        contract.setStatus(ContractStatus.REFUNDED);
        saleContractRepository.save(contract);

        PaymentTransaction saved = paymentRepository.save(payment);

        // Unlock / Credit refund back to buyer wallet
        try {
            walletService.unlockFundsForBid(contract.getBuyer().getId(), contract.getTotalAmount(), contract.getContractNumber(), "Escrow Dispute Refund: " + reason);
        } catch (Exception e) {
            log.warn("Buyer wallet refund handled gracefully: {}", e.getMessage());
        }

        notificationService.createNotification(
                contract.getBuyer().getId(),
                "Escrow Payment Refunded",
                "Your Escrow payment of ₹" + contract.getTotalAmount() + " has been refunded to your wallet. Reason: " + reason,
                NotificationType.PAYMENT_REFUNDED,
                saved.getId().toString(),
                "PAYMENT"
        );

        auditService.logAction(actorId, AuditAction.PAYMENT_REFUNDED, "PAYMENT",
                saved.getId().toString(), "Escrow payment refunded: " + reason, null);

        return PaymentResponseDto.from(saved);
    }

    private String generateTxnReference() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int rand = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "TXN-" + dateStr + "-" + rand;
    }
}
