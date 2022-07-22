package com.brenner.portfoliomgmt.data.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.brenner.portfoliomgmt.domain.TransactionTypeEnum;

@Entity
@Table(name="transactions")
public class TransactionDTO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionId;
	
	private Long associatedCashTransactionId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd", fallbackPatterns = "MM/dd/yyyy")
	private Date transactionDate;
	
	private BigDecimal tradePrice;
	
	private BigDecimal tradeQuantity;
	
	@Enumerated(EnumType.STRING)
	private TransactionTypeEnum transactionType;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	private HoldingDTO holding;
	
	@ManyToOne
	private AccountDTO account;
	
	@Transient
	private BigDecimal dividend;
	
	public TransactionDTO() {}
	
	public TransactionDTO(HoldingDTO holding) {
		this.holding = holding;
	}
	
	public TransactionDTO(AccountDTO account) {
		this.account = account;
	}
	
	public TransactionDTO(Long transactionId, Long associatedCashTransactionId, Date transactionDate, BigDecimal tradePrice,
			BigDecimal tradeQuantity, TransactionTypeEnum transactionType, HoldingDTO holding, AccountDTO account, BigDecimal dividend) {
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
	}
	
	public TransactionDTO (AccountDTO account, Date transactionDate, BigDecimal price, BigDecimal quantity, String tradeType) {
		
		this.transactionDate = transactionDate;
		this.tradePrice = price;
		this.tradeQuantity = quantity;
		this.transactionType = TransactionTypeEnum.valueOf(tradeType);
		this.account = account;
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

	public BigDecimal getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(BigDecimal tradePrice) {
		this.tradePrice = tradePrice;
	}

	public BigDecimal getTradeQuantity() {
		return tradeQuantity;
	}

	public void setTradeQuantity(BigDecimal tradeQuantity) {
		this.tradeQuantity = tradeQuantity;
	}

	public TransactionTypeEnum getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionTypeEnum transactionType) {
		this.transactionType = transactionType;
	}

	public BigDecimal getDividend() {
		return dividend;
	}

	public void setDividend(BigDecimal dividend) {
		this.dividend = dividend;
	}

	public HoldingDTO getHolding() {
		return holding;
	}

	public void setHolding(HoldingDTO holding) {
		this.holding = holding;
	}

	public AccountDTO getAccount() {
		return this.account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Transaction [transactionId=").append(this.transactionId)
				.append(", associatedCashTransactionId=").append(this.associatedCashTransactionId)
				.append(", transactionDate=").append(this.transactionDate).append(", tradePrice=")
				.append(this.tradePrice).append(", tradeQuantity=").append(this.tradeQuantity)
				.append(", transactionType=").append(this.transactionType).append(", holding=").append(this.holding)
				.append(", account=").append(this.account).append(", dividend=").append(this.dividend).append("]");
		return builder.toString();
	}
}
