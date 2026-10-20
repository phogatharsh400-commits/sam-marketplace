package com.sam.marketplace.advisor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class StorageAdviceDto {
    private UUID id;
    private UUID lotId;
    private String lotNumber;
    private String crop;
    private String variety;
    private String district;
    private BigDecimal quantityQuintals;
    private String recommendation; // SELL_NOW, STORE, WAIT_AND_MONITOR
    private BigDecimal currentBestBidPrice;
    private BigDecimal currentMandiModalPrice;
    private BigDecimal mspPrice;
    private BigDecimal estimatedFuturePrice;
    private BigDecimal monthlyStorageCost;
    private BigDecimal handlingCost;
    private BigDecimal totalStorageAndHandlingCost;
    private BigDecimal netExpectedGainPerQuintal;
    private BigDecimal totalEstimatedBenefit;
    private String confidence; // LOW, MEDIUM, HIGH
    private List<String> reasons;
    private Integer recommendedHoldingDays;
    private String disclaimer;
    private Instant createdAt;

    public StorageAdviceDto() {}

    public static StorageAdviceDto from(StorageAdvice advice, List<String> reasons) {
        StorageAdviceDto dto = new StorageAdviceDto();
        dto.setId(advice.getId());
        dto.setLotId(advice.getLot().getId());
        dto.setLotNumber(advice.getLot().getLotNumber());
        dto.setCrop(advice.getLot().getCrop().name());
        dto.setVariety(advice.getLot().getVariety());
        dto.setDistrict(advice.getLot().getDistrict());
        dto.setQuantityQuintals(advice.getLot().getQuantityQuintals());
        dto.setRecommendation(advice.getRecommendation());
        dto.setCurrentBestBidPrice(advice.getCurrentBestBidPrice());
        dto.setCurrentMandiModalPrice(advice.getCurrentMandiModalPrice());
        dto.setMspPrice(advice.getMspPrice());
        dto.setEstimatedFuturePrice(advice.getEstimatedFuturePrice());
        dto.setMonthlyStorageCost(advice.getMonthlyStorageCost());
        dto.setHandlingCost(advice.getHandlingCost());
        dto.setTotalStorageAndHandlingCost(advice.getMonthlyStorageCost().add(advice.getHandlingCost()));
        dto.setNetExpectedGainPerQuintal(advice.getNetExpectedGainPerQuintal());
        dto.setTotalEstimatedBenefit(advice.getTotalEstimatedBenefit());
        dto.setConfidence(advice.getConfidence());
        dto.setReasons(reasons);
        dto.setRecommendedHoldingDays(advice.getRecommendedHoldingDays());
        dto.setDisclaimer(advice.getDisclaimer());
        dto.setCreatedAt(advice.getCreatedAt());
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getLotId() { return lotId; }
    public void setLotId(UUID lotId) { this.lotId = lotId; }
    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }
    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }
    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public BigDecimal getQuantityQuintals() { return quantityQuintals; }
    public void setQuantityQuintals(BigDecimal quantityQuintals) { this.quantityQuintals = quantityQuintals; }
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
    public BigDecimal getTotalStorageAndHandlingCost() { return totalStorageAndHandlingCost; }
    public void setTotalStorageAndHandlingCost(BigDecimal totalStorageAndHandlingCost) { this.totalStorageAndHandlingCost = totalStorageAndHandlingCost; }
    public BigDecimal getNetExpectedGainPerQuintal() { return netExpectedGainPerQuintal; }
    public void setNetExpectedGainPerQuintal(BigDecimal netExpectedGainPerQuintal) { this.netExpectedGainPerQuintal = netExpectedGainPerQuintal; }
    public BigDecimal getTotalEstimatedBenefit() { return totalEstimatedBenefit; }
    public void setTotalEstimatedBenefit(BigDecimal totalEstimatedBenefit) { this.totalEstimatedBenefit = totalEstimatedBenefit; }
    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }
    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
    public Integer getRecommendedHoldingDays() { return recommendedHoldingDays; }
    public void setRecommendedHoldingDays(Integer recommendedHoldingDays) { this.recommendedHoldingDays = recommendedHoldingDays; }
    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
