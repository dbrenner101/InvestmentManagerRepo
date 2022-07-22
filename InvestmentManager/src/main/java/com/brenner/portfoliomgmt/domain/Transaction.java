/**
 * 
 */
package com.brenner.portfoliomgmt.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;

import com.brenner.portfoliomgmt.domain.serialize.TransactionSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 *
 * @author dbrenner
 * 
 */
@JsonSerialize(using = TransactionSerializer.class)
public class Transaction {
	
	private Long transactionId;
	
	private Long associatedCashTransactionId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = "MM/dd/yyyy")
	private Date transactionDate;
	
	private BigDecimal tradePrice;
	
	private BigDecimal tradeQuantity;
	
	private TransactionTypeEnum transactionType;
	
	private Holding holding;
	
	private Account account;
	
	private BigDecimal dividend;
	
	private Investment investment;
	
	public Transaction() {}
	
	public Transaction(Long transactionId, Long associatedCashTransactionId, Date transactionDate,
			BigDecimal tradePrice, BigDecimal tradeQuantity, TransactionTypeEnum transactionType, Holding holding,
			Account account, BigDecimal dividend, Investment investment) {
		super();
		this.transactionId = transactionId;
		this.associatedCashTransactionId = associatedCashTransactionId;
		this.transactionDate = transactionDate;
		this.tradePrice = tradePrice;
		this.tradeQuantity = tradeQuantity;
		this.transactionType = transactionType;
		this.holding = holding;
		this.account = account;
		this.dividend = dividend;
		this.investment = investment;
	}

	public Transaction(Account account) {
		this.account = account;
	}
	
	public Transaction(Holding holding) {
		this.holding = holding;
	}


	public Long getTransactionId() {
		return this.transactionId;
	}


	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}


	public Long getAssociatedCashTransactionId() {
		return this.associatedCashTransactionId;
	}


	public void setAssociatedCashTransactionId(Long associatedCashTransactionId) {
		this.associatedCashTransactionId = associatedCashTransactionId;
	}


	public Date getTransactionDate() {
		return this.transactionDate;
	}


	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}


	public BigDecimal getTradePrice() {
		return this.tradePrice;
	}


	public void setTradePrice(BigDecimal tradePrice) {
		this.tradePrice = tradePrice;
	}


	public BigDecimal getTradeQuantity() {
		return this.tradeQuantity;
	}


	public void setTradeQuantity(BigDecimal tradeQuantity) {
		this.tradeQuantity = tradeQuantity;
	}


	public TransactionTypeEnum getTransactionType() {
		return this.transactionType;
	}


	public void setTransactionType(TransactionTypeEnum transactionType) {
		this.transactionType = transactionType;
	}


	public Holding getHolding() {
		return this.holding;
	}


	public void setHolding(Holding holding) {
		this.holding = holding;
	}


	public Account getAccount() {
		return this.account;
	}


	public void setAccount(Account account) {
		this.account = account;
	}


	public BigDecimal getDividend() {
		return this.dividend;
	}


	public void setDividend(BigDecimal dividend) {
		this.dividend = dividend;
	}


	public Investment getInvestment() {
		return this.investment;
	}


	public void setInvestment(Investment investment) {
		this.investment = investment;
	}


	@Override
	public int hashCode() {
		return Objects.hash(account, associatedCashTransactionId, dividend, holding, investment, tradePrice,
				tradeQuantity, transactionDate, transactionId, transactionType);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		return Objects.equals(this.account, other.account)
				&& Objects.equals(this.associatedCashTransactionId, other.associatedCashTransactionId)
				&& Objects.equals(this.dividend, other.dividend) && Objects.equals(this.holding, other.holding)
				&& Objects.equals(this.investment, other.investment)
				&& Objects.equals(this.tradePrice, other.tradePrice)
				&& Objects.equals(this.tradeQuantity, other.tradeQuantity)
				&& Objects.equals(this.transactionDate, other.transactionDate)
				&& Objects.equals(this.transactionId, other.transactionId)
				&& this.transactionType == other.transactionType;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TransactionValue [transactionId=").append(this.transactionId)
				.append(", associatedCashTransactionId=").append(this.associatedCashTransactionId)
				.append(", transactionDate=").append(this.transactionDate).append(", tradePrice=")
				.append(this.tradePrice).append(", tradeQuantity=").append(this.tradeQuantity)
				.append(", transactionType=").append(this.transactionType)
				.append(", account=").append(this.account).append(", dividend=").append(this.dividend)
				.append(", investment=").append(this.investment).append("]");
		return builder.toString();
	}

}
