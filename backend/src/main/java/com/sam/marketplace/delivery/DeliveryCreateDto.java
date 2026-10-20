package com.sam.marketplace.delivery;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DeliveryCreateDto {

    @NotNull(message = "Delivered quantity is required")
    @DecimalMin(value = "0.1", message = "Quantity delivered must be greater than zero")
    private BigDecimal quantityDeliveredQuintals;

    @NotNull(message = "Delivery date is required")
    private LocalDate deliveryDate;

    private String qualityRemarks;
    private String weightSlipUrl;
    private String deliveryPhotosMetadata;

    public DeliveryCreateDto() {}

    public DeliveryCreateDto(BigDecimal quantityDeliveredQuintals, LocalDate deliveryDate,
                             String qualityRemarks, String weightSlipUrl, String deliveryPhotosMetadata) {
        this.quantityDeliveredQuintals = quantityDeliveredQuintals;
        this.deliveryDate = deliveryDate;
        this.qualityRemarks = qualityRemarks;
        this.weightSlipUrl = weightSlipUrl;
        this.deliveryPhotosMetadata = deliveryPhotosMetadata;
    }

    public BigDecimal getQuantityDeliveredQuintals() { return quantityDeliveredQuintals; }
    public void setQuantityDeliveredQuintals(BigDecimal quantityDeliveredQuintals) { this.quantityDeliveredQuintals = quantityDeliveredQuintals; }
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }
    public String getQualityRemarks() { return qualityRemarks; }
    public void setQualityRemarks(String qualityRemarks) { this.qualityRemarks = qualityRemarks; }
    public String getWeightSlipUrl() { return weightSlipUrl; }
    public void setWeightSlipUrl(String weightSlipUrl) { this.weightSlipUrl = weightSlipUrl; }
    public String getDeliveryPhotosMetadata() { return deliveryPhotosMetadata; }
    public void setDeliveryPhotosMetadata(String deliveryPhotosMetadata) { this.deliveryPhotosMetadata = deliveryPhotosMetadata; }
}
