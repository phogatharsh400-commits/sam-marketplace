package com.sam.marketplace.advisor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class PriceForecastDto {
    private UUID id;
    private String crop;
    private String district;
    private LocalDate forecastDate;
    private BigDecimal currentPrice;
    private BigDecimal movingAverage7Day;
    private BigDecimal trendSlope;
    private BigDecimal forecastPrice30Day;
    private BigDecimal forecastPrice60Day;
    private String forecastConfidence;
    private String rationale;
    private Instant createdAt;

    public PriceForecastDto() {}

    public static PriceForecastDto from(PriceForecast forecast) {
        PriceForecastDto dto = new PriceForecastDto();
        dto.setId(forecast.getId());
        dto.setCrop(forecast.getCrop().name());
        dto.setDistrict(forecast.getDistrict());
        dto.setForecastDate(forecast.getForecastDate());
        dto.setCurrentPrice(forecast.getCurrentPrice());
        dto.setMovingAverage7Day(forecast.getMovingAverage7Day());
        dto.setTrendSlope(forecast.getTrendSlope());
        dto.setForecastPrice30Day(forecast.getForecastPrice30Day());
        dto.setForecastPrice60Day(forecast.getForecastPrice60Day());
        dto.setForecastConfidence(forecast.getForecastConfidence());
        dto.setRationale(forecast.getRationale());
        dto.setCreatedAt(forecast.getCreatedAt());
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }
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
