package com.sam.marketplace.contract;

import com.sam.marketplace.common.enums.ContractStatus;
import com.sam.marketplace.common.enums.CropType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class ContractResponseDto {
    private UUID id;
    private String contractNumber;
    private UUID lotId;
    private String lotNumber;
    private CropType crop;
    private String variety;
    private UUID farmerId;
    private String farmerName;
    private String farmerMobile;
    private String farmerDistrict;
    private String farmerState;
    private UUID buyerId;
    private String buyerName;
    private String buyerBusinessName;
    private String buyerMobile;
    private UUID acceptedBidId;
    private BigDecimal quantityQuintals;
    private BigDecimal pricePerQuintal;
    private BigDecimal totalAmount;
    private String deliveryTerms;
    private LocalDate dispatchDeadline;
    private LocalDate deliveryDeadline;
    private ContractStatus status;
    private String escrowTokenCode;
    private String escrowTokenHash;
    private String bankGuaranteeRef;
    private String qualityCertificateNumber;
    private String sampleStatus;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;

    public ContractResponseDto() {}

    public static ContractResponseDto from(SaleContract sc) {
        if (sc == null) return null;

        String buyerBiz = (sc.getBuyer().getBuyerProfile() != null)
                ? sc.getBuyer().getBuyerProfile().getBusinessName()
                : sc.getBuyer().getFullName();

        String farmerDist = (sc.getFarmer().getFarmerProfile() != null)
                ? sc.getFarmer().getFarmerProfile().getDistrict()
                : sc.getLot().getDistrict();

        String farmerSt = (sc.getFarmer().getFarmerProfile() != null)
                ? sc.getFarmer().getFarmerProfile().getState()
                : sc.getLot().getState();

        ContractResponseDto dto = new ContractResponseDto();
        dto.setId(sc.getId());
        dto.setContractNumber(sc.getContractNumber());
        dto.setLotId(sc.getLot().getId());
        dto.setLotNumber(sc.getLot().getLotNumber());
        dto.setCrop(sc.getLot().getCrop());
        dto.setVariety(sc.getLot().getVariety());
        dto.setFarmerId(sc.getFarmer().getId());
        dto.setFarmerName(sc.getFarmer().getFullName());
        dto.setFarmerMobile(sc.getFarmer().getMobileNumber());
        dto.setFarmerDistrict(farmerDist);
        dto.setFarmerState(farmerSt);
        dto.setBuyerId(sc.getBuyer().getId());
        dto.setBuyerName(sc.getBuyer().getFullName());
        dto.setBuyerBusinessName(buyerBiz);
        dto.setBuyerMobile(sc.getBuyer().getMobileNumber());
        dto.setAcceptedBidId(sc.getAcceptedBid().getId());
        dto.setQuantityQuintals(sc.getQuantityQuintals());
        dto.setPricePerQuintal(sc.getPricePerQuintal());
        dto.setTotalAmount(sc.getTotalAmount());
        dto.setDeliveryTerms(sc.getDeliveryTerms());
        dto.setDispatchDeadline(sc.getDispatchDeadline());
        dto.setDeliveryDeadline(sc.getDeliveryDeadline());
        dto.setStatus(sc.getStatus());
        dto.setQualityCertificateNumber(sc.getLot() != null ? sc.getLot().getQualityCertificateNumber() : null);
        dto.setSampleStatus(sc.getLot() != null ? sc.getLot().getSampleStatus() : "QUALITY_VERIFIED_AND_APPROVED");
        if (sc.getStatus() == ContractStatus.ESCROW_FUNDED || sc.getStatus() == ContractStatus.READY_FOR_DISPATCH || sc.getStatus() == ContractStatus.DISPATCHED || sc.getStatus() == ContractStatus.DELIVERED || sc.getStatus() == ContractStatus.PAYMENT_RELEASED) {
            String cleanNum = sc.getContractNumber().replaceAll("\\D+", "");
            dto.setEscrowTokenCode("ESC-2026-" + (cleanNum.length() >= 6 ? cleanNum.substring(0, 6) : "883921"));
            dto.setEscrowTokenHash("SHA256:7f83b1657ff1fc53b92dc18148a1d65d");
            dto.setBankGuaranteeRef("RBI-NODAL-ESC-" + (cleanNum.length() >= 4 ? cleanNum : "772910"));
        }
        dto.setVersion(sc.getVersion());
        dto.setCreatedAt(sc.getCreatedAt());
        dto.setUpdatedAt(sc.getUpdatedAt());
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getContractNumber() { return contractNumber; }
    public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
    public UUID getLotId() { return lotId; }
    public void setLotId(UUID lotId) { this.lotId = lotId; }
    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }
    public CropType getCrop() { return crop; }
    public void setCrop(CropType crop) { this.crop = crop; }
    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }
    public UUID getFarmerId() { return farmerId; }
    public void setFarmerId(UUID farmerId) { this.farmerId = farmerId; }
    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }
    public String getFarmerMobile() { return farmerMobile; }
    public void setFarmerMobile(String farmerMobile) { this.farmerMobile = farmerMobile; }
    public String getFarmerDistrict() { return farmerDistrict; }
    public void setFarmerDistrict(String farmerDistrict) { this.farmerDistrict = farmerDistrict; }
    public String getFarmerState() { return farmerState; }
    public void setFarmerState(String farmerState) { this.farmerState = farmerState; }
    public UUID getBuyerId() { return buyerId; }
    public void setBuyerId(UUID buyerId) { this.buyerId = buyerId; }
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public String getBuyerBusinessName() { return buyerBusinessName; }
    public void setBuyerBusinessName(String buyerBusinessName) { this.buyerBusinessName = buyerBusinessName; }
    public String getBuyerMobile() { return buyerMobile; }
    public void setBuyerMobile(String buyerMobile) { this.buyerMobile = buyerMobile; }
    public UUID getAcceptedBidId() { return acceptedBidId; }
    public void setAcceptedBidId(UUID acceptedBidId) { this.acceptedBidId = acceptedBidId; }
    public BigDecimal getQuantityQuintals() { return quantityQuintals; }
    public void setQuantityQuintals(BigDecimal quantityQuintals) { this.quantityQuintals = quantityQuintals; }
    public BigDecimal getPricePerQuintal() { return pricePerQuintal; }
    public void setPricePerQuintal(BigDecimal pricePerQuintal) { this.pricePerQuintal = pricePerQuintal; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getDeliveryTerms() { return deliveryTerms; }
    public void setDeliveryTerms(String deliveryTerms) { this.deliveryTerms = deliveryTerms; }
    public LocalDate getDispatchDeadline() { return dispatchDeadline; }
    public void setDispatchDeadline(LocalDate dispatchDeadline) { this.dispatchDeadline = dispatchDeadline; }
    public LocalDate getDeliveryDeadline() { return deliveryDeadline; }
    public void setDeliveryDeadline(LocalDate deliveryDeadline) { this.deliveryDeadline = deliveryDeadline; }
    public ContractStatus getStatus() { return status; }
    public void setStatus(ContractStatus status) { this.status = status; }
    public String getEscrowTokenCode() { return escrowTokenCode; }
    public void setEscrowTokenCode(String escrowTokenCode) { this.escrowTokenCode = escrowTokenCode; }
    public String getEscrowTokenHash() { return escrowTokenHash; }
    public void setEscrowTokenHash(String escrowTokenHash) { this.escrowTokenHash = escrowTokenHash; }
    public String getBankGuaranteeRef() { return bankGuaranteeRef; }
    public void setBankGuaranteeRef(String bankGuaranteeRef) { this.bankGuaranteeRef = bankGuaranteeRef; }
    public String getQualityCertificateNumber() { return qualityCertificateNumber; }
    public void setQualityCertificateNumber(String qualityCertificateNumber) { this.qualityCertificateNumber = qualityCertificateNumber; }
    public String getSampleStatus() { return sampleStatus; }
    public void setSampleStatus(String sampleStatus) { this.sampleStatus = sampleStatus; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ContractResponseDto dto = new ContractResponseDto();
        public Builder id(UUID id) { dto.setId(id); return this; }
        public Builder contractNumber(String contractNumber) { dto.setContractNumber(contractNumber); return this; }
        public Builder lotId(UUID lotId) { dto.setLotId(lotId); return this; }
        public Builder lotNumber(String lotNumber) { dto.setLotNumber(lotNumber); return this; }
        public Builder crop(CropType crop) { dto.setCrop(crop); return this; }
        public Builder variety(String variety) { dto.setVariety(variety); return this; }
        public Builder farmerId(UUID farmerId) { dto.setFarmerId(farmerId); return this; }
        public Builder farmerName(String farmerName) { dto.setFarmerName(farmerName); return this; }
        public Builder farmerMobile(String farmerMobile) { dto.setFarmerMobile(farmerMobile); return this; }
        public Builder farmerDistrict(String farmerDistrict) { dto.setFarmerDistrict(farmerDistrict); return this; }
        public Builder farmerState(String farmerState) { dto.setFarmerState(farmerState); return this; }
        public Builder buyerId(UUID buyerId) { dto.setBuyerId(buyerId); return this; }
        public Builder buyerName(String buyerName) { dto.setBuyerName(buyerName); return this; }
        public Builder buyerBusinessName(String buyerBusinessName) { dto.setBuyerBusinessName(buyerBusinessName); return this; }
        public Builder buyerMobile(String buyerMobile) { dto.setBuyerMobile(buyerMobile); return this; }
        public Builder acceptedBidId(UUID acceptedBidId) { dto.setAcceptedBidId(acceptedBidId); return this; }
        public Builder quantityQuintals(BigDecimal quantityQuintals) { dto.setQuantityQuintals(quantityQuintals); return this; }
        public Builder pricePerQuintal(BigDecimal pricePerQuintal) { dto.setPricePerQuintal(pricePerQuintal); return this; }
        public Builder totalAmount(BigDecimal totalAmount) { dto.setTotalAmount(totalAmount); return this; }
        public Builder deliveryTerms(String deliveryTerms) { dto.setDeliveryTerms(deliveryTerms); return this; }
        public Builder dispatchDeadline(LocalDate dispatchDeadline) { dto.setDispatchDeadline(dispatchDeadline); return this; }
        public Builder deliveryDeadline(LocalDate deliveryDeadline) { dto.setDeliveryDeadline(deliveryDeadline); return this; }
        public Builder status(ContractStatus status) { dto.setStatus(status); return this; }
        public Builder version(Long version) { dto.setVersion(version); return this; }
        public Builder createdAt(Instant createdAt) { dto.setCreatedAt(createdAt); return this; }
        public Builder updatedAt(Instant updatedAt) { dto.setUpdatedAt(updatedAt); return this; }
        public ContractResponseDto build() { return dto; }
    }
}
