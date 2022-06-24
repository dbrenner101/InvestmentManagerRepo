package com.brenner.investments.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@Entity
@Table(name="holdings")
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonRootName(value="holding")
public class Holding implements Comparable<Holding>, Cloneable {

	@JsonProperty("holdingId")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long holdingId;
	
	@OneToOne(fetch = FetchType.LAZY)
	private Investment investment;

	@OneToMany(fetch = FetchType.LAZY)
    private List<Quote> quotes;
	
	@JsonManagedReference
	@OneToOne(fetch = FetchType.LAZY)
	private Account account;
	
	private Float quantity;
	
	private Float purchasePrice;
	
	@JsonProperty("valueAtPurchase")
	@Transient
	private Float valueAtPurchase;
	
	@JsonProperty("changeInValue")
	@Transient
	private Float changeInValue;
	
	@JsonProperty("currentValue")
	@Transient
	private Float currentValue;
	
	@Transient
	private Float totalCurrentValue;
	
	@OneToMany
	private List<Transaction> transactions;
	
	@Transient
	private Float totalDividends;
	
	public Holding() {}
	
	public Holding(
			Float valueAtPurchase, 
			String symbol,
			String companyName, 
			Float priceAtClose,
			Float marketValue, 
			Float changeInValue, 
			Long investmentId) {
		this.valueAtPurchase = valueAtPurchase;
		Investment i = new Investment(symbol);
		i.setCompanyName(companyName);
		i.setInvestmentId(investmentId);
		this.setInvestment(i);
		this.currentValue = marketValue;
		this.changeInValue = changeInValue;
	}
	
	public Float getCurrentValue() {
		
		if (this.currentValue != null) {
			return this.currentValue;
		}
		
		if (this.quotes != null && ! this.quotes.isEmpty() && this.quotes.get(0) != null && this.quantity != null) {
			this.currentValue = this.quotes.get(0).getClose() * this.quantity;
		}
		
		return this.currentValue;
	}
	
	
	public Float getChangeInValue() {
		
		if (this.changeInValue != null) {
			return this.changeInValue;
		}
		
		if (this.valueAtPurchase != null && this.currentValue != null) {
			return this.currentValue - this.valueAtPurchase;
		}
		
		if (this.getValueAtPurchase() != null && this.quotes != null && ! this.quotes.isEmpty()) {
			this.changeInValue = this.quantity * this.quotes.get(0).getClose() - this.valueAtPurchase;
			return this.changeInValue;
		}
		
		return 0F;
	}

	
	public Float getValueAtPurchase() {
		
		if (this.valueAtPurchase != null) {
			return this.valueAtPurchase;
		}
		
		if (this.quantity != null && this.purchasePrice != null) {
			this.valueAtPurchase = this.quantity * this.purchasePrice;
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

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}

	public Float getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Float purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public Float getTotalCurrentValue() {
		return totalCurrentValue;
	}

	public void setTotalCurrentValue(Float totalCurrentValue) {
		this.totalCurrentValue = totalCurrentValue;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public Float getTotalDividends() {
		return totalDividends;
	}

	public void setTotalDividends(Float totalDividends) {
		this.totalDividends = totalDividends;
	}

	public void setValueAtPurchase(Float valueAtPurchase) {
		this.valueAtPurchase = valueAtPurchase;
	}

	public void setChangeInValue(Float changeInValue) {
		this.changeInValue = changeInValue;
	}

	public void setCurrentValue(Float currentValue) {
		this.currentValue = currentValue;
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
