package com.sam.marketplace.advisor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PriceAlertDto {
    private UUID id;
    private UUID userId;
    private String crop;
    private String district;
    private BigDecimal targetPrice;
    private String condition;
    private boolean isActive;
    private boolean isTriggered;
    private Instant triggeredAt;
    private String notificationChannel;
    private String notes;
    private Instant createdAt;

    public PriceAlertDto() {}

    public static PriceAlertDto from(PriceAlert alert) {
        PriceAlertDto dto = new PriceAlertDto();
        dto.setId(alert.getId());
        dto.setUserId(alert.getUser().getId());
        dto.setCrop(alert.getCrop().name());
        dto.setDistrict(alert.getDistrict());
        dto.setTargetPrice(alert.getTargetPrice());
        dto.setCondition(alert.getCondition());
        dto.setActive(alert.isActive());
        dto.setTriggered(alert.isTriggered());
        dto.setTriggeredAt(alert.getTriggeredAt());
        dto.setNotificationChannel(alert.getNotificationChannel());
        dto.setNotes(alert.getNotes());
        dto.setCreatedAt(alert.getCreatedAt());
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public boolean isTriggered() { return isTriggered; }
    public void setTriggered(boolean triggered) { isTriggered = triggered; }
    public Instant getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(Instant triggeredAt) { this.triggeredAt = triggeredAt; }
    public String getNotificationChannel() { return notificationChannel; }
    public void setNotificationChannel(String notificationChannel) { this.notificationChannel = notificationChannel; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
