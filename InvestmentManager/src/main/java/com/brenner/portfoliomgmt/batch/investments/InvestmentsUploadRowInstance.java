/**
 * 
 */
package com.brenner.portfoliomgmt.batch.investments;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author dbrenner
 * 
 */
public class InvestmentsUploadRowInstance {
	
	private String symbol;
	private String name;
	private BigDecimal lastPrice;
	private Date date;
	
	public InvestmentsUploadRowInstance() {}

	public InvestmentsUploadRowInstance(String symbol, String name, BigDecimal lastPrice, Date date) {
		super();
		this.symbol = symbol;
		this.name = name;
		this.lastPrice = lastPrice;
		this.date = date;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getLastPrice() {
		return this.lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvestmentsUploadRowInstance [symbol=").append(this.symbol).append(", name=").append(this.name)
				.append(", lastPrice=").append(this.lastPrice).append(", date=").append(this.date).append("]");
		return builder.toString();
	}

}
