package com.sam.marketplace.warehouse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class WarehouseResponseDto {
    private UUID id;
    private UUID operatorId;
    private String operatorName;
    private String warehouseName;
    private String ownerName;
    private String contactNumber;
    private String state;
    private String district;
    private String address;
    private BigDecimal totalCapacityQuintals;
    private BigDecimal availableCapacityQuintals;
    private String supportedCrops;
    private BigDecimal storageChargePerQuintalMonth;
    private boolean isAccredited;
    private String description;
    private Instant createdAt;

    public WarehouseResponseDto() {}

    public WarehouseResponseDto(UUID id, UUID operatorId, String operatorName, String warehouseName,
                                String ownerName, String contactNumber, String state, String district,
                                String address, BigDecimal totalCapacityQuintals,
                                BigDecimal availableCapacityQuintals, String supportedCrops,
                                BigDecimal storageChargePerQuintalMonth, boolean isAccredited,
                                String description, Instant createdAt) {
        this.id = id;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.warehouseName = warehouseName;
        this.ownerName = ownerName;
        this.contactNumber = contactNumber;
        this.state = state;
        this.district = district;
        this.address = address;
        this.totalCapacityQuintals = totalCapacityQuintals;
        this.availableCapacityQuintals = availableCapacityQuintals;
        this.supportedCrops = supportedCrops;
        this.storageChargePerQuintalMonth = storageChargePerQuintalMonth;
        this.isAccredited = isAccredited;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID operatorId;
        private String operatorName;
        private String warehouseName;
        private String ownerName;
        private String contactNumber;
        private String state;
        private String district;
        private String address;
        private BigDecimal totalCapacityQuintals;
        private BigDecimal availableCapacityQuintals;
        private String supportedCrops;
        private BigDecimal storageChargePerQuintalMonth;
        private boolean isAccredited;
        private String description;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder operatorId(UUID operatorId) { this.operatorId = operatorId; return this; }
        public Builder operatorName(String operatorName) { this.operatorName = operatorName; return this; }
        public Builder warehouseName(String warehouseName) { this.warehouseName = warehouseName; return this; }
        public Builder ownerName(String ownerName) { this.ownerName = ownerName; return this; }
        public Builder contactNumber(String contactNumber) { this.contactNumber = contactNumber; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder district(String district) { this.district = district; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder totalCapacityQuintals(BigDecimal totalCapacityQuintals) { this.totalCapacityQuintals = totalCapacityQuintals; return this; }
        public Builder availableCapacityQuintals(BigDecimal availableCapacityQuintals) { this.availableCapacityQuintals = availableCapacityQuintals; return this; }
        public Builder supportedCrops(String supportedCrops) { this.supportedCrops = supportedCrops; return this; }
        public Builder storageChargePerQuintalMonth(BigDecimal storageChargePerQuintalMonth) { this.storageChargePerQuintalMonth = storageChargePerQuintalMonth; return this; }
        public Builder isAccredited(boolean isAccredited) { this.isAccredited = isAccredited; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public WarehouseResponseDto build() {
            return new WarehouseResponseDto(id, operatorId, operatorName, warehouseName, ownerName,
                    contactNumber, state, district, address, totalCapacityQuintals,
                    availableCapacityQuintals, supportedCrops, storageChargePerQuintalMonth,
                    isAccredited, description, createdAt);
        }
    }

    public static WarehouseResponseDto from(Warehouse w) {
        if (w == null) return null;
        return WarehouseResponseDto.builder()
                .id(w.getId())
                .operatorId(w.getOperator().getId())
                .operatorName(w.getOperator().getFullName())
                .warehouseName(w.getWarehouseName())
                .ownerName(w.getOwnerName())
                .contactNumber(w.getContactNumber())
                .state(w.getState())
                .district(w.getDistrict())
                .address(w.getAddress())
                .totalCapacityQuintals(w.getTotalCapacityQuintals())
                .availableCapacityQuintals(w.getAvailableCapacityQuintals())
                .supportedCrops(w.getSupportedCrops())
                .storageChargePerQuintalMonth(w.getStorageChargePerQuintalMonth())
                .isAccredited(w.isAccredited())
                .description(w.getDescription())
                .createdAt(w.getCreatedAt())
                .build();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getOperatorId() { return operatorId; }
    public void setOperatorId(UUID operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
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
    public BigDecimal getAvailableCapacityQuintals() { return availableCapacityQuintals; }
    public void setAvailableCapacityQuintals(BigDecimal availableCapacityQuintals) { this.availableCapacityQuintals = availableCapacityQuintals; }
    public String getSupportedCrops() { return supportedCrops; }
    public void setSupportedCrops(String supportedCrops) { this.supportedCrops = supportedCrops; }
    public BigDecimal getStorageChargePerQuintalMonth() { return storageChargePerQuintalMonth; }
    public void setStorageChargePerQuintalMonth(BigDecimal storageChargePerQuintalMonth) { this.storageChargePerQuintalMonth = storageChargePerQuintalMonth; }
    public boolean isAccredited() { return isAccredited; }
    public void setAccredited(boolean accredited) { isAccredited = accredited; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
