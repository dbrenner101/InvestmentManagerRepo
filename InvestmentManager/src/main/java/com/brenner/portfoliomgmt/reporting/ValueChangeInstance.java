package com.brenner.portfoliomgmt.reporting;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ValueChangeInstance implements Comparable<ValueChangeInstance> {

	private Float purchaseValue;
	
	private Float marketValue;
	
	@SuppressWarnings("unused")
	private Float changeInValue;
	
	private String symbol;
	
	private String companyName;
	
	private String sector;
	
	private Date quoteDate;

	public Float getPurchaseValue() {
		return purchaseValue;
	}

	public void setPurchaseValue(Float purchaseValue) {
		this.purchaseValue = purchaseValue;
	}

	public Float getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(Float marketValue) {
		this.marketValue = marketValue;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Date getQuoteDate() {
		return quoteDate;
	}

	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}
	
	public Float getChangeInValue() {
		
		if (this.purchaseValue != null && this.marketValue != null) {
			return this.marketValue - this.purchaseValue;
		}
		
		return 0F;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Override
	public String toString() {
		
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
		
		builder.append("purchaseValue", this.purchaseValue)
			.append("marketValue", this.marketValue)
			.append("changeInValue", this.getChangeInValue())
			.append("symbol", this.symbol)
			.append("companyName", this.companyName)
			.append("sector", this.sector)
			.append("quoteDate", this.quoteDate);
		
		return builder.toString();
	}

	@Override
	public int compareTo(ValueChangeInstance o) {
		
		return this.getChangeInValue().compareTo(o.getChangeInValue());
	}
}
