package com.sam.marketplace.user;

import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.RoleType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class UserDto {
    private UUID id;
    private String mobileNumber;
    private String email;
    private String fullName;
    private RoleType role;
    private AccountStatus status;
    private boolean isMobileVerified;
    private Instant createdAt;

    // Merit & Trust Scoring
    private Integer meritPoints = 100;
    private String trustTier = "PLATINUM";
    private Integer totalViolations = 0;
    private Integer totalSuccessfulTrades = 0;

    // Profile Details
    private String state;
    private String district;
    private String village;
    private String bankAccountNumber;
    private String bankIfsc;
    private String preferredLanguage;
    private BigDecimal landAreaAcres;

    // Buyer Specific
    private String businessName;
    private String gstin;
    private String panNumber;
    private String businessAddress;
    private String buyerType;

    // Warehouse Operator Specific
    private String licenseNumber;

    public UserDto() {}

    public static UserDto from(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setMobileVerified(user.isMobileVerified());
        dto.setCreatedAt(user.getCreatedAt());

        dto.setMeritPoints(user.getMeritPoints());
        dto.setTrustTier(user.getTrustTier());
        dto.setTotalViolations(user.getTotalViolations());
        dto.setTotalSuccessfulTrades(user.getTotalSuccessfulTrades());

        if (user.getFarmerProfile() != null) {
            FarmerProfile fp = user.getFarmerProfile();
            dto.setState(fp.getState());
            dto.setDistrict(fp.getDistrict());
            dto.setVillage(fp.getVillage());
            dto.setBankAccountNumber(fp.getBankAccountNumber());
            dto.setBankIfsc(fp.getBankIfsc());
            dto.setPreferredLanguage(fp.getPreferredLanguage());
            dto.setLandAreaAcres(fp.getLandAreaAcres());
        }

        if (user.getBuyerProfile() != null) {
            BuyerProfile bp = user.getBuyerProfile();
            dto.setBusinessName(bp.getBusinessName());
            dto.setGstin(bp.getGstin());
            dto.setPanNumber(bp.getPanNumber());
            dto.setBusinessAddress(bp.getBusinessAddress());
            dto.setState(bp.getState());
            dto.setDistrict(bp.getDistrict());
            dto.setBuyerType(bp.getBuyerType());
        }

        if (user.getWarehouseOperatorProfile() != null) {
            WarehouseOperatorProfile wp = user.getWarehouseOperatorProfile();
            dto.setBusinessName(wp.getBusinessName());
            dto.setLicenseNumber(wp.getLicenseNumber());
            dto.setState(wp.getState());
            dto.setDistrict(wp.getDistrict());
        }

        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public boolean isMobileVerified() { return isMobileVerified; }
    public void setMobileVerified(boolean mobileVerified) { isMobileVerified = mobileVerified; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Integer getMeritPoints() { return meritPoints != null ? meritPoints : 100; }
    public void setMeritPoints(Integer meritPoints) { this.meritPoints = meritPoints; }
    public String getTrustTier() { return trustTier != null ? trustTier : "PLATINUM"; }
    public void setTrustTier(String trustTier) { this.trustTier = trustTier; }
    public Integer getTotalViolations() { return totalViolations != null ? totalViolations : 0; }
    public void setTotalViolations(Integer totalViolations) { this.totalViolations = totalViolations; }
    public Integer getTotalSuccessfulTrades() { return totalSuccessfulTrades != null ? totalSuccessfulTrades : 0; }
    public void setTotalSuccessfulTrades(Integer totalSuccessfulTrades) { this.totalSuccessfulTrades = totalSuccessfulTrades; }

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
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }
    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public String getBuyerType() { return buyerType; }
    public void setBuyerType(String buyerType) { this.buyerType = buyerType; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final UserDto dto = new UserDto();
        public Builder id(UUID id) { dto.setId(id); return this; }
        public Builder mobileNumber(String mobileNumber) { dto.setMobileNumber(mobileNumber); return this; }
        public Builder email(String email) { dto.setEmail(email); return this; }
        public Builder fullName(String fullName) { dto.setFullName(fullName); return this; }
        public Builder role(RoleType role) { dto.setRole(role); return this; }
        public Builder status(AccountStatus status) { dto.setStatus(status); return this; }
        public Builder isMobileVerified(boolean isMobileVerified) { dto.setMobileVerified(isMobileVerified); return this; }
        public Builder createdAt(Instant createdAt) { dto.setCreatedAt(createdAt); return this; }
        public Builder meritPoints(Integer meritPoints) { dto.setMeritPoints(meritPoints); return this; }
        public Builder trustTier(String trustTier) { dto.setTrustTier(trustTier); return this; }
        public Builder totalViolations(Integer totalViolations) { dto.setTotalViolations(totalViolations); return this; }
        public Builder totalSuccessfulTrades(Integer totalSuccessfulTrades) { dto.setTotalSuccessfulTrades(totalSuccessfulTrades); return this; }
        public Builder state(String state) { dto.setState(state); return this; }
        public Builder district(String district) { dto.setDistrict(district); return this; }
        public Builder village(String village) { dto.setVillage(village); return this; }
        public Builder bankAccountNumber(String bankAccountNumber) { dto.setBankAccountNumber(bankAccountNumber); return this; }
        public Builder bankIfsc(String bankIfsc) { dto.setBankIfsc(bankIfsc); return this; }
        public Builder preferredLanguage(String preferredLanguage) { dto.setPreferredLanguage(preferredLanguage); return this; }
        public Builder landAreaAcres(BigDecimal landAreaAcres) { dto.setLandAreaAcres(landAreaAcres); return this; }
        public Builder businessName(String businessName) { dto.setBusinessName(businessName); return this; }
        public Builder gstin(String gstin) { dto.setGstin(gstin); return this; }
        public Builder panNumber(String panNumber) { dto.setPanNumber(panNumber); return this; }
        public Builder businessAddress(String businessAddress) { dto.setBusinessAddress(businessAddress); return this; }
        public Builder buyerType(String buyerType) { dto.setBuyerType(buyerType); return this; }
        public Builder licenseNumber(String licenseNumber) { dto.setLicenseNumber(licenseNumber); return this; }
        public UserDto build() { return dto; }
    }
}
