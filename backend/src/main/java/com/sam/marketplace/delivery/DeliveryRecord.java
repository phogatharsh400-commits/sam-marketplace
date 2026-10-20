package com.sam.marketplace.delivery;

import com.sam.marketplace.contract.SaleContract;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
public class DeliveryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false, unique = true)
    private SaleContract contract;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityDeliveredQuintals;

    @Column(nullable = false)
    private LocalDate deliveryDate;

    @Column(columnDefinition = "TEXT")
    private String qualityRemarks;

    @Column(nullable = false)
    private boolean buyerConfirmed = false;

    @Column(nullable = false)
    private boolean farmerConfirmed = true;

    @Column(columnDefinition = "TEXT")
    private String weightSlipUrl;

    @Column(columnDefinition = "TEXT")
    private String deliveryPhotosMetadata;

    @Column(nullable = false, length = 30)
    private String status = "PENDING"; // PENDING, DELIVERED, CONFIRMED, DISPUTED

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public DeliveryRecord() {}

    public DeliveryRecord(UUID id, SaleContract contract, BigDecimal quantityDeliveredQuintals,
                          LocalDate deliveryDate, String qualityRemarks, boolean buyerConfirmed,
                          boolean farmerConfirmed, String weightSlipUrl, String deliveryPhotosMetadata,
                          String status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.contract = contract;
        this.quantityDeliveredQuintals = quantityDeliveredQuintals;
        this.deliveryDate = deliveryDate;
        this.qualityRemarks = qualityRemarks;
        this.buyerConfirmed = buyerConfirmed;
        this.farmerConfirmed = farmerConfirmed;
        this.weightSlipUrl = weightSlipUrl;
        this.deliveryPhotosMetadata = deliveryPhotosMetadata;
        this.status = status != null ? status : "PENDING";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private SaleContract contract;
        private BigDecimal quantityDeliveredQuintals;
        private LocalDate deliveryDate;
        private String qualityRemarks;
        private boolean buyerConfirmed = false;
        private boolean farmerConfirmed = true;
        private String weightSlipUrl;
        private String deliveryPhotosMetadata;
        private String status = "PENDING";
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder contract(SaleContract contract) { this.contract = contract; return this; }
        public Builder quantityDeliveredQuintals(BigDecimal q) { this.quantityDeliveredQuintals = q; return this; }
        public Builder deliveryDate(LocalDate d) { this.deliveryDate = d; return this; }
        public Builder qualityRemarks(String q) { this.qualityRemarks = q; return this; }
        public Builder buyerConfirmed(boolean b) { this.buyerConfirmed = b; return this; }
        public Builder farmerConfirmed(boolean f) { this.farmerConfirmed = f; return this; }
        public Builder weightSlipUrl(String w) { this.weightSlipUrl = w; return this; }
        public Builder deliveryPhotosMetadata(String p) { this.deliveryPhotosMetadata = p; return this; }
        public Builder status(String s) { this.status = s; return this; }
        public Builder createdAt(Instant c) { this.createdAt = c; return this; }
        public Builder updatedAt(Instant u) { this.updatedAt = u; return this; }

        public DeliveryRecord build() {
            return new DeliveryRecord(id, contract, quantityDeliveredQuintals, deliveryDate,
                    qualityRemarks, buyerConfirmed, farmerConfirmed, weightSlipUrl, deliveryPhotosMetadata,
                    status, createdAt, updatedAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SaleContract getContract() { return contract; }
    public void setContract(SaleContract contract) { this.contract = contract; }
    public BigDecimal getQuantityDeliveredQuintals() { return quantityDeliveredQuintals; }
    public void setQuantityDeliveredQuintals(BigDecimal quantityDeliveredQuintals) { this.quantityDeliveredQuintals = quantityDeliveredQuintals; }
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }
    public String getQualityRemarks() { return qualityRemarks; }
    public void setQualityRemarks(String qualityRemarks) { this.qualityRemarks = qualityRemarks; }
    public boolean isBuyerConfirmed() { return buyerConfirmed; }
    public void setBuyerConfirmed(boolean buyerConfirmed) { this.buyerConfirmed = buyerConfirmed; }
    public boolean isFarmerConfirmed() { return farmerConfirmed; }
    public void setFarmerConfirmed(boolean farmerConfirmed) { this.farmerConfirmed = farmerConfirmed; }
    public String getWeightSlipUrl() { return weightSlipUrl; }
    public void setWeightSlipUrl(String weightSlipUrl) { this.weightSlipUrl = weightSlipUrl; }
    public String getDeliveryPhotosMetadata() { return deliveryPhotosMetadata; }
    public void setDeliveryPhotosMetadata(String deliveryPhotosMetadata) { this.deliveryPhotosMetadata = deliveryPhotosMetadata; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
