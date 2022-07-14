package com.brenner.portfoliomgmt.transactions;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import com.brenner.portfoliomgmt.accounts.Account;
import com.brenner.portfoliomgmt.holdings.Holding;
import com.brenner.portfoliomgmt.investments.Investment;

@Entity
@Table(name="transactions")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionId;
	
	private Long associatedCashTransactionId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = "MM/dd/yyyy")
	private Date transactionDate;
	
	private Float tradePrice;
	
	private Float tradeQuantity;
	
	@Enumerated(EnumType.STRING)
	private TransactionTypeEnum transactionType;
	
	@OneToOne
	private Investment investment;
	
	@OneToOne
	private Account account;
	
	@ManyToOne
	private Holding holding;
	
	@Transient
	private Float dividend;
	
	public Transaction() {}
	
	
	
	public Transaction(Long transactionId, Long associatedCashTransactionId, Date transactionDate, Float tradePrice,
			Float tradeQuantity, TransactionTypeEnum transactionType, Investment investment, Account account,
			Holding holding, Float dividend) {
		super();
		this.transactionId = transactionId;
		this.associatedCashTransactionId = associatedCashTransactionId;
		this.transactionDate = transactionDate;
		this.tradePrice = tradePrice;
		this.tradeQuantity = tradeQuantity;
		this.transactionType = transactionType;
		this.investment = investment;
		this.account = account;
		this.holding = holding;
		this.dividend = dividend;
	}



	public Transaction(Long investmentId, Float dividend) {
		this.investment = new Investment(investmentId);
		this.dividend = dividend;
	}
	
	public Transaction (Date transactionDate, Float price, Float quantity, String tradeType) {
		
		this.transactionDate = transactionDate;
		this.tradePrice = price;
		this.tradeQuantity = quantity;
		this.transactionType = TransactionTypeEnum.valueOf(tradeType);
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getAssociatedCashTransactionId() {
		return associatedCashTransactionId;
	}

	public void setAssociatedCashTransactionId(Long associatedCashTransactionId) {
		this.associatedCashTransactionId = associatedCashTransactionId;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Float getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(Float tradePrice) {
		this.tradePrice = tradePrice;
	}

	public Float getTradeQuantity() {
		return tradeQuantity;
	}

	public void setTradeQuantity(Float tradeQuantity) {
		this.tradeQuantity = tradeQuantity;
	}

	public TransactionTypeEnum getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionTypeEnum transactionType) {
		this.transactionType = transactionType;
	}

	public Investment getInvestment() {
		return investment;
	}

	public void setInvestment(Investment investment) {
		this.investment = investment;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Float getDividend() {
		return dividend;
	}

	public void setDividend(Float dividend) {
		this.dividend = dividend;
	}

	public Holding getHolding() {
		return holding;
	}

	public void setHolding(Holding holding) {
		this.holding = holding;
	}

	@Override
	public String toString() {
		
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
		builder.append("transactionId", transactionId)
			.append("associatedCashTransactionId", associatedCashTransactionId)
			.append("transactionDate", transactionDate)
			.append("tradePrice", tradePrice)
			.append("tradeQuantity", tradeQuantity)
			.append("holding", holding)
			.append("transactionType", transactionType)
			.append("investment", investment)
			.append("account", account)
			.append("dividend", dividend);
		
		return builder.toString();
	}
}
