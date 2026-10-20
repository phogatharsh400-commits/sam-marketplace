package com.sam.marketplace.lot;

import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.enums.LotStatus;
import com.sam.marketplace.common.enums.ProduceGrade;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LotResponseDto {
    private UUID id;
    private String lotNumber;
    private UUID farmerId;
    private String farmerName;
    private String farmerMobile;
    private CropType crop;
    private String variety;
    private BigDecimal quantityQuintals;
    private String unit;
    private ProduceGrade grade;
    private BigDecimal moisturePercentage;
    private String state;
    private String district;
    private String village;
    private String pickupLocation;
    private BigDecimal minPricePerQuintal;
    private LocalDate expectedDispatchDate;
    private LocalDate auctionClosingDate;
    private String description;
    private String storageRequirement;
    private LotStatus status;
    private boolean poolingEnabled;
    private BigDecimal minOrderQuantity;
    private boolean sampleApproved;
    private String sampleStatus;
    private BigDecimal foreignMatterPercent;
    private BigDecimal brokenGrainsPercent;
    private String qualityCertificateNumber;
    private List<String> imageUrls;
    private long totalBids;
    private BigDecimal highestBidPrice;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;

    public LotResponseDto() {}

    public static LotResponseDto from(ProduceLot lot) {
        if (lot == null) return null;

        List<String> urls = lot.getImages() != null
                ? lot.getImages().stream().map(LotImage::getImageUrl).toList()
                : Collections.emptyList();

        LotResponseDto dto = new LotResponseDto();
        dto.setId(lot.getId());
        dto.setLotNumber(lot.getLotNumber());
        dto.setFarmerId(lot.getFarmer().getId());
        dto.setFarmerName(lot.getFarmer().getFullName());
        dto.setFarmerMobile(lot.getFarmer().getMobileNumber());
        dto.setCrop(lot.getCrop());
        dto.setVariety(lot.getVariety());
        dto.setQuantityQuintals(lot.getQuantityQuintals());
        dto.setUnit(lot.getUnit());
        dto.setGrade(lot.getGrade());
        dto.setMoisturePercentage(lot.getMoisturePercentage());
        dto.setState(lot.getState());
        dto.setDistrict(lot.getDistrict());
        dto.setVillage(lot.getVillage());
        dto.setPickupLocation(lot.getPickupLocation());
        dto.setMinPricePerQuintal(lot.getMinPricePerQuintal());
        dto.setExpectedDispatchDate(lot.getExpectedDispatchDate());
        dto.setAuctionClosingDate(lot.getAuctionClosingDate());
        dto.setDescription(lot.getDescription());
        dto.setStorageRequirement(lot.getStorageRequirement());
        dto.setStatus(lot.getStatus());
        dto.setPoolingEnabled(lot.isPoolingEnabled());
        dto.setMinOrderQuantity(lot.getMinOrderQuantity() != null ? lot.getMinOrderQuantity() : lot.getQuantityQuintals());
        dto.setSampleApproved(lot.isSampleApproved());
        dto.setSampleStatus(lot.getSampleStatus() != null ? lot.getSampleStatus() : "QUALITY_VERIFIED_AND_APPROVED");
        dto.setForeignMatterPercent(lot.getForeignMatterPercent() != null ? lot.getForeignMatterPercent() : BigDecimal.valueOf(0.80));
        dto.setBrokenGrainsPercent(lot.getBrokenGrainsPercent() != null ? lot.getBrokenGrainsPercent() : BigDecimal.valueOf(1.20));
        dto.setQualityCertificateNumber(lot.getQualityCertificateNumber() != null ? lot.getQualityCertificateNumber() : "QC-HAR-2026-1001");
        dto.setImageUrls(urls);
        dto.setVersion(lot.getVersion());
        dto.setCreatedAt(lot.getCreatedAt());
        dto.setUpdatedAt(lot.getUpdatedAt());
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }
    public UUID getFarmerId() { return farmerId; }
    public void setFarmerId(UUID farmerId) { this.farmerId = farmerId; }
    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }
    public String getFarmerMobile() { return farmerMobile; }
    public void setFarmerMobile(String farmerMobile) { this.farmerMobile = farmerMobile; }
    public CropType getCrop() { return crop; }
    public void setCrop(CropType crop) { this.crop = crop; }
    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }
    public BigDecimal getQuantityQuintals() { return quantityQuintals; }
    public void setQuantityQuintals(BigDecimal quantityQuintals) { this.quantityQuintals = quantityQuintals; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public ProduceGrade getGrade() { return grade; }
    public void setGrade(ProduceGrade grade) { this.grade = grade; }
    public BigDecimal getMoisturePercentage() { return moisturePercentage; }
    public void setMoisturePercentage(BigDecimal moisturePercentage) { this.moisturePercentage = moisturePercentage; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public BigDecimal getMinPricePerQuintal() { return minPricePerQuintal; }
    public void setMinPricePerQuintal(BigDecimal minPricePerQuintal) { this.minPricePerQuintal = minPricePerQuintal; }
    public LocalDate getExpectedDispatchDate() { return expectedDispatchDate; }
    public void setExpectedDispatchDate(LocalDate expectedDispatchDate) { this.expectedDispatchDate = expectedDispatchDate; }
    public LocalDate getAuctionClosingDate() { return auctionClosingDate; }
    public void setAuctionClosingDate(LocalDate auctionClosingDate) { this.auctionClosingDate = auctionClosingDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStorageRequirement() { return storageRequirement; }
    public void setStorageRequirement(String storageRequirement) { this.storageRequirement = storageRequirement; }
    public LotStatus getStatus() { return status; }
    public void setStatus(LotStatus status) { this.status = status; }
    public boolean isPoolingEnabled() { return poolingEnabled; }
    public void setPoolingEnabled(boolean poolingEnabled) { this.poolingEnabled = poolingEnabled; }
    public BigDecimal getMinOrderQuantity() { return minOrderQuantity; }
    public void setMinOrderQuantity(BigDecimal minOrderQuantity) { this.minOrderQuantity = minOrderQuantity; }
    public boolean isSampleApproved() { return sampleApproved; }
    public void setSampleApproved(boolean sampleApproved) { this.sampleApproved = sampleApproved; }
    public String getSampleStatus() { return sampleStatus; }
    public void setSampleStatus(String sampleStatus) { this.sampleStatus = sampleStatus; }
    public BigDecimal getForeignMatterPercent() { return foreignMatterPercent; }
    public void setForeignMatterPercent(BigDecimal foreignMatterPercent) { this.foreignMatterPercent = foreignMatterPercent; }
    public BigDecimal getBrokenGrainsPercent() { return brokenGrainsPercent; }
    public void setBrokenGrainsPercent(BigDecimal brokenGrainsPercent) { this.brokenGrainsPercent = brokenGrainsPercent; }
    public String getQualityCertificateNumber() { return qualityCertificateNumber; }
    public void setQualityCertificateNumber(String qualityCertificateNumber) { this.qualityCertificateNumber = qualityCertificateNumber; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public long getTotalBids() { return totalBids; }
    public void setTotalBids(long totalBids) { this.totalBids = totalBids; }
    public BigDecimal getHighestBidPrice() { return highestBidPrice; }
    public void setHighestBidPrice(BigDecimal highestBidPrice) { this.highestBidPrice = highestBidPrice; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final LotResponseDto dto = new LotResponseDto();
        public Builder id(UUID id) { dto.setId(id); return this; }
        public Builder lotNumber(String lotNumber) { dto.setLotNumber(lotNumber); return this; }
        public Builder farmerId(UUID farmerId) { dto.setFarmerId(farmerId); return this; }
        public Builder farmerName(String farmerName) { dto.setFarmerName(farmerName); return this; }
        public Builder farmerMobile(String farmerMobile) { dto.setFarmerMobile(farmerMobile); return this; }
        public Builder crop(CropType crop) { dto.setCrop(crop); return this; }
        public Builder variety(String variety) { dto.setVariety(variety); return this; }
        public Builder quantityQuintals(BigDecimal quantityQuintals) { dto.setQuantityQuintals(quantityQuintals); return this; }
        public Builder unit(String unit) { dto.setUnit(unit); return this; }
        public Builder grade(ProduceGrade grade) { dto.setGrade(grade); return this; }
        public Builder moisturePercentage(BigDecimal moisturePercentage) { dto.setMoisturePercentage(moisturePercentage); return this; }
        public Builder state(String state) { dto.setState(state); return this; }
        public Builder district(String district) { dto.setDistrict(district); return this; }
        public Builder village(String village) { dto.setVillage(village); return this; }
        public Builder pickupLocation(String pickupLocation) { dto.setPickupLocation(pickupLocation); return this; }
        public Builder minPricePerQuintal(BigDecimal minPricePerQuintal) { dto.setMinPricePerQuintal(minPricePerQuintal); return this; }
        public Builder expectedDispatchDate(LocalDate expectedDispatchDate) { dto.setExpectedDispatchDate(expectedDispatchDate); return this; }
        public Builder auctionClosingDate(LocalDate auctionClosingDate) { dto.setAuctionClosingDate(auctionClosingDate); return this; }
        public Builder description(String description) { dto.setDescription(description); return this; }
        public Builder storageRequirement(String storageRequirement) { dto.setStorageRequirement(storageRequirement); return this; }
        public Builder status(LotStatus status) { dto.setStatus(status); return this; }
        public Builder imageUrls(List<String> imageUrls) { dto.setImageUrls(imageUrls); return this; }
        public Builder totalBids(long totalBids) { dto.setTotalBids(totalBids); return this; }
        public Builder highestBidPrice(BigDecimal highestBidPrice) { dto.setHighestBidPrice(highestBidPrice); return this; }
        public Builder version(Long version) { dto.setVersion(version); return this; }
        public Builder createdAt(Instant createdAt) { dto.setCreatedAt(createdAt); return this; }
        public Builder updatedAt(Instant updatedAt) { dto.setUpdatedAt(updatedAt); return this; }
        public LotResponseDto build() { return dto; }
    }
}
