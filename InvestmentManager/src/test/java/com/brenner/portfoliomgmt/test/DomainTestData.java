/**
 * 
 */
package com.brenner.portfoliomgmt.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.brenner.portfoliomgmt.data.entities.Watchlist;
import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.domain.BucketEnum;
import com.brenner.portfoliomgmt.domain.Holding;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.InvestmentTypeEnum;
import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.domain.Transaction;
import com.brenner.portfoliomgmt.domain.TransactionTypeEnum;

/**
 *
 * @author dbrenner
 * 
 */
public class DomainTestData {
	
	public static final String ACCOUNT_ONE_NAME = "Account 1";
	public static final String ACCOUNT_ONE_NUM = "1234";
	public static final String ACCOUNT_ONE_TYPE = "Investment";
	public static final String ACCCOUNT_ONE_COMPANY = "Company 1";
	public static final String ACCOUNT_ONE_OWNER = "Owner 1";
	
	private static final String ALPHABET = "QWERTYUIOPLKJHGFDSAZXCVBNM";
	
	public static List<Account> generateAccountsList(int listSize) {
		List<Account> accounts = new ArrayList<>(listSize);
		for (int i=0; i<listSize; i++) {
			Account account = generateAccount(i, 4);
			accounts.add(account);
		}
		return accounts;
	}
	
	public static Account generateAccount(int sequence, int numHoldings) {
		Account account = new Account(Long.valueOf(sequence), "Account " + sequence, "Company " + sequence, 
				"Owner " + sequence, "AccountNum " + sequence, "AccountType " + sequence);
		account.setHoldings(generateHoldingsList(numHoldings, account));
		
		return account;
	}
	
	public static List<Transaction> generateTransactionsList(int listSize, Holding holding, Account account) {
		List<Transaction> transactions = new ArrayList<>(listSize);
		for (int i=0; i<listSize; i++) {
			Transaction t = generateTransaction(i, holding, account);
			transactions.add(t);
		}
		return transactions;
	}
	
	public static Transaction generateTransaction(int sequence, Holding holding, Account account) {
		int rand = new Random().nextInt(0,  5);
		TransactionTypeEnum transactionType = TransactionTypeEnum.values()[rand];
		
		Transaction transaction = new Transaction(Long.valueOf(sequence), Long.valueOf(sequence*2), generateDate(sequence, new Date()), 
				BigDecimal.valueOf(287), BigDecimal.valueOf(125), transactionType, holding, account, 
				BigDecimal.valueOf(444), holding.getInvestment());
		
		return transaction;
	}
	
	public static List<Holding> generateHoldingsList(int listSize, Account account) {
		List<Holding> holdings = new ArrayList<>(listSize);
		for (int i=0; i<listSize; i++) {
			Holding holding = generateHolding(i, account, generateInvestment(i));
			holdings.add(holding);
		}
		return holdings;
	}

	public static Holding generateHolding(int sequence, Account account, Investment inv) {
		Holding holding = new Holding(Long.valueOf(sequence), inv, account, BigDecimal.valueOf(100), BigDecimal.valueOf(15.55), BucketEnum.BUCKET_1, 
				generateDate(sequence, new Date()), BigDecimal.valueOf(534), null, null);
		holding.setTransactions(generateTransactionsList(3, holding, account));
		return holding;
	}

	public static List<Investment> generateInvestmentList(int listSize) {
		List<Investment> investments = new ArrayList<>(listSize);
		for (int i=0; i<listSize; i++) {
			investments.add(generateInvestment(i));
		}
		
		return investments;
	}
	
	public static Investment generateInvestment(int sequence) {
		int rand = new Random().nextInt(0, 23);
		Investment inv = new Investment(Long.valueOf(sequence), ALPHABET.substring(rand, rand+3), "Company Name " + rand, "Exchange " + sequence, "Sector " + sequence,
				InvestmentTypeEnum.MutualFund);
		inv.setMostRecentQuote(generateQuote(sequence, inv));
		return inv;
	}
	
	public static List<Quote> generateQuotesList(int listSize, Investment inv) {
		List<Quote> quotes = new ArrayList<>(listSize);
		for (int i=0; i<listSize; i++) {
			Quote q = generateQuote(i, inv);
			quotes.add(q);
		}
		return quotes;
	}
	
	public static Quote generateQuote(int sequence, Investment i) {
		
		Quote q = new Quote(Long.valueOf(sequence), generateDate(sequence, new Date()), BigDecimal.valueOf(100), BigDecimal.valueOf(100.55), 
				BigDecimal.valueOf(200), BigDecimal.valueOf(50), 100000, null, null, null, i);
		
		return q;
	}
	
	private static Date generateDate(int sequence, Date startDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.MONTH, sequence);
		return cal.getTime();
	}
	
	
	public static final Watchlist getWatchlistNoInvestments1() {
		
		Watchlist list = new Watchlist();
		list.setWatchlistName("No Investments List One");
		
		return list;
	}
	
	public static final Watchlist getWatchlistNoInvestments2() {
		
		Watchlist list = new Watchlist();
		list.setWatchlistName("No Investments List Two");
		
		return list;
		
	}
	
	public static final Watchlist getWatchlistWith2Investments() {
		
		Watchlist list = new Watchlist();
		list.setWatchlistId(2);
		list.setWatchlistName("Two Investments List");
		
		List<Investment> investments = new ArrayList<>(2);
		investments.add(getInvestmentAAPL());
		investments.add(getInvestmentFB());
		
		list.setInvestmentsToWatch(investments);
		
		return list;
	}

	public static final Account getAccount1() {
		
		Account a = new Account();
		a.setAccountId(1L);
		a.setAccountName(DomainTestData.ACCOUNT_ONE_NAME);
		a.setAccountNumber(DomainTestData.ACCOUNT_ONE_NUM);
		a.setAccountType(DomainTestData.ACCOUNT_ONE_TYPE);
		a.setCompany(DomainTestData.ACCCOUNT_ONE_COMPANY);
		a.setOwner(DomainTestData.ACCOUNT_ONE_OWNER);
		
		return a;
	}
	
	public static final Account getAccount2() {
		Account a = new Account();
		a.setAccountId(2L);
		a.setAccountName("Account 2");
		a.setAccountNumber("4321");
		a.setAccountType("IRA");
		a.setCompany("Company 2");
		a.setOwner("Owner 2");
		
		return a;
	}
	
	public static final Account getAccount3() {
		Account a = new Account();
		a.setAccountId(3L);
		a.setAccountName("Account 3");
		a.setAccountNumber("5678");
		a.setAccountType("ROTH");
		a.setCompany("Company 3");
		a.setOwner("Owner 3");
		
		return a;
	}
	
	public static final List<Account> getAllAccountsList() {
		List<Account> accounts = new ArrayList<>();
		accounts.add(DomainTestData.getAccount1());
		accounts.add(DomainTestData.getAccount2());
		accounts.add(DomainTestData.getAccount3());
		
		return accounts;
	}
	
	public static final Transaction getCashTransaction1() {
		Transaction t = new Transaction(DomainTestData.getAccount1());
		t.setTransactionId(1L);
		t.setTradePrice(BigDecimal.valueOf(1));
		t.setTradeQuantity(BigDecimal.valueOf(100.5));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Cash);
		
		return t;
	}
	
	public static final Transaction getCashTransaction2() {
		Transaction t = new Transaction(DomainTestData.getAccount2());
		t.setTransactionId(2L);
		t.setTradePrice(BigDecimal.valueOf(1));
		t.setTradeQuantity(BigDecimal.valueOf(100));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Cash);
		
		return t;
	}
	
	public static final Transaction getBuyTransaction1() {
		Transaction t = new Transaction(DomainTestData.getHolding1());
		t.setTransactionId(3L);
		t.setTradePrice(BigDecimal.valueOf(1));
		t.setTradeQuantity(BigDecimal.valueOf(100));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Buy);
		
		return t;
	}
	
	public static final Transaction getBuyTransaction2() {
		Transaction t = new Transaction(DomainTestData.getHolding2());
		t.setTransactionId(4L);
		t.setTradePrice(BigDecimal.valueOf(17));
		t.setTradeQuantity(BigDecimal.valueOf(99));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Buy);
		
		return t;
	}
	
	public static final Transaction getTransferTransaction1() {
		Transaction t = new Transaction(DomainTestData.getAccount1());
		t.setTransactionId(5L);
		t.setTradePrice(BigDecimal.valueOf(1));
		t.setTradeQuantity(BigDecimal.valueOf(100));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Transfer);
		
		return t;
	}
	
	public static final Transaction getSaleTransaction1() {
		Transaction t = new Transaction(DomainTestData.getHolding3());
		t.setTransactionId(99L);
		t.setTradePrice(BigDecimal.valueOf(170));
		t.setTradeQuantity(BigDecimal.valueOf(1));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Sell);
		
		return t;
	}
	
	public static final Transaction getSaleTransaction2() {
		Transaction t = new Transaction(DomainTestData.getHolding2());
		t.setTransactionId(6L);
		t.setTradePrice(BigDecimal.valueOf(12));
		t.setTradeQuantity(BigDecimal.valueOf(500));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Sell);
		
		return t;
	}
	
	public static final List<Transaction> getTransactionForAccount(Account a) {
		List<Transaction> transactions = new ArrayList<>();
		
		Transaction t1 = DomainTestData.getBuyTransaction1();
		t1.setAccount(a);
		transactions.add(t1);
		
		Transaction t2 = DomainTestData.getBuyTransaction2();
		t2.setAccount(a);
		transactions.add(t2);
		
		Transaction t3 = DomainTestData.getSaleTransaction1();
		t3.setAccount(a);
		transactions.add(t3);
		
		Transaction t4 = DomainTestData.getCashTransaction1();
		t4.setAccount(a);
		transactions.add(t4);
		
		return transactions;
	}
	
	public static final Investment getInvestmentAAPL() {
		
		Investment i = new Investment();
		i.setInvestmentType(InvestmentTypeEnum.Stock);
		i.setInvestmentId(1L);
		i.setCompanyName("Apple");
		i.setSymbol("AAPL");
		i.setExchange("NASQAQ");
		i.setSector("Technology");
		
		return i;
	}
	
	public static final Investment getInvestmentFB() {
		
		Investment i = new Investment();
		i.setInvestmentType(InvestmentTypeEnum.Stock);
		i.setInvestmentId(4L);
		i.setCompanyName("Facebook");
		i.setSymbol("FB");
		i.setExchange("NASQAQ");
		i.setSector("Technology");
		
		return i;
	}
	
	public static final Investment getInvestmentGE() {

		Investment i = new Investment();
		i.setInvestmentType(InvestmentTypeEnum.Stock);
		i.setInvestmentId(2L);
		i.setCompanyName("GE");
		i.setSymbol("GE");
		i.setExchange("NYSE");
		i.setSector("Consumer Goods");
		return i;
	}
	
	public static final Investment getInvestmentPVTL() {

		Investment i = new Investment();
		i.setInvestmentType(InvestmentTypeEnum.Stock);
		i.setInvestmentId(3L);
		i.setCompanyName("Pivotal");
		i.setSymbol("PVTL");
		i.setExchange("NASQAQ");
		i.setSector("Technology");
		
		return i;
	}
	
	public static final List<Investment> getAllInvestments() {
		List<Investment> allInvestments = new ArrayList<>();
		allInvestments.add(getInvestmentAAPL());
		allInvestments.add(getInvestmentFB());
		allInvestments.add(getInvestmentGE());
		allInvestments.add(getInvestmentPVTL());
		
		return allInvestments;
	}
	
	public static final List<Investment> getAllInvestmentsAndQuotes() {
		List<Investment> allInvestments = new ArrayList<>();
		Investment i1 = DomainTestData.getInvestmentAAPL();
		List<Quote> i1Quotes = new ArrayList<>();
		i1Quotes.add(DomainTestData.getQuoteAAPL());
		allInvestments.add(i1);
		
		Investment i2 = DomainTestData.getInvestmentFB();
		List<Quote> i2Quotes = new ArrayList<>();
		i2Quotes.add(DomainTestData.getQuoteFB());
		allInvestments.add(i2);
		
		return allInvestments;
	}
	
	public static final Holding getHolding1() {
		
		Holding holding = new Holding();
		holding.setHoldingId(1L);
		holding.setPurchasePrice(BigDecimal.valueOf(10));
		holding.setQuantity(BigDecimal.valueOf(10));
		
		return holding;
	}
	
	public static final Holding getHolding2() {
		
		Holding holding = new Holding();
		holding.setHoldingId(2L);
		holding.setPurchasePrice(BigDecimal.valueOf(20));
		holding.setQuantity(BigDecimal.valueOf(20));
		
		return holding;
	}
	
	public static final Holding getHolding3() {
		
		Holding holding = new Holding();
		holding.setHoldingId(2L);
		holding.setPurchasePrice(BigDecimal.valueOf(200));
		holding.setQuantity(BigDecimal.valueOf(200));
		
		return holding;
	}
	
	public static final Holding getZeroQuantityHolding() {
		
		Holding holding = new Holding();
		holding.setHoldingId(3L);
		holding.setPurchasePrice(BigDecimal.valueOf(100));
		holding.setQuantity(BigDecimal.valueOf(0));
		
		return holding;
	}
	
	public static final List<Holding> getHoldingsForAccount(Account a) {
		List<Holding> holdings = new ArrayList<>();
		
		Holding h1 = DomainTestData.getHolding1();
		h1.setAccount(a);
		holdings.add(h1);
		
		Holding h2 = DomainTestData.getHolding2();
		h2.setAccount(a);
		holdings.add(h2);
		
		return holdings;
	}
	
	public static final Quote getQuoteAAPL() {
		
		Quote q = new Quote();
		q.setInvestment(getInvestmentAAPL());
		q.setQuoteId(1L);
		q.setClose(BigDecimal.valueOf(10));
		q.setDate(new Date());
		q.setHigh(BigDecimal.valueOf(15));
		q.setLow(BigDecimal.valueOf(8));
		q.setOpen(BigDecimal.valueOf(9));
		q.setPriceChange(BigDecimal.valueOf(.09));
		q.setVolume(10000);
		
		return q;
	}
	
	public static final Quote getQuoteFB() {
		
		Quote q = new Quote();
		q.setInvestment(getInvestmentFB());
		q.setQuoteId(2L);
		q.setClose(BigDecimal.valueOf(20));
		q.setDate(new Date());
		q.setHigh(BigDecimal.valueOf(25));
		q.setLow(BigDecimal.valueOf(8));
		q.setOpen(BigDecimal.valueOf(12));
		q.setPriceChange(BigDecimal.valueOf(1.35));
		q.setVolume(1000000);
		
		return q;
	}

}
