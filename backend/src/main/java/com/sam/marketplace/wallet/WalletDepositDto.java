package com.sam.marketplace.wallet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class WalletDepositDto {

    @NotNull(message = "Deposit amount is required")
    @DecimalMin(value = "10.00", message = "Minimum deposit amount is ₹10.00")
    private BigDecimal amount;

    private String paymentMethod = "UPI"; // UPI, NET_BANKING, BANK_TRANSFER

    private String referenceNumber;

    private String notes;

    public WalletDepositDto() {}

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
