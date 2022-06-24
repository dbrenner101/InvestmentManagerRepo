package com.brenner.investments.service.unit.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import com.brenner.investments.data.AccountDataService;
import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Transaction;
import com.brenner.investments.service.implementation.PersistentAccountsService;
import com.brenner.investments.service.implementation.PersistentTransactionsService;
import com.brenner.investments.test.TestDataHelper;

@ActiveProfiles("test")
public class AccountsDataServiceTest {
	
	
	PersistentAccountsService accountService;
	
	@Mock
	AccountDataService accountRepo;
	
	@Mock
	PersistentTransactionsService transactionsService;
	
	@Before
	public void setup() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		this.accountService = new PersistentAccountsService();
		this.accountService.setAccountRepo(this.accountRepo);
		this.accountService.setTransactionsService(this.transactionsService);
	}
	
	@Test
	public void testGetAllAccounts() throws Exception {
		
		Account a1 = TestDataHelper.getAccount1();
		Account a2 = TestDataHelper.getAccount2();
		Account a3 = TestDataHelper.getAccount3();
		
		List<Account> accountsList = new ArrayList<>(3);
		accountsList.add(a1);
		accountsList.add(a2);
		accountsList.add(a3);
		
		when(this.accountRepo.findAll()).thenReturn(accountsList);;
		
		Iterable<Account> accounts = this.accountService.getAllAccounts();
		assertNotNull(accounts);
		
		Iterator<Account> iter = accounts.iterator();
		List<Account> accountsListReturn = new ArrayList<>();
		while (iter.hasNext()) {
			accountsListReturn.add(iter.next());
		}
		
		assertEquals(3, accountsList.size());
	}
	
	@Test
	public void testGetAccountByAccountId() throws Exception {
		
		Account a1 = TestDataHelper.getAccount1();
		a1.setAccountId(1L);
		
		when(this.accountRepo.findById(1L)).thenReturn(a1);
		
		Account a = this.accountService.getAccountByAccountId(a1.getAccountId());
		assertNotNull(a);
		assertEquals(a1.getAccountId(), a.getAccountId());
		assertEquals(a1.getCompany(), a.getCompany());
		assertEquals(a1.getAccountName(), a.getAccountName());
		assertEquals(a1.getOwner(), a.getOwner());
		assertEquals(a1.getAccountNumber(), a.getAccountNumber());
		assertEquals(a1.getAccountType(), a.getAccountType());
	}
	
	@Test
	public void testGetAccountsAndCash() throws Exception {
		
		Account a1 = TestDataHelper.getAccount1();
		Account a2 = TestDataHelper.getAccount2();
		Account a3 = TestDataHelper.getAccount3();
		
		Transaction t1 = TestDataHelper.getCashTransaction1();
		t1.setAccount(a1);
		
		Transaction t2 = TestDataHelper.getCashTransaction2();
		t2.setAccount(a1);
		
		List<Account> accountsList = new ArrayList<>();
		accountsList.add(a1);
		accountsList.add(a2);
		accountsList.add(a3);
		
		List<Transaction> t1List = new ArrayList<>();
		t1List.add(t1);
		t1List.add(t2);
		
		when(this.accountRepo.findAll()).thenReturn(accountsList);
		when(this.transactionsService.findAllCashTransactionsForAccount(a1.getAccountId())).thenReturn(t1List);
		when(this.transactionsService.findAllCashTransactionsForAccount(a2.getAccountId())).thenReturn(null);
		when(this.transactionsService.findAllCashTransactionsForAccount(a3.getAccountId())).thenReturn(null);
		
		Float totalCash = t1.getTradeQuantity() + t2.getTradeQuantity();
		
		List<Account> accountsAndCash = this.accountService.getAccountsAndCash();
		
		assertNotNull(accountsAndCash);
		assertFalse(accountsAndCash.isEmpty());
		assertEquals(3, accountsAndCash.size());
		
		boolean foundCash = false;
		
		Iterator<Account> iter = accountsAndCash.iterator();
		while(iter.hasNext()) {
			Account a = iter.next();
			
			if (a.getCashOnAccount() != null && a.getAccountId() == 1 && a.getCashOnAccount() > 0) {
				foundCash = true;
				assertEquals(totalCash, a.getCashOnAccount());
				
			}
		}
		
		assertTrue(foundCash);
	}
	
	@Test
	public void testGetAllAccountsOrderByAccountNameAsc() throws Exception {
		
		Account a1 = TestDataHelper.getAccount1();
		Account a2 = TestDataHelper.getAccount2();
		Account a3 = TestDataHelper.getAccount3();
		
		List<Account> accountsForMock = new ArrayList<>(3);
		accountsForMock.add(a1);
		accountsForMock.add(a2);
		accountsForMock.add(a3);
		
		when(this.accountRepo.findAllByOrderByAccountNameAsc()).thenReturn(accountsForMock);
		
		
		List<Account> allAccounts = this.accountService.getAllAccountsOrderByAccountNameAsc();
		assertNotNull(allAccounts);
		assertFalse(allAccounts.isEmpty());
		assertTrue(allAccounts.size() == 3);
		
		Account a = allAccounts.get(0);
		assertEquals(a1.getCompany(), a.getCompany());
		assertEquals(a1.getAccountName(), a.getAccountName());
		assertEquals(a1.getAccountNumber(), a.getAccountNumber());
		assertEquals(a1.getOwner(), a.getOwner());
		assertEquals(a1.getAccountType(), a.getAccountType());
	}

}
