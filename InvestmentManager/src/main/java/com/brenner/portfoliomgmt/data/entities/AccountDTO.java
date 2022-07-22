package com.brenner.portfoliomgmt.data.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Entity object representing an account
 * 
 * @author dbrenner
 *
 */
@Entity
@Table(name="accounts")
public class AccountDTO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId;
	
	@Column(unique = true)
	private String accountName;
	
	private String company;
	
	private String owner;
	
	private String accountNumber;
	
	private String accountType;
	
	private BigDecimal cashOnAccount;
	
	private int visible;
	
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.PERSIST)
	List<HoldingDTO> holdings;
	
	public AccountDTO() {}
	
	public AccountDTO(Long accountId, String accountName, String company, String owner, String accountNumber,
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

	public AccountDTO(Long accountId) {
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

	public List<HoldingDTO> getHoldings() {
		return holdings;
	}

	public void setHoldings(List<HoldingDTO> holdings) {
		this.holdings = holdings;
	}
	
	public void addHolding(HoldingDTO holding) {
		if (this.holdings == null) {
			this.holdings = new ArrayList<>(1);
		}
		this.holdings.add(holding);
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

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("AccountDTO [accountId=").append(this.accountId).append(", accountName=")
				.append(this.accountName).append(", company=").append(this.company).append(", owner=")
				.append(this.owner).append(", accountNumber=").append(this.accountNumber).append(", accountType=")
				.append(this.accountType).append(", cashOnAccount=").append(this.cashOnAccount).append(", visible=")
				.append(this.visible).append(", holdings=")
				.append(this.holdings != null ? this.holdings.subList(0, Math.min(this.holdings.size(), maxLen)) : null)
				.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId, accountName, accountNumber, accountType, cashOnAccount, company, holdings, owner, visible);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountDTO other = (AccountDTO) obj;
		return Objects.equals(this.accountId, other.accountId) && Objects.equals(this.accountName, other.accountName)
				&& Objects.equals(this.accountNumber, other.accountNumber)
				&& Objects.equals(this.accountType, other.accountType)
				&& Objects.equals(this.cashOnAccount, other.cashOnAccount)
				&& Objects.equals(this.company, other.company) && Objects.equals(this.holdings, other.holdings)
				&& Objects.equals(this.owner, other.owner) && this.visible == other.visible;
	}
	
}
