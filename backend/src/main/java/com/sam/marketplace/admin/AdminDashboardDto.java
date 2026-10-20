package com.sam.marketplace.admin;

import java.math.BigDecimal;
import java.util.Map;

public class AdminDashboardDto {
    private long totalUsers;
    private long totalFarmers;
    private long totalBuyers;
    private long totalOperators;
    private long pendingVerifications;
    private long totalLots;
    private long openLots;
    private long totalBids;
    private long totalContracts;
    private long activeDisputes;
    private long totalWarehouses;
    private BigDecimal totalEscrowHeld;
    private BigDecimal totalReleasedAmount;
    private Map<String, Long> lotsByCrop;
    private Map<String, Long> contractsByStatus;

    public AdminDashboardDto() {}

    public AdminDashboardDto(long totalUsers, long totalFarmers, long totalBuyers, long totalOperators,
                             long pendingVerifications, long totalLots, long openLots, long totalBids,
                             long totalContracts, long activeDisputes, long totalWarehouses,
                             BigDecimal totalEscrowHeld, BigDecimal totalReleasedAmount,
                             Map<String, Long> lotsByCrop, Map<String, Long> contractsByStatus) {
        this.totalUsers = totalUsers;
        this.totalFarmers = totalFarmers;
        this.totalBuyers = totalBuyers;
        this.totalOperators = totalOperators;
        this.pendingVerifications = pendingVerifications;
        this.totalLots = totalLots;
        this.openLots = openLots;
        this.totalBids = totalBids;
        this.totalContracts = totalContracts;
        this.activeDisputes = activeDisputes;
        this.totalWarehouses = totalWarehouses;
        this.totalEscrowHeld = totalEscrowHeld;
        this.totalReleasedAmount = totalReleasedAmount;
        this.lotsByCrop = lotsByCrop;
        this.contractsByStatus = contractsByStatus;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long totalUsers;
        private long totalFarmers;
        private long totalBuyers;
        private long totalOperators;
        private long pendingVerifications;
        private long totalLots;
        private long openLots;
        private long totalBids;
        private long totalContracts;
        private long activeDisputes;
        private long totalWarehouses;
        private BigDecimal totalEscrowHeld;
        private BigDecimal totalReleasedAmount;
        private Map<String, Long> lotsByCrop;
        private Map<String, Long> contractsByStatus;

        public Builder totalUsers(long totalUsers) { this.totalUsers = totalUsers; return this; }
        public Builder totalFarmers(long totalFarmers) { this.totalFarmers = totalFarmers; return this; }
        public Builder totalBuyers(long totalBuyers) { this.totalBuyers = totalBuyers; return this; }
        public Builder totalOperators(long totalOperators) { this.totalOperators = totalOperators; return this; }
        public Builder pendingVerifications(long pendingVerifications) { this.pendingVerifications = pendingVerifications; return this; }
        public Builder totalLots(long totalLots) { this.totalLots = totalLots; return this; }
        public Builder openLots(long openLots) { this.openLots = openLots; return this; }
        public Builder totalBids(long totalBids) { this.totalBids = totalBids; return this; }
        public Builder totalContracts(long totalContracts) { this.totalContracts = totalContracts; return this; }
        public Builder activeDisputes(long activeDisputes) { this.activeDisputes = activeDisputes; return this; }
        public Builder totalWarehouses(long totalWarehouses) { this.totalWarehouses = totalWarehouses; return this; }
        public Builder totalEscrowHeld(BigDecimal totalEscrowHeld) { this.totalEscrowHeld = totalEscrowHeld; return this; }
        public Builder totalReleasedAmount(BigDecimal totalReleasedAmount) { this.totalReleasedAmount = totalReleasedAmount; return this; }
        public Builder lotsByCrop(Map<String, Long> lotsByCrop) { this.lotsByCrop = lotsByCrop; return this; }
        public Builder contractsByStatus(Map<String, Long> contractsByStatus) { this.contractsByStatus = contractsByStatus; return this; }

        public AdminDashboardDto build() {
            return new AdminDashboardDto(totalUsers, totalFarmers, totalBuyers, totalOperators,
                    pendingVerifications, totalLots, openLots, totalBids, totalContracts,
                    activeDisputes, totalWarehouses, totalEscrowHeld, totalReleasedAmount,
                    lotsByCrop, contractsByStatus);
        }
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalFarmers() { return totalFarmers; }
    public void setTotalFarmers(long totalFarmers) { this.totalFarmers = totalFarmers; }
    public long getTotalBuyers() { return totalBuyers; }
    public void setTotalBuyers(long totalBuyers) { this.totalBuyers = totalBuyers; }
    public long getTotalOperators() { return totalOperators; }
    public void setTotalOperators(long totalOperators) { this.totalOperators = totalOperators; }
    public long getPendingVerifications() { return pendingVerifications; }
    public void setPendingVerifications(long pendingVerifications) { this.pendingVerifications = pendingVerifications; }
    public long getTotalLots() { return totalLots; }
    public void setTotalLots(long totalLots) { this.totalLots = totalLots; }
    public long getOpenLots() { return openLots; }
    public void setOpenLots(long openLots) { this.openLots = openLots; }
    public long getTotalBids() { return totalBids; }
    public void setTotalBids(long totalBids) { this.totalBids = totalBids; }
    public long getTotalContracts() { return totalContracts; }
    public void setTotalContracts(long totalContracts) { this.totalContracts = totalContracts; }
    public long getActiveDisputes() { return activeDisputes; }
    public void setActiveDisputes(long activeDisputes) { this.activeDisputes = activeDisputes; }
    public long getTotalWarehouses() { return totalWarehouses; }
    public void setTotalWarehouses(long totalWarehouses) { this.totalWarehouses = totalWarehouses; }
    public BigDecimal getTotalEscrowHeld() { return totalEscrowHeld; }
    public void setTotalEscrowHeld(BigDecimal totalEscrowHeld) { this.totalEscrowHeld = totalEscrowHeld; }
    public BigDecimal getTotalReleasedAmount() { return totalReleasedAmount; }
    public void setTotalReleasedAmount(BigDecimal totalReleasedAmount) { this.totalReleasedAmount = totalReleasedAmount; }
    public Map<String, Long> getLotsByCrop() { return lotsByCrop; }
    public void setLotsByCrop(Map<String, Long> lotsByCrop) { this.lotsByCrop = lotsByCrop; }
    public Map<String, Long> getContractsByStatus() { return contractsByStatus; }
    public void setContractsByStatus(Map<String, Long> contractsByStatus) { this.contractsByStatus = contractsByStatus; }
}
