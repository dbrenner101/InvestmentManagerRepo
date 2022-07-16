package com.brenner.portfoliomgmt.holdings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.brenner.portfoliomgmt.accounts.Account;
import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.quotes.Quote;
import com.brenner.portfoliomgmt.transactions.Transaction;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name="holdings")
public class Holding implements Comparable<Holding>, Cloneable {

	@JsonProperty("holdingId")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long holdingId;
	
	@OneToOne(fetch = FetchType.EAGER)
	private Investment investment;

	@OneToMany(fetch = FetchType.LAZY)
    private List<Quote> quotes;
	
	@JsonManagedReference
	@OneToOne(fetch = FetchType.LAZY)
	private Account account;
	
	private BigDecimal quantity;
	
	private BigDecimal purchasePrice;
	
	@Enumerated
	private BucketEnum bucketEnum;
	
	@JsonProperty("valueAtPurchase")
	@Transient
	private BigDecimal valueAtPurchase;
	
	@JsonProperty("changeInValue")
	@Transient
	private BigDecimal changeInValue;
	
	@JsonProperty("currentValue")
	@Transient
	private BigDecimal currentValue;
	
	@Transient
	private BigDecimal totalCurrentValue;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<Transaction> transactions;
	
	@Transient
	private Date purchaseDate;
	
	@Transient
	private BigDecimal totalDividends;
	
	public Holding() {}
	
	public Holding(
			BigDecimal valueAtPurchase, 
			String symbol,
			String companyName, 
			BigDecimal priceAtClose,
			BigDecimal marketValue, 
			BigDecimal changeInValue, 
			Long investmentId) {
		this.valueAtPurchase = valueAtPurchase;
		Investment i = new Investment(symbol);
		i.setCompanyName(companyName);
		i.setInvestmentId(investmentId);
		this.setInvestment(i);
		this.currentValue = marketValue;
		this.changeInValue = changeInValue;
	}
	
	public BigDecimal getCurrentValue() {
		
		if (this.currentValue != null) {
			return this.currentValue;
		}
		
		if (this.quotes != null && ! this.quotes.isEmpty() && this.quotes.get(0) != null && this.quantity != null) {
			this.currentValue = this.quotes.get(0).getClose().multiply(this.quantity);
		}
		
		return this.currentValue;
	}
	
	
	public BigDecimal getChangeInValue() {
		
		if (this.changeInValue != null) {
			return this.changeInValue;
		}
		
		if (this.valueAtPurchase != null && this.currentValue != null) {
			return this.currentValue.subtract(this.valueAtPurchase);
		}
		
		if (this.getValueAtPurchase() != null && this.quotes != null && ! this.quotes.isEmpty()) {
			this.changeInValue = this.quantity.multiply(this.quotes.get(0).getClose()).subtract(this.valueAtPurchase);
			return this.changeInValue;
		}
		
		return BigDecimal.ZERO;
	}

	
	public BigDecimal getValueAtPurchase() {
		
		if (this.valueAtPurchase != null) {
			return this.valueAtPurchase;
		}
		
		if (this.quantity != null && this.purchasePrice != null) {
			this.valueAtPurchase = this.quantity.multiply(this.purchasePrice);
		}
		return valueAtPurchase;
	}
    
    public void addQuote(Quote quote) {
    	if (this.quotes == null) {
    		quotes = new ArrayList<>();
    	}
    	
    	quotes.add(quote);
    }
	
	public void addTransaction(Transaction transaction) {
		if (this.transactions == null) {
			this.transactions = new ArrayList<>();
		}
		
		this.transactions.add(transaction);
	}

	public Long getHoldingId() {
		return holdingId;
	}

	public void setHoldingId(Long holdingId) {
		this.holdingId = holdingId;
	}

	public Investment getInvestment() {
		return investment;
	}

	public void setInvestment(Investment investment) {
		this.investment = investment;
	}

	public List<Quote> getQuotes() {
		return quotes;
	}

	public void setQuotes(List<Quote> quotes) {
		this.quotes = quotes;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public BigDecimal getTotalCurrentValue() {
		return totalCurrentValue;
	}

	public void setTotalCurrentValue(BigDecimal totalCurrentValue) {
		this.totalCurrentValue = totalCurrentValue;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public BigDecimal getTotalDividends() {
		return totalDividends;
	}

	public void setTotalDividends(BigDecimal totalDividends) {
		this.totalDividends = totalDividends;
	}

	public void setValueAtPurchase(BigDecimal valueAtPurchase) {
		this.valueAtPurchase = valueAtPurchase;
	}

	public void setChangeInValue(BigDecimal changeInValue) {
		this.changeInValue = changeInValue;
	}

	public void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
	}

	public BucketEnum getBucketEnum() {
		return this.bucketEnum;
	}

	public void setBucketEnum(BucketEnum bucketEnum) {
		this.bucketEnum = bucketEnum;
	}

	public Date getPurchaseDate() {
		return this.purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	@Override
	public String toString() {
		
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
		builder.append("holdingId", holdingId)
			.append("investment", investment)
			//.append("quotes", quotes)
			.append("account", account)
			.append("quantity", quantity)
			.append("purchasePrice", purchasePrice)
			.append("valueAtPurchase", valueAtPurchase)
			.append("changeInValue", changeInValue)
			.append("currentValue", currentValue)
			.append("totalCurrentValue", totalCurrentValue)
			//.append("transactions", transactions)
			.append("totalDividends", totalDividends);
		
		return builder.toString();
	}
	
	
	public Holding shallowClone(Holding o) {
		
		Holding h = new Holding();
		h.setAccount(o.getAccount());
		h.setCurrentValue(o.getCurrentValue());
		h.setHoldingId(o.getHoldingId());
		h.setInvestment(o.getInvestment());
		h.setPurchasePrice(o.getPurchasePrice());
		h.setQuantity(o.getQuantity());
		h.setQuotes(o.getQuotes());
		h.setTransactions(o.getTransactions());
		h.setTotalCurrentValue(o.getTotalCurrentValue());
		h.setTotalDividends(o.getTotalDividends());
		h.setValueAtPurchase(o.getValueAtPurchase());
		
		return h;
	}

	@Override
	public int compareTo(Holding o) {
		
		if (this.investment != null && o.investment != null) {
			return this.investment.getSymbol().compareTo(o.investment.getSymbol());
		}
		return 0;
	}
	
}
