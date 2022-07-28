/**
 * 
 */
package com.brenner.portfoliomgmt.batch.quotes;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author dbrenner
 * 
 */
public class QuotesUploadRowInstance {
	
	private String symbol;
	private Date quoteDate;
	private BigDecimal close;
	
	QuotesUploadRowInstance() {}

	public QuotesUploadRowInstance(String symbol, Date quoteDate, BigDecimal close) {
		this.symbol = symbol;
		this.quoteDate = quoteDate;
		this.close = close;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Date getQuoteDate() {
		return this.quoteDate;
	}

	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}

	public BigDecimal getClose() {
		return this.close;
	}

	public void setClose(BigDecimal close) {
		this.close = close;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QuotesUploadRowInstance [symbol=").append(this.symbol).append(", quoteDate=")
				.append(this.quoteDate).append(", close=").append(this.close).append("]");
		return builder.toString();
	}
	
	

}
