package com.sam.marketplace.user;

import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.RoleType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String mobileNumber;

    @Column(unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountStatus status = AccountStatus.PENDING_VERIFICATION;

    @Column(nullable = false)
    private boolean isMobileVerified = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FarmerProfile farmerProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BuyerProfile buyerProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private WarehouseOperatorProfile warehouseOperatorProfile;

    @Column(nullable = false)
    private Integer meritPoints = 100;

    @Column(nullable = false, length = 30)
    private String trustTier = "PLATINUM";

    @Column(nullable = false)
    private Integer totalViolations = 0;

    @Column(nullable = false)
    private Integer totalSuccessfulTrades = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public User() {}

    public User(UUID id, String mobileNumber, String email, String passwordHash, String fullName,
                RoleType role, AccountStatus status, boolean isMobileVerified,
                FarmerProfile farmerProfile, BuyerProfile buyerProfile,
                WarehouseOperatorProfile warehouseOperatorProfile, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.status = status != null ? status : AccountStatus.PENDING_VERIFICATION;
        this.isMobileVerified = isMobileVerified;
        this.farmerProfile = farmerProfile;
        this.buyerProfile = buyerProfile;
        this.warehouseOperatorProfile = warehouseOperatorProfile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public boolean isMobileVerified() { return isMobileVerified; }
    public void setMobileVerified(boolean mobileVerified) { isMobileVerified = mobileVerified; }
    public FarmerProfile getFarmerProfile() { return farmerProfile; }
    public void setFarmerProfile(FarmerProfile farmerProfile) { this.farmerProfile = farmerProfile; }
    public BuyerProfile getBuyerProfile() { return buyerProfile; }
    public void setBuyerProfile(BuyerProfile buyerProfile) { this.buyerProfile = buyerProfile; }
    public WarehouseOperatorProfile getWarehouseOperatorProfile() { return warehouseOperatorProfile; }
    public void setWarehouseOperatorProfile(WarehouseOperatorProfile warehouseOperatorProfile) { this.warehouseOperatorProfile = warehouseOperatorProfile; }
    public Integer getMeritPoints() { return meritPoints != null ? meritPoints : 100; }
    public void setMeritPoints(Integer meritPoints) { 
        this.meritPoints = Math.max(0, Math.min(100, meritPoints != null ? meritPoints : 100));
        this.trustTier = calculateTrustTier(this.meritPoints);
    }
    public String getTrustTier() { return trustTier != null ? trustTier : calculateTrustTier(getMeritPoints()); }
    public void setTrustTier(String trustTier) { this.trustTier = trustTier; }
    public Integer getTotalViolations() { return totalViolations != null ? totalViolations : 0; }
    public void setTotalViolations(Integer totalViolations) { this.totalViolations = totalViolations; }
    public Integer getTotalSuccessfulTrades() { return totalSuccessfulTrades != null ? totalSuccessfulTrades : 0; }
    public void setTotalSuccessfulTrades(Integer totalSuccessfulTrades) { this.totalSuccessfulTrades = totalSuccessfulTrades; }

    public static String calculateTrustTier(int points) {
        if (points >= 90) return "PLATINUM";
        if (points >= 75) return "GOLD";
        if (points >= 60) return "SILVER";
        return "WATCHLIST";
    }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String mobileNumber;
        private String email;
        private String passwordHash;
        private String fullName;
        private RoleType role;
        private AccountStatus status = AccountStatus.PENDING_VERIFICATION;
        private boolean isMobileVerified = false;
        private Integer meritPoints = 100;
        private String trustTier = "PLATINUM";
        private Integer totalViolations = 0;
        private Integer totalSuccessfulTrades = 0;
        private FarmerProfile farmerProfile;
        private BuyerProfile buyerProfile;
        private WarehouseOperatorProfile warehouseOperatorProfile;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder mobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder role(RoleType role) { this.role = role; return this; }
        public Builder status(AccountStatus status) { this.status = status; return this; }
        public Builder isMobileVerified(boolean isMobileVerified) { this.isMobileVerified = isMobileVerified; return this; }
        public Builder meritPoints(Integer meritPoints) { 
            this.meritPoints = meritPoints; 
            this.trustTier = calculateTrustTier(meritPoints != null ? meritPoints : 100);
            return this; 
        }
        public Builder trustTier(String trustTier) { this.trustTier = trustTier; return this; }
        public Builder totalViolations(Integer totalViolations) { this.totalViolations = totalViolations; return this; }
        public Builder totalSuccessfulTrades(Integer totalSuccessfulTrades) { this.totalSuccessfulTrades = totalSuccessfulTrades; return this; }
        public Builder farmerProfile(FarmerProfile farmerProfile) { this.farmerProfile = farmerProfile; return this; }
        public Builder buyerProfile(BuyerProfile buyerProfile) { this.buyerProfile = buyerProfile; return this; }
        public Builder warehouseOperatorProfile(WarehouseOperatorProfile warehouseOperatorProfile) { this.warehouseOperatorProfile = warehouseOperatorProfile; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public User build() {
            User user = new User(id, mobileNumber, email, passwordHash, fullName, role, status, isMobileVerified, farmerProfile, buyerProfile, warehouseOperatorProfile, createdAt, updatedAt);
            user.setMeritPoints(meritPoints);
            user.setTrustTier(trustTier);
            user.setTotalViolations(totalViolations);
            user.setTotalSuccessfulTrades(totalSuccessfulTrades);
            return user;
        }
    }
}
