package com.sam.marketplace.user;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "buyer_profiles")
public class BuyerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 200)
    private String businessName;

    @Column(length = 30)
    private String gstin;

    @Column(length = 20)
    private String panNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String businessAddress;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(length = 50)
    private String buyerType = "TRADER";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public BuyerProfile() {}

    public BuyerProfile(UUID id, User user, String businessName, String gstin, String panNumber,
                        String businessAddress, String state, String district, String buyerType,
                        Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.businessName = businessName;
        this.gstin = gstin;
        this.panNumber = panNumber;
        this.businessAddress = businessAddress;
        this.state = state;
        this.district = district;
        this.buyerType = buyerType != null ? buyerType : "TRADER";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }
    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getBuyerType() { return buyerType; }
    public void setBuyerType(String buyerType) { this.buyerType = buyerType; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private User user;
        private String businessName;
        private String gstin;
        private String panNumber;
        private String businessAddress;
        private String state;
        private String district;
        private String buyerType = "TRADER";
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder businessName(String businessName) { this.businessName = businessName; return this; }
        public Builder gstin(String gstin) { this.gstin = gstin; return this; }
        public Builder panNumber(String panNumber) { this.panNumber = panNumber; return this; }
        public Builder businessAddress(String businessAddress) { this.businessAddress = businessAddress; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder district(String district) { this.district = district; return this; }
        public Builder buyerType(String buyerType) { this.buyerType = buyerType; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public BuyerProfile build() {
            return new BuyerProfile(id, user, businessName, gstin, panNumber, businessAddress, state, district, buyerType, createdAt, updatedAt);
        }
    }
}
