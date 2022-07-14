package com.brenner.portfoliomgmt.reporting;

import java.math.BigDecimal;
import java.util.Date;

public class InvestmentPerformance {

	Date quoteDate;
	BigDecimal close;
	BigDecimal change;
	
	public Date getQuoteDate() {
		return quoteDate;
	}
	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}
	public BigDecimal getClose() {
		return close;
	}
	public void setClose(BigDecimal close) {
		this.close = close;
	}
	public BigDecimal getChange() {
		return change;
	}
	public void setChange(BigDecimal change) {
		this.change = change;
	}
	
}
