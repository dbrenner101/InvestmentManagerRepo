/**
 * 
 */
package com.brenner.investments.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

import com.brenner.investments.data.TransactionsRepository;
import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Transaction;
import com.brenner.investments.entities.constants.TransactionTypeEnum;
import com.brenner.investments.test.TestDataHelper;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest
@DirtiesContext
@TestInstance(Lifecycle.PER_METHOD)
public class TransactionsServiceTests {
    
    @MockBean
    TransactionsRepository transactionsRepo;
    
    @Autowired
    TransactionsService transactionsService;
    
    @Test
    public void testGetTotalDividendForInvestments_Success() throws Exception {
    	
    	Transaction t1 = TestDataHelper.getBuyTransaction1();
    	Transaction t2 = TestDataHelper.getSaleTransaction1();
    	
    	List<Transaction> transactions = new ArrayList<>(Arrays.asList(t1, t2));
    	
    	Mockito.when(this.transactionsRepo.findTotalDividendsForInvestments()).thenReturn(transactions);
    	
    	List<Transaction> results = this.transactionsService.getTotalDividendsForInvestments();
    	
    	assertNotNull(results);
    	assertEquals(2, results.size());
    	assertEquals(transactions, results);
    }
    
    @Test
    public void testGetTotalDividendForInvestment_Success() throws Exception {
    	
    	Investment i = TestDataHelper.getInvestmentAAPL();
    	Transaction t1 = TestDataHelper.getBuyTransaction1();
    	
    	Mockito.when(this.transactionsRepo.findTotalDividendsForInvestment(i.getInvestmentId())).thenReturn(t1);
    	
    	Transaction result = this.transactionsService.getTotalDividendsForInvestment(i.getInvestmentId());
    	
    	assertNotNull(result);
    	assertEquals(t1, result);
    }
    
    @Test
    public void testGetTotalDividendForInvestmentNullId_Fail() throws Exception {
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getTotalDividendsForInvestment(null);
    	});
    	
    	assertEquals("investmentId must be non-null", e.getMessage());
    }
    
    @Test
    public void testGetTotalDividendsForAllInvestments_Success() throws Exception {
    	
    	Transaction t1 = TestDataHelper.getBuyTransaction1();
    	Investment i1 = TestDataHelper.getInvestmentAAPL();
    	t1.setInvestment(i1);
    	
    	Transaction t2 = TestDataHelper.getSaleTransaction1();
    	Investment i2 = TestDataHelper.getInvestmentFB();
    	t2.setInvestment(i2);
    	
    	List<Transaction> transactions = new ArrayList<>(Arrays.asList(t1, t2));
    	
    	Mockito.when(this.transactionsRepo.findTotalDividendsForInvestments()).thenReturn(transactions);
    	
    	Map<Long, Transaction> dividends = this.transactionsService.getTotalDidivendsForAllInvestments();
    	
    	assertNotNull(dividends);
    	assertEquals(2, dividends.size());
    	assertEquals(t1, dividends.get(t1.getInvestment().getInvestmentId()));
    }
    
    @Test
    public void testGetAllTransactionsForAccountAndHolding_Success() throws Exception {
    	
    	Account a = TestDataHelper.getAccount1();
    	Holding h = TestDataHelper.getHolding1();
    	
    	Transaction t1 = TestDataHelper.getBuyTransaction1();
    	Transaction t2 = TestDataHelper.getSaleTransaction1();
    	
    	List<Transaction> transactions = new ArrayList<>(Arrays.asList(t1, t2));
    	
    	Mockito.when(this.transactionsRepo.findAllByAccountAccountIdAndHoldingHoldingId(a, h)).thenReturn(transactions);
    	
    	List<Transaction> results = this.transactionsService.getAllTransactionsForAccountAndHolding(a, h);
    	
    	assertNotNull(results);
    	assertEquals(transactions, results);
    }
    
    @Test
    public void testGetAllTransactionsForAccountAndHoldingNulls_Fail() throws Exception {
    	
    	Account a = TestDataHelper.getAccount1();
    	Holding h = TestDataHelper.getHolding1();
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getAllTransactionsForAccountAndHolding(null, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    	
    	e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getAllTransactionsForAccountAndHolding(a, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getAllTransactionsForAccountAndHolding(null, h);
    	});
    	
    	a.setAccountId(null);
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getAllTransactionsForAccountAndHolding(a, h);
    	});

    	a.setAccountId(1L);
    	h.setHoldingId(null);
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getAllTransactionsForAccountAndHolding(a, h);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    }
    
    @Test
    public void testGetAllTransactionsForAccountAndInvestment_Success() throws Exception {
    	Account a = TestDataHelper.getAccount1();
    	Investment i = TestDataHelper.getInvestmentGE();
    	
    	Transaction t1 = TestDataHelper.getBuyTransaction1();
    	Transaction t2 = TestDataHelper.getSaleTransaction1();
    	
    	List<Transaction> transactions = new ArrayList<>(Arrays.asList(t1, t2));
    	
    	Mockito.when(this.transactionsRepo.findAllByAccountAccountIdAndInvestmentInvestmentId(a, i)).thenReturn(transactions);
    	
    	List<Transaction> results = this.transactionsService.getAllTransactionsForAccountAndInvestment(a, i);
    	
    	assertNotNull(results);
    	assertEquals(transactions, results);
    }
    
    @Test
    public void testGetAllTransactionsForAccountAndInvestmentNullValues_Fail() throws Exception {
    	
    	Account a = TestDataHelper.getAccount1();
    	Investment i = TestDataHelper.getInvestmentGE();
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getAllTransactionsForAccountAndInvestment(null, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    	
    	e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getAllTransactionsForAccountAndInvestment(a, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getAllTransactionsForAccountAndInvestment(null, i);
    	});
    	
    	a.setAccountId(null);
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getAllTransactionsForAccountAndInvestment(a, i);
    	});

    	a.setAccountId(1L);
    	i.setInvestmentId(null);
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getAllTransactionsForAccountAndInvestment(a, i);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    }
    
    @Test
    public void testGetBuyTransactionsForHoldingId_Success() throws Exception {
    	
    	Holding h = TestDataHelper.getHolding1();
    	
    	Transaction t1 = TestDataHelper.getBuyTransaction1();
    	
    	Mockito.when(this.transactionsRepo.findBuyTransactionforHoldingId(h.getHoldingId())).thenReturn(t1);
    	
    	Transaction result = this.transactionsService.getBuyTransactionsForHoldingId(h.getHoldingId());
    	
    	assertNotNull(result);
    	assertEquals(t1, result);
    }
    
    @Test
    public void testGetBuyTransactionsForHoldingIdNullId_Fail() throws Exception {
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getBuyTransactionsForHoldingId(null);
    	});
    	
    	assertEquals("holdingId must be non-null", e.getMessage());
    }
    
    @Test
    public void testSaveTransaction_Success() throws Exception {
    	
    	Transaction t1 = TestDataHelper.getBuyTransaction1();
    	
    	Mockito.when(this.transactionsRepo.save(t1)).thenReturn(t1);
    	
    	Transaction result = this.transactionsService.saveTransaction(t1);
    	
    	assertNotNull(result);
    	assertEquals(t1, result);
    }
    
    @Test
    public void testSaveTransactionNull_Fail() throws Exception {
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.saveTransaction(null);
    	});
    	
    	assertEquals("transaction must be non-null", e.getMessage());
    }
    
    @Test
    public void testFindAllCashTransactionsForAccount_Success() throws Exception {
    	
    	Account a = TestDataHelper.getAccount1();
    	
    	Transaction t1 = TestDataHelper.getCashTransaction1();
    	Transaction t2 = TestDataHelper.getCashTransaction2();
    	
    	List<Transaction> transactions = new ArrayList<>(Arrays.asList(t1, t2));
    	
    	Mockito.when(this.transactionsRepo.findAllByAccountAccountIdWithCash(a.getAccountId())).thenReturn(transactions);
    	
    	List<Transaction> results = this.transactionsService.findAllCashTransactionsForAccount(a.getAccountId());
    	
    	assertNotNull(results);
    	assertEquals(transactions, results);
    }
    
    @Test
    public void testFindAllCashTransactionsForAccountNullId_Fail() throws Exception {
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.findAllCashTransactionsForAccount(null);
    	});
    	
    	assertEquals("accountId must be non-null", e.getMessage());
    }
    
    @Test
    public void testGetTradesForAccount_Success() throws Exception {
    	
    	Account a = TestDataHelper.getAccount3();

    	Transaction t1 = TestDataHelper.getBuyTransaction1();
    	Transaction t2 = TestDataHelper.getSaleTransaction1();
    	
    	List<Transaction> transactions = new ArrayList<>(Arrays.asList(t1, t2));
    	
    	Mockito.when(this.transactionsRepo.findAllByAccountAccountIdOrderByTransactionDateDesc(a)).thenReturn(transactions);
    	
    	List<Transaction> results = this.transactionsService.getTradesForAccount(a);
    	
    	assertNotNull(results);
    	assertEquals(transactions, results);
    }
    
    @Test
    public void testGetTradesForAccountNull_Fail() throws Exception {
    	
    	Account a = TestDataHelper.getAccount3();
    	a.setAccountId(null);
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getTradesForAccount(null);
    	});
    	
    	assertEquals("account and accountId must be non-null", e.getMessage());
    	
    	e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getTradesForAccount(a);
    	});
    	
    	assertEquals("account and accountId must be non-null", e.getMessage());
    }
    
    @Test
    public void testGetTransaction_Success() throws Exception {
    	
    	Transaction t2 = TestDataHelper.getSaleTransaction1();
    	Mockito.when(this.transactionsRepo.findById(t2.getTransactionId())).thenReturn(Optional.of(t2));
    	
    	Transaction result = this.transactionsService.getTransaction(t2.getTransactionId());
    	
    	assertNotNull(result);
    	assertEquals(t2, result);
    }
    
    @Test
    public void testGetTransactionNotFound_Success() throws Exception {
    	
    	Mockito.when(this.transactionsRepo.findById(10L)).thenReturn(Optional.empty());
    	
    	Transaction result = this.transactionsService.getTransaction(10L);
    	
    	assertNull(result);
    }
    
    @Test
    public void testGetTransactionNullId_Fail() throws Exception {
    	
    	Exception e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.getTransaction(null);
    	});
    	
    	assertEquals("transactionId must be non-null", e.getMessage());
    }
    
    @Test
    public void testTransferCash_Success() throws Exception {
    	
    	Date transferDate = new Date();
    	Float transferAmount = 5567.67F;
    	Long fromAccountId = 12345L;
    	Long toAccountId = 54321L;
    	
    	Transaction debitTransaction = new Transaction();
        debitTransaction.setAccount(new Account(fromAccountId));
        debitTransaction.setTradePrice(-1F);
        debitTransaction.setTradeQuantity(transferAmount);
        debitTransaction.setTransactionDate(transferDate);
        debitTransaction.setTransactionType(TransactionTypeEnum.Cash);
        
        Transaction creditTransaction = new Transaction();
        creditTransaction.setAccount(new Account(toAccountId));
        creditTransaction.setTradePrice(1F);
        creditTransaction.setTradeQuantity(transferAmount);
        creditTransaction.setTransactionDate(transferDate);
        creditTransaction.setTransactionType(TransactionTypeEnum.Cash);
    	
        Mockito.when(this.transactionsRepo.save(Mockito.any(Transaction.class))).thenAnswer(invocation -> {
        	Transaction t = (Transaction) invocation.getArgument(0);
    		
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
    		this.transactionsService.transferCash(null, null, null, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    	
    	e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.transferCash(new Date(), null, null, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.transferCash(null, 1F, null, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    	
    	e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.transferCash(null, null, 1L, null);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    	
    	e = assertThrows(InvalidRequestException.class, () -> {
    		this.transactionsService.transferCash(null, null, null, 10L);
    	});
    	
    	assertEquals("required attributes are null", e.getMessage());
    }

}
