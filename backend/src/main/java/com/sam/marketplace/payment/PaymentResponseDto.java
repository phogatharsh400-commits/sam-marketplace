package com.sam.marketplace.payment;

import com.sam.marketplace.common.enums.PaymentMethod;
import com.sam.marketplace.common.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PaymentResponseDto {
    private UUID id;
    private UUID contractId;
    private String contractNumber;
    private UUID payerId;
    private String payerName;
    private UUID payeeId;
    private String payeeName;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionReference;
    private String idempotencyKey;
    private boolean simulationMode;
    private String notes;
    private String escrowTokenCode;
    private String escrowTokenHash;
    private String bankGuaranteeRef;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;

    public PaymentResponseDto() {}

    public static PaymentResponseDto from(PaymentTransaction pt) {
        if (pt == null) return null;
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(pt.getId());
        dto.setContractId(pt.getContract().getId());
        dto.setContractNumber(pt.getContract().getContractNumber());
        dto.setPayerId(pt.getPayer().getId());
        dto.setPayerName(pt.getPayer().getFullName());
        dto.setPayeeId(pt.getPayee().getId());
        dto.setPayeeName(pt.getPayee().getFullName());
        dto.setAmount(pt.getAmount());
        dto.setPaymentMethod(pt.getPaymentMethod());
        dto.setStatus(pt.getStatus());
        dto.setTransactionReference(pt.getTransactionReference());
        dto.setIdempotencyKey(pt.getIdempotencyKey());
        dto.setSimulationMode(pt.isSimulationMode());
        dto.setNotes(pt.getNotes());
        dto.setEscrowTokenCode(pt.getEscrowTokenCode());
        dto.setEscrowTokenHash(pt.getEscrowTokenHash());
        dto.setBankGuaranteeRef(pt.getBankGuaranteeRef());
        dto.setVersion(pt.getVersion());
        dto.setCreatedAt(pt.getCreatedAt());
        dto.setUpdatedAt(pt.getUpdatedAt());
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getContractId() { return contractId; }
    public void setContractId(UUID contractId) { this.contractId = contractId; }
    public String getContractNumber() { return contractNumber; }
    public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
    public UUID getPayerId() { return payerId; }
    public void setPayerId(UUID payerId) { this.payerId = payerId; }
    public String getPayerName() { return payerName; }
    public void setPayerName(String payerName) { this.payerName = payerName; }
    public UUID getPayeeId() { return payeeId; }
    public void setPayeeId(UUID payeeId) { this.payeeId = payeeId; }
    public String getPayeeName() { return payeeName; }
    public void setPayeeName(String payeeName) { this.payeeName = payeeName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public boolean isSimulationMode() { return simulationMode; }
    public void setSimulationMode(boolean simulationMode) { this.simulationMode = simulationMode; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getEscrowTokenCode() { return escrowTokenCode; }
    public void setEscrowTokenCode(String escrowTokenCode) { this.escrowTokenCode = escrowTokenCode; }
    public String getEscrowTokenHash() { return escrowTokenHash; }
    public void setEscrowTokenHash(String escrowTokenHash) { this.escrowTokenHash = escrowTokenHash; }
    public String getBankGuaranteeRef() { return bankGuaranteeRef; }
    public void setBankGuaranteeRef(String bankGuaranteeRef) { this.bankGuaranteeRef = bankGuaranteeRef; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final PaymentResponseDto dto = new PaymentResponseDto();
        public Builder id(UUID id) { dto.setId(id); return this; }
        public Builder contractId(UUID contractId) { dto.setContractId(contractId); return this; }
        public Builder contractNumber(String contractNumber) { dto.setContractNumber(contractNumber); return this; }
        public Builder payerId(UUID payerId) { dto.setPayerId(payerId); return this; }
        public Builder payerName(String payerName) { dto.setPayerName(payerName); return this; }
        public Builder payeeId(UUID payeeId) { dto.setPayeeId(payeeId); return this; }
        public Builder payeeName(String payeeName) { dto.setPayeeName(payeeName); return this; }
        public Builder amount(BigDecimal amount) { dto.setAmount(amount); return this; }
        public Builder paymentMethod(PaymentMethod paymentMethod) { dto.setPaymentMethod(paymentMethod); return this; }
        public Builder status(PaymentStatus status) { dto.setStatus(status); return this; }
        public Builder transactionReference(String transactionReference) { dto.setTransactionReference(transactionReference); return this; }
        public Builder idempotencyKey(String idempotencyKey) { dto.setIdempotencyKey(idempotencyKey); return this; }
        public Builder simulationMode(boolean simulationMode) { dto.setSimulationMode(simulationMode); return this; }
        public Builder notes(String notes) { dto.setNotes(notes); return this; }
        public Builder version(Long version) { dto.setVersion(version); return this; }
        public Builder createdAt(Instant createdAt) { dto.setCreatedAt(createdAt); return this; }
        public Builder updatedAt(Instant updatedAt) { dto.setUpdatedAt(updatedAt); return this; }
        public PaymentResponseDto build() { return dto; }
    }
}
