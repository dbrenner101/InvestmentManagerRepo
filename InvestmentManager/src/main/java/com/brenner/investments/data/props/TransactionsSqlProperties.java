package com.brenner.investments.data.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sql/transactions.sql.xml")
public class TransactionsSqlProperties {
	
	@Value("${transactionsByAccountIdAndInvestmentId}")
	private String transactionsByAccountIdAndInvestmentId;
	
	@Value("${transactionsBuyHoldingId}")
	private String transactionsBuyHoldingId;
	
	@Value("${transactionsByAccountIdOrderedByTransactionDateDesc}")
	private String transactionsByAccountIdOrderedByTransactionDateDesc;
	
	@Value("${transactionsByAccountIdOrderedByInvestmentSymbol}")
	private String transactionsByAccountIdOrderedByInvestmentSymbol;
	
	@Value("${transactionsByAccountIdAndHoldingId}")
	private String transactionsByAccountIdAndHoldingId;
	
	@Value("${transactionForAccountsWithCash}")
	private String transactionForAccountsWithCash;
	
	@Value("${totalDividendsForInvestments}")
	private String totalDividendsForInvestments;
	
	@Value("${totalDividendsByInvestment}")
	private String totalDividendsByInvestment;
	
	@Value("${transactionsInsert}")
	private String transactionsInsert;
	
	@Value("${transactionsUpdate}")
	private String transactionsUpdate;
	
	@Value("${transactionsDelete}")
	private String transactionsDelete;
	
	@Value("${transactionByTransactionId}")
	private String transactionByTransactionId;
	

	public String getTransactionsByAccountIdAndInvestmentId() {
		return transactionsByAccountIdAndInvestmentId;
	}

	public void setTransactionsByAccountIdAndInvestmentId(String transactionsByAccountIdAndInvestmentId) {
		this.transactionsByAccountIdAndInvestmentId = transactionsByAccountIdAndInvestmentId;
	}

	public String getTransactionsByAccountIdOrderedByTransactionDateDesc() {
		return transactionsByAccountIdOrderedByTransactionDateDesc;
	}

	public void setTransactionsByAccountIdOrderedByTransactionDateDesc(
			String transactionsByAccountIdOrderedByTransactionDateDesc) {
		this.transactionsByAccountIdOrderedByTransactionDateDesc = transactionsByAccountIdOrderedByTransactionDateDesc;
	}

	public String getTransactionsByAccountIdOrderedByInvestmentSymbol() {
		return transactionsByAccountIdOrderedByInvestmentSymbol;
	}

	public void setTransactionsByAccountIdOrderedByInvestmentSymbol(
			String transactionsByAccountIdOrderedByInvestmentSymbol) {
		this.transactionsByAccountIdOrderedByInvestmentSymbol = transactionsByAccountIdOrderedByInvestmentSymbol;
	}

	public String getTransactionsByAccountIdAndHoldingId() {
		return transactionsByAccountIdAndHoldingId;
	}

	public void setTransactionsByAccountIdAndHoldingId(String transactionsByAccountIdAndHoldingId) {
		this.transactionsByAccountIdAndHoldingId = transactionsByAccountIdAndHoldingId;
	}

	public String getTransactionForAccountsWithCash() {
		return transactionForAccountsWithCash;
	}

	public void setTransactionForAccountsWithCash(String transactionForAccountsWithCash) {
		this.transactionForAccountsWithCash = transactionForAccountsWithCash;
	}

	public String getTotalDividendsForInvestments() {
		return totalDividendsForInvestments;
	}

	public void setTotalDividendsForInvestments(String totalDividendsForInvestments) {
		this.totalDividendsForInvestments = totalDividendsForInvestments;
	}

	public String getTotalDividendsByInvestment() {
		return totalDividendsByInvestment;
	}

	public void setTotalDividendsByInvestment(String totalDividendsByInvestment) {
		this.totalDividendsByInvestment = totalDividendsByInvestment;
	}

	public String getTransactionsInsert() {
		return transactionsInsert;
	}

	public void setTransactionsInsert(String transactionsInsert) {
		this.transactionsInsert = transactionsInsert;
	}

	public String getTransactionsUpdate() {
		return transactionsUpdate;
	}

	public void setTransactionsUpdate(String transactionsUpdate) {
		this.transactionsUpdate = transactionsUpdate;
	}

	public String getTransactionsDelete() {
		return transactionsDelete;
	}

	public void setTransactionsDelete(String transactionsDelete) {
		this.transactionsDelete = transactionsDelete;
	}

	public String getTransactionByTransactionId() {
		return transactionByTransactionId;
	}

	public void setTransactionByTransactionId(String transactionByTransactionId) {
		this.transactionByTransactionId = transactionByTransactionId;
	}

	public String getTransactionsBuyHoldingId() {
		return transactionsBuyHoldingId;
	}

	public void setTransactionsBuyHoldingId(String transactionsBuyHoldingId) {
		this.transactionsBuyHoldingId = transactionsBuyHoldingId;
	}
	

	
}
