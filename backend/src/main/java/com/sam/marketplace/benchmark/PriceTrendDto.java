package com.sam.marketplace.benchmark;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceTrendDto {
    private LocalDate date;
    private BigDecimal modalPrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal msp;

    public PriceTrendDto() {}

    public PriceTrendDto(LocalDate date, BigDecimal modalPrice, BigDecimal minPrice, BigDecimal maxPrice, BigDecimal msp) {
        this.date = date;
        this.modalPrice = modalPrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.msp = msp;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public BigDecimal getModalPrice() { return modalPrice; }
    public void setModalPrice(BigDecimal modalPrice) { this.modalPrice = modalPrice; }
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
    public BigDecimal getMsp() { return msp; }
    public void setMsp(BigDecimal msp) { this.msp = msp; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final PriceTrendDto dto = new PriceTrendDto();
        public Builder date(LocalDate date) { dto.setDate(date); return this; }
        public Builder modalPrice(BigDecimal modalPrice) { dto.setModalPrice(modalPrice); return this; }
        public Builder minPrice(BigDecimal minPrice) { dto.setMinPrice(minPrice); return this; }
        public Builder maxPrice(BigDecimal maxPrice) { dto.setMaxPrice(maxPrice); return this; }
        public Builder msp(BigDecimal msp) { dto.setMsp(msp); return this; }
        public PriceTrendDto build() { return dto; }
    }
}
