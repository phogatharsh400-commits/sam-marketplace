package com.sam.marketplace.contract;

import com.sam.marketplace.bid.Bid;
import com.sam.marketplace.common.enums.ContractStatus;
import com.sam.marketplace.lot.ProduceLot;
import com.sam.marketplace.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sale_contracts")
public class SaleContract {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String contractNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private ProduceLot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_bid_id", nullable = false)
    private Bid acceptedBid;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityQuintals;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerQuintal;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String deliveryTerms;

    @Column(nullable = false)
    private LocalDate dispatchDeadline;

    @Column(nullable = false)
    private LocalDate deliveryDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContractStatus status = ContractStatus.AWAITING_ESCROW;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public SaleContract() {}

    public SaleContract(UUID id, String contractNumber, ProduceLot lot, User farmer, User buyer,
                        Bid acceptedBid, BigDecimal quantityQuintals, BigDecimal pricePerQuintal,
                        BigDecimal totalAmount, String deliveryTerms, LocalDate dispatchDeadline,
                        LocalDate deliveryDeadline, ContractStatus status, Long version,
                        Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.contractNumber = contractNumber;
        this.lot = lot;
        this.farmer = farmer;
        this.buyer = buyer;
        this.acceptedBid = acceptedBid;
        this.quantityQuintals = quantityQuintals;
        this.pricePerQuintal = pricePerQuintal;
        this.totalAmount = totalAmount;
        this.deliveryTerms = deliveryTerms;
        this.dispatchDeadline = dispatchDeadline;
        this.deliveryDeadline = deliveryDeadline;
        this.status = status != null ? status : ContractStatus.AWAITING_ESCROW;
        this.version = version != null ? version : 0L;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getContractNumber() { return contractNumber; }
    public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
    public ProduceLot getLot() { return lot; }
    public void setLot(ProduceLot lot) { this.lot = lot; }
    public User getFarmer() { return farmer; }
    public void setFarmer(User farmer) { this.farmer = farmer; }
    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }
    public Bid getAcceptedBid() { return acceptedBid; }
    public void setAcceptedBid(Bid acceptedBid) { this.acceptedBid = acceptedBid; }
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
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String contractNumber;
        private ProduceLot lot;
        private User farmer;
        private User buyer;
        private Bid acceptedBid;
        private BigDecimal quantityQuintals;
        private BigDecimal pricePerQuintal;
        private BigDecimal totalAmount;
        private String deliveryTerms;
        private LocalDate dispatchDeadline;
        private LocalDate deliveryDeadline;
        private ContractStatus status = ContractStatus.AWAITING_ESCROW;
        private Long version = 0L;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder contractNumber(String contractNumber) { this.contractNumber = contractNumber; return this; }
        public Builder lot(ProduceLot lot) { this.lot = lot; return this; }
        public Builder farmer(User farmer) { this.farmer = farmer; return this; }
        public Builder buyer(User buyer) { this.buyer = buyer; return this; }
        public Builder acceptedBid(Bid acceptedBid) { this.acceptedBid = acceptedBid; return this; }
        public Builder quantityQuintals(BigDecimal quantityQuintals) { this.quantityQuintals = quantityQuintals; return this; }
        public Builder pricePerQuintal(BigDecimal pricePerQuintal) { this.pricePerQuintal = pricePerQuintal; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder deliveryTerms(String deliveryTerms) { this.deliveryTerms = deliveryTerms; return this; }
        public Builder dispatchDeadline(LocalDate dispatchDeadline) { this.dispatchDeadline = dispatchDeadline; return this; }
        public Builder deliveryDeadline(LocalDate deliveryDeadline) { this.deliveryDeadline = deliveryDeadline; return this; }
        public Builder status(ContractStatus status) { this.status = status; return this; }
        public Builder version(Long version) { this.version = version; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public SaleContract build() {
            return new SaleContract(id, contractNumber, lot, farmer, buyer, acceptedBid, quantityQuintals, pricePerQuintal, totalAmount, deliveryTerms, dispatchDeadline, deliveryDeadline, status, version, createdAt, updatedAt);
        }
    }
}
