/**
 * 
 */
package com.brenner.portfoliomgmt.batch.holdings;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author dbrenner
 * 
 */
public class NewHoldingsUploadRowInstance {

	private String accountName;
	
	private String investmentSymbol;
	
	private Date dateOfData;
	
	private Date acquiredDate;
	
	private BigDecimal currentValue;
	
	private BigDecimal quantity;
	
	private BigDecimal sharePrice;
	
	public NewHoldingsUploadRowInstance() {}
	
	public NewHoldingsUploadRowInstance(String accountName, String investmentSymbol, Date dateOfData, Date acquiredDate,
			BigDecimal currentValue, BigDecimal quantity, BigDecimal sharePrice) {
		super();
		this.accountName = accountName;
		this.investmentSymbol = investmentSymbol;
		this.dateOfData = dateOfData;
		this.acquiredDate = acquiredDate;
		this.currentValue = currentValue;
		this.quantity = quantity;
		this.sharePrice = sharePrice;
	}

	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getInvestmentSymbol() {
		return this.investmentSymbol;
	}

	public void setInvestmentSymbol(String investmentSymbol) {
		this.investmentSymbol = investmentSymbol;
	}

	public Date getDateOfData() {
		return this.dateOfData;
	}

	public void setDateOfData(Date dateOfData) {
		this.dateOfData = dateOfData;
	}

	public Date getAcquiredDate() {
		return this.acquiredDate;
	}

	public void setAcquiredDate(Date acquiredDate) {
		this.acquiredDate = acquiredDate;
	}

	public BigDecimal getCurrentValue() {
		return this.currentValue;
	}

	public void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
	}

	public BigDecimal getQuantity() {
		return this.quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getSharePrice() {
		return this.sharePrice;
	}

	public void setSharePrice(BigDecimal sharePrice) {
		this.sharePrice = sharePrice;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NewHoldingsUploadRowInstance [accountName=").append(this.accountName)
				.append(", investmentSymbol=").append(this.investmentSymbol).append(", dateOfData=")
				.append(this.dateOfData).append(", acquiredDate=").append(this.acquiredDate).append(", currentValue=")
				.append(this.currentValue).append(", quantity=").append(this.quantity).append(", sharePrice=")
				.append(this.sharePrice).append("]");
		return builder.toString();
	}
	
	

}
