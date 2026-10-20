package com.sam.marketplace.bid;

import java.time.LocalDate;

public class AcceptBidRequestDto {
    private String deliveryTerms;
    private String dispatchDeadline;
    private String deliveryDeadline;

    public AcceptBidRequestDto() {}

    public String getDeliveryTerms() { return deliveryTerms; }
    public void setDeliveryTerms(String deliveryTerms) { this.deliveryTerms = deliveryTerms; }
    public String getDispatchDeadline() { return dispatchDeadline; }
    public void setDispatchDeadline(String dispatchDeadline) { this.dispatchDeadline = dispatchDeadline; }
    public String getDeliveryDeadline() { return deliveryDeadline; }
    public void setDeliveryDeadline(String deliveryDeadline) { this.deliveryDeadline = deliveryDeadline; }
}
