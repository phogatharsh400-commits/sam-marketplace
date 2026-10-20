package com.sam.marketplace.auth;

import com.sam.marketplace.common.enums.AccountStatus;
import com.sam.marketplace.common.enums.RoleType;

import java.util.UUID;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private UUID userId;
    private String mobileNumber;
    private String email;
    private String fullName;
    private RoleType role;
    private AccountStatus status;
    private boolean isMobileVerified;
    private String mockOtpNote; // Dev prototype hint

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, String tokenType, UUID userId,
                        String mobileNumber, String email, String fullName, RoleType role,
                        AccountStatus status, boolean isMobileVerified, String mockOtpNote) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.userId = userId;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.isMobileVerified = isMobileVerified;
        this.mockOtpNote = mockOtpNote;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private UUID userId;
        private String mobileNumber;
        private String email;
        private String fullName;
        private RoleType role;
        private AccountStatus status;
        private boolean isMobileVerified;
        private String mockOtpNote;

        public Builder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public Builder refreshToken(String refreshToken) { this.refreshToken = refreshToken; return this; }
        public Builder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder mobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder role(RoleType role) { this.role = role; return this; }
        public Builder status(AccountStatus status) { this.status = status; return this; }
        public Builder isMobileVerified(boolean isMobileVerified) { this.isMobileVerified = isMobileVerified; return this; }
        public Builder mockOtpNote(String mockOtpNote) { this.mockOtpNote = mockOtpNote; return this; }

        public AuthResponse build() {
            return new AuthResponse(accessToken, refreshToken, tokenType, userId, mobileNumber,
                    email, fullName, role, status, isMobileVerified, mockOtpNote);
        }
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
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
    public String getMockOtpNote() { return mockOtpNote; }
    public void setMockOtpNote(String mockOtpNote) { this.mockOtpNote = mockOtpNote; }
}
