package com.brenner.investments.service.unit.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import com.brenner.investments.data.HoldingsDataService;
import com.brenner.investments.data.InvestmentsDataService;
import com.brenner.investments.data.TransactionsDataService;
import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.entities.Transaction;
import com.brenner.investments.quote.service.QuoteRetrievalService;
import com.brenner.investments.service.QuotesService;
import com.brenner.investments.service.implementation.PersistentTransactionsService;
import com.brenner.investments.test.TestDataHelper;

@ActiveProfiles("test")
public class TransactionsDataServiceTest {
	
	@Mock
	TransactionsDataService transactionsRepo;
	
	@Mock
	HoldingsDataService holdingsRepo;
	
	@Mock
	InvestmentsDataService investmentsRepo;
	
	@Mock
	QuoteRetrievalService quoteRetrievalService;
	
	@Mock
	QuotesService quotesService;
	
	PersistentTransactionsService transactionsService;
	
	@Before
	public void setup() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		this.transactionsService = new PersistentTransactionsService();
		this.transactionsService.setTransactionsRepo(this.transactionsRepo);
		this.transactionsService.setHoldingsRepo(this.holdingsRepo);
		this.transactionsService.setQuotesService(quotesService);
		this.transactionsService.setInvestmentRepo(this.investmentsRepo);
		this.transactionsService.setQuoteRetrievalService(this.quoteRetrievalService);
	}
	
	@Test
	public void testGetAllTransactionsForAccountAndHolding() throws Exception {
		
		Account a = TestDataHelper.getAccount1();
		Holding h = TestDataHelper.getHolding1();
		h.setAccount(a);
		
		Investment i1 = TestDataHelper.getInvestmentAAPL();
		
		Transaction t1 = TestDataHelper.getBuyTransaction1();
		t1.setAccount(a);
		t1.setInvestment(i1);
		t1.setHoldingId(h.getHoldingId());
		
		List<Transaction> retTrans = new ArrayList<>(2);
		retTrans.add(t1);
		
		when(this.transactionsRepo.findAllByAccountAccountIdAndHoldingHoldingId(a, h))
			.thenReturn(retTrans);
		
		List<Transaction> transactionList = this.transactionsService.getAllTransactionsForAccountAndHolding(a, h);
		
		assertNotNull(transactionList);
		assertFalse(transactionList.isEmpty());
		assertEquals(1, transactionList.size());
		
		Transaction t = transactionList.get(0);
		
		assertEquals(t.getAccount().getAccountId(), a.getAccountId());
		assertEquals(t.getHoldingId(), h.getHoldingId());
		assertEquals(t.getInvestment().getInvestmentId(), i1.getInvestmentId());
		assertEquals(t.getTradePrice(), t1.getTradePrice());
		assertEquals(t.getTradeQuantity(), t1.getTradeQuantity());
		assertEquals(t.getTransactionDate(), t1.getTransactionDate());
		assertEquals(t.getTransactionId(), t1.getTransactionId());
		assertEquals(t.getTransactionType(), t1.getTransactionType());
	}
	
	@Test
	public void testGetHoldingsForAccount() throws Exception {
		
		Account a = TestDataHelper.getAccount1();
		Holding h1 = TestDataHelper.getHolding1();
		h1.setAccount(a);
		h1.setInvestment(TestDataHelper.getInvestmentGE());
		List<Transaction> trans = new ArrayList<>();
		trans.add(TestDataHelper.getBuyTransaction1());
		trans.add(TestDataHelper.getCashTransaction1());
		h1.setTransactions(trans);
		
		Holding h2 = TestDataHelper.getHolding2();
		h2.setAccount(a);
		h2.setInvestment(TestDataHelper.getInvestmentAAPL());
		
		List<Holding> retHoldings = new ArrayList<>(2);
		retHoldings.add(h1);
		retHoldings.add(h2);
		
		when(this.holdingsRepo.findByAccountAccountId(a.getAccountId()))
				.thenReturn(retHoldings);
		
		List<Holding> holdings = this.transactionsService.getHoldingsForAccount(a.getAccountId());
		
		assertNotNull(holdings);
		assertFalse(holdings.isEmpty());
		assertEquals(2, holdings.size());
		
		Holding h = holdings.get(0);
		assertEquals(a.getAccountId(), h.getAccount().getAccountId());
		assertEquals(h.getInvestment().getInvestmentId(), TestDataHelper.getInvestmentGE().getInvestmentId());
		assertEquals(h.getQuantity(), TestDataHelper.getHolding1().getQuantity());
		
		List<Transaction> transactions = h.getTransactions();
		assertNotNull(transactions);
		assertFalse(transactions.isEmpty());
		assertEquals(2, transactions.size());
	}
	
	@Test
	public void testGetHoldingsForAccountAndQuantityGreaterThanOrderedBySymbol() throws Exception {
		
		Account a = TestDataHelper.getAccount1();
		Holding h1 = TestDataHelper.getHolding1();
		h1.setAccount(a);
		Investment i1 = TestDataHelper.getInvestmentGE();
		h1.setInvestment(i1);
		List<Transaction> trans = new ArrayList<>();
		trans.add(TestDataHelper.getBuyTransaction1());
		trans.add(TestDataHelper.getCashTransaction1());
		h1.setTransactions(trans);
		
		Holding h2 = TestDataHelper.getHolding2();
		h2.setAccount(a);
		Investment i2 = TestDataHelper.getInvestmentAAPL();
		h2.setInvestment(i2);
		
		Holding h3 = TestDataHelper.getHolding1();
		h3.setAccount(a);
		h3.setInvestment(h1.getInvestment());
		
		List<Holding> retHoldings = new ArrayList<>(2);
		retHoldings.add(h1);
		retHoldings.add(h2);
		retHoldings.add(h3);
		
		Date quoteDate = new Date();
		Quote q1 = TestDataHelper.getQuoteAAPL();
		
		when(this.holdingsRepo.findByAccountAccountId(a.getAccountId())).thenReturn(retHoldings);
		
		List<Holding> allHoldings = this.transactionsService.getHoldingsForAccount(a.getAccountId());
		
		assertNotNull(allHoldings);
		assertFalse(allHoldings.isEmpty());
		
		when(this.holdingsRepo.findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol(a.getAccountId()))
			.thenReturn(retHoldings);
		
		when(this.quotesService.findGreatestQuoteDateForSymbol(h1.getInvestment().getSymbol())).thenReturn(quoteDate);
		
		when(this.quotesService.getQuoteBySymbolAndDate(h1.getInvestment().getSymbol(), quoteDate))
			.thenReturn(q1);
		
		List<Holding> holdings = 
				this.transactionsService.getHoldingsForAccountAndQuantityGreaterThanOrderedBySymbol(
						a.getAccountId());
		
		assertNotNull(holdings);
		assertFalse(holdings.isEmpty());
		assertTrue(allHoldings.size() > holdings.size());
	}
	
	@Test
	public void testAddHoldingKnownInvestment() throws Exception {
		
		Investment i1 = TestDataHelper.getInvestmentPVTL();
		
		Quote quote = TestDataHelper.getQuoteAAPL();
		quote.setInvestment(i1);
		
		Transaction t1 = TestDataHelper.getBuyTransaction1();
		
		when(this.investmentsRepo.findBySymbol(i1.getSymbol())).thenReturn(i1);
		when(this.quoteRetrievalService.getQuote(i1.getSymbol())).thenReturn(quote);
		
		/*doAnswer((invocation) -> {
			return i1;
		}).when(this.investmentsRepo.save(Mockito.any(Investment.class)));*/
		
		Holding h1 = TestDataHelper.getHolding1();
		
		doAnswer((invocation) -> {
			Holding h = (Holding) invocation.getArgument(0);
			h.setHoldingId(1L);
			h1.setAccount(h.getAccount());
			h1.setInvestment(h.getInvestment());
			h1.setPurchasePrice(h.getPurchasePrice());
			h1.setQuantity(h.getQuantity());
			h1.setTransactions(h.getTransactions());
			return h1;
		}).when(this.holdingsRepo).save(Mockito.any((Holding.class)));
		
		/*doAnswer((invocation) -> {
			return t1;
		}).when(this.transactionsRepo.save(Mockito.any(Transaction.class)));*/
		
		this.transactionsService.addHolding(t1);
		
		assertNotNull(t1.getInvestment());
		assertEquals(i1.getSymbol(), t1.getInvestment().getSymbol());
		
		assertNotNull(h1);
		assertEquals(h1.getInvestment(), t1.getInvestment());
		assertEquals(h1.getQuantity(), t1.getTradeQuantity());
		assertEquals(h1.getAccount(), t1.getAccount());
	}
	
	@Test
	public void testAddHoldingNewInvestment() throws Exception {
		
		Investment i1 = TestDataHelper.getInvestmentPVTL();
		i1.setSymbol("ZZZ");
		
		Quote quote = TestDataHelper.getQuoteAAPL();
		quote.setInvestment(i1);
		
		Transaction t1 = TestDataHelper.getBuyTransaction1();
		
		when(this.investmentsRepo.findBySymbol(i1.getSymbol())).thenReturn(null);
		when(this.quoteRetrievalService.getQuote(i1.getSymbol())).thenReturn(quote);
		
		when(this.investmentsRepo.save(Mockito.any(Investment.class))).thenReturn(i1);
		
		Holding h1 = TestDataHelper.getHolding1();
		
		doAnswer((invocation) -> {
			Holding h = (Holding) invocation.getArgument(0);
			h.setHoldingId(1L);
			h1.setAccount(h.getAccount());
			h1.setInvestment(h.getInvestment());
			h1.setPurchasePrice(h.getPurchasePrice());
			h1.setQuantity(h.getQuantity());
			h1.setTransactions(h.getTransactions());
			return h1;
		}).when(this.holdingsRepo).save(Mockito.any((Holding.class)));
		
		this.transactionsService.addHolding(t1);
		
		assertNotNull(t1.getInvestment());
		assertEquals(i1.getSymbol(), t1.getInvestment().getSymbol());
		
		assertNotNull(h1);
		assertEquals(h1.getInvestment(), t1.getInvestment());
		assertEquals(h1.getQuantity(), t1.getTradeQuantity());
		assertEquals(h1.getAccount(), t1.getAccount());
		
		assertEquals(i1.getSymbol(), t1.getInvestment().getSymbol());
		assertEquals(quote.getInvestment().getSymbol(), i1.getSymbol());
		assertEquals(quote.getInvestment().getCompanyName(), i1.getCompanyName());
		assertEquals(quote.getInvestment().getInvestmentId(), i1.getInvestmentId());
	}
	
	@Test
	public void testGetHoldingByAccountAccountIdAndInvestmentInvestmentId() throws Exception {
		
		Account account = TestDataHelper.getAccount1();
		Investment i1 = TestDataHelper.getInvestmentAAPL();
		Holding h1 = TestDataHelper.getHolding1();
		h1.setInvestment(i1);
		h1.setAccount(account);
		Holding h2 = TestDataHelper.getHolding2();
		h2.setAccount(account);
		h2.setInvestment(i1);
		
		List<Holding> retHoldings = new ArrayList<>(2);
		retHoldings.add(h1);
		retHoldings.add(h2);
		
		when(this.holdingsRepo.findByAccountIdAndInvestmentId(account.getAccountId(), i1.getInvestmentId()))
			.thenReturn(retHoldings);
		
		List<Holding> holdings = this.transactionsService.getHoldingByAccountAccountIdAndInvestmentInvestmentId(account.getAccountId(), i1.getInvestmentId());
		
		assertNotNull(holdings);
		assertFalse(holdings.isEmpty());
		assertEquals(retHoldings.size(), holdings.size());
		
	}
	
	@Test
	public void testGetHoldingByHoldingId() throws Exception {
		
		Account a = TestDataHelper.getAccount1();
		Holding h = TestDataHelper.getHolding1();
		h.setAccount(a);
		
		Investment i1 = TestDataHelper.getInvestmentGE();
		h.setInvestment(i1);
		
		Transaction t1 = TestDataHelper.getBuyTransaction1();
		t1.setHoldingId(h.getHoldingId());
		t1.setAccount(a);
		t1.setInvestment(i1);
		Transaction t2 = TestDataHelper.getBuyTransaction2();
		t2.setHoldingId(h.getHoldingId());
		t2.setAccount(a);
		t2.setInvestment(i1);
		
		List<Transaction> retTransactions = new ArrayList<>(2);
		retTransactions.add(t1);
		retTransactions.add(t2);
		
		when(this.holdingsRepo.findById(Mockito.any(Long.class))).thenReturn(h);
		
		when(this.transactionsRepo.findAllByAccountAccountIdAndInvestmentInvestmentId(a, i1))
			.thenReturn(retTransactions);
		
		Holding holding = this.transactionsService.getHoldingByHoldingId(h.getHoldingId());
		
		assertNotNull(holding);
		assertEquals(holding.getInvestment().getSymbol(), i1.getSymbol());
		assertNotNull(holding.getTransactions());
		assertFalse(holding.getTransactions().isEmpty());
		assertEquals(2, holding.getTransactions().size());
	}
	
	@Test
	public void testGetHoldingByHoldingIdNoHoldingFound() throws Exception {
		
		when(this.holdingsRepo.findById(Mockito.any(Long.class))).thenReturn(null);
		
		Holding holding = this.transactionsService.getHoldingByHoldingId(1L);
		
		assertNull(holding);
	}
	
	@Test
	public void testUpdateHoldingAndTrade() throws Exception {
		
		Account a = TestDataHelper.getAccount1();
		Holding h = TestDataHelper.getHolding1();
		h.setAccount(a);
		Transaction t = TestDataHelper.getBuyTransaction1();
		t.setInvestment(h.getInvestment());
		t.setAccount(h.getAccount());
		
		when(this.holdingsRepo.save(Mockito.any(Holding.class))).thenReturn(h);
		
		this.transactionsService.updateHoldingAndTrade(h, t);
		
		assertEquals(h.getHoldingId(), t.getHoldingId());
	}

}
