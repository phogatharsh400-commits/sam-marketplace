package com.sam.marketplace.bid;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class BidCreateDto {

    @NotNull(message = "Bid price per quintal is required")
    @DecimalMin(value = "1.0", message = "Bid price must be greater than zero")
    private BigDecimal bidPricePerQuintal;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.1", message = "Quantity must be at least 0.1 quintals")
    private BigDecimal quantityQuintals;

    public BidCreateDto() {}

    public BigDecimal getBidPricePerQuintal() { return bidPricePerQuintal; }
    public void setBidPricePerQuintal(BigDecimal bidPricePerQuintal) { this.bidPricePerQuintal = bidPricePerQuintal; }
    public BigDecimal getQuantityQuintals() { return quantityQuintals; }
    public void setQuantityQuintals(BigDecimal quantityQuintals) { this.quantityQuintals = quantityQuintals; }
}
