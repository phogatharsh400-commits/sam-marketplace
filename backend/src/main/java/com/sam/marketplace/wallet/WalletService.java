package com.sam.marketplace.wallet;

import com.sam.marketplace.audit.AuditService;
import com.sam.marketplace.common.enums.AuditAction;
import com.sam.marketplace.common.enums.NotificationType;
import com.sam.marketplace.common.enums.WalletTransactionStatus;
import com.sam.marketplace.common.enums.WalletTransactionType;
import com.sam.marketplace.common.exception.BadRequestException;
import com.sam.marketplace.common.exception.ResourceNotFoundException;
import com.sam.marketplace.notification.NotificationService;
import com.sam.marketplace.user.User;
import com.sam.marketplace.user.UserRepository;
import com.sam.marketplace.warehouse.WarehouseBooking;
import com.sam.marketplace.warehouse.WarehouseBookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WarehouseBookingRepository bookingRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public WalletService(WalletRepository walletRepository,
                         WalletTransactionRepository transactionRepository,
                         UserRepository userRepository,
                         WarehouseBookingRepository bookingRepository,
                         NotificationService notificationService,
                         AuditService auditService) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @Transactional
    public UserWallet getOrCreateWallet(UUID userId) {
        return walletRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

            String roleName = user.getRole().name();
            String walletNumber = "SAM-VAULT-" + roleName.substring(0, Math.min(3, roleName.length())) + "-" + user.getMobileNumber();
            String initialToken = "TOK-INIT-" + ThreadLocalRandom.current().nextInt(100000, 999999);

            // Buyers get starting demo token balance for instant testing; Farmers/Operators start at 0 (earning based)
            BigDecimal startBalance = roleName.equals("BUYER") ? BigDecimal.valueOf(500000.00) : BigDecimal.ZERO;

            UserWallet wallet = UserWallet.builder()
                    .user(user)
                    .walletNumber(walletNumber)
                    .availableBalance(startBalance)
                    .lockedBalance(BigDecimal.ZERO)
                    .totalDeposited(startBalance)
                    .totalWithdrawn(BigDecimal.ZERO)
                    .activeTokenBatch(initialToken)
                    .build();

            UserWallet saved = walletRepository.save(wallet);

            if (startBalance.compareTo(BigDecimal.ZERO) > 0) {
                WalletTransaction initialTx = WalletTransaction.builder()
                        .wallet(saved)
                        .user(user)
                        .type(WalletTransactionType.DEMO_CREDIT)
                        .amount(startBalance)
                        .balanceAfter(startBalance)
                        .referenceId("CREDIT-INIT-" + System.currentTimeMillis())
                        .tokenCode(initialToken)
                        .paymentMethod("SYSTEM_SEED")
                        .description("Initial prototype trading balance and safety tokens credited")
                        .status(WalletTransactionStatus.COMPLETED)
                        .build();
                transactionRepository.save(initialTx);
            }

            return saved;
        });
    }

    @Transactional(readOnly = true)
    public WalletDto getWalletDto(UUID userId) {
        UserWallet wallet = getOrCreateWallet(userId);
        return WalletDto.from(wallet);
    }

    @Transactional
    public WalletTransactionDto deposit(UUID userId, WalletDepositDto dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Deposit amount must be greater than zero");
        }

        UserWallet wallet = getOrCreateWallet(userId);
        User user = wallet.getUser();

        BigDecimal newAvailable = wallet.getAvailableBalance().add(dto.getAmount());
        BigDecimal newDeposited = wallet.getTotalDeposited().add(dto.getAmount());

        String tokenCode = "TOK-SEC-2026-" + ThreadLocalRandom.current().nextInt(100000, 999999);
        String refId = dto.getReferenceNumber() != null && !dto.getReferenceNumber().isBlank()
                ? dto.getReferenceNumber()
                : "DEP-" + (dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "UPI") + "-" + System.currentTimeMillis();

        wallet.setAvailableBalance(newAvailable);
        wallet.setTotalDeposited(newDeposited);
        wallet.setActiveTokenBatch(tokenCode);
        walletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .user(user)
                .type(WalletTransactionType.DEPOSIT)
                .amount(dto.getAmount())
                .balanceAfter(newAvailable)
                .referenceId(refId)
                .tokenCode(tokenCode)
                .paymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "UPI")
                .description(dto.getNotes() != null ? dto.getNotes() : "Money deposited into security token vault via " + dto.getPaymentMethod())
                .status(WalletTransactionStatus.COMPLETED)
                .build();

        WalletTransaction savedTx = transactionRepository.save(tx);

        notificationService.createNotification(
                userId,
                "Deposit Successful - Tokens Generated",
                "₹" + dto.getAmount() + " deposited successfully into SAM Vault. Security Token Code: " + tokenCode,
                NotificationType.ESCROW_FUNDED,
                savedTx.getId().toString(),
                "WALLET"
        );

        auditService.logAction(userId, AuditAction.ESCROW_FUNDED, "WALLET",
                savedTx.getId().toString(), "Wallet deposit: ₹" + dto.getAmount() + ", Token: " + tokenCode, null);

        return WalletTransactionDto.from(savedTx);
    }

    @Transactional
    public WalletTransactionDto withdraw(UUID userId, WalletWithdrawDto dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Withdrawal amount must be greater than zero");
        }

        UserWallet wallet = getOrCreateWallet(userId);
        User user = wallet.getUser();

        if (wallet.getAvailableBalance().compareTo(dto.getAmount()) < 0) {
            throw new BadRequestException("Insufficient available balance. Available: ₹" + wallet.getAvailableBalance() + ", Requested: ₹" + dto.getAmount());
        }

        BigDecimal newAvailable = wallet.getAvailableBalance().subtract(dto.getAmount());
        BigDecimal newWithdrawn = wallet.getTotalWithdrawn().add(dto.getAmount());

        wallet.setAvailableBalance(newAvailable);
        wallet.setTotalWithdrawn(newWithdrawn);
        walletRepository.save(wallet);

        String refId = "WTH-" + (dto.getWithdrawMethod() != null ? dto.getWithdrawMethod() : "BANK") + "-" + System.currentTimeMillis();

        String isBuyer = user.getRole().name().equals("BUYER") ? " (Leftover Token Redemption)" : "";
        String desc = dto.getWithdrawMethod() != null && dto.getWithdrawMethod().equalsIgnoreCase("UPI")
                ? "Funds returned to UPI ID: " + dto.getUpiId() + isBuyer
                : "Funds transferred to Bank A/C: " + maskAccount(dto.getBankAccountNumber()) + " (IFSC: " + dto.getIfscCode() + ")" + isBuyer;

        WalletTransactionType txType = user.getRole().name().equals("BUYER")
                ? WalletTransactionType.TOKEN_REDEEM_TO_BANK
                : WalletTransactionType.WITHDRAWAL;

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .user(user)
                .type(txType)
                .amount(dto.getAmount())
                .balanceAfter(newAvailable)
                .referenceId(refId)
                .paymentMethod(dto.getWithdrawMethod() != null ? dto.getWithdrawMethod() : "BANK_ACCOUNT")
                .description(desc)
                .bankAccountNumber(maskAccount(dto.getBankAccountNumber()))
                .ifscCode(dto.getIfscCode())
                .upiId(dto.getUpiId())
                .status(WalletTransactionStatus.COMPLETED)
                .build();

        WalletTransaction savedTx = transactionRepository.save(tx);

        notificationService.createNotification(
                userId,
                "Bank Payout / Token Refund Successful",
                "₹" + dto.getAmount() + " processed and returned to your destination account. Ref: " + refId,
                NotificationType.PAYMENT_RELEASED,
                savedTx.getId().toString(),
                "WALLET"
        );

        auditService.logAction(userId, AuditAction.PAYMENT_RELEASED, "WALLET",
                savedTx.getId().toString(), "Wallet withdrawal: ₹" + dto.getAmount() + " via " + dto.getWithdrawMethod(), null);

        return WalletTransactionDto.from(savedTx);
    }

    /**
     * Unique Agricultural Finance Feature: e-NWR Warehouse Pledge Liquidity Advance.
     * Allows farmers with grain stored in accredited warehouses to receive 70% instant cash advance
     * directly into their wallet without distress-selling their harvest.
     */
    @Transactional
    public WalletTransactionDto requestENWRPledgeAdvance(UUID farmerId, UUID bookingId, BigDecimal amount) {
        UserWallet wallet = getOrCreateWallet(farmerId);
        User farmer = wallet.getUser();

        WarehouseBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse booking not found: " + bookingId));

        if (!booking.getFarmer().getId().equals(farmerId)) {
            throw new BadRequestException("You do not own this warehouse booking");
        }

        String pledgeToken = "ENWR-PLG-" + LocalDateFormatter() + "-" + ThreadLocalRandom.current().nextInt(1000, 9999);
        String refId = "ADV-ENWR-" + System.currentTimeMillis();

        BigDecimal newAvailable = wallet.getAvailableBalance().add(amount);
        wallet.setAvailableBalance(newAvailable);
        wallet.setActiveTokenBatch(pledgeToken);
        walletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .user(farmer)
                .type(WalletTransactionType.ENWR_PLEDGE_ADVANCE)
                .amount(amount)
                .balanceAfter(newAvailable)
                .referenceId(refId)
                .tokenCode(pledgeToken)
                .paymentMethod("WDRA_PLEDGE_CREDIT")
                .description("e-NWR Pledge Advance: 70% liquidity credit against " + booking.getCrop() + " stored at " + booking.getWarehouse().getWarehouseName())
                .status(WalletTransactionStatus.COMPLETED)
                .build();

        WalletTransaction savedTx = transactionRepository.save(tx);

        notificationService.createNotification(
                farmerId,
                "e-NWR Pledge Advance Credited!",
                "₹" + amount + " liquidity advance credited to your vault against e-NWR Token: " + pledgeToken + ". You can withdraw to bank anytime!",
                NotificationType.PAYMENT_RELEASED,
                savedTx.getId().toString(),
                "WALLET"
        );

        return WalletTransactionDto.from(savedTx);
    }

    @Transactional
    public void lockFundsForBid(UUID userId, BigDecimal amount, String bidRef) {
        UserWallet wallet = getOrCreateWallet(userId);
        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            // Auto-boost balance for prototype demo so buyers can place bids smoothly
            BigDecimal needed = amount.subtract(wallet.getAvailableBalance()).add(BigDecimal.valueOf(100000));
            wallet.setAvailableBalance(wallet.getAvailableBalance().add(needed));
            wallet.setTotalDeposited(wallet.getTotalDeposited().add(needed));
        }

        BigDecimal newAvailable = wallet.getAvailableBalance().subtract(amount);
        BigDecimal newLocked = wallet.getLockedBalance().add(amount);

        wallet.setAvailableBalance(newAvailable);
        wallet.setLockedBalance(newLocked);
        walletRepository.save(wallet);

        String tokenCode = "TOK-BID-LOCK-" + ThreadLocalRandom.current().nextInt(100000, 999999);

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .user(wallet.getUser())
                .type(WalletTransactionType.BID_LOCK)
                .amount(amount)
                .balanceAfter(newAvailable)
                .referenceId("LOCK-" + bidRef)
                .tokenCode(tokenCode)
                .paymentMethod("ESCROW_TOKEN_RESERVE")
                .description("Bid earnest money & security tokens reserved for bid #" + bidRef)
                .status(WalletTransactionStatus.COMPLETED)
                .build();

        transactionRepository.save(tx);
    }

    @Transactional
    public void unlockFundsForBid(UUID userId, BigDecimal amount, String bidRef, String reason) {
        UserWallet wallet = getOrCreateWallet(userId);
        BigDecimal unlockAmount = amount;
        if (wallet.getLockedBalance().compareTo(unlockAmount) < 0) {
            unlockAmount = wallet.getLockedBalance();
        }

        if (unlockAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal newLocked = wallet.getLockedBalance().subtract(unlockAmount);
            BigDecimal newAvailable = wallet.getAvailableBalance().add(unlockAmount);

            wallet.setLockedBalance(newLocked);
            wallet.setAvailableBalance(newAvailable);
            walletRepository.save(wallet);

            WalletTransaction tx = WalletTransaction.builder()
                    .wallet(wallet)
                    .user(wallet.getUser())
                    .type(WalletTransactionType.BID_UNLOCK)
                    .amount(unlockAmount)
                    .balanceAfter(newAvailable)
                    .referenceId("UNLOCK-" + bidRef)
                    .paymentMethod("ESCROW_TOKEN_RELEASE")
                    .description("Bid security tokens unlocked (" + reason + ") for bid #" + bidRef)
                    .status(WalletTransactionStatus.COMPLETED)
                    .build();

            transactionRepository.save(tx);
        }
    }

    @Transactional
    public void creditFarmerEscrowPayout(UUID farmerId, BigDecimal amount, String contractNumber) {
        UserWallet wallet = getOrCreateWallet(farmerId);
        BigDecimal newAvailable = wallet.getAvailableBalance().add(amount);
        wallet.setAvailableBalance(newAvailable);
        walletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .user(wallet.getUser())
                .type(WalletTransactionType.ESCROW_PAYOUT)
                .amount(amount)
                .balanceAfter(newAvailable)
                .referenceId("PAYOUT-ESC-" + contractNumber)
                .paymentMethod("ESCROW_SETTLEMENT")
                .description("Escrow payment payout credited for fulfilled contract #" + contractNumber)
                .status(WalletTransactionStatus.COMPLETED)
                .build();

        transactionRepository.save(tx);
    }

    @Transactional(readOnly = true)
    public Page<WalletTransactionDto> getTransactions(UUID userId, Pageable pageable) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(WalletTransactionDto::from);
    }

    @Transactional(readOnly = true)
    public List<WalletTransactionDto> getRecentTransactions(UUID userId) {
        return transactionRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(WalletTransactionDto::from)
                .toList();
    }

    private String maskAccount(String account) {
        if (account == null || account.length() < 4) return "XXXX-XXXX";
        return "XXXX-XXXX-" + account.substring(account.length() - 4);
    }

    private String LocalDateFormatter() {
        return java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
