package com.brenner.investments.data.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sql/accounts.sql.xml")
public class AccountsSqlProperties {
	
	@Value("${allAccountSql}")
	private String allAccountSql;
	
	@Value("${accountByAccountId}")
	private String accountByAccountId;
	
	@Value("${allAccountsOrderedByAccountName}")
	private String allAccountsOrderedByAccountName;
	
	@Value("${accountByAccountNumber}")
	private String accountByAccountNumber;
	
	@Value("${accountDelete}")
	private String accountDelete;
	
	@Value("${accountInsert}")
	private String accountInsert;
	
	@Value("${accountUpdate}")
	private String accountUpdate;
	

	public String getAccountInsert() {
		return accountInsert;
	}

	public void setAccountInsert(String accountInsert) {
		this.accountInsert = accountInsert;
	}

	public String getAccountUpdate() {
		return accountUpdate;
	}

	public void setAccountUpdate(String accountUpdate) {
		this.accountUpdate = accountUpdate;
	}

	public String getAccountDelete() {
		return accountDelete;
	}

	public void setAccountDelete(String accountDelete) {
		this.accountDelete = accountDelete;
	}

	public String getAccountByAccountNumber() {
		return accountByAccountNumber;
	}

	public void setAccountByAccountNumber(String accountByAccountNumber) {
		this.accountByAccountNumber = accountByAccountNumber;
	}

	public String getAllAccountSql() {
		return allAccountSql;
	}

	public void setAllAccountSql(String allAccountSql) {
		this.allAccountSql = allAccountSql;
	}

	public String getAccountByAccountId() {
		return accountByAccountId;
	}

	public void setAccountByAccountId(String accountByAccountId) {
		this.accountByAccountId = accountByAccountId;
	}

	public String getAllAccountsOrderedByAccountName() {
		return allAccountsOrderedByAccountName;
	}

	public void setAllAccountsOrderedByAccountName(String allAccountsOrderedByAccountName) {
		this.allAccountsOrderedByAccountName = allAccountsOrderedByAccountName;
	}

}
