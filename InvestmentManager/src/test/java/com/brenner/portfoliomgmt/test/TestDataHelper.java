package com.brenner.portfoliomgmt.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.brenner.portfoliomgmt.accounts.Account;
import com.brenner.portfoliomgmt.holdings.Holding;
import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.investments.InvestmentTypeEnum;
import com.brenner.portfoliomgmt.quotes.Quote;
import com.brenner.portfoliomgmt.transactions.Transaction;
import com.brenner.portfoliomgmt.transactions.TransactionTypeEnum;
import com.brenner.portfoliomgmt.watchlist.Watchlist;

public class TestDataHelper {
	
	public static final String ACCOUNT_ONE_NAME = "Account 1";
	public static final String ACCOUNT_ONE_NUM = "1234";
	public static final String ACCOUNT_ONE_TYPE = "Investment";
	public static final String ACCCOUNT_ONE_COMPANY = "Company 1";
	public static final String ACCOUNT_ONE_OWNER = "Owner 1";
	
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
		a.setAccountName(TestDataHelper.ACCOUNT_ONE_NAME);
		a.setAccountNumber(TestDataHelper.ACCOUNT_ONE_NUM);
		a.setAccountType(TestDataHelper.ACCOUNT_ONE_TYPE);
		a.setCashOnAccount(100F);
		a.setCompany(TestDataHelper.ACCCOUNT_ONE_COMPANY);
		a.setOwner(TestDataHelper.ACCOUNT_ONE_OWNER);
		
		return a;
	}
	
	public static final Account getAccount2() {
		Account a = new Account();
		a.setAccountId(2L);
		a.setAccountName("Account 2");
		a.setAccountNumber("4321");
		a.setAccountType("IRA");
		a.setCashOnAccount(200F);
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
		a.setCashOnAccount(300F);
		a.setCompany("Company 3");
		a.setOwner("Owner 3");
		
		return a;
	}
	
	public static final List<Account> getAllAccountsList() {
		List<Account> accounts = new ArrayList<>();
		accounts.add(TestDataHelper.getAccount1());
		accounts.add(TestDataHelper.getAccount2());
		accounts.add(TestDataHelper.getAccount3());
		
		return accounts;
	}
	
	public static final Transaction getCashTransaction1() {
		Transaction t = new Transaction();
		t.setTransactionId(1L);
		t.setTradePrice(1F);
		t.setTradeQuantity(100F);
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Cash);
		
		return t;
	}
	
	public static final Transaction getCashTransaction2() {
		Transaction t = new Transaction();
		t.setTransactionId(2L);
		t.setTradePrice(1F);
		t.setTradeQuantity(100F);
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Cash);
		
		return t;
	}
	
	public static final Transaction getBuyTransaction1() {
		Transaction t = new Transaction();
		t.setTransactionId(3L);
		t.setTradePrice(1F);
		t.setTradeQuantity(100F);
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Buy);
		
		return t;
	}
	
	public static final Transaction getBuyTransaction2() {
		Transaction t = new Transaction();
		t.setTransactionId(4L);
		t.setTradePrice(17F);
		t.setTradeQuantity(99F);
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Buy);
		
		return t;
	}
	
	public static final Transaction getTransferTransaction1() {
		Transaction t = new Transaction();
		t.setTransactionId(5L);
		t.setTradePrice(1F);
		t.setTradeQuantity(100F);
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Transfer);
		
		return t;
	}
	
	public static final Transaction getSaleTransaction1() {
		Transaction t = new Transaction();
		t.setTransactionId(99L);
		t.setTradePrice(170F);
		t.setTradeQuantity(1F);
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Sell);
		
		return t;
	}
	
	public static final Transaction getSaleTransaction2() {
		Transaction t = new Transaction();
		t.setTransactionId(6L);
		t.setTradePrice(12F);
		t.setTradeQuantity(500F);
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Sell);
		
		return t;
	}
	
	public static final List<Transaction> getTransactionForAccount(Account a) {
		List<Transaction> transactions = new ArrayList<>();
		
		Transaction t1 = TestDataHelper.getBuyTransaction1();
		t1.setAccount(a);
		transactions.add(t1);
		
		Transaction t2 = TestDataHelper.getBuyTransaction2();
		t2.setAccount(a);
		transactions.add(t2);
		
		Transaction t3 = TestDataHelper.getSaleTransaction1();
		t3.setAccount(a);
		transactions.add(t3);
		
		Transaction t4 = TestDataHelper.getCashTransaction1();
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
		Investment i1 = TestDataHelper.getInvestmentAAPL();
		List<Quote> i1Quotes = new ArrayList<>();
		i1Quotes.add(TestDataHelper.getQuoteAAPL());
		i1.setQuotes(i1Quotes);
		allInvestments.add(i1);
		
		Investment i2 = TestDataHelper.getInvestmentFB();
		List<Quote> i2Quotes = new ArrayList<>();
		i2Quotes.add(TestDataHelper.getQuoteFB());
		i2.setQuotes(i2Quotes);
		allInvestments.add(i2);
		
		return allInvestments;
	}
	
	public static final Holding getHolding1() {
		
		Holding holding = new Holding();
		holding.setHoldingId(1L);
		holding.setPurchasePrice(10F);
		holding.setQuantity(10F);
		
		return holding;
	}
	
	public static final Holding getHolding2() {
		
		Holding holding = new Holding();
		holding.setHoldingId(2L);
		holding.setPurchasePrice(20F);
		holding.setQuantity(20F);
		
		return holding;
	}
	
	public static final Holding getHolding3() {
		
		Holding holding = new Holding();
		holding.setHoldingId(2L);
		holding.setPurchasePrice(200F);
		holding.setQuantity(200F);
		
		return holding;
	}
	
	public static final Holding getZeroQuantityHolding() {
		
		Holding holding = new Holding();
		holding.setHoldingId(3L);
		holding.setPurchasePrice(100F);
		holding.setQuantity(0F);
		
		return holding;
	}
	
	public static final List<Holding> getHoldingsForAccount(Account a) {
		List<Holding> holdings = new ArrayList<>();
		
		Holding h1 = TestDataHelper.getHolding1();
		h1.setAccount(a);
		holdings.add(h1);
		
		Holding h2 = TestDataHelper.getHolding2();
		h2.setAccount(a);
		holdings.add(h2);
		
		return holdings;
	}
	
	public static final Quote getQuoteAAPL() {
		
		Quote q = new Quote();
		q.setInvestment(getInvestmentAAPL());
		q.setQuoteId(1L);
		q.setClose(10F);
		q.setDate(new Date());
		q.setHigh(15F);
		q.setLow(8F);
		q.setOpen(9F);
		q.setPriceChange(.09F);
		q.setVolume(10000);
		
		return q;
	}
	
	public static final Quote getQuoteFB() {
		
		Quote q = new Quote();
		q.setInvestment(getInvestmentFB());
		q.setQuoteId(2L);
		q.setClose(20F);
		q.setDate(new Date());
		q.setHigh(25F);
		q.setLow(8F);
		q.setOpen(12F);
		q.setPriceChange(1.35F);
		q.setVolume(1000000);
		
		return q;
	}
}
