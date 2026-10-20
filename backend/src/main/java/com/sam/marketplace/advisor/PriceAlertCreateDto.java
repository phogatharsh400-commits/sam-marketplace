package com.sam.marketplace.advisor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PriceAlertCreateDto {

    @NotBlank(message = "Crop is required")
    private String crop;

    @NotBlank(message = "District is required")
    private String district;

    @NotNull(message = "Target price is required")
    @DecimalMin(value = "100.00", message = "Target price must be at least ₹100.00")
    private BigDecimal targetPrice;

    private String condition = "EQUAL_OR_ABOVE"; // EQUAL_OR_ABOVE, BELOW

    private String notificationChannel = "IN_APP"; // IN_APP, SMS, ALL

    private String notes;

    public PriceAlertCreateDto() {}

    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getNotificationChannel() { return notificationChannel; }
    public void setNotificationChannel(String notificationChannel) { this.notificationChannel = notificationChannel; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
