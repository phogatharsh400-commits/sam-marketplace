package com.sam.marketplace.benchmark;

import com.sam.marketplace.common.enums.CropType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "price_benchmarks")
public class MandiPriceBenchmark {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CropType crop;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 100)
    private String state = "Haryana";

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentMandiPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal maxPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal modalPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal msp;

    @Column(nullable = false, length = 100)
    private String dataSource = "Agmarknet Simulated Mandi API (Prototype Data)";

    @Column(nullable = false)
    private LocalDate priceDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public MandiPriceBenchmark() {}

    public MandiPriceBenchmark(UUID id, CropType crop, String district, String state,
                               BigDecimal currentMandiPrice, BigDecimal minPrice, BigDecimal maxPrice,
                               BigDecimal modalPrice, BigDecimal msp, String dataSource,
                               LocalDate priceDate, Instant createdAt) {
        this.id = id;
        this.crop = crop;
        this.district = district;
        this.state = state != null ? state : "Haryana";
        this.currentMandiPrice = currentMandiPrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.modalPrice = modalPrice;
        this.msp = msp;
        this.dataSource = dataSource != null ? dataSource : "Agmarknet Simulated Mandi API (Prototype Data)";
        this.priceDate = priceDate;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public CropType getCrop() { return crop; }
    public void setCrop(CropType crop) { this.crop = crop; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public BigDecimal getCurrentMandiPrice() { return currentMandiPrice; }
    public void setCurrentMandiPrice(BigDecimal currentMandiPrice) { this.currentMandiPrice = currentMandiPrice; }
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
    public BigDecimal getModalPrice() { return modalPrice; }
    public void setModalPrice(BigDecimal modalPrice) { this.modalPrice = modalPrice; }
    public BigDecimal getMsp() { return msp; }
    public void setMsp(BigDecimal msp) { this.msp = msp; }
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    public LocalDate getPriceDate() { return priceDate; }
    public void setPriceDate(LocalDate priceDate) { this.priceDate = priceDate; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private CropType crop;
        private String district;
        private String state = "Haryana";
        private BigDecimal currentMandiPrice;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private BigDecimal modalPrice;
        private BigDecimal msp;
        private String dataSource = "Agmarknet Simulated Mandi API (Prototype Data)";
        private LocalDate priceDate;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder crop(CropType crop) { this.crop = crop; return this; }
        public Builder district(String district) { this.district = district; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder currentMandiPrice(BigDecimal currentMandiPrice) { this.currentMandiPrice = currentMandiPrice; return this; }
        public Builder minPrice(BigDecimal minPrice) { this.minPrice = minPrice; return this; }
        public Builder maxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; return this; }
        public Builder modalPrice(BigDecimal modalPrice) { this.modalPrice = modalPrice; return this; }
        public Builder msp(BigDecimal msp) { this.msp = msp; return this; }
        public Builder dataSource(String dataSource) { this.dataSource = dataSource; return this; }
        public Builder priceDate(LocalDate priceDate) { this.priceDate = priceDate; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public MandiPriceBenchmark build() {
            return new MandiPriceBenchmark(id, crop, district, state, currentMandiPrice, minPrice, maxPrice, modalPrice, msp, dataSource, priceDate, createdAt);
        }
    }
}
