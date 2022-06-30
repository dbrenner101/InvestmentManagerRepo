/**
 * 
 */
package com.brenner.portfoliomgmt.holdings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;

import com.brenner.portfoliomgmt.accounts.Account;
import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.quotes.Quote;
import com.brenner.portfoliomgmt.quotes.QuotesService;
import com.brenner.portfoliomgmt.test.TestDataHelper;
import com.brenner.portfoliomgmt.transactions.Transaction;
import com.brenner.portfoliomgmt.transactions.TransactionTypeEnum;
import com.brenner.portfoliomgmt.transactions.TransactionsRepository;
import com.brenner.portfoliomgmt.util.CommonUtils;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest
@DirtiesContext
@TestInstance(Lifecycle.PER_METHOD)
public class HoldingsServiceTests {
	
	@MockBean
	HoldingsRepository holdingsRepo;
	
	@MockBean
	TransactionsRepository transactionsRepo;
	
	@MockBean
	QuotesService quotesService;
	
	@Autowired
	HoldingsService holdingsService;
	
	@Test
	public void testAddHolding_Success() throws Exception {
		
		Transaction t = TestDataHelper.getBuyTransaction1();
		t.setTransactionType(null);
		Holding h = TestDataHelper.getHolding1();
		Investment i = TestDataHelper.getInvestmentAAPL();
		h.setInvestment(i);
		
		Mockito.when(this.holdingsRepo.save(h)).thenReturn(h);
		Mockito.when(this.transactionsRepo.save(t)).thenReturn(t);
		
		this.holdingsService.addHolding(t, h);
		assertEquals(TransactionTypeEnum.Buy, t.getTransactionType());
		assertEquals(i.getInvestmentId(), t.getInvestment().getInvestmentId());
		assertEquals(h.getHoldingId(), t.getHoldingId());
	}
	
	@Test
	public void testAddHoldingNullValues_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.addHolding(null, null);
		});
		
		assertEquals("Required attributes are null", e.getMessage());
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.addHolding(TestDataHelper.getBuyTransaction1(), TestDataHelper.getHolding1());
		});
		
		assertEquals("Required attributes are null", e.getMessage());
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.addHolding(null, TestDataHelper.getHolding1());
		});
		
		assertEquals("Required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.addHolding(TestDataHelper.getBuyTransaction1(), null);
		});
		
		assertEquals("Required attributes are null", e.getMessage());
	}
	
	@Test
	public void testSaveHolding_Success() throws Exception {
		
		Holding h = TestDataHelper.getHolding1();
		
		Mockito.when(this.holdingsRepo.save(h)).thenReturn(h);
		
		Holding holding = this.holdingsService.saveHolding(h);
		
		assertNotNull(holding);
		assertEquals(h, holding);
	}
	
	@Test
	public void testSaveHoldingNullHolding_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.saveHolding(null);
		});
		
		assertEquals("Holding must not be null", e.getMessage());
	}
	
	@Test
	public void testGetHoldingsForAccount_Success() throws Exception {
		
		Account a = TestDataHelper.getAccount1();
		List<Holding> returnVal = TestDataHelper.getHoldingsForAccount(a);
		Mockito.when(this.holdingsRepo.findByAccountAccountId(a.getAccountId())).thenReturn(returnVal);
		
		List<Holding> holdings = this.holdingsService.getHoldingsForAccount(a.getAccountId());
		
		assertNotNull(holdings);
		assertEquals(returnVal, holdings);
	}
	
	@Test
	public void testGetHoldingsForAccountNullAccountId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.getHoldingsForAccount(null);
		});
		
		assertEquals("accountId must be non-null", e.getMessage());
	}
	
	@Test
	public void testGetHoldingsForAccountAndQuantityGreaterThanOrderedBySymbol_Success() throws Exception {
		
		Account a = TestDataHelper.getAccount2();
		List<Holding> holdings = TestDataHelper.getHoldingsForAccount(a);
		assertEquals(2, holdings.size());
		
		holdings.get(0).setInvestment(TestDataHelper.getInvestmentAAPL());
		holdings.get(1).setInvestment(TestDataHelper.getInvestmentFB());
		
		Date latestQuoteDate = CommonUtils.convertCommonDateFormatStringToDate("02/20/2021");
		Quote q = TestDataHelper.getQuoteAAPL();
		
		Mockito.when(this.holdingsRepo.findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol(a.getAccountId())).thenReturn(holdings);
		Mockito.when(this.quotesService.findGreatestQuoteDateForSymbol(Mockito.anyString())).thenReturn(Optional.of(latestQuoteDate));
		Mockito.when(this.quotesService.getQuoteBySymbolAndDate(Mockito.anyString(), Mockito.any(Date.class))).thenReturn(Optional.of(q));
		
		List<Holding> holdingList = this.holdingsService.getHoldingsForAccountAndQuantityGreaterThanOrderedBySymbol(a.getAccountId());
		
		assertNotNull(holdingList);
		assertEquals(2, holdingList.size());
		assertNotNull(holdingList.get(0).getQuotes());
		assertNotNull(holdingList.get(1).getQuotes());
	}
	
	@Test
	public void testGetHoldingsForAccountAndQuantityGreaterThanOrderedBySymbol_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.getHoldingsForAccountAndQuantityGreaterThanOrderedBySymbol(null);
		});
		
		assertEquals("accountId must be non-null", e.getMessage());
	}
	
	@Test
	public void testSellHolding_Success() throws Exception {
		
		Account a = TestDataHelper.getAccount1();
		Investment i = TestDataHelper.getInvestmentAAPL();
		Holding h = TestDataHelper.getHolding1();
		h.setAccount(a);
		h.setInvestment(i);
		
		Long transactionId = 1L;
		Date saleDate = new Date();
		Float tradeQuantity = 10F;
		Float tradePrice = 25F;
		
		Transaction existingTransaction = TestDataHelper.getBuyTransaction1();
		existingTransaction.setAccount(a);
		existingTransaction.setHolding(h);
		existingTransaction.setInvestment(i);
		existingTransaction.setTransactionId(transactionId);
		existingTransaction.setHoldingId(h.getHoldingId());
		
		Mockito.when(this.transactionsRepo.findById(existingTransaction.getTransactionId())).thenReturn(Optional.of(existingTransaction));
		Mockito.when(this.holdingsRepo.findById(h.getHoldingId())).thenReturn(Optional.of(h));
		
		Mockito.when(this.transactionsRepo.save(Mockito.any(Transaction.class))).thenAnswer(invocation ->  {
			Object[] args = invocation.getArguments();
			Transaction t = (Transaction) args[0];
			if (t.getTransactionType().equals(TransactionTypeEnum.Sell)) {
				assertEquals(a, t.getAccount());
	        	assertEquals(h.getHoldingId(), t.getHoldingId());
	        	assertEquals(i, t.getInvestment());
	        	assertEquals(tradePrice, t.getTradePrice());
	        	assertEquals(tradeQuantity, t.getTradeQuantity());
	        	assertEquals(saleDate, t.getTransactionDate());
	        	assertNotNull(t.getAssociatedCashTransactionId());
	        }
	        else if (t.getTransactionType().equals(TransactionTypeEnum.Cash)) {
	        	t.setTransactionId(2L);
	        	assertEquals(a, t.getAccount());
	        	assertEquals(tradePrice, t.getTradePrice());
	        	assertEquals(tradeQuantity, t.getTradeQuantity());
	        	assertEquals(saleDate, t.getTransactionDate());
	        }
	        return t;
		});
		
		this.holdingsService.sellHolding(transactionId, saleDate, tradeQuantity, tradePrice);
		
		assertEquals(0, h.getQuantity());
	}
	
	@Test
	public void testSellHoldingNulls_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.sellHolding(null, null, null, null);
		});
		
		assertEquals("Required attributes are null", e.getMessage());
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.sellHolding(1L, null, null, null);
		});
		
		assertEquals("Required attributes are null", e.getMessage());
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.sellHolding(1L, new Date(), null, null);
		});
		
		assertEquals("Required attributes are null", e.getMessage());
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.sellHolding(1L, new Date(), 1F, null);
		});
		
		assertEquals("Required attributes are null", e.getMessage());
	}
	
	@Test
	public void testGetHoldingByAccountAccountIdAndInvestmentInvestmentId_Success() throws Exception {
		
		Account a = TestDataHelper.getAccount1();
		Investment i = TestDataHelper.getInvestmentAAPL();
		List<Holding> holdings = TestDataHelper.getHoldingsForAccount(a);
		
		Mockito.when(this.holdingsRepo.findByAccountAccountIdAndInvestmentInvestmentId(a.getAccountId(), i.getInvestmentId())).thenReturn(holdings);
		
		List<Holding> returnedList = this.holdingsService.getHoldingByAccountAccountIdAndInvestmentInvestmentId(a.getAccountId(), i.getInvestmentId());
		
		assertNotNull(returnedList);
		assertEquals(holdings, returnedList);
	}
	
	@Test
	public void testGetHoldingByAccountAccountIdAndInvestmentInvestmentIdNulls_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.getHoldingByAccountAccountIdAndInvestmentInvestmentId(null, null);
		});
		
		assertEquals("accountId and investmentId must be non-null", e.getMessage());
	}
	
	@Test
	public void testGetHoldingByHoldingId_Success() throws Exception {
		
		Holding h = TestDataHelper.getHolding2();
		h.setAccount(TestDataHelper.getAccount3());
		h.setInvestment(TestDataHelper.getInvestmentPVTL());
		
		List<Transaction> transactions = new ArrayList<>(Arrays.asList(
				TestDataHelper.getBuyTransaction1(), 
				TestDataHelper.getBuyTransaction2(), 
				TestDataHelper.getCashTransaction1()));
		
		Mockito.when(this.holdingsRepo.findById(h.getHoldingId())).thenReturn(Optional.of(h));
		Mockito.when(this.transactionsRepo.findAllByAccountAndInvestment(h.getAccount(), h.getInvestment())).thenReturn(transactions);
		
		Optional<Holding> optHolding = this.holdingsService.getHoldingByHoldingId(h.getHoldingId());
		assertTrue(optHolding.isPresent());
		
		Holding holding = optHolding.get();
		
		assertNotNull(holding);
		assertNotNull(holding.getTransactions());
		assertEquals(transactions.size(), holding.getTransactions().size());
	}
	
	@Test
	public void testGetHoldingByHoldingIdNullId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.getHoldingByHoldingId(null);
		});
		
		assertEquals("holding id must not be null", e.getMessage());
	}
	
	@Test
	public void testGetHoldingByHoldingIdNotFound_Success() throws Exception {
		
		Mockito.when(this.holdingsService.getHoldingByHoldingId(1L)).thenReturn(Optional.empty());
		
		Optional<Holding> optHolding =  this.holdingsService.getHoldingByHoldingId(1L);
		
		assertFalse(optHolding.isPresent());
	}
	
	@Test
	public void testGetHoldingByHoldingIdWithBuyTransactions_Success() throws Exception {
		
		Holding h = TestDataHelper.getHolding2();
		h.setAccount(TestDataHelper.getAccount3());
		h.setInvestment(TestDataHelper.getInvestmentPVTL());
		
		Transaction transaction = TestDataHelper.getBuyTransaction1();
		
		Mockito.when(this.holdingsRepo.findById(h.getHoldingId())).thenReturn(Optional.of(h));
		Mockito.when(this.transactionsRepo.findBuyTransactionforHoldingId(h.getHoldingId())).thenReturn(Optional.of(transaction));
		
		Optional<Holding> optHolding = this.holdingsService.getHoldingByHoldingIdWithBuyTransaction(h.getHoldingId());
		assertTrue(optHolding.isPresent());
		
		Holding holding = optHolding.get();
		
		assertNotNull(holding);
		assertNotNull(holding.getTransactions());
		assertEquals(1, holding.getTransactions().size());
		assertEquals(TransactionTypeEnum.Buy, holding.getTransactions().get(0).getTransactionType());
	}
	
	@Test
	public void testGetHoldingByHoldingIdWithBuyTransactionsNullId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.getHoldingByHoldingIdWithBuyTransaction(null);
		});
		
		assertEquals("holding id must not be null", e.getMessage());
	}
	
	@Test
	public void testUpdateHoldingAndTrade_Success() throws Exception {
		
		Holding h = TestDataHelper.getHolding1();
		Transaction t = TestDataHelper.getBuyTransaction1();
		
		Mockito.when(this.holdingsRepo.save(h)).thenReturn(h);
		Mockito.when(this.transactionsRepo.save(t)).thenReturn(t);
		
		assertNull(t.getHoldingId());
		
		this.holdingsService.updateHoldingAndTrade(h, t);
		
		assertEquals(h.getHoldingId(), t.getHoldingId());
	}
	
	@Test
	public void testUpdateHoldingAndTradeNullId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.updateHoldingAndTrade(null, null);
		});
		
		assertEquals("holding and trade must be non-null", e.getMessage());
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.updateHoldingAndTrade(new Holding(), null);
		});
		
		assertEquals("holding and trade must be non-null", e.getMessage());
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.updateHoldingAndTrade(null, new Transaction());
		});
		
		assertEquals("holding and trade must be non-null", e.getMessage());
	}
	
	@Test
	public void testGetAllHoldings_Success() throws Exception {
		
		List<Holding> holdings = TestDataHelper.getHoldingsForAccount(TestDataHelper.getAccount1());
		
		Mockito.when(this.holdingsRepo.findAll()).thenReturn(holdings);
		
		List<Holding> values = this.holdingsService.getAllHoldings();
		
		assertNotNull(values);
		assertEquals(holdings.size(), values.size());
	}
	
	@Test
	public void testGetAllHoldingsOrderedBySymbol_Success() throws Exception {
		
		List<Holding> holdings = TestDataHelper.getHoldingsForAccount(TestDataHelper.getAccount1());
		
		Mockito.when(this.holdingsRepo.findAll(Sort.by(Sort.Direction.ASC, "investment.symbol"))).thenReturn(holdings);
		
		List<Holding> values = this.holdingsService.getAllHoldingsOrderedBySymbol();
		
		assertNotNull(values);
		assertEquals(holdings.size(), values.size());
	}
	
	@Test
	public void testGetHoldingsByInvesmentId_Success() throws Exception {
		
		Investment i = TestDataHelper.getInvestmentAAPL();
		Account a = TestDataHelper.getAccount1();
		List<Holding> holdings = TestDataHelper.getHoldingsForAccount(a);
		
		Mockito.when(this.holdingsRepo.findHoldingsByInvestmentInvestmentId(i.getInvestmentId())).thenReturn(holdings);
		
		List<Holding> values = this.holdingsService.getHoldingsByInvestmentId(i.getInvestmentId());
		
		assertNotNull(values);
		assertEquals(holdings.size(), values.size());
	}
	
	@Test
	public void testGetHoldingsByInvesmentIdNullId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.getHoldingsByInvestmentId(null);
		});
		
		assertEquals("investmentId must be non-null", e.getMessage());
	}
	
	@Test
	public void testPersistBuy_Success() throws Exception {
		Date tradeDate = new Date(); 
		Float price = 12.25F;
		Float quantity = 100F;
		Long investmentId = 1L;
		Long accountId = 1L;
		
		Account a = new Account(accountId);
		Investment i = new Investment(investmentId);
		
		Holding h = new Holding();
		h.setAccount(a);
		h.setInvestment(i);
		h.setHoldingId(12L);
		
		Mockito.when(this.holdingsRepo.save(Mockito.any(Holding.class))).thenReturn(h);
		Mockito.when(this.transactionsRepo.save(Mockito.any(Transaction.class))).thenAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			Transaction t = (Transaction) args[0];
			if (t.getTransactionType().equals(TransactionTypeEnum.Cash)) {
				t.setTransactionId(1L);
				assertEquals(a, t.getAccount());
				assertEquals(-1F, t.getTradePrice());
				assertEquals(price * quantity, t.getTradeQuantity());
				assertEquals(tradeDate, t.getTransactionDate());
			}
			else {
				assertEquals(tradeDate, t.getTransactionDate());
				assertEquals(price, t.getTradePrice());
				assertEquals(quantity, t.getTradeQuantity());
				assertEquals(i, t.getInvestment());
				assertEquals(a, t.getAccount());
				assertNotNull(t.getAssociatedCashTransactionId());
				assertEquals(1L, t.getTransactionId());
			}
			return t;
		});
	}
	
	@Test
	public void testPersistBuyNulls_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.persistBuy(null, null, null, null, null);
		});
		
		assertEquals("required attributes are null", e.getMessage());
	}

}
