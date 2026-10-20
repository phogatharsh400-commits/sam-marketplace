package com.sam.marketplace.advisor;

import com.sam.marketplace.lot.ProduceLot;
import com.sam.marketplace.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "storage_advice")
public class StorageAdvice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private ProduceLot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    @Column(nullable = false, length = 30)
    private String recommendation; // SELL_NOW, STORE, WAIT_AND_MONITOR

    @Column(precision = 10, scale = 2)
    private BigDecimal currentBestBidPrice = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentMandiModalPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal mspPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedFuturePrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyStorageCost = BigDecimal.valueOf(20.00);

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal handlingCost = BigDecimal.valueOf(15.00);

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal netExpectedGainPerQuintal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalEstimatedBenefit = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String confidence = "MEDIUM"; // LOW, MEDIUM, HIGH

    @Column(columnDefinition = "TEXT")
    private String reasonsJson;

    @Column(nullable = false)
    private Integer recommendedHoldingDays = 30;

    @Column(columnDefinition = "TEXT")
    private String disclaimer = "Prototype decision support based on 7-day moving average trend modeling and simulated warehouse cost calculations. Not guaranteed financial advice.";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public StorageAdvice() {}

    public StorageAdvice(UUID id, ProduceLot lot, User farmer, String recommendation,
                         BigDecimal currentBestBidPrice, BigDecimal currentMandiModalPrice,
                         BigDecimal mspPrice, BigDecimal estimatedFuturePrice,
                         BigDecimal monthlyStorageCost, BigDecimal handlingCost,
                         BigDecimal netExpectedGainPerQuintal, BigDecimal totalEstimatedBenefit,
                         String confidence, String reasonsJson, Integer recommendedHoldingDays,
                         String disclaimer, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.lot = lot;
        this.farmer = farmer;
        this.recommendation = recommendation;
        this.currentBestBidPrice = currentBestBidPrice != null ? currentBestBidPrice : BigDecimal.ZERO;
        this.currentMandiModalPrice = currentMandiModalPrice;
        this.mspPrice = mspPrice;
        this.estimatedFuturePrice = estimatedFuturePrice;
        this.monthlyStorageCost = monthlyStorageCost != null ? monthlyStorageCost : BigDecimal.valueOf(20.00);
        this.handlingCost = handlingCost != null ? handlingCost : BigDecimal.valueOf(15.00);
        this.netExpectedGainPerQuintal = netExpectedGainPerQuintal != null ? netExpectedGainPerQuintal : BigDecimal.ZERO;
        this.totalEstimatedBenefit = totalEstimatedBenefit != null ? totalEstimatedBenefit : BigDecimal.ZERO;
        this.confidence = confidence != null ? confidence : "MEDIUM";
        this.reasonsJson = reasonsJson;
        this.recommendedHoldingDays = recommendedHoldingDays != null ? recommendedHoldingDays : 30;
        this.disclaimer = disclaimer != null ? disclaimer : "Prototype decision support tool based on 7-day moving average trend forecasting and simulated storage tariffs. Not guaranteed financial advice.";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private ProduceLot lot;
        private User farmer;
        private String recommendation;
        private BigDecimal currentBestBidPrice = BigDecimal.ZERO;
        private BigDecimal currentMandiModalPrice;
        private BigDecimal mspPrice;
        private BigDecimal estimatedFuturePrice;
        private BigDecimal monthlyStorageCost = BigDecimal.valueOf(20.00);
        private BigDecimal handlingCost = BigDecimal.valueOf(15.00);
        private BigDecimal netExpectedGainPerQuintal = BigDecimal.ZERO;
        private BigDecimal totalEstimatedBenefit = BigDecimal.ZERO;
        private String confidence = "MEDIUM";
        private String reasonsJson;
        private Integer recommendedHoldingDays = 30;
        private String disclaimer;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder lot(ProduceLot lot) { this.lot = lot; return this; }
        public Builder farmer(User farmer) { this.farmer = farmer; return this; }
        public Builder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public Builder currentBestBidPrice(BigDecimal currentBestBidPrice) { this.currentBestBidPrice = currentBestBidPrice; return this; }
        public Builder currentMandiModalPrice(BigDecimal currentMandiModalPrice) { this.currentMandiModalPrice = currentMandiModalPrice; return this; }
        public Builder mspPrice(BigDecimal mspPrice) { this.mspPrice = mspPrice; return this; }
        public Builder estimatedFuturePrice(BigDecimal estimatedFuturePrice) { this.estimatedFuturePrice = estimatedFuturePrice; return this; }
        public Builder monthlyStorageCost(BigDecimal monthlyStorageCost) { this.monthlyStorageCost = monthlyStorageCost; return this; }
        public Builder handlingCost(BigDecimal handlingCost) { this.handlingCost = handlingCost; return this; }
        public Builder netExpectedGainPerQuintal(BigDecimal netExpectedGainPerQuintal) { this.netExpectedGainPerQuintal = netExpectedGainPerQuintal; return this; }
        public Builder totalEstimatedBenefit(BigDecimal totalEstimatedBenefit) { this.totalEstimatedBenefit = totalEstimatedBenefit; return this; }
        public Builder confidence(String confidence) { this.confidence = confidence; return this; }
        public Builder reasonsJson(String reasonsJson) { this.reasonsJson = reasonsJson; return this; }
        public Builder recommendedHoldingDays(Integer recommendedHoldingDays) { this.recommendedHoldingDays = recommendedHoldingDays; return this; }
        public Builder disclaimer(String disclaimer) { this.disclaimer = disclaimer; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public StorageAdvice build() {
            return new StorageAdvice(id, lot, farmer, recommendation, currentBestBidPrice,
                    currentMandiModalPrice, mspPrice, estimatedFuturePrice, monthlyStorageCost,
                    handlingCost, netExpectedGainPerQuintal, totalEstimatedBenefit,
                    confidence, reasonsJson, recommendedHoldingDays, disclaimer, createdAt, updatedAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ProduceLot getLot() { return lot; }
    public void setLot(ProduceLot lot) { this.lot = lot; }
    public User getFarmer() { return farmer; }
    public void setFarmer(User farmer) { this.farmer = farmer; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public BigDecimal getCurrentBestBidPrice() { return currentBestBidPrice; }
    public void setCurrentBestBidPrice(BigDecimal currentBestBidPrice) { this.currentBestBidPrice = currentBestBidPrice; }
    public BigDecimal getCurrentMandiModalPrice() { return currentMandiModalPrice; }
    public void setCurrentMandiModalPrice(BigDecimal currentMandiModalPrice) { this.currentMandiModalPrice = currentMandiModalPrice; }
    public BigDecimal getMspPrice() { return mspPrice; }
    public void setMspPrice(BigDecimal mspPrice) { this.mspPrice = mspPrice; }
    public BigDecimal getEstimatedFuturePrice() { return estimatedFuturePrice; }
    public void setEstimatedFuturePrice(BigDecimal estimatedFuturePrice) { this.estimatedFuturePrice = estimatedFuturePrice; }
    public BigDecimal getMonthlyStorageCost() { return monthlyStorageCost; }
    public void setMonthlyStorageCost(BigDecimal monthlyStorageCost) { this.monthlyStorageCost = monthlyStorageCost; }
    public BigDecimal getHandlingCost() { return handlingCost; }
    public void setHandlingCost(BigDecimal handlingCost) { this.handlingCost = handlingCost; }
    public BigDecimal getNetExpectedGainPerQuintal() { return netExpectedGainPerQuintal; }
    public void setNetExpectedGainPerQuintal(BigDecimal netExpectedGainPerQuintal) { this.netExpectedGainPerQuintal = netExpectedGainPerQuintal; }
    public BigDecimal getTotalEstimatedBenefit() { return totalEstimatedBenefit; }
    public void setTotalEstimatedBenefit(BigDecimal totalEstimatedBenefit) { this.totalEstimatedBenefit = totalEstimatedBenefit; }
    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }
    public String getReasonsJson() { return reasonsJson; }
    public void setReasonsJson(String reasonsJson) { this.reasonsJson = reasonsJson; }
    public Integer getRecommendedHoldingDays() { return recommendedHoldingDays; }
    public void setRecommendedHoldingDays(Integer recommendedHoldingDays) { this.recommendedHoldingDays = recommendedHoldingDays; }
    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
