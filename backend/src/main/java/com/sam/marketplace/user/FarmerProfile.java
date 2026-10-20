package com.sam.marketplace.user;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "farmer_profiles")
public class FarmerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 150)
    private String village;

    @Column(length = 50)
    private String bankAccountNumber;

    @Column(length = 20)
    private String bankIfsc;

    @Column(length = 20)
    private String preferredLanguage = "en";

    @Column(precision = 8, scale = 2)
    private BigDecimal landAreaAcres = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public FarmerProfile() {}

    public FarmerProfile(UUID id, User user, String state, String district, String village,
                         String bankAccountNumber, String bankIfsc, String preferredLanguage,
                         BigDecimal landAreaAcres, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.state = state;
        this.district = district;
        this.village = village;
        this.bankAccountNumber = bankAccountNumber;
        this.bankIfsc = bankIfsc;
        this.preferredLanguage = preferredLanguage != null ? preferredLanguage : "en";
        this.landAreaAcres = landAreaAcres != null ? landAreaAcres : BigDecimal.ZERO;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
    public String getBankIfsc() { return bankIfsc; }
    public void setBankIfsc(String bankIfsc) { this.bankIfsc = bankIfsc; }
    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
    public BigDecimal getLandAreaAcres() { return landAreaAcres; }
    public void setLandAreaAcres(BigDecimal landAreaAcres) { this.landAreaAcres = landAreaAcres; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private User user;
        private String state;
        private String district;
        private String village;
        private String bankAccountNumber;
        private String bankIfsc;
        private String preferredLanguage = "en";
        private BigDecimal landAreaAcres = BigDecimal.ZERO;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder district(String district) { this.district = district; return this; }
        public Builder village(String village) { this.village = village; return this; }
        public Builder bankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; return this; }
        public Builder bankIfsc(String bankIfsc) { this.bankIfsc = bankIfsc; return this; }
        public Builder preferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; return this; }
        public Builder landAreaAcres(BigDecimal landAreaAcres) { this.landAreaAcres = landAreaAcres; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public FarmerProfile build() {
            return new FarmerProfile(id, user, state, district, village, bankAccountNumber, bankIfsc, preferredLanguage, landAreaAcres, createdAt, updatedAt);
        }
    }
}
