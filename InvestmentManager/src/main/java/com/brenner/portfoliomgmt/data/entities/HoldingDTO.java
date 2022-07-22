package com.brenner.portfoliomgmt.data.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.brenner.portfoliomgmt.domain.BucketEnum;

@Entity
@Table(name="holdings")
public class HoldingDTO implements Comparable<HoldingDTO> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long holdingId;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
	private InvestmentDTO investment;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
	private AccountDTO account;
	
	@Column(nullable = false)
	private BigDecimal quantity;
	
	@Column(nullable = false)
	private BigDecimal purchasePrice;
	
	@Enumerated
	private BucketEnum bucketEnum;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<TransactionDTO> transactions;
	
	@Column(nullable = false)
	private Date purchaseDate;
	
	@Transient
	private BigDecimal totalDividends;
	
	public HoldingDTO() {}
	
	public HoldingDTO(Long holdingId, InvestmentDTO investment, AccountDTO account,
			BigDecimal quantity, BigDecimal purchasePrice, BucketEnum bucketEnum, 
			Date purchaseDate, BigDecimal totalDividends) {
		super();
		this.holdingId = holdingId;
		this.investment = investment;
		this.account = account;
		this.quantity = quantity;
		this.purchasePrice = purchasePrice;
		this.bucketEnum = bucketEnum;
		this.purchaseDate = purchaseDate;
		this.totalDividends = totalDividends;
	}

	public HoldingDTO(Long holdingId) {
		this.holdingId = holdingId;
	}
	
	public HoldingDTO(
			BigDecimal valueAtPurchase, 
			String symbol,
			String companyName, 
			BigDecimal priceAtClose,
			BigDecimal marketValue, 
			BigDecimal changeInValue, 
			Long investmentId) {
		InvestmentDTO i = new InvestmentDTO(symbol);
		i.setCompanyName(companyName);
		i.setInvestmentId(investmentId);
		this.setInvestment(i);
	}
	
	public void addTransaction(TransactionDTO transaction) {
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

	public InvestmentDTO getInvestment() {
		return investment;
	}

	public void setInvestment(InvestmentDTO investment) {
		this.investment = investment;
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
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

	public List<TransactionDTO> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<TransactionDTO> transactions) {
		this.transactions = transactions;
	}

	public BigDecimal getTotalDividends() {
		return totalDividends;
	}

	public void setTotalDividends(BigDecimal totalDividends) {
		this.totalDividends = totalDividends;
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
	public int compareTo(HoldingDTO o) {
		
		if (this.investment != null && o.investment != null) {
			return this.investment.getSymbol().compareTo(o.investment.getSymbol());
		}
		return 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(account, bucketEnum, holdingId, investment, purchaseDate, purchasePrice, quantity,
				totalDividends, transactions);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HoldingDTO other = (HoldingDTO) obj;
		return Objects.equals(this.account, other.account) && this.bucketEnum == other.bucketEnum
				&& Objects.equals(this.holdingId, other.holdingId) && Objects.equals(this.investment, other.investment)
				&& Objects.equals(this.purchaseDate, other.purchaseDate)
				&& Objects.equals(this.purchasePrice, other.purchasePrice)
				&& Objects.equals(this.totalDividends, other.totalDividends)
				&& Objects.equals(this.transactions, other.transactions);
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("HoldingDTO [holdingId=").append(this.holdingId).append(", investment=").append(this.investment)
				.append(", account=").append(this.account).append(", quantity=").append(this.quantity)
				.append(", purchasePrice=").append(this.purchasePrice).append(", bucketEnum=").append(this.bucketEnum)
				.append(", transactions=")
				.append(this.transactions != null
						? this.transactions.subList(0, Math.min(this.transactions.size(), maxLen))
						: null)
				.append(", purchaseDate=").append(this.purchaseDate).append(", totalDividends=")
				.append(this.totalDividends).append("]");
		return builder.toString();
	}
	
	
	
}
