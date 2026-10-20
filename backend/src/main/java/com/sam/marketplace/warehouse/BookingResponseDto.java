package com.sam.marketplace.warehouse;

import com.sam.marketplace.common.enums.WarehouseBookingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class BookingResponseDto {
    private UUID id;
    private UUID warehouseId;
    private String warehouseName;
    private String warehouseDistrict;
    private String warehouseAddress;
    private UUID farmerId;
    private String farmerName;
    private String farmerMobile;
    private String crop;
    private BigDecimal quantityQuintals;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalEstimatedCost;
    private WarehouseBookingStatus status;
    private String remarks;
    private Instant createdAt;
    private Instant updatedAt;

    public BookingResponseDto() {}

    public BookingResponseDto(UUID id, UUID warehouseId, String warehouseName, String warehouseDistrict,
                              String warehouseAddress, UUID farmerId, String farmerName, String farmerMobile,
                              String crop, BigDecimal quantityQuintals, LocalDate startDate, LocalDate endDate,
                              BigDecimal totalEstimatedCost, WarehouseBookingStatus status,
                              String remarks, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.warehouseDistrict = warehouseDistrict;
        this.warehouseAddress = warehouseAddress;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.farmerMobile = farmerMobile;
        this.crop = crop;
        this.quantityQuintals = quantityQuintals;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalEstimatedCost = totalEstimatedCost;
        this.status = status;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID warehouseId;
        private String warehouseName;
        private String warehouseDistrict;
        private String warehouseAddress;
        private UUID farmerId;
        private String farmerName;
        private String farmerMobile;
        private String crop;
        private BigDecimal quantityQuintals;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal totalEstimatedCost;
        private WarehouseBookingStatus status;
        private String remarks;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder warehouseId(UUID warehouseId) { this.warehouseId = warehouseId; return this; }
        public Builder warehouseName(String warehouseName) { this.warehouseName = warehouseName; return this; }
        public Builder warehouseDistrict(String warehouseDistrict) { this.warehouseDistrict = warehouseDistrict; return this; }
        public Builder warehouseAddress(String warehouseAddress) { this.warehouseAddress = warehouseAddress; return this; }
        public Builder farmerId(UUID farmerId) { this.farmerId = farmerId; return this; }
        public Builder farmerName(String farmerName) { this.farmerName = farmerName; return this; }
        public Builder farmerMobile(String farmerMobile) { this.farmerMobile = farmerMobile; return this; }
        public Builder crop(String crop) { this.crop = crop; return this; }
        public Builder quantityQuintals(BigDecimal quantityQuintals) { this.quantityQuintals = quantityQuintals; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder totalEstimatedCost(BigDecimal totalEstimatedCost) { this.totalEstimatedCost = totalEstimatedCost; return this; }
        public Builder status(WarehouseBookingStatus status) { this.status = status; return this; }
        public Builder remarks(String remarks) { this.remarks = remarks; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public BookingResponseDto build() {
            return new BookingResponseDto(id, warehouseId, warehouseName, warehouseDistrict,
                    warehouseAddress, farmerId, farmerName, farmerMobile, crop, quantityQuintals,
                    startDate, endDate, totalEstimatedCost, status, remarks, createdAt, updatedAt);
        }
    }

    public static BookingResponseDto from(WarehouseBooking wb) {
        if (wb == null) return null;
        return BookingResponseDto.builder()
                .id(wb.getId())
                .warehouseId(wb.getWarehouse().getId())
                .warehouseName(wb.getWarehouse().getWarehouseName())
                .warehouseDistrict(wb.getWarehouse().getDistrict())
                .warehouseAddress(wb.getWarehouse().getAddress())
                .farmerId(wb.getFarmer().getId())
                .farmerName(wb.getFarmer().getFullName())
                .farmerMobile(wb.getFarmer().getMobileNumber())
                .crop(wb.getCrop())
                .quantityQuintals(wb.getQuantityQuintals())
                .startDate(wb.getStartDate())
                .endDate(wb.getEndDate())
                .totalEstimatedCost(wb.getTotalEstimatedCost())
                .status(wb.getStatus())
                .remarks(wb.getRemarks())
                .createdAt(wb.getCreatedAt())
                .updatedAt(wb.getUpdatedAt())
                .build();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getWarehouseId() { return warehouseId; }
    public void setWarehouseId(UUID warehouseId) { this.warehouseId = warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getWarehouseDistrict() { return warehouseDistrict; }
    public void setWarehouseDistrict(String warehouseDistrict) { this.warehouseDistrict = warehouseDistrict; }
    public String getWarehouseAddress() { return warehouseAddress; }
    public void setWarehouseAddress(String warehouseAddress) { this.warehouseAddress = warehouseAddress; }
    public UUID getFarmerId() { return farmerId; }
    public void setFarmerId(UUID farmerId) { this.farmerId = farmerId; }
    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }
    public String getFarmerMobile() { return farmerMobile; }
    public void setFarmerMobile(String farmerMobile) { this.farmerMobile = farmerMobile; }
    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }
    public BigDecimal getQuantityQuintals() { return quantityQuintals; }
    public void setQuantityQuintals(BigDecimal quantityQuintals) { this.quantityQuintals = quantityQuintals; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getTotalEstimatedCost() { return totalEstimatedCost; }
    public void setTotalEstimatedCost(BigDecimal totalEstimatedCost) { this.totalEstimatedCost = totalEstimatedCost; }
    public WarehouseBookingStatus getStatus() { return status; }
    public void setStatus(WarehouseBookingStatus status) { this.status = status; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
