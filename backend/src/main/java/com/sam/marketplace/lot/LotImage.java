package com.sam.marketplace.lot;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lot_images")
public class LotImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private ProduceLot lot;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private boolean isPrimary = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public LotImage() {}

    public LotImage(UUID id, ProduceLot lot, String imageUrl, boolean isPrimary, Instant createdAt) {
        this.id = id;
        this.lot = lot;
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ProduceLot getLot() { return lot; }
    public void setLot(ProduceLot lot) { this.lot = lot; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { isPrimary = primary; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private ProduceLot lot;
        private String imageUrl;
        private boolean isPrimary = false;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder lot(ProduceLot lot) { this.lot = lot; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public Builder isPrimary(boolean isPrimary) { this.isPrimary = isPrimary; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public LotImage build() {
            return new LotImage(id, lot, imageUrl, isPrimary, createdAt);
        }
    }
}
