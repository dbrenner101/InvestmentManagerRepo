/**
 * 
 */
package com.brenner.portfoliomgmt.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author dbrenner
 * 
 */
public class Holding {
	
	private Long holdingId;
	
	private Investment investment;
	
	private Account account;
	
	private BigDecimal quantity;
	
	private BigDecimal purchasePrice;
	
	private BucketEnum bucketEnum;
	
	private BigDecimal valueAtPurchase;
	
	private BigDecimal changeInValue;
	
	private BigDecimal currentValue;
	
	private BigDecimal totalCurrentValue;
	
	private Date purchaseDate;
	
	private BigDecimal totalDividends;
	
	private Quote mostRecentQuote;
	
	private List<Transaction> transactions;
	
	public Holding() {}

	public Holding(Long holdingId, Investment investment, Account account, BigDecimal quantity,
			BigDecimal purchasePrice, BucketEnum bucketEnum, Date purchaseDate, BigDecimal totalDividends,
			Quote mostRecentQuote, List<Transaction> transactions) {
		super();
		this.holdingId = holdingId;
		this.investment = investment;
		this.account = account;
		this.quantity = quantity;
		this.purchasePrice = purchasePrice;
		this.bucketEnum = bucketEnum;
		this.purchaseDate = purchaseDate;
		this.totalDividends = totalDividends;
		this.mostRecentQuote = mostRecentQuote;
		this.transactions = transactions;
	}

	public Long getHoldingId() {
		return this.holdingId;
	}

	public void setHoldingId(Long holdingId) {
		this.holdingId = holdingId;
	}

	public Investment getInvestment() {
		return this.investment;
	}

	public void setInvestment(Investment investment) {
		this.investment = investment;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public BigDecimal getQuantity() {
		return this.quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPurchasePrice() {
		return this.purchasePrice;
	}

	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public BucketEnum getBucketEnum() {
		return this.bucketEnum;
	}

	public void setBucketEnum(BucketEnum bucketEnum) {
		this.bucketEnum = bucketEnum;
	}

	public BigDecimal getValueAtPurchase() {
		return this.valueAtPurchase;
	}

	public void setValueAtPurchase(BigDecimal valueAtPurchase) {
		this.valueAtPurchase = valueAtPurchase;
	}
	
	public BigDecimal getChangeInValue() {
		
		return this.changeInValue;
	}

	public void setChangeInValue(BigDecimal changeInValue) {
		this.changeInValue = changeInValue;
	}
	
	public BigDecimal getCurrentValue() {
		return this.currentValue;
	}

	public void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
	}

	public BigDecimal getTotalCurrentValue() {
		return this.totalCurrentValue;
	}

	public void setTotalCurrentValue(BigDecimal totalCurrentValue) {
		this.totalCurrentValue = totalCurrentValue;
	}

	public Date getPurchaseDate() {
		return this.purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public BigDecimal getTotalDividends() {
		return this.totalDividends;
	}

	public void setTotalDividends(BigDecimal totalDividends) {
		this.totalDividends = totalDividends;
	}

	public List<Transaction> getTransactions() {
		return this.transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	public void addTransaction(Transaction transaction) {
		if (this.transactions == null) {
			this.transactions = new ArrayList<>(1);
		}
		this.transactions.add(transaction);
	}
	

	public Quote getMostRecentQuote() {
		return this.mostRecentQuote;
	}

	public void setMostRecentQuote(Quote mostRecentQuote) {
		this.mostRecentQuote = mostRecentQuote;
	}

	@Override
	public int hashCode() {
		return Objects.hash(account, bucketEnum, changeInValue, currentValue, holdingId, investment, purchaseDate,
				purchasePrice, quantity, mostRecentQuote, totalCurrentValue, totalDividends, transactions, valueAtPurchase);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Holding other = (Holding) obj;
		return Objects.equals(this.account, other.account) && this.bucketEnum == other.bucketEnum
				&& Objects.equals(this.changeInValue, other.changeInValue)
				&& Objects.equals(this.currentValue, other.currentValue)
				&& Objects.equals(this.holdingId, other.holdingId) && Objects.equals(this.investment, other.investment)
				&& Objects.equals(this.purchaseDate, other.purchaseDate)
				&& Objects.equals(this.purchasePrice, other.purchasePrice)
				&& Objects.equals(this.quantity, other.quantity) && Objects.equals(this.mostRecentQuote, other.mostRecentQuote)
				&& Objects.equals(this.totalCurrentValue, other.totalCurrentValue)
				&& Objects.equals(this.totalDividends, other.totalDividends)
				&& Objects.equals(this.transactions, other.transactions)
				&& Objects.equals(this.valueAtPurchase, other.valueAtPurchase);
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("Holding [holdingId=").append(this.holdingId).append(", investment=").append(this.investment)
				.append(", account=").append(this.account).append(", quantity=").append(this.quantity)
				.append(", purchasePrice=").append(this.purchasePrice).append(", bucketEnum=").append(this.bucketEnum)
				.append(", valueAtPurchase=").append(this.valueAtPurchase).append(", changeInValue=")
				.append(this.changeInValue).append(", currentValue=").append(this.currentValue)
				.append(", totalCurrentValue=").append(this.totalCurrentValue).append(", purchaseDate=")
				.append(this.purchaseDate).append(", totalDividends=").append(this.totalDividends).append(", quote=")
				.append(this.mostRecentQuote).append(", transactions=")
				.append(this.transactions != null
						? this.transactions.subList(0, Math.min(this.transactions.size(), maxLen))
						: null)
				.append("]");
		return builder.toString();
	}

}
