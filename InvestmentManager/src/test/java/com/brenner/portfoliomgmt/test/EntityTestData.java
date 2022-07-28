package com.brenner.portfoliomgmt.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.brenner.portfoliomgmt.data.entities.AccountDTO;
import com.brenner.portfoliomgmt.data.entities.HoldingDTO;
import com.brenner.portfoliomgmt.data.entities.InvestmentDTO;
import com.brenner.portfoliomgmt.data.entities.QuoteDTO;
import com.brenner.portfoliomgmt.data.entities.TransactionDTO;
import com.brenner.portfoliomgmt.data.entities.WatchlistDTO;
import com.brenner.portfoliomgmt.domain.BucketEnum;
import com.brenner.portfoliomgmt.domain.InvestmentTypeEnum;
import com.brenner.portfoliomgmt.domain.TransactionTypeEnum;

public class EntityTestData {
	
	public static final String ACCOUNT_ONE_NAME = "Account 1";
	public static final String ACCOUNT_ONE_NUM = "1234";
	public static final String ACCOUNT_ONE_TYPE = "Investment";
	public static final String ACCCOUNT_ONE_COMPANY = "Company 1";
	public static final String ACCOUNT_ONE_OWNER = "Owner 1";
	
	private static final String ALPHABET = "QWERTYUIOPLKJHGFDSAZXCVBNM";
	
	public static List<AccountDTO> generateAccountsDTOList(int listSize) {
		List<AccountDTO> accounts = new ArrayList<>(listSize);
		for (int i=0; i<listSize; i++) {
			AccountDTO account = generateAccount(i, 4);
			accounts.add(account);
		}
		return accounts;
	}
	
	public static AccountDTO generateAccount(int sequence, int numHoldings) {
		AccountDTO account = new AccountDTO(Long.valueOf(sequence), "Account " + sequence, "Company " + sequence, 
				"Owner " + sequence, "AccountNum " + sequence, "AccountType " + sequence, 
				BigDecimal.valueOf(100*sequence));
		account.setHoldings(generateHoldingsList(numHoldings, account));
		
		return account;
	}
	
	public static List<TransactionDTO> generateTransactionsList(int listSize, HoldingDTO holding, AccountDTO account) {
		List<TransactionDTO> transactions = new ArrayList<>(listSize);
		for (int i=0; i<listSize; i++) {
			TransactionDTO t = generateTransaction(i, holding, account);
			transactions.add(t);
		}
		return transactions;
	}
	
	public static TransactionDTO generateTransaction(int sequence, HoldingDTO holding, AccountDTO account) {
		int rand = new Random().nextInt(0,  5);
		TransactionTypeEnum transactionType = TransactionTypeEnum.values()[rand];
		
		TransactionDTO transaction = new TransactionDTO(Long.valueOf(sequence), Long.valueOf(sequence*2), generateDate(sequence, new Date()), 
				BigDecimal.valueOf(287), BigDecimal.valueOf(125), transactionType, holding, account, 
				BigDecimal.valueOf(444));
		
		return transaction;
	}
	
	public static List<HoldingDTO> generateHoldingsList(int listSize, AccountDTO account) {
		List<HoldingDTO> holdings = new ArrayList<>(listSize);
		for (int i=0; i<listSize; i++) {
			HoldingDTO holding = generateHolding(i, account, generateInvestment(i, 3));
			holdings.add(holding);
		}
		return holdings;
	}

	public static HoldingDTO generateHolding(int sequence, AccountDTO account, InvestmentDTO inv) {
		HoldingDTO holding = new HoldingDTO(Long.valueOf(sequence), inv, account, BigDecimal.valueOf(100), BigDecimal.valueOf(15.55), BucketEnum.BUCKET_1, 
				generateDate(sequence, new Date()), BigDecimal.valueOf(534));
		holding.setTransactions(generateTransactionsList(3, holding, account));
		return holding;
	}
	public static List<InvestmentDTO> generateInvestmentList(int listSize, int quoteCount) {
		List<InvestmentDTO> investments = new ArrayList<>(listSize);
		for (int i=0; i<listSize; i++) {
			investments.add(generateInvestment(i, quoteCount));
		}
		
		return investments;
	}
	
	public static InvestmentDTO generateInvestment(int sequence, int quoteCount) {
		int rand = new Random().nextInt(0, 23);
		InvestmentDTO inv = new InvestmentDTO(Long.valueOf(sequence), ALPHABET.substring(rand, rand+3), "Company Name " + rand, "Exchange " + sequence, "Sector " + sequence,
				InvestmentTypeEnum.MutualFund);
		inv.setQuotes(generateQuotesList(quoteCount, inv));
		return inv;
	}
	
	public static List<QuoteDTO> generateQuotesList(int listSize, InvestmentDTO inv) {
		List<QuoteDTO> quotes = new ArrayList<>(listSize);
		for (int i=0; i<listSize; i++) {
			QuoteDTO q = generateQuote(i, inv);
			quotes.add(q);
		}
		return quotes;
	}
	
	public static QuoteDTO generateQuote(int sequence, InvestmentDTO i) {
		
		QuoteDTO q = new QuoteDTO(Long.valueOf(sequence), generateDate(sequence, new Date()), BigDecimal.valueOf(100), BigDecimal.valueOf(100.55), 
				BigDecimal.valueOf(200), BigDecimal.valueOf(50), 100000, null, null, null, i);
		
		return q;
	}
	
	private static Date generateDate(int sequence, Date startDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.MONTH, sequence);
		return cal.getTime();
	}
	
	public static final WatchlistDTO getWatchlistNoInvestments1() {
		
		WatchlistDTO list = new WatchlistDTO();
		list.setWatchlistName("No Investments List One");
		
		return list;
	}
	
	public static final WatchlistDTO getWatchlistNoInvestments2() {
		
		WatchlistDTO list = new WatchlistDTO();
		list.setWatchlistName("No Investments List Two");
		
		return list;
		
	}

	public static final AccountDTO getAccount1() {
		
		AccountDTO a = new AccountDTO();
		a.setAccountId(1L);
		a.setAccountName(EntityTestData.ACCOUNT_ONE_NAME);
		a.setAccountNumber(EntityTestData.ACCOUNT_ONE_NUM);
		a.setAccountType(EntityTestData.ACCOUNT_ONE_TYPE);
		a.setCashOnAccount(BigDecimal.valueOf(100));
		a.setCompany(EntityTestData.ACCCOUNT_ONE_COMPANY);
		a.setOwner(EntityTestData.ACCOUNT_ONE_OWNER);
		
		return a;
	}
	
	public static final AccountDTO getAccount2() {
		AccountDTO a = new AccountDTO();
		a.setAccountId(2L);
		a.setAccountName("Account 2");
		a.setAccountNumber("4321");
		a.setAccountType("IRA");
		a.setCashOnAccount(BigDecimal.valueOf(200));
		a.setCompany("Company 2");
		a.setOwner("Owner 2");
		
		return a;
	}
	
	public static final AccountDTO getAccount3() {
		AccountDTO a = new AccountDTO();
		a.setAccountId(3L);
		a.setAccountName("Account 3");
		a.setAccountNumber("5678");
		a.setAccountType("ROTH");
		a.setCashOnAccount(BigDecimal.valueOf(300));
		a.setCompany("Company 3");
		a.setOwner("Owner 3");
		
		return a;
	}
	
	public static final List<AccountDTO> getAllAccountsList() {
		List<AccountDTO> accounts = new ArrayList<>();
		accounts.add(EntityTestData.getAccount1());
		accounts.add(EntityTestData.getAccount2());
		accounts.add(EntityTestData.getAccount3());
		
		return accounts;
	}
	
	public static final TransactionDTO getCashTransaction1() {
		TransactionDTO t = new TransactionDTO(EntityTestData.getAccount1());
		t.setTransactionId(1L);
		t.setTradePrice(BigDecimal.valueOf(1));
		t.setTradeQuantity(BigDecimal.valueOf(100.5));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Cash);
		
		return t;
	}
	
	public static final TransactionDTO getCashTransaction2() {
		TransactionDTO t = new TransactionDTO(EntityTestData.getAccount2());
		t.setTransactionId(2L);
		t.setTradePrice(BigDecimal.valueOf(1));
		t.setTradeQuantity(BigDecimal.valueOf(100));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Cash);
		
		return t;
	}
	
	public static final TransactionDTO getBuyTransaction1() {
		TransactionDTO t = new TransactionDTO(EntityTestData.getHolding1());
		t.setTransactionId(3L);
		t.setTradePrice(BigDecimal.valueOf(1));
		t.setTradeQuantity(BigDecimal.valueOf(100));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Buy);
		
		return t;
	}
	
	public static final TransactionDTO getBuyTransaction2() {
		TransactionDTO t = new TransactionDTO(EntityTestData.getHolding2());
		t.setTransactionId(4L);
		t.setTradePrice(BigDecimal.valueOf(17));
		t.setTradeQuantity(BigDecimal.valueOf(99));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Buy);
		
		return t;
	}
	
	public static final TransactionDTO getTransferTransaction1() {
		TransactionDTO t = new TransactionDTO(EntityTestData.getAccount1());
		t.setTransactionId(5L);
		t.setTradePrice(BigDecimal.valueOf(1));
		t.setTradeQuantity(BigDecimal.valueOf(100));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Transfer);
		
		return t;
	}
	
	public static final TransactionDTO getSaleTransaction1() {
		TransactionDTO t = new TransactionDTO(EntityTestData.getHolding3());
		t.setTransactionId(99L);
		t.setTradePrice(BigDecimal.valueOf(170));
		t.setTradeQuantity(BigDecimal.valueOf(1));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Sell);
		
		return t;
	}
	
	public static final TransactionDTO getSaleTransaction2() {
		TransactionDTO t = new TransactionDTO(EntityTestData.getHolding2());
		t.setTransactionId(6L);
		t.setTradePrice(BigDecimal.valueOf(12));
		t.setTradeQuantity(BigDecimal.valueOf(500));
		t.setTransactionDate(new Date());
		t.setTransactionType(TransactionTypeEnum.Sell);
		
		return t;
	}
	
	public static final List<TransactionDTO> getTransactionForAccount(AccountDTO a) {
		List<TransactionDTO> transactions = new ArrayList<>();
		
		TransactionDTO t1 = EntityTestData.getBuyTransaction1();
		t1.setAccount(a);
		transactions.add(t1);
		
		TransactionDTO t2 = EntityTestData.getBuyTransaction2();
		t2.setAccount(a);
		transactions.add(t2);
		
		TransactionDTO t3 = EntityTestData.getSaleTransaction1();
		t3.setAccount(a);
		transactions.add(t3);
		
		TransactionDTO t4 = EntityTestData.getCashTransaction1();
		t4.setAccount(a);
		transactions.add(t4);
		
		return transactions;
	}
	
	public static final InvestmentDTO getInvestmentAAPL() {
		
		InvestmentDTO i = new InvestmentDTO();
		i.setInvestmentType(InvestmentTypeEnum.Stock);
		i.setInvestmentId(1L);
		i.setCompanyName("Apple");
		i.setSymbol("AAPL");
		i.setExchange("NASQAQ");
		i.setSector("Technology");
		
		return i;
	}
	
	public static final InvestmentDTO getInvestmentFB() {
		
		InvestmentDTO i = new InvestmentDTO();
		i.setInvestmentType(InvestmentTypeEnum.Stock);
		i.setInvestmentId(4L);
		i.setCompanyName("Facebook");
		i.setSymbol("FB");
		i.setExchange("NASQAQ");
		i.setSector("Technology");
		
		return i;
	}
	
	public static final InvestmentDTO getInvestmentGE() {

		InvestmentDTO i = new InvestmentDTO();
		i.setInvestmentType(InvestmentTypeEnum.Stock);
		i.setInvestmentId(2L);
		i.setCompanyName("GE");
		i.setSymbol("GE");
		i.setExchange("NYSE");
		i.setSector("Consumer Goods");
		return i;
	}
	
	public static final InvestmentDTO getInvestmentPVTL() {

		InvestmentDTO i = new InvestmentDTO();
		i.setInvestmentType(InvestmentTypeEnum.Stock);
		i.setInvestmentId(3L);
		i.setCompanyName("Pivotal");
		i.setSymbol("PVTL");
		i.setExchange("NASQAQ");
		i.setSector("Technology");
		
		return i;
	}
	
	public static final List<InvestmentDTO> getAllInvestments() {
		List<InvestmentDTO> allInvestments = new ArrayList<>();
		allInvestments.add(getInvestmentAAPL());
		allInvestments.add(getInvestmentFB());
		allInvestments.add(getInvestmentGE());
		allInvestments.add(getInvestmentPVTL());
		
		return allInvestments;
	}
	
	public static final List<InvestmentDTO> getAllInvestmentsAndQuotes() {
		List<InvestmentDTO> allInvestments = new ArrayList<>();
		InvestmentDTO i1 = EntityTestData.getInvestmentAAPL();
		List<QuoteDTO> i1Quotes = new ArrayList<>();
		i1Quotes.add(EntityTestData.getQuoteAAPL());
		i1.setQuotes(i1Quotes);
		allInvestments.add(i1);
		
		InvestmentDTO i2 = EntityTestData.getInvestmentFB();
		List<QuoteDTO> i2Quotes = new ArrayList<>();
		i2Quotes.add(EntityTestData.getQuoteFB());
		i2.setQuotes(i2Quotes);
		allInvestments.add(i2);
		
		return allInvestments;
	}
	
	public static final HoldingDTO getHolding1() {
		
		HoldingDTO holding = new HoldingDTO();
		holding.setHoldingId(1L);
		holding.setPurchasePrice(BigDecimal.valueOf(10));
		holding.setQuantity(BigDecimal.valueOf(10));
		
		return holding;
	}
	
	public static final HoldingDTO getHolding2() {
		
		HoldingDTO holding = new HoldingDTO();
		holding.setHoldingId(2L);
		holding.setPurchasePrice(BigDecimal.valueOf(20));
		holding.setQuantity(BigDecimal.valueOf(20));
		
		return holding;
	}
	
	public static final HoldingDTO getHolding3() {
		
		HoldingDTO holding = new HoldingDTO();
		holding.setHoldingId(2L);
		holding.setPurchasePrice(BigDecimal.valueOf(200));
		holding.setQuantity(BigDecimal.valueOf(200));
		
		return holding;
	}
	
	public static final HoldingDTO getZeroQuantityHolding() {
		
		HoldingDTO holding = new HoldingDTO();
		holding.setHoldingId(3L);
		holding.setPurchasePrice(BigDecimal.valueOf(100));
		holding.setQuantity(BigDecimal.valueOf(0));
		
		return holding;
	}
	
	public static final List<HoldingDTO> getHoldingsForAccount(AccountDTO a) {
		List<HoldingDTO> holdings = new ArrayList<>();
		
		HoldingDTO h1 = EntityTestData.getHolding1();
		h1.setAccount(a);
		holdings.add(h1);
		
		HoldingDTO h2 = EntityTestData.getHolding2();
		h2.setAccount(a);
		holdings.add(h2);
		
		return holdings;
	}
	
	public static final QuoteDTO getQuoteAAPL() {
		
		QuoteDTO q = new QuoteDTO();
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
	
	public static final QuoteDTO getQuoteFB() {
		
		QuoteDTO q = new QuoteDTO();
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
