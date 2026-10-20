package com.sam.marketplace.warehouse;

import com.sam.marketplace.common.enums.WarehouseBookingStatus;
import com.sam.marketplace.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "warehouse_bookings")
public class WarehouseBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    @Column(nullable = false, length = 50)
    private String crop;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityQuintals;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalEstimatedCost = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WarehouseBookingStatus status = WarehouseBookingStatus.REQUESTED;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public WarehouseBooking() {}

    public WarehouseBooking(UUID id, Warehouse warehouse, User farmer, String crop,
                            BigDecimal quantityQuintals, LocalDate startDate, LocalDate endDate,
                            BigDecimal totalEstimatedCost, WarehouseBookingStatus status,
                            String remarks, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.warehouse = warehouse;
        this.farmer = farmer;
        this.crop = crop;
        this.quantityQuintals = quantityQuintals;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalEstimatedCost = totalEstimatedCost != null ? totalEstimatedCost : BigDecimal.ZERO;
        this.status = status != null ? status : WarehouseBookingStatus.REQUESTED;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private Warehouse warehouse;
        private User farmer;
        private String crop;
        private BigDecimal quantityQuintals;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal totalEstimatedCost = BigDecimal.ZERO;
        private WarehouseBookingStatus status = WarehouseBookingStatus.REQUESTED;
        private String remarks;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder warehouse(Warehouse warehouse) { this.warehouse = warehouse; return this; }
        public Builder farmer(User farmer) { this.farmer = farmer; return this; }
        public Builder crop(String crop) { this.crop = crop; return this; }
        public Builder quantityQuintals(BigDecimal q) { this.quantityQuintals = q; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder totalEstimatedCost(BigDecimal cost) { this.totalEstimatedCost = cost; return this; }
        public Builder status(WarehouseBookingStatus status) { this.status = status; return this; }
        public Builder remarks(String remarks) { this.remarks = remarks; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public WarehouseBooking build() {
            return new WarehouseBooking(id, warehouse, farmer, crop, quantityQuintals,
                    startDate, endDate, totalEstimatedCost, status, remarks, createdAt, updatedAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }
    public User getFarmer() { return farmer; }
    public void setFarmer(User farmer) { this.farmer = farmer; }
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
