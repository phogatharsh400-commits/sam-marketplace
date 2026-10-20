package com.sam.marketplace.lot;

import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.enums.ProduceGrade;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class LotCreateDto {

    @NotNull(message = "Crop type is required")
    private CropType crop;

    @NotBlank(message = "Variety is required")
    private String variety;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.1", message = "Quantity must be at least 0.1 quintals")
    private BigDecimal quantityQuintals;

    private String unit = "QUINTAL";

    @NotNull(message = "Grade is required")
    private ProduceGrade grade;

    @NotNull(message = "Moisture percentage is required")
    @DecimalMin(value = "0.0", message = "Moisture percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Moisture percentage cannot exceed 100%")
    private BigDecimal moisturePercentage;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Village is required")
    private String village;

    @NotBlank(message = "Pickup location details are required")
    private String pickupLocation;

    @NotNull(message = "Minimum acceptable price per quintal is required")
    @DecimalMin(value = "1.0", message = "Minimum price must be greater than zero")
    private BigDecimal minPricePerQuintal;

    @NotNull(message = "Expected dispatch date is required")
    private LocalDate expectedDispatchDate;

    @NotNull(message = "Auction closing date is required")
    private LocalDate auctionClosingDate;

    private String description;
    private String storageRequirement = "DRY_COVERED";
    private boolean poolingEnabled = false;
    private BigDecimal minOrderQuantity;
    private boolean sampleApproved = true;
    private BigDecimal foreignMatterPercent = BigDecimal.valueOf(0.80);
    private BigDecimal brokenGrainsPercent = BigDecimal.valueOf(1.20);
    private List<String> imageUrls;
    private boolean publishImmediately = true;

    public LotCreateDto() {}

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
    public boolean isPoolingEnabled() { return poolingEnabled; }
    public void setPoolingEnabled(boolean poolingEnabled) { this.poolingEnabled = poolingEnabled; }
    public BigDecimal getMinOrderQuantity() { return minOrderQuantity; }
    public void setMinOrderQuantity(BigDecimal minOrderQuantity) { this.minOrderQuantity = minOrderQuantity; }
    public boolean isSampleApproved() { return sampleApproved; }
    public void setSampleApproved(boolean sampleApproved) { this.sampleApproved = sampleApproved; }
    public BigDecimal getForeignMatterPercent() { return foreignMatterPercent; }
    public void setForeignMatterPercent(BigDecimal foreignMatterPercent) { this.foreignMatterPercent = foreignMatterPercent; }
    public BigDecimal getBrokenGrainsPercent() { return brokenGrainsPercent; }
    public void setBrokenGrainsPercent(BigDecimal brokenGrainsPercent) { this.brokenGrainsPercent = brokenGrainsPercent; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public boolean isPublishImmediately() { return publishImmediately; }
    public void setPublishImmediately(boolean publishImmediately) { this.publishImmediately = publishImmediately; }
}
