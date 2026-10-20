package com.sam.marketplace.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingCreateDto {

    @NotBlank(message = "Crop name is required")
    private String crop;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.1", message = "Quantity must be at least 0.1 quintals")
    private BigDecimal quantityQuintals;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String remarks;

    public BookingCreateDto() {}

    public BookingCreateDto(String crop, BigDecimal quantityQuintals, LocalDate startDate,
                            LocalDate endDate, String remarks) {
        this.crop = crop;
        this.quantityQuintals = quantityQuintals;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remarks = remarks;
    }

    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }
    public BigDecimal getQuantityQuintals() { return quantityQuintals; }
    public void setQuantityQuintals(BigDecimal quantityQuintals) { this.quantityQuintals = quantityQuintals; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
