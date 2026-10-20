package com.sam.marketplace.payment;

import com.sam.marketplace.common.enums.PaymentMethod;
import com.sam.marketplace.common.enums.PaymentStatus;
import com.sam.marketplace.contract.SaleContract;
import com.sam.marketplace.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private SaleContract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payee_id", nullable = false)
    private User payee;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod paymentMethod = PaymentMethod.UPI;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status = PaymentStatus.INITIATED;

    @Column(nullable = false, unique = true, length = 100)
    private String transactionReference;

    @Column(unique = true, length = 100)
    private String idempotencyKey;

    @Column(nullable = false)
    private boolean simulationMode = true;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(length = 100)
    private String escrowTokenCode;

    @Column(length = 150)
    private String escrowTokenHash;

    @Column(length = 100)
    private String bankGuaranteeRef;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public PaymentTransaction() {}

    public PaymentTransaction(UUID id, SaleContract contract, User payer, User payee,
                              BigDecimal amount, PaymentMethod paymentMethod, PaymentStatus status,
                              String transactionReference, String idempotencyKey, boolean simulationMode,
                              String notes, Long version, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.contract = contract;
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
        this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.UPI;
        this.status = status != null ? status : PaymentStatus.INITIATED;
        this.transactionReference = transactionReference;
        this.idempotencyKey = idempotencyKey;
        this.simulationMode = simulationMode;
        this.notes = notes;
        this.version = version != null ? version : 0L;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SaleContract getContract() { return contract; }
    public void setContract(SaleContract contract) { this.contract = contract; }
    public User getPayer() { return payer; }
    public void setPayer(User payer) { this.payer = payer; }
    public User getPayee() { return payee; }
    public void setPayee(User payee) { this.payee = payee; }
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
        private UUID id;
        private SaleContract contract;
        private User payer;
        private User payee;
        private BigDecimal amount;
        private PaymentMethod paymentMethod = PaymentMethod.UPI;
        private PaymentStatus status = PaymentStatus.INITIATED;
        private String transactionReference;
        private String idempotencyKey;
        private boolean simulationMode = true;
        private String notes;
        private String escrowTokenCode;
        private String escrowTokenHash;
        private String bankGuaranteeRef;
        private Long version = 0L;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder contract(SaleContract contract) { this.contract = contract; return this; }
        public Builder payer(User payer) { this.payer = payer; return this; }
        public Builder payee(User payee) { this.payee = payee; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder status(PaymentStatus status) { this.status = status; return this; }
        public Builder transactionReference(String transactionReference) { this.transactionReference = transactionReference; return this; }
        public Builder idempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; return this; }
        public Builder simulationMode(boolean simulationMode) { this.simulationMode = simulationMode; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder escrowTokenCode(String escrowTokenCode) { this.escrowTokenCode = escrowTokenCode; return this; }
        public Builder escrowTokenHash(String escrowTokenHash) { this.escrowTokenHash = escrowTokenHash; return this; }
        public Builder bankGuaranteeRef(String bankGuaranteeRef) { this.bankGuaranteeRef = bankGuaranteeRef; return this; }
        public Builder version(Long version) { this.version = version; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public PaymentTransaction build() {
            PaymentTransaction pt = new PaymentTransaction(id, contract, payer, payee, amount, paymentMethod, status, transactionReference, idempotencyKey, simulationMode, notes, version, createdAt, updatedAt);
            pt.setEscrowTokenCode(escrowTokenCode);
            pt.setEscrowTokenHash(escrowTokenHash);
            pt.setBankGuaranteeRef(bankGuaranteeRef);
            return pt;
        }
    }
}
