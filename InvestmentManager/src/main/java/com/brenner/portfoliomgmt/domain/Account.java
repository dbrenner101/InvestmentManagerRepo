/**
 * 
 */
package com.brenner.portfoliomgmt.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author dbrenner
 * 
 */
public class Account {
	
	private Long accountId;
	
	private String accountName;
	
	private String company;
	
	private String owner;
	
	private String accountNumber;
	
	private String accountType;
	
	private BigDecimal cashOnAccount;
	
	List<Holding> holdings;
	
	public Account() {}
	
	public Account(Long accountId) {
		this.accountId = accountId;
	}

	public Account(Long accountId, String accountName, String company, String owner, String accountNumber,
			String accountType, BigDecimal cashOnAccount) {
		super();
		this.accountId = accountId;
		this.accountName = accountName;
		this.company = company;
		this.owner = owner;
		this.accountNumber = accountNumber;
		this.accountType = accountType;
		this.cashOnAccount = cashOnAccount;
	}

	public Long getAccountId() {
		return this.accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getCompany() {
		return this.company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getAccountNumber() {
		return this.accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountType() {
		return this.accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public BigDecimal getCashOnAccount() {
		return this.cashOnAccount;
	}

	public void setCashOnAccount(BigDecimal cashOnAccount) {
		this.cashOnAccount = cashOnAccount;
	}

	public List<Holding> getHoldings() {
		return this.holdings;
	}

	public void setHoldings(List<Holding> holdings) {
		this.holdings = holdings;
	}
	
	public void addHolding(Holding holding) {
		if (this.holdings == null) {
			this.holdings = new ArrayList<>(1);
		}
		this.holdings.add(holding);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId, accountName, accountNumber, accountType, cashOnAccount, company, holdings, owner);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		return Objects.equals(this.accountId, other.accountId) && Objects.equals(this.accountName, other.accountName)
				&& Objects.equals(this.accountNumber, other.accountNumber)
				&& Objects.equals(this.accountType, other.accountType)
				&& Objects.equals(this.cashOnAccount, other.cashOnAccount)
				&& Objects.equals(this.company, other.company) && Objects.equals(this.holdings, other.holdings);
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("Account [accountId=").append(this.accountId).append(", accountName=").append(this.accountName)
				.append(", company=").append(this.company).append(", owner=").append(this.owner)
				.append(", accountNumber=").append(this.accountNumber).append(", accountType=").append(this.accountType)
				.append(", cashOnAccount=").append(this.cashOnAccount).append(", holdings=")
				.append(this.holdings != null ? this.holdings.subList(0, Math.min(this.holdings.size(), maxLen)) : null)
				.append("]");
		return builder.toString();
	}

}
