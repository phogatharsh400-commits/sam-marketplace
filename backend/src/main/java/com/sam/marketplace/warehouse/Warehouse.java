package com.sam.marketplace.warehouse;

import com.sam.marketplace.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "warehouses")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private User operator;

    @Column(nullable = false, length = 200)
    private String warehouseName;

    @Column(nullable = false, length = 150)
    private String ownerName;

    @Column(nullable = false, length = 20)
    private String contactNumber;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCapacityQuintals;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal availableCapacityQuintals;

    @Column(nullable = false, length = 255)
    private String supportedCrops;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal storageChargePerQuintalMonth;

    @Column(nullable = false)
    private boolean isAccredited = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public Warehouse() {}

    public Warehouse(UUID id, User operator, String warehouseName, String ownerName,
                     String contactNumber, String state, String district, String address,
                     BigDecimal totalCapacityQuintals, BigDecimal availableCapacityQuintals,
                     String supportedCrops, BigDecimal storageChargePerQuintalMonth,
                     boolean isAccredited, String description, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.operator = operator;
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
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private User operator;
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
        private boolean isAccredited = true;
        private String description;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder operator(User operator) { this.operator = operator; return this; }
        public Builder warehouseName(String warehouseName) { this.warehouseName = warehouseName; return this; }
        public Builder ownerName(String ownerName) { this.ownerName = ownerName; return this; }
        public Builder contactNumber(String contactNumber) { this.contactNumber = contactNumber; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder district(String district) { this.district = district; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder totalCapacityQuintals(BigDecimal q) { this.totalCapacityQuintals = q; return this; }
        public Builder availableCapacityQuintals(BigDecimal q) { this.availableCapacityQuintals = q; return this; }
        public Builder supportedCrops(String supportedCrops) { this.supportedCrops = supportedCrops; return this; }
        public Builder storageChargePerQuintalMonth(BigDecimal sc) { this.storageChargePerQuintalMonth = sc; return this; }
        public Builder isAccredited(boolean isAccredited) { this.isAccredited = isAccredited; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public Warehouse build() {
            return new Warehouse(id, operator, warehouseName, ownerName, contactNumber, state, district,
                    address, totalCapacityQuintals, availableCapacityQuintals, supportedCrops,
                    storageChargePerQuintalMonth, isAccredited, description, createdAt, updatedAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getOperator() { return operator; }
    public void setOperator(User operator) { this.operator = operator; }
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
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
