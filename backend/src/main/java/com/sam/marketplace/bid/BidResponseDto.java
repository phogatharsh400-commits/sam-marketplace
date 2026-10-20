package com.sam.marketplace.bid;

import com.sam.marketplace.common.enums.BidStatus;
import com.sam.marketplace.common.enums.CropType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class BidResponseDto {
    private UUID id;
    private UUID lotId;
    private String lotNumber;
    private CropType crop;
    private String variety;
    private UUID buyerId;
    private String buyerName;
    private String buyerBusinessName;
    private String buyerMobile;
    private BigDecimal bidPricePerQuintal;
    private BigDecimal quantityQuintals;
    private BigDecimal totalAmount;
    private BidStatus status;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;

    public BidResponseDto() {}

    public static BidResponseDto from(Bid bid) {
        if (bid == null) return null;

        String bizName = (bid.getBuyer().getBuyerProfile() != null)
                ? bid.getBuyer().getBuyerProfile().getBusinessName()
                : bid.getBuyer().getFullName();

        BidResponseDto dto = new BidResponseDto();
        dto.setId(bid.getId());
        dto.setLotId(bid.getLot().getId());
        dto.setLotNumber(bid.getLot().getLotNumber());
        dto.setCrop(bid.getLot().getCrop());
        dto.setVariety(bid.getLot().getVariety());
        dto.setBuyerId(bid.getBuyer().getId());
        dto.setBuyerName(bid.getBuyer().getFullName());
        dto.setBuyerBusinessName(bizName);
        dto.setBuyerMobile(bid.getBuyer().getMobileNumber());
        dto.setBidPricePerQuintal(bid.getBidPricePerQuintal());
        dto.setQuantityQuintals(bid.getQuantityQuintals());
        dto.setTotalAmount(bid.getTotalAmount());
        dto.setStatus(bid.getStatus());
        dto.setVersion(bid.getVersion());
        dto.setCreatedAt(bid.getCreatedAt());
        dto.setUpdatedAt(bid.getUpdatedAt());
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getLotId() { return lotId; }
    public void setLotId(UUID lotId) { this.lotId = lotId; }
    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }
    public CropType getCrop() { return crop; }
    public void setCrop(CropType crop) { this.crop = crop; }
    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }
    public UUID getBuyerId() { return buyerId; }
    public void setBuyerId(UUID buyerId) { this.buyerId = buyerId; }
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public String getBuyerBusinessName() { return buyerBusinessName; }
    public void setBuyerBusinessName(String buyerBusinessName) { this.buyerBusinessName = buyerBusinessName; }
    public String getBuyerMobile() { return buyerMobile; }
    public void setBuyerMobile(String buyerMobile) { this.buyerMobile = buyerMobile; }
    public BigDecimal getBidPricePerQuintal() { return bidPricePerQuintal; }
    public void setBidPricePerQuintal(BigDecimal bidPricePerQuintal) { this.bidPricePerQuintal = bidPricePerQuintal; }
    public BigDecimal getQuantityQuintals() { return quantityQuintals; }
    public void setQuantityQuintals(BigDecimal quantityQuintals) { this.quantityQuintals = quantityQuintals; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BidStatus getStatus() { return status; }
    public void setStatus(BidStatus status) { this.status = status; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final BidResponseDto dto = new BidResponseDto();
        public Builder id(UUID id) { dto.setId(id); return this; }
        public Builder lotId(UUID lotId) { dto.setLotId(lotId); return this; }
        public Builder lotNumber(String lotNumber) { dto.setLotNumber(lotNumber); return this; }
        public Builder crop(CropType crop) { dto.setCrop(crop); return this; }
        public Builder variety(String variety) { dto.setVariety(variety); return this; }
        public Builder buyerId(UUID buyerId) { dto.setBuyerId(buyerId); return this; }
        public Builder buyerName(String buyerName) { dto.setBuyerName(buyerName); return this; }
        public Builder buyerBusinessName(String buyerBusinessName) { dto.setBuyerBusinessName(buyerBusinessName); return this; }
        public Builder buyerMobile(String buyerMobile) { dto.setBuyerMobile(buyerMobile); return this; }
        public Builder bidPricePerQuintal(BigDecimal bidPricePerQuintal) { dto.setBidPricePerQuintal(bidPricePerQuintal); return this; }
        public Builder quantityQuintals(BigDecimal quantityQuintals) { dto.setQuantityQuintals(quantityQuintals); return this; }
        public Builder totalAmount(BigDecimal totalAmount) { dto.setTotalAmount(totalAmount); return this; }
        public Builder status(BidStatus status) { dto.setStatus(status); return this; }
        public Builder version(Long version) { dto.setVersion(version); return this; }
        public Builder createdAt(Instant createdAt) { dto.setCreatedAt(createdAt); return this; }
        public Builder updatedAt(Instant updatedAt) { dto.setUpdatedAt(updatedAt); return this; }
        public BidResponseDto build() { return dto; }
    }
}
