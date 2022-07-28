/**
 * 
 */
package com.brenner.portfoliomgmt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import com.brenner.portfoliomgmt.data.entities.AccountDTO;
import com.brenner.portfoliomgmt.data.entities.HoldingDTO;
import com.brenner.portfoliomgmt.data.entities.InvestmentDTO;
import com.brenner.portfoliomgmt.data.entities.TransactionDTO;
import com.brenner.portfoliomgmt.data.repo.AccountsRepository;
import com.brenner.portfoliomgmt.data.repo.HoldingsRepository;
import com.brenner.portfoliomgmt.data.repo.InvestmentsRepository;
import com.brenner.portfoliomgmt.data.repo.QuotesRepository;
import com.brenner.portfoliomgmt.data.repo.TransactionsRepository;
import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.domain.Holding;
import com.brenner.portfoliomgmt.domain.Transaction;
import com.brenner.portfoliomgmt.domain.TransactionTypeEnum;
import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.test.DomainTestData;
import com.brenner.portfoliomgmt.test.EntityTestData;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		TransactionsRepository.class,
		HoldingsRepository.class,
		HoldingsService.class,
		InvestmentsRepository.class,
		AccountsRepository.class,
		QuotesRepository.class
	})
@DirtiesContext
@TestInstance(Lifecycle.PER_METHOD)
public class TransactionsServiceTests {
	
	@MockBean HoldingsRepository holdingsRepo;
	@MockBean TransactionsRepository transactionsRepo;
	@MockBean InvestmentsRepository investmentsRepository;
	@MockBean QuotesService quotesService;
	@MockBean AccountsRepository accountsRepo;
    
    @Autowired HoldingsService holdingsService;
    
    @Test
    public void testGetTotalDividendForInvestments_Success() throws Exception {
    	
    	TransactionDTO t1 = EntityTestData.getBuyTransaction1();
    	TransactionDTO t2 = EntityTestData.getSaleTransaction1();
    	
    	List<TransactionDTO> transactions = new ArrayList<>(Arrays.asList(t1, t2));
    	
    	Mockito.when(this.transactionsRepo.findTotalDividendsForHoldings()).thenReturn(transactions);
    	
    	List<Transaction> results = this.holdingsService.findTotalDividendsForHoldings();
    	
    	assertNotNull(results);
    	assertEquals(2, results.size());
    	assertEquals(transactions.size(), results.size());
    }
    
    @Test
    public void testGetTotalDividendForInvestment_Success() throws Exception {
    	
    	InvestmentDTO i = EntityTestData.getInvestmentAAPL();
    	TransactionDTO t1 = EntityTestData.getBuyTransaction1();
    	
    	Mockito.when(this.transactionsRepo.findTotalDividendsForHolding(i.getInvestmentId())).thenReturn(Optional.of(t1));
    	
    	Optional<Transaction> optTransaction = this.holdingsService.findTotalDividendsForHolding(i.getInvestmentId());
    	assertTrue(optTransaction.isPresent());
    	
    	Transaction result = optTransaction.get();
    	
    	assertNotNull(result);
    	assertEquals(t1.getTransactionId(), result.getTransactionId());
    }
    
    @Test
    public void testGetTotalDividendForHoldingNullId_Fail() throws Exception {
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findTotalDividendsForHolding(null);
    	});
    	
    	assertEquals("investmentId must be non-null", e.getMessage());
    }
    
    @Test
    public void testGetTotalDividendsForAllHoldings_Success() throws Exception {
    	
    	TransactionDTO t1 = EntityTestData.getBuyTransaction1();
    	
    	TransactionDTO t2 = EntityTestData.getSaleTransaction1();
    	
    	List<TransactionDTO> transactions = new ArrayList<>(Arrays.asList(t1, t2));
    	
    	Mockito.when(this.transactionsRepo.findTotalDividendsForHoldings()).thenReturn(transactions);
    	
    	Map<Long, Transaction> dividends = this.holdingsService.findTotalDidivendsForAllHoldings();
    	
    	assertNotNull(dividends);
    	assertEquals(2, dividends.size());
    	assertEquals(t1.getHolding().getHoldingId(), dividends.get(t1.getHolding().getHoldingId()).getHolding().getHoldingId());
    }
    
    @Test
    public void testGetAllTransactionsForAccountAndHolding_Success() throws Exception {
    	
    	Account account = DomainTestData.getAccount1();
    	Holding holding = DomainTestData.getHolding1();
    	
    	TransactionDTO t1 = EntityTestData.getBuyTransaction1();
    	TransactionDTO t2 = EntityTestData.getSaleTransaction1();
    	
    	List<TransactionDTO> transactions = new ArrayList<>(Arrays.asList(t1, t2));
    	
    	Mockito.when(this.transactionsRepo.findAllByAccountAndHolding(Mockito.any(AccountDTO.class), Mockito.any(HoldingDTO.class))).thenReturn(transactions);
    	
    	List<Transaction> results = this.holdingsService.findAllTransactionsForAccountAndHolding(account, holding);
    	
    	assertNotNull(results);
    	assertEquals(transactions.size(), results.size());
		assertEquals(transactions.get(0).getTransactionId(), results.get(0).getTransactionId());
    }
    
    @Test
    public void testGetAllTransactionsForAccountAndHoldingNulls_Fail() throws Exception {
    	
    	Account a = DomainTestData.getAccount1();
    	Holding h = DomainTestData.getHolding1();
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findAllTransactionsForAccountAndHolding(null, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    	
    	e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findAllTransactionsForAccountAndHolding(a, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findAllTransactionsForAccountAndHolding(null, h);
    	});
    	
    	a.setAccountId(null);
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findAllTransactionsForAccountAndHolding(a, h);
    	});

    	a.setAccountId(1L);
    	h.setHoldingId(null);
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findAllTransactionsForAccountAndHolding(a, h);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    }
    
    @Test
    public void testGetAllTransactionsForAccountAndInvestment_Success() throws Exception {
    	
    	Account account = DomainTestData.getAccount1();
    	Holding holding = DomainTestData.getHolding1();
    	
    	TransactionDTO t1 = EntityTestData.getBuyTransaction1();
    	TransactionDTO t2 = EntityTestData.getSaleTransaction1();
    	
    	List<TransactionDTO> transactions = new ArrayList<>(Arrays.asList(t1, t2));
    	
    	Mockito.when(this.transactionsRepo.findAllByAccountAndHolding(Mockito.any(AccountDTO.class), Mockito.any(HoldingDTO.class))).thenReturn(transactions);
    	
    	List<Transaction> results = this.holdingsService.findAllTransactionsForAccountAndHolding(account, holding);
    	
    	assertNotNull(results);
    	assertEquals(transactions.size(), results.size());
		assertEquals(transactions.get(0).getTransactionId(), results.get(0).getTransactionId());
    }
    
    @Test
    public void testGetAllTransactionsForAccountAndInvestmentNullValues_Fail() throws Exception {
    	
    	Account a = DomainTestData.getAccount1();
    	Holding h = DomainTestData.getHolding1();
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findAllTransactionsForAccountAndHolding(null, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    	
    	e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findAllTransactionsForAccountAndHolding(a, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findAllTransactionsForAccountAndHolding(null, h);
    	});
    	
    	a.setAccountId(null);
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findAllTransactionsForAccountAndHolding(a, h);
    	});

    	a.setAccountId(1L);
    	h.setHoldingId(null);
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findAllTransactionsForAccountAndHolding(a, h);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    }
    
    @Test
    public void testGetBuyTransactionsForHoldingId_Success() throws Exception {
    	
    	HoldingDTO h = EntityTestData.getHolding1();
    	
    	TransactionDTO t1 = EntityTestData.getBuyTransaction1();
    	
    	Mockito.when(this.transactionsRepo.findBuyTransactionforHoldingId(h.getHoldingId())).thenReturn(Optional.of(t1));
    	
    	Optional<Transaction> optTransaction = this.holdingsService.findBuyTransactionsForHoldingId(h.getHoldingId());
    	assertTrue(optTransaction.isPresent());
    	
    	Transaction result = optTransaction.get();
    	
    	assertNotNull(result);
    	assertEquals(t1.getTransactionId(), result.getTransactionId());
		assertEquals(t1.getTransactionDate(), result.getTransactionDate());
		assertEquals(t1.getTradePrice(), result.getTradePrice());
		assertEquals(t1.getTradeQuantity(), result.getTradeQuantity());
    }
    
    @Test
    public void testGetBuyTransactionsForHoldingIdNullId_Fail() throws Exception {
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findBuyTransactionsForHoldingId(null);
    	});
    	
    	assertEquals("holdingId must be non-null", e.getMessage());
    }
    
    @Test
    public void testSaveTransaction_Success() throws Exception {
    	
    	TransactionDTO t1 = EntityTestData.getBuyTransaction1();
    	Transaction transaction = DomainTestData.getBuyTransaction1();
    	
    	Mockito.when(this.transactionsRepo.save(Mockito.any(TransactionDTO.class))).thenReturn(t1);
    	
    	Transaction result = this.holdingsService.saveTransaction(transaction);
		
    	
    	assertNotNull(result);
    	assertEquals(t1.getTransactionId(), result.getTransactionId());
    }
    
    @Test
    public void testSaveTransactionNull_Fail() throws Exception {
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.saveTransaction(null);
    	});
    	
    	assertEquals("transaction must be non-null", e.getMessage());
    }
    
    @Test
    public void testFindAllCashTransactionsForAccount_Success() throws Exception {
    	
    	AccountDTO a = EntityTestData.getAccount1();
    	
    	TransactionDTO t1 = EntityTestData.getCashTransaction1();
    	TransactionDTO t2 = EntityTestData.getCashTransaction2();
    	
    	List<TransactionDTO> transactions = new ArrayList<>(Arrays.asList(t1, t2));
    	
    	Mockito.when(this.transactionsRepo.findAllByAccountAccountIdWithCash(a.getAccountId())).thenReturn(transactions);
    	
    	List<Transaction> results = this.holdingsService.findAllCashTransactionsForAccount(a.getAccountId());
    	
    	assertNotNull(results);
		assertEquals(transactions.size(), results.size());
		assertEquals(transactions.get(0).getTransactionId(), results.get(0).getTransactionId());
    }
    
    @Test
    public void testFindAllCashTransactionsForAccountNullId_Fail() throws Exception {
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findAllCashTransactionsForAccount(null);
    	});
    	
    	assertEquals("accountId must be non-null", e.getMessage());
    }
    
    @Test
    public void testGetTradesForAccount_Success() throws Exception {
    	
    	Account account = DomainTestData.getAccount3();

    	TransactionDTO t1 = EntityTestData.getBuyTransaction1();
    	TransactionDTO t2 = EntityTestData.getSaleTransaction1();
    	
    	List<TransactionDTO> transactions = new ArrayList<>(Arrays.asList(t1, t2));
    	
    	Mockito.when(this.transactionsRepo.findAllByAccountAccountIdOrderByTransactionDateDesc(Mockito.any(AccountDTO.class))).thenReturn(transactions);
    	
    	List<Transaction> results = this.holdingsService.findTradesForAccount(account);
    	
    	assertNotNull(results);
    	assertEquals(transactions.size(), results.size());
		assertEquals(transactions.get(0).getTransactionId(), results.get(0).getTransactionId());
    }
    
    @Test
    public void testGetTradesForAccountNull_Fail() throws Exception {
    	
    	Account a = DomainTestData.getAccount3();
    	a.setAccountId(null);
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findTradesForAccount(null);
    	});
    	
    	assertEquals("account and accountId must be non-null", e.getMessage());
    	
    	e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findTradesForAccount(a);
    	});
    	
    	assertEquals("account and accountId must be non-null", e.getMessage());
    }
    
    @Test
    public void testGetTransaction_Success() throws Exception {
    	
    	TransactionDTO transactionData = EntityTestData.getSaleTransaction1();
    	Transaction transaction = DomainTestData.getSaleTransaction1();
    	Mockito.when(this.transactionsRepo.findById(transactionData.getTransactionId())).thenReturn(Optional.of(transactionData));
    	
    	Optional<Transaction> optResult = this.holdingsService.findTransaction(transactionData.getTransactionId());
    	assertFalse(optResult.isEmpty());
	
		Transaction result = optResult.get();
		assertEquals(transaction.getTransactionId(), result.getTransactionId());
		assertEquals(transaction.getTradePrice(), result.getTradePrice());
    }
    
    @Test
    public void testGetTransactionNotFound_Success() throws Exception {
    	
    	Mockito.when(this.transactionsRepo.findById(10L)).thenReturn(Optional.empty());
    	
    	Optional<Transaction> optResult = this.holdingsService.findTransaction(10L);
    	
    	assertTrue(optResult.isEmpty());
    }
    
    @Test
    public void testGetTransactionNullId_Fail() throws Exception {
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.findTransaction(null);
    	});
    	
    	assertEquals("transactionId must be non-null", e.getMessage());
    }
    
    @Test
    public void testTransferCash_Success() throws Exception {
    	
    	Date transferDate = new Date();
    	BigDecimal transferAmount = BigDecimal.valueOf(5567.67);
    	Long fromAccountId = 12345L;
    	Long toAccountId = 54321L;
    	
    	TransactionDTO debitTransaction = new TransactionDTO(new AccountDTO(fromAccountId));
        debitTransaction.setTradePrice(BigDecimal.valueOf(-1));
        debitTransaction.setTradeQuantity(transferAmount);
        debitTransaction.setTransactionDate(transferDate);
        debitTransaction.setTransactionType(TransactionTypeEnum.Cash);
        
        TransactionDTO creditTransaction = new TransactionDTO(new AccountDTO(toAccountId));
        creditTransaction.setTradePrice(BigDecimal.valueOf(1));
        creditTransaction.setTradeQuantity(transferAmount);
        creditTransaction.setTransactionDate(transferDate);
        creditTransaction.setTransactionType(TransactionTypeEnum.Cash);
    	
        Mockito.when(this.transactionsRepo.save(Mockito.any(TransactionDTO.class))).thenAnswer(invocation -> {
        	TransactionDTO t = (TransactionDTO) invocation.getArgument(0);
    		
        	assertEquals(transferDate, t.getTransactionDate());
    		assertEquals(transferAmount, t.getTradeQuantity());
    		assertEquals(TransactionTypeEnum.Cash, t.getTransactionType());
        	
        	if (t.getAccount().getAccountId().equals(fromAccountId)) {
        		assertEquals(-1F, t.getTradePrice());
        	} 
        	else if (t.getAccount().getAccountId().equals(toAccountId)) {
        		assertEquals(1F, t.getTradePrice());
        	}
        	else {
        		assertFalse(true);
        	}
        	
        	return null;
        });
    }
    
    @Test
    public void testTransferCashNullValues_Fail() throws Exception {
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.transferCash(null, null, null, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    	
    	e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.transferCash(new Date(), null, null, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.transferCash(null, BigDecimal.valueOf(1), null, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    	
    	e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.transferCash(null, null, 1L, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    	
    	e = assertThrows(InvalidRequestException.class, () -> {
    		this.holdingsService.transferCash(null, null, null, 10L);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    }

}
