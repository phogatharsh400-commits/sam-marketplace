package com.sam.marketplace.advisor;

import com.sam.marketplace.common.enums.CropType;
import com.sam.marketplace.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "price_alerts")
public class PriceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CropType crop;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal targetPrice;

    @Column(nullable = false, length = 30)
    private String condition = "EQUAL_OR_ABOVE"; // EQUAL_OR_ABOVE, BELOW

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private boolean isTriggered = false;

    private Instant triggeredAt;

    @Column(length = 30)
    private String notificationChannel = "IN_APP"; // IN_APP, SMS, ALL

    @Column(length = 255)
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public PriceAlert() {}

    public PriceAlert(UUID id, User user, CropType crop, String district, BigDecimal targetPrice,
                      String condition, boolean isActive, boolean isTriggered, Instant triggeredAt,
                      String notificationChannel, String notes, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.crop = crop;
        this.district = district;
        this.targetPrice = targetPrice;
        this.condition = condition != null ? condition : "EQUAL_OR_ABOVE";
        this.isActive = isActive;
        this.isTriggered = isTriggered;
        this.triggeredAt = triggeredAt;
        this.notificationChannel = notificationChannel != null ? notificationChannel : "IN_APP";
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private User user;
        private CropType crop;
        private String district;
        private BigDecimal targetPrice;
        private String condition = "EQUAL_OR_ABOVE";
        private boolean isActive = true;
        private boolean isTriggered = false;
        private Instant triggeredAt;
        private String notificationChannel = "IN_APP";
        private String notes;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder crop(CropType crop) { this.crop = crop; return this; }
        public Builder district(String district) { this.district = district; return this; }
        public Builder targetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; return this; }
        public Builder condition(String condition) { this.condition = condition; return this; }
        public Builder isActive(boolean isActive) { this.isActive = isActive; return this; }
        public Builder isTriggered(boolean isTriggered) { this.isTriggered = isTriggered; return this; }
        public Builder triggeredAt(Instant triggeredAt) { this.triggeredAt = triggeredAt; return this; }
        public Builder notificationChannel(String notificationChannel) { this.notificationChannel = notificationChannel; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public PriceAlert build() {
            return new PriceAlert(id, user, crop, district, targetPrice, condition,
                    isActive, isTriggered, triggeredAt, notificationChannel, notes, createdAt, updatedAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public CropType getCrop() { return crop; }
    public void setCrop(CropType crop) { this.crop = crop; }
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
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
