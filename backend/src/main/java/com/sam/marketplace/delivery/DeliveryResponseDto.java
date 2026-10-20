package com.sam.marketplace.delivery;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class DeliveryResponseDto {
    private UUID id;
    private UUID contractId;
    private String contractNumber;
    private BigDecimal quantityDeliveredQuintals;
    private LocalDate deliveryDate;
    private String qualityRemarks;
    private boolean buyerConfirmed;
    private boolean farmerConfirmed;
    private String weightSlipUrl;
    private String deliveryPhotosMetadata;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public DeliveryResponseDto() {}

    public DeliveryResponseDto(UUID id, UUID contractId, String contractNumber, BigDecimal quantityDeliveredQuintals,
                               LocalDate deliveryDate, String qualityRemarks, boolean buyerConfirmed,
                               boolean farmerConfirmed, String weightSlipUrl, String deliveryPhotosMetadata,
                               String status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.contractId = contractId;
        this.contractNumber = contractNumber;
        this.quantityDeliveredQuintals = quantityDeliveredQuintals;
        this.deliveryDate = deliveryDate;
        this.qualityRemarks = qualityRemarks;
        this.buyerConfirmed = buyerConfirmed;
        this.farmerConfirmed = farmerConfirmed;
        this.weightSlipUrl = weightSlipUrl;
        this.deliveryPhotosMetadata = deliveryPhotosMetadata;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID contractId;
        private String contractNumber;
        private BigDecimal quantityDeliveredQuintals;
        private LocalDate deliveryDate;
        private String qualityRemarks;
        private boolean buyerConfirmed;
        private boolean farmerConfirmed;
        private String weightSlipUrl;
        private String deliveryPhotosMetadata;
        private String status;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder contractId(UUID contractId) { this.contractId = contractId; return this; }
        public Builder contractNumber(String contractNumber) { this.contractNumber = contractNumber; return this; }
        public Builder quantityDeliveredQuintals(BigDecimal q) { this.quantityDeliveredQuintals = q; return this; }
        public Builder deliveryDate(LocalDate d) { this.deliveryDate = d; return this; }
        public Builder qualityRemarks(String q) { this.qualityRemarks = q; return this; }
        public Builder buyerConfirmed(boolean b) { this.buyerConfirmed = b; return this; }
        public Builder farmerConfirmed(boolean f) { this.farmerConfirmed = f; return this; }
        public Builder weightSlipUrl(String w) { this.weightSlipUrl = w; return this; }
        public Builder deliveryPhotosMetadata(String d) { this.deliveryPhotosMetadata = d; return this; }
        public Builder status(String s) { this.status = s; return this; }
        public Builder createdAt(Instant c) { this.createdAt = c; return this; }
        public Builder updatedAt(Instant u) { this.updatedAt = u; return this; }

        public DeliveryResponseDto build() {
            return new DeliveryResponseDto(id, contractId, contractNumber, quantityDeliveredQuintals,
                    deliveryDate, qualityRemarks, buyerConfirmed, farmerConfirmed,
                    weightSlipUrl, deliveryPhotosMetadata, status, createdAt, updatedAt);
        }
    }

    public static DeliveryResponseDto from(DeliveryRecord dr) {
        if (dr == null) return null;
        return DeliveryResponseDto.builder()
                .id(dr.getId())
                .contractId(dr.getContract().getId())
                .contractNumber(dr.getContract().getContractNumber())
                .quantityDeliveredQuintals(dr.getQuantityDeliveredQuintals())
                .deliveryDate(dr.getDeliveryDate())
                .qualityRemarks(dr.getQualityRemarks())
                .buyerConfirmed(dr.isBuyerConfirmed())
                .farmerConfirmed(dr.isFarmerConfirmed())
                .weightSlipUrl(dr.getWeightSlipUrl())
                .deliveryPhotosMetadata(dr.getDeliveryPhotosMetadata())
                .status(dr.getStatus())
                .createdAt(dr.getCreatedAt())
                .updatedAt(dr.getUpdatedAt())
                .build();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getContractId() { return contractId; }
    public void setContractId(UUID contractId) { this.contractId = contractId; }
    public String getContractNumber() { return contractNumber; }
    public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
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
