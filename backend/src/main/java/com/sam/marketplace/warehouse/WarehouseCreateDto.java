package com.sam.marketplace.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class WarehouseCreateDto {

    @NotBlank(message = "Warehouse name is required")
    private String warehouseName;

    @NotBlank(message = "Owner/Manager name is required")
    private String ownerName;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Total capacity is required")
    @DecimalMin(value = "1.0", message = "Capacity must be greater than zero")
    private BigDecimal totalCapacityQuintals;

    @NotBlank(message = "Supported crops is required (comma separated)")
    private String supportedCrops;

    @NotNull(message = "Storage charge is required")
    @DecimalMin(value = "0.0", message = "Storage charge cannot be negative")
    private BigDecimal storageChargePerQuintalMonth;

    private boolean isAccredited = true;
    private String description;

    public WarehouseCreateDto() {}

    public WarehouseCreateDto(String warehouseName, String ownerName, String contactNumber,
                              String state, String district, String address,
                              BigDecimal totalCapacityQuintals, String supportedCrops,
                              BigDecimal storageChargePerQuintalMonth, boolean isAccredited,
                              String description) {
        this.warehouseName = warehouseName;
        this.ownerName = ownerName;
        this.contactNumber = contactNumber;
        this.state = state;
        this.district = district;
        this.address = address;
        this.totalCapacityQuintals = totalCapacityQuintals;
        this.supportedCrops = supportedCrops;
        this.storageChargePerQuintalMonth = storageChargePerQuintalMonth;
        this.isAccredited = isAccredited;
        this.description = description;
    }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public BigDecimal getTotalCapacityQuintals() { return totalCapacityQuintals; }
    public void setTotalCapacityQuintals(BigDecimal totalCapacityQuintals) { this.totalCapacityQuintals = totalCapacityQuintals; }
    public String getSupportedCrops() { return supportedCrops; }
    public void setSupportedCrops(String supportedCrops) { this.supportedCrops = supportedCrops; }
    public BigDecimal getStorageChargePerQuintalMonth() { return storageChargePerQuintalMonth; }
    public void setStorageChargePerQuintalMonth(BigDecimal storageChargePerQuintalMonth) { this.storageChargePerQuintalMonth = storageChargePerQuintalMonth; }
    public boolean isAccredited() { return isAccredited; }
    public void setAccredited(boolean accredited) { isAccredited = accredited; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
