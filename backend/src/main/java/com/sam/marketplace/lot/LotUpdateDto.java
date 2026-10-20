package com.sam.marketplace.lot;

import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.common.enums.ProduceGrade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class LotUpdateDto {
    private CropType crop;
    private String variety;
    private BigDecimal quantityQuintals;
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
    private List<String> imageUrls;

    public LotUpdateDto() {}

    public CropType getCrop() { return crop; }
    public void setCrop(CropType crop) { this.crop = crop; }
    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }
    public BigDecimal getQuantityQuintals() { return quantityQuintals; }
    public void setQuantityQuintals(BigDecimal quantityQuintals) { this.quantityQuintals = quantityQuintals; }
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
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}
