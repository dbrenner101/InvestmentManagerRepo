package com.brenner.portfoliomgmt.reporting;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value="portfolioReport")
public class PortfolioReport {
    
    private Double totalValueAtPurchase;
    
    private Double totalChangeInValue;
    
    private Double totalCurrentValue;
    
    private Double totalChangeInPrice;
    

    public Double getTotalValueAtPurchase() {
        return totalValueAtPurchase;
    }

    public void setTotalValueAtPurchase(Double totalValueAtPurchase) {
        this.totalValueAtPurchase = totalValueAtPurchase;
    }

    public Double getTotalChangeInValue() {
        return totalChangeInValue;
    }

    public void setTotalChangeInValue(Double totalChangeInValue) {
        this.totalChangeInValue = totalChangeInValue;
    }

    public Double getTotalCurrentValue() {
        return totalCurrentValue;
    }

    public void setTotalCurrentValue(Double totalCurrentValue) {
        this.totalCurrentValue = totalCurrentValue;
    }

    public Double getTotalChangeInPrice() {
        return totalChangeInPrice;
    }

    public void setTotalChangeInPrice(Double totalChangeInPrice) {
        this.totalChangeInPrice = totalChangeInPrice;
    }
    

}

