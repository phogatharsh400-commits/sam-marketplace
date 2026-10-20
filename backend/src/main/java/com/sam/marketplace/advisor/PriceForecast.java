package com.sam.marketplace.advisor;

import com.sam.marketplace.common.enums.CropType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "price_forecasts")
public class PriceForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CropType crop;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false)
    private LocalDate forecastDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal movingAverage7Day;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal trendSlope; // Daily change rate

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal forecastPrice30Day;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal forecastPrice60Day;

    @Column(nullable = false, length = 20)
    private String forecastConfidence = "MEDIUM";

    @Column(columnDefinition = "TEXT")
    private String rationale;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public PriceForecast() {}

    public PriceForecast(UUID id, CropType crop, String district, LocalDate forecastDate,
                         BigDecimal currentPrice, BigDecimal movingAverage7Day, BigDecimal trendSlope,
                         BigDecimal forecastPrice30Day, BigDecimal forecastPrice60Day,
                         String forecastConfidence, String rationale, Instant createdAt) {
        this.id = id;
        this.crop = crop;
        this.district = district;
        this.forecastDate = forecastDate;
        this.currentPrice = currentPrice;
        this.movingAverage7Day = movingAverage7Day;
        this.trendSlope = trendSlope;
        this.forecastPrice30Day = forecastPrice30Day;
        this.forecastPrice60Day = forecastPrice60Day;
        this.forecastConfidence = forecastConfidence != null ? forecastConfidence : "MEDIUM";
        this.rationale = rationale;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private CropType crop;
        private String district;
        private LocalDate forecastDate;
        private BigDecimal currentPrice;
        private BigDecimal movingAverage7Day;
        private BigDecimal trendSlope;
        private BigDecimal forecastPrice30Day;
        private BigDecimal forecastPrice60Day;
        private String forecastConfidence = "MEDIUM";
        private String rationale;
        private Instant createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder crop(CropType crop) { this.crop = crop; return this; }
        public Builder district(String district) { this.district = district; return this; }
        public Builder forecastDate(LocalDate forecastDate) { this.forecastDate = forecastDate; return this; }
        public Builder currentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; return this; }
        public Builder movingAverage7Day(BigDecimal movingAverage7Day) { this.movingAverage7Day = movingAverage7Day; return this; }
        public Builder trendSlope(BigDecimal trendSlope) { this.trendSlope = trendSlope; return this; }
        public Builder forecastPrice30Day(BigDecimal forecastPrice30Day) { this.forecastPrice30Day = forecastPrice30Day; return this; }
        public Builder forecastPrice60Day(BigDecimal forecastPrice60Day) { this.forecastPrice60Day = forecastPrice60Day; return this; }
        public Builder forecastConfidence(String forecastConfidence) { this.forecastConfidence = forecastConfidence; return this; }
        public Builder rationale(String rationale) { this.rationale = rationale; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public PriceForecast build() {
            return new PriceForecast(id, crop, district, forecastDate, currentPrice,
                    movingAverage7Day, trendSlope, forecastPrice30Day, forecastPrice60Day,
                    forecastConfidence, rationale, createdAt);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public CropType getCrop() { return crop; }
    public void setCrop(CropType crop) { this.crop = crop; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public LocalDate getForecastDate() { return forecastDate; }
    public void setForecastDate(LocalDate forecastDate) { this.forecastDate = forecastDate; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public BigDecimal getMovingAverage7Day() { return movingAverage7Day; }
    public void setMovingAverage7Day(BigDecimal movingAverage7Day) { this.movingAverage7Day = movingAverage7Day; }
    public BigDecimal getTrendSlope() { return trendSlope; }
    public void setTrendSlope(BigDecimal trendSlope) { this.trendSlope = trendSlope; }
    public BigDecimal getForecastPrice30Day() { return forecastPrice30Day; }
    public void setForecastPrice30Day(BigDecimal forecastPrice30Day) { this.forecastPrice30Day = forecastPrice30Day; }
    public BigDecimal getForecastPrice60Day() { return forecastPrice60Day; }
    public void setForecastPrice60Day(BigDecimal forecastPrice60Day) { this.forecastPrice60Day = forecastPrice60Day; }
    public String getForecastConfidence() { return forecastConfidence; }
    public void setForecastConfidence(String forecastConfidence) { this.forecastConfidence = forecastConfidence; }
    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
