package com.brenner.portfoliomgmt.accounts;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.brenner.portfoliomgmt.holdings.Holding;
import com.brenner.portfoliomgmt.transactions.Transaction;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Entity object representing an account
 * 
 * @author dbrenner
 *
 */
@Entity
@Table(name="accounts")
@JsonSerialize(using=AccountSerializer.class)
@JsonDeserialize(using=AccountDeserializer.class)
public class Account {

	@JsonProperty("accountId")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId;
	
	private String accountName;
	
	private String company;
	
	private String owner;
	
	private String accountNumber;
	
	private String accountType;
	
	private BigDecimal cashOnAccount;
	
	private int visible;
	
	@JsonBackReference
	@OneToMany(fetch = FetchType.LAZY)
	List<Holding> holdings;
	
	@OneToMany(fetch = FetchType.LAZY)
	List<Transaction> transactions;
	
	public Account() {}
	
	public Account(Long accountId) {
		this.accountId = accountId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public List<Holding> getHoldings() {
		return holdings;
	}

	public void setHoldings(List<Holding> holdings) {
		this.holdings = holdings;
	}

	public BigDecimal getCashOnAccount() {
		return cashOnAccount;
	}

	public void setCashOnAccount(BigDecimal cashOnAccount) {
		this.cashOnAccount = cashOnAccount;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	@Override
	public String toString() {
		
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
		builder.append("accountId", accountId)
		.append("accountName", accountName)
		.append("company", company)
		.append("owner", owner)
		.append("accountNumber", accountNumber)
		.append("accountType", accountType)
		.append("cashOnAccount", cashOnAccount);
		
		return builder.toString();
	}
	
}
