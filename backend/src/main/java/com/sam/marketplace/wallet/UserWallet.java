package com.sam.marketplace.wallet;

import com.sam.marketplace.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_wallets")
public class UserWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true, length = 50)
    private String walletNumber;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal lockedBalance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalDeposited = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalWithdrawn = BigDecimal.ZERO;

    @Column(length = 100)
    private String activeTokenBatch;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public UserWallet() {}

    public UserWallet(UUID id, User user, String walletNumber, BigDecimal availableBalance,
                      BigDecimal lockedBalance, BigDecimal totalDeposited, BigDecimal totalWithdrawn,
                      String activeTokenBatch, Long version, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.walletNumber = walletNumber;
        this.availableBalance = availableBalance != null ? availableBalance : BigDecimal.ZERO;
        this.lockedBalance = lockedBalance != null ? lockedBalance : BigDecimal.ZERO;
        this.totalDeposited = totalDeposited != null ? totalDeposited : BigDecimal.ZERO;
        this.totalWithdrawn = totalWithdrawn != null ? totalWithdrawn : BigDecimal.ZERO;
        this.activeTokenBatch = activeTokenBatch;
        this.version = version != null ? version : 0L;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private User user;
        private String walletNumber;
        private BigDecimal availableBalance = BigDecimal.ZERO;
        private BigDecimal lockedBalance = BigDecimal.ZERO;
        private BigDecimal totalDeposited = BigDecimal.ZERO;
        private BigDecimal totalWithdrawn = BigDecimal.ZERO;
        private String activeTokenBatch;
        private Long version = 0L;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder walletNumber(String walletNumber) { this.walletNumber = walletNumber; return this; }
        public Builder availableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; return this; }
        public Builder lockedBalance(BigDecimal lockedBalance) { this.lockedBalance = lockedBalance; return this; }
        public Builder totalDeposited(BigDecimal totalDeposited) { this.totalDeposited = totalDeposited; return this; }
        public Builder totalWithdrawn(BigDecimal totalWithdrawn) { this.totalWithdrawn = totalWithdrawn; return this; }
        public Builder activeTokenBatch(String activeTokenBatch) { this.activeTokenBatch = activeTokenBatch; return this; }
        public Builder version(Long version) { this.version = version; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public UserWallet build() {
            return new UserWallet(id, user, walletNumber, availableBalance, lockedBalance,
                    totalDeposited, totalWithdrawn, activeTokenBatch, version, createdAt, updatedAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getWalletNumber() { return walletNumber; }
    public void setWalletNumber(String walletNumber) { this.walletNumber = walletNumber; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
    public BigDecimal getLockedBalance() { return lockedBalance; }
    public void setLockedBalance(BigDecimal lockedBalance) { this.lockedBalance = lockedBalance; }
    public BigDecimal getTotalDeposited() { return totalDeposited; }
    public void setTotalDeposited(BigDecimal totalDeposited) { this.totalDeposited = totalDeposited; }
    public BigDecimal getTotalWithdrawn() { return totalWithdrawn; }
    public void setTotalWithdrawn(BigDecimal totalWithdrawn) { this.totalWithdrawn = totalWithdrawn; }
    public String getActiveTokenBatch() { return activeTokenBatch; }
    public void setActiveTokenBatch(String activeTokenBatch) { this.activeTokenBatch = activeTokenBatch; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
