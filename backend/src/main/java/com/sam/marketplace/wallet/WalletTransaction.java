package com.sam.marketplace.wallet;

import com.sam.marketplace.common.enums.WalletTransactionStatus;
import com.sam.marketplace.common.enums.WalletTransactionType;
import com.sam.marketplace.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private UserWallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WalletTransactionType type;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal balanceAfter;

    @Column(nullable = false, unique = true, length = 100)
    private String referenceId;

    @Column(length = 100)
    private String tokenCode;

    @Column(length = 50)
    private String paymentMethod;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String bankAccountNumber;

    @Column(length = 20)
    private String ifscCode;

    @Column(length = 100)
    private String upiId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WalletTransactionStatus status = WalletTransactionStatus.COMPLETED;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public WalletTransaction() {}

    public WalletTransaction(UUID id, UserWallet wallet, User user, WalletTransactionType type,
                             BigDecimal amount, BigDecimal balanceAfter, String referenceId,
                             String tokenCode, String paymentMethod, String description,
                             String bankAccountNumber, String ifscCode, String upiId,
                             WalletTransactionStatus status, Instant createdAt) {
        this.id = id;
        this.wallet = wallet;
        this.user = user;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.referenceId = referenceId;
        this.tokenCode = tokenCode;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.bankAccountNumber = bankAccountNumber;
        this.ifscCode = ifscCode;
        this.upiId = upiId;
        this.status = status != null ? status : WalletTransactionStatus.COMPLETED;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UserWallet wallet;
        private User user;
        private WalletTransactionType type;
        private BigDecimal amount;
        private BigDecimal balanceAfter;
        private String referenceId;
        private String tokenCode;
        private String paymentMethod;
        private String description;
        private String bankAccountNumber;
        private String ifscCode;
        private String upiId;
        private WalletTransactionStatus status = WalletTransactionStatus.COMPLETED;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder wallet(UserWallet wallet) { this.wallet = wallet; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder type(WalletTransactionType type) { this.type = type; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder balanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; return this; }
        public Builder referenceId(String referenceId) { this.referenceId = referenceId; return this; }
        public Builder tokenCode(String tokenCode) { this.tokenCode = tokenCode; return this; }
        public Builder paymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder bankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; return this; }
        public Builder ifscCode(String ifscCode) { this.ifscCode = ifscCode; return this; }
        public Builder upiId(String upiId) { this.upiId = upiId; return this; }
        public Builder status(WalletTransactionStatus status) { this.status = status; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public WalletTransaction build() {
            return new WalletTransaction(id, wallet, user, type, amount, balanceAfter, referenceId,
                    tokenCode, paymentMethod, description, bankAccountNumber, ifscCode, upiId, status, createdAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UserWallet getWallet() { return wallet; }
    public void setWallet(UserWallet wallet) { this.wallet = wallet; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public WalletTransactionType getType() { return type; }
    public void setType(WalletTransactionType type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    public String getTokenCode() { return tokenCode; }
    public void setTokenCode(String tokenCode) { this.tokenCode = tokenCode; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
    public WalletTransactionStatus getStatus() { return status; }
    public void setStatus(WalletTransactionStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
