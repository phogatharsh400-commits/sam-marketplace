package com.sam.marketplace.advisor;

import java.math.BigDecimal;

public class PriceAlertUpdateDto {
    private BigDecimal targetPrice;
    private String condition;
    private Boolean isActive;
    private String notificationChannel;
    private String notes;

    public PriceAlertUpdateDto() {}

    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public String getNotificationChannel() { return notificationChannel; }
    public void setNotificationChannel(String notificationChannel) { this.notificationChannel = notificationChannel; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
