package com.brenner.investments.model;

import java.util.Date;

public class InvestmentPerformance {

	Date quoteDate;
	Float close;
	Float change;
	
	public Date getQuoteDate() {
		return quoteDate;
	}
	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}
	public Float getClose() {
		return close;
	}
	public void setClose(Float close) {
		this.close = close;
	}
	public Float getChange() {
		return change;
	}
	public void setChange(Float change) {
		this.change = change;
	}
	
}
