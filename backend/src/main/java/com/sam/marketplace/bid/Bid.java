package com.sam.marketplace.bid;

import com.sam.marketplace.common.enums.BidStatus;
import com.sam.marketplace.lot.ProduceLot;
import com.sam.marketplace.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private ProduceLot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal bidPricePerQuintal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityQuintals;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BidStatus status = BidStatus.ACTIVE;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public Bid() {}

    public Bid(UUID id, ProduceLot lot, User buyer, BigDecimal bidPricePerQuintal,
               BigDecimal quantityQuintals, BigDecimal totalAmount, BidStatus status,
               Long version, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.lot = lot;
        this.buyer = buyer;
        this.bidPricePerQuintal = bidPricePerQuintal;
        this.quantityQuintals = quantityQuintals;
        this.totalAmount = totalAmount;
        this.status = status != null ? status : BidStatus.ACTIVE;
        this.version = version != null ? version : 0L;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ProduceLot getLot() { return lot; }
    public void setLot(ProduceLot lot) { this.lot = lot; }
    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }
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
        private UUID id;
        private ProduceLot lot;
        private User buyer;
        private BigDecimal bidPricePerQuintal;
        private BigDecimal quantityQuintals;
        private BigDecimal totalAmount;
        private BidStatus status = BidStatus.ACTIVE;
        private Long version = 0L;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder lot(ProduceLot lot) { this.lot = lot; return this; }
        public Builder buyer(User buyer) { this.buyer = buyer; return this; }
        public Builder bidPricePerQuintal(BigDecimal bidPricePerQuintal) { this.bidPricePerQuintal = bidPricePerQuintal; return this; }
        public Builder quantityQuintals(BigDecimal quantityQuintals) { this.quantityQuintals = quantityQuintals; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder status(BidStatus status) { this.status = status; return this; }
        public Builder version(Long version) { this.version = version; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Bid build() {
            return new Bid(id, lot, buyer, bidPricePerQuintal, quantityQuintals, totalAmount, status, version, createdAt, updatedAt);
        }
    }
}
