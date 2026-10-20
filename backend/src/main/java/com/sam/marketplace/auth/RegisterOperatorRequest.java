package com.sam.marketplace.auth;

import jakarta.validation.constraints.*;

public class RegisterOperatorRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Business/Warehouse firm name is required")
    private String businessName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be a valid 10-digit number")
    private String mobileNumber;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String licenseNumber;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "District is required")
    private String district;

    public RegisterOperatorRequest() {}

    public RegisterOperatorRequest(String fullName, String businessName, String mobileNumber,
                                   String email, String password, String licenseNumber,
                                   String state, String district) {
        this.fullName = fullName;
        this.businessName = businessName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.password = password;
        this.licenseNumber = licenseNumber;
        this.state = state;
        this.district = district;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
}
