package com.sam.marketplace.payment;

import com.sam.marketplace.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class PaymentInitiateDto {

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod = PaymentMethod.UPI;

    private String idempotencyKey;
    private boolean autoSimulateSuccess = true;
    private String notes;

    public PaymentInitiateDto() {}

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public boolean isAutoSimulateSuccess() { return autoSimulateSuccess; }
    public void setAutoSimulateSuccess(boolean autoSimulateSuccess) { this.autoSimulateSuccess = autoSimulateSuccess; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
