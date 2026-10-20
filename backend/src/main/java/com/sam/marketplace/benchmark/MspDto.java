package com.sam.marketplace.benchmark;

import com.sam.marketplace.common.enums.CropType;

import java.math.BigDecimal;

public class MspDto {
    private CropType crop;
    private BigDecimal mspPerQuintal;
    private String marketingSeason;
    private String source;

    public MspDto() {}

    public MspDto(CropType crop, BigDecimal mspPerQuintal, String marketingSeason, String source) {
        this.crop = crop;
        this.mspPerQuintal = mspPerQuintal;
        this.marketingSeason = marketingSeason;
        this.source = source;
    }

    public CropType getCrop() { return crop; }
    public void setCrop(CropType crop) { this.crop = crop; }
    public BigDecimal getMspPerQuintal() { return mspPerQuintal; }
    public void setMspPerQuintal(BigDecimal mspPerQuintal) { this.mspPerQuintal = mspPerQuintal; }
    public String getMarketingSeason() { return marketingSeason; }
    public void setMarketingSeason(String marketingSeason) { this.marketingSeason = marketingSeason; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final MspDto dto = new MspDto();
        public Builder crop(CropType crop) { dto.setCrop(crop); return this; }
        public Builder mspPerQuintal(BigDecimal mspPerQuintal) { dto.setMspPerQuintal(mspPerQuintal); return this; }
        public Builder marketingSeason(String marketingSeason) { dto.setMarketingSeason(marketingSeason); return this; }
        public Builder source(String source) { dto.setSource(source); return this; }
        public MspDto build() { return dto; }
    }
}
