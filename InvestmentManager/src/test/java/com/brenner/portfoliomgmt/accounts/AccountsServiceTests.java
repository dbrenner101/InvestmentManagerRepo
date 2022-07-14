/**
 * 
 */
package com.brenner.portfoliomgmt.accounts;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;

import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.holdings.HoldingsRepository;
import com.brenner.portfoliomgmt.test.TestDataHelper;
import com.brenner.portfoliomgmt.transactions.Transaction;
import com.brenner.portfoliomgmt.transactions.TransactionsRepository;
import com.brenner.portfoliomgmt.transactions.TransactionsService;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		AccountsRepository.class,
		TransactionsService.class,
		TransactionsRepository.class,
		AccountsService.class,
		HoldingsRepository.class
})
@DirtiesContext
@TestInstance(Lifecycle.PER_METHOD)
public class AccountsServiceTests {

	@MockBean AccountsRepository accountsRepo;
	@MockBean HoldingsRepository holdingsRepo;
	@MockBean TransactionsRepository transactionsRepo;
	
	@Autowired AccountsService service;
	
	Account a1 = TestDataHelper.getAccount1();
	Account a2 = TestDataHelper.getAccount2();
	Account a3 = TestDataHelper.getAccount3();
	
	@Test
	public void testGetAccountByAccountNumber_Success() throws Exception {
		
		Mockito.when(this.accountsRepo.findAccountByAccountNumber(a1.getAccountNumber())).thenReturn(Optional.of(a1));
		
		Optional<Account> optAccount = service.getAccountByAccountNumber(a1.getAccountNumber());
		assertTrue(optAccount.isPresent());
		
		Account result = optAccount.get();
		assertNotNull(result);
		assertEquals(a1.getAccountNumber(), result.getAccountNumber());
		assertEquals(a1.getAccountId(), result.getAccountId());
	}
	
	@Test
	public void testGetAccountNumberUnknownAccount_Success() throws Exception {
		
		Optional<Account> optAccount = service.getAccountByAccountNumber(a1.getAccountNumber());
		
		assertFalse(optAccount.isPresent());
	}
	
	@Test
	public void testGetAccountNumberNullValue_Fail() throws Exception {

		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.getAccountByAccountNumber(null);
		});
		
		assertEquals("account number must be non-null", e.getMessage());
	}
	
	@Test
	public void testDeleteAccount_Success() throws Exception {
		
		Mockito.when(this.accountsRepo.findById(a2.getAccountId())).thenReturn(Optional.of(a2));
		
		assertThatNoException().isThrownBy(() -> this.service.deleteAccount(a2));
	}
	
	@Test
	public void testDeleteAccountNullAccount_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.deleteAccount(null);
		});
		
		assertEquals("account must be non-null", e.getMessage());
	}
	
	@Test
	public void testDeleteAccountNotFound_Fail() throws Exception {
		
		Mockito.when(this.accountsRepo.findById(a2.getAccountId())).thenReturn(Optional.empty());
		
		Exception e = assertThrows(NotFoundException.class, () -> {
			this.service.deleteAccount(a2);
		});
		
		assertEquals("Account with accountId " + a2.getAccountId() + " does not exist.", e.getMessage());
	}
	
	@Test 
	public void testSaveAccount_Success() throws Exception {
		
		Mockito.when(this.accountsRepo.save(a1)).thenReturn(a1);
		
		Account account = this.service.save(a1);
		assertNotNull(account);
		assertEquals(a1, account);
	}
	
	@Test
	public void testSaveAccountNullAccount_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.save(null);
		});
		
		assertEquals("Account must be non-null.", e.getMessage());
	}
	
	@Test
	public void testAddNewAccount_Success() throws Exception {
		
		Account newAccount = new Account();
		BeanUtils.copyProperties(a1, newAccount);
		
		Transaction cashTrans1 = TestDataHelper.getCashTransaction1();
		cashTrans1.setAccount(newAccount);
		Transaction cashTrans2 = TestDataHelper.getCashTransaction2();
		cashTrans2.setAccount(newAccount);
		List<Transaction> cashTransactions = new ArrayList<>(Arrays.asList(cashTrans1, cashTrans2));
		
		
		BigDecimal totalCashOnAccount = 
				(cashTrans1.getTradeQuantity().multiply(cashTrans1.getTradePrice()))
					.add( 
				(cashTrans2.getTradeQuantity().multiply(cashTrans2.getTradePrice()))
				);
		
		Mockito.when(this.accountsRepo.save(newAccount)).thenReturn(newAccount);
		Mockito.when(this.transactionsRepo.findAllByAccountAccountIdWithCash(newAccount.getAccountId())).thenReturn(cashTransactions);
		
		Account savedAccount = this.service.addNewAccount(newAccount);
		assertNotNull(savedAccount);
		assertEquals(newAccount.getAccountId(), savedAccount.getAccountId());
		assertEquals(totalCashOnAccount, savedAccount.getCashOnAccount());
	}
	
	@Test
	public void testAddNewAccountNoCash_Success() throws Exception {
		
		Mockito.when(this.accountsRepo.save(a1)).thenReturn(a1);
		
		Account savedAccount = this.service.save(a1);
		assertNotNull(savedAccount);
		assertEquals(savedAccount.getAccountId(), a1.getAccountId());
	}
	
	@Test
	public void testAddNewAccountNullAccount_Success() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.addNewAccount(null);
		});
		
		assertEquals("Account must be non-null.", e.getMessage());
	}
	
	@Test
	public void testGetAllAccounts() throws Exception {
		
		Mockito.when(this.accountsRepo.findAll()).thenReturn(TestDataHelper.getAllAccountsList());
		
		List<Account> accounts = this.service.getAllAccounts();
		
		assertNotNull(accounts);
		assertEquals(TestDataHelper.getAllAccountsList().size(), accounts.size());
	}
	
	@Test
	public void testGetAccountByAccountId_Success() throws Exception {
		
		Mockito.when(this.accountsRepo.findById(a3.getAccountId())).thenReturn(Optional.of(a3));
		
		Optional<Account> optAccount = this.service.getAccountByAccountId(a3.getAccountId());
		
		assertTrue(optAccount.isPresent());
		
		Account a = optAccount.get();
		assertNotNull(a);
		assertEquals(a3.getAccountId(), a.getAccountId());
		assertEquals(a3, a);
	}
	
	@Test
	public void testGetAccountByAccountIdNullId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.getAccountByAccountId(null);
		});
		
		assertEquals("account id must be non-null.", e.getMessage());
	}
	
	@Test
	public void testGetAccountByAccountIdUnknownId_Success() throws Exception {
		
		Optional<Account> a = this.service.getAccountByAccountId(123L);
		assertFalse(a.isPresent());
	}
	
	@Test
	public void testGetAccountAndCash_Success() throws Exception {
		
		Account newAccount = new Account();
		BeanUtils.copyProperties(a1, newAccount);
		
		Transaction cashTrans1 = TestDataHelper.getCashTransaction1();
		cashTrans1.setAccount(newAccount);
		Transaction cashTrans2 = TestDataHelper.getCashTransaction2();
		cashTrans2.setAccount(newAccount);
		List<Transaction> cashTransactions = new ArrayList<>(Arrays.asList(cashTrans1, cashTrans2));
		
		BigDecimal totalCashOnAccount = 
				(cashTrans1.getTradeQuantity().multiply(cashTrans1.getTradePrice()))
				.add(
				(cashTrans2.getTradeQuantity().multiply(cashTrans2.getTradePrice()))
				);
		
		Mockito.when(this.accountsRepo.findById(newAccount.getAccountId())).thenReturn(Optional.of(newAccount));
		Mockito.when(this.transactionsRepo.findAllByAccountAccountIdWithCash(newAccount.getAccountId())).thenReturn(cashTransactions);
		
		Optional<Account> optAccount = this.service.getAccountAndCash(newAccount.getAccountId());
		assertTrue(optAccount.isPresent());
		
		Account savedAccount = optAccount.get();
		assertNotNull(savedAccount);
		assertEquals(newAccount.getAccountId(), savedAccount.getAccountId());
		assertEquals(totalCashOnAccount, savedAccount.getCashOnAccount());
	}
	
	@Test
	public void testGetAccountAndCashNullAccountId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.getAccountAndCash(null);
		});
		
		assertEquals("accountId must be non-null.", e.getMessage());
	}
	
	@Test
	public void testGetAllAccountsOrderByAccountNameAsc_Success() throws Exception {
		
		List<Account> accounts = TestDataHelper.getAllAccountsList();
		PropertyComparator<Account> comparator = new PropertyComparator<>("AccountName", false, false);
		Collections.sort(accounts, comparator);
		
		Mockito.when(this.accountsRepo.findAll(Sort.by(Sort.Direction.ASC, "accountName"))).thenReturn(accounts);
		
		List<Account> sortedAccounts = this.service.getAllAccountsOrderByAccountNameAsc();
		
		assertNotNull(sortedAccounts);
		assertEquals(accounts.size(), sortedAccounts.size());
		assertEquals(3, sortedAccounts.size());
		assertEquals(accounts.get(0), sortedAccounts.get(0)); 
		assertEquals(accounts.get(1), sortedAccounts.get(1));
		assertEquals(accounts.get(2), sortedAccounts.get(2));
	}

}
