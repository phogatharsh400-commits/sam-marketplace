package com.sam.marketplace.benchmark;

import com.sam.marketplace.common.enums.CropType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class BenchmarkDto {
    private UUID id;
    private CropType crop;
    private String district;
    private String state;
    private BigDecimal currentMandiPrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal modalPrice;
    private BigDecimal msp;
    private String dataSource;
    private LocalDate priceDate;
    private BigDecimal indicativeMinPrice;
    private BigDecimal indicativeMaxPrice;

    public BenchmarkDto() {}

    public static BenchmarkDto from(MandiPriceBenchmark b) {
        if (b == null) return null;
        BigDecimal indMin = b.getMinPrice().multiply(BigDecimal.valueOf(0.95));
        BigDecimal indMax = b.getMaxPrice().multiply(BigDecimal.valueOf(1.05));

        BenchmarkDto dto = new BenchmarkDto();
        dto.setId(b.getId());
        dto.setCrop(b.getCrop());
        dto.setDistrict(b.getDistrict());
        dto.setState(b.getState());
        dto.setCurrentMandiPrice(b.getCurrentMandiPrice());
        dto.setMinPrice(b.getMinPrice());
        dto.setMaxPrice(b.getMaxPrice());
        dto.setModalPrice(b.getModalPrice());
        dto.setMsp(b.getMsp());
        dto.setDataSource(b.getDataSource());
        dto.setPriceDate(b.getPriceDate());
        dto.setIndicativeMinPrice(indMin);
        dto.setIndicativeMaxPrice(indMax);
        return dto;
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
    public BigDecimal getIndicativeMinPrice() { return indicativeMinPrice; }
    public void setIndicativeMinPrice(BigDecimal indicativeMinPrice) { this.indicativeMinPrice = indicativeMinPrice; }
    public BigDecimal getIndicativeMaxPrice() { return indicativeMaxPrice; }
    public void setIndicativeMaxPrice(BigDecimal indicativeMaxPrice) { this.indicativeMaxPrice = indicativeMaxPrice; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final BenchmarkDto dto = new BenchmarkDto();
        public Builder id(UUID id) { dto.setId(id); return this; }
        public Builder crop(CropType crop) { dto.setCrop(crop); return this; }
        public Builder district(String district) { dto.setDistrict(district); return this; }
        public Builder state(String state) { dto.setState(state); return this; }
        public Builder currentMandiPrice(BigDecimal currentMandiPrice) { dto.setCurrentMandiPrice(currentMandiPrice); return this; }
        public Builder minPrice(BigDecimal minPrice) { dto.setMinPrice(minPrice); return this; }
        public Builder maxPrice(BigDecimal maxPrice) { dto.setMaxPrice(maxPrice); return this; }
        public Builder modalPrice(BigDecimal modalPrice) { dto.setModalPrice(modalPrice); return this; }
        public Builder msp(BigDecimal msp) { dto.setMsp(msp); return this; }
        public Builder dataSource(String dataSource) { dto.setDataSource(dataSource); return this; }
        public Builder priceDate(LocalDate priceDate) { dto.setPriceDate(priceDate); return this; }
        public Builder indicativeMinPrice(BigDecimal indicativeMinPrice) { dto.setIndicativeMinPrice(indicativeMinPrice); return this; }
        public Builder indicativeMaxPrice(BigDecimal indicativeMaxPrice) { dto.setIndicativeMaxPrice(indicativeMaxPrice); return this; }
        public BenchmarkDto build() { return dto; }
    }
}
