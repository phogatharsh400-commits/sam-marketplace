package com.sam.marketplace.wallet;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class WalletDto {
    private UUID id;
    private UUID userId;
    private String userName;
    private String userRole;
    private String walletNumber;
    private BigDecimal availableBalance;
    private BigDecimal lockedBalance;
    private BigDecimal totalBalance;
    private BigDecimal totalDeposited;
    private BigDecimal totalWithdrawn;
    private String activeTokenBatch;
    private Instant updatedAt;

    public WalletDto() {}

    public static WalletDto from(UserWallet wallet) {
        WalletDto dto = new WalletDto();
        dto.setId(wallet.getId());
        dto.setUserId(wallet.getUser().getId());
        dto.setUserName(wallet.getUser().getFullName());
        dto.setUserRole(wallet.getUser().getRole().name());
        dto.setWalletNumber(wallet.getWalletNumber());
        dto.setAvailableBalance(wallet.getAvailableBalance());
        dto.setLockedBalance(wallet.getLockedBalance());
        dto.setTotalBalance(wallet.getAvailableBalance().add(wallet.getLockedBalance()));
        dto.setTotalDeposited(wallet.getTotalDeposited());
        dto.setTotalWithdrawn(wallet.getTotalWithdrawn());
        dto.setActiveTokenBatch(wallet.getActiveTokenBatch());
        dto.setUpdatedAt(wallet.getUpdatedAt());
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public String getWalletNumber() { return walletNumber; }
    public void setWalletNumber(String walletNumber) { this.walletNumber = walletNumber; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
    public BigDecimal getLockedBalance() { return lockedBalance; }
    public void setLockedBalance(BigDecimal lockedBalance) { this.lockedBalance = lockedBalance; }
    public BigDecimal getTotalBalance() { return totalBalance; }
    public void setTotalBalance(BigDecimal totalBalance) { this.totalBalance = totalBalance; }
    public BigDecimal getTotalDeposited() { return totalDeposited; }
    public void setTotalDeposited(BigDecimal totalDeposited) { this.totalDeposited = totalDeposited; }
    public BigDecimal getTotalWithdrawn() { return totalWithdrawn; }
    public void setTotalWithdrawn(BigDecimal totalWithdrawn) { this.totalWithdrawn = totalWithdrawn; }
    public String getActiveTokenBatch() { return activeTokenBatch; }
    public void setActiveTokenBatch(String activeTokenBatch) { this.activeTokenBatch = activeTokenBatch; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
