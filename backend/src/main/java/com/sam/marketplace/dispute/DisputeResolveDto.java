package com.sam.marketplace.dispute;

import com.sam.marketplace.common.enums.DisputeResolution;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class DisputeResolveDto {

    @NotNull(message = "Resolution action is required")
    private DisputeResolution resolution;

    private String resolutionNotes;
    private BigDecimal partialFarmerAmount;
    private BigDecimal partialBuyerRefund;

    public DisputeResolveDto() {}

    public DisputeResolveDto(DisputeResolution resolution, String resolutionNotes,
                             BigDecimal partialFarmerAmount, BigDecimal partialBuyerRefund) {
        this.resolution = resolution;
        this.resolutionNotes = resolutionNotes;
        this.partialFarmerAmount = partialFarmerAmount;
        this.partialBuyerRefund = partialBuyerRefund;
    }

    public DisputeResolution getResolution() { return resolution; }
    public void setResolution(DisputeResolution resolution) { this.resolution = resolution; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    public BigDecimal getPartialFarmerAmount() { return partialFarmerAmount; }
    public void setPartialFarmerAmount(BigDecimal partialFarmerAmount) { this.partialFarmerAmount = partialFarmerAmount; }
    public BigDecimal getPartialBuyerRefund() { return partialBuyerRefund; }
    public void setPartialBuyerRefund(BigDecimal partialBuyerRefund) { this.partialBuyerRefund = partialBuyerRefund; }
}
