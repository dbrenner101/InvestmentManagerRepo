package com.brenner.investments.controller.integration.data.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.brenner.investments.data.AccountDataService;
import com.brenner.investments.entities.Account;
import com.brenner.investments.test.TestDataHelper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles("test")
public class AccountDataServiceTests {

	@Autowired
	AccountDataService accountDataService;
	
	Account account1;
	Account account2;
	
	@Before
	public void setUp() throws Exception {
		
		this.account1 = TestDataHelper.getAccount1();
		this.account1.setAccountId(null);
		this.account2 = TestDataHelper.getAccount2();
		this.account2.setAccountId(null);
	}
	
	private int persistAccounts() throws Exception {
		
		this.account1 = this.accountDataService.save(this.account1);
		this.account2 = this.accountDataService.save(this.account2);
		
		return 2;
	}
	
	private void removeData() throws Exception {
		
		List<Account> accounts = this.accountDataService.findAll();
		Iterator<Account> iter = accounts.iterator();
		while(iter.hasNext()) {
			this.accountDataService.delete(iter.next().getAccountId());
		}
	}
	
	@Test
	public void testSaveNewAccount() throws Exception {
		
		persistAccounts();
		
		assertNotNull(this.account1.getAccountId());
		assertNotNull(this.account2.getAccountId());
		
		Account a1 = TestDataHelper.getAccount1();
		Account a2 = TestDataHelper.getAccount2();
		
		assertEquals(a1.getAccountName(), this.account1.getAccountName());
		assertEquals(a1.getAccountNumber(), this.account1.getAccountNumber());
		assertEquals(a1.getAccountType(), this.account1.getAccountType());
		assertEquals(a1.getCompany(), this.account1.getCompany());
		assertEquals(a1.getOwner(), this.account1.getOwner());
		
		assertEquals(a2.getAccountName(), this.account2.getAccountName());
		assertEquals(a2.getAccountNumber(), this.account2.getAccountNumber());
		assertEquals(a2.getAccountType(), this.account2.getAccountType());
		assertEquals(a2.getCompany(), this.account2.getCompany());
		assertEquals(a2.getOwner(), this.account2.getOwner());
		
		removeData();
	}
	
	@Test
	public void testUpdateAccount() throws Exception {
		
		final String NEW_NAME = "New Account Name";
		final String NEW_NUMBER = "New Account Number";
		final String NEW_COMPANY = "New Company";
		final String NEW_OWNER = "New Owner";
		
		persistAccounts();
		
		this.account1.setAccountName(NEW_NAME);
		this.account1.setAccountNumber(NEW_NUMBER);
		this.account1.setCompany(NEW_COMPANY);
		this.account1.setOwner(NEW_OWNER);
		
		Account savedAccount = this.accountDataService.save(this.account1);
		
		assertNotNull(savedAccount);
		assertEquals(NEW_NAME, savedAccount.getAccountName());
		assertEquals(NEW_NUMBER, savedAccount.getAccountNumber());
		assertEquals(NEW_COMPANY, savedAccount.getCompany());
		assertEquals(NEW_OWNER, savedAccount.getOwner());
		assertEquals(this.account1.getAccountId(), savedAccount.getAccountId());
		
		removeData();
	}
	
	@Test
	public void testFindAll() throws Exception {
		
		int numAccounts = persistAccounts();
		
		List<Account> accounts = this.accountDataService.findAll();
		
		assertNotNull(accounts);
		assertEquals(numAccounts, accounts.size());
		
		removeData();
	}
	
	@Test
	public void testFindById() throws Exception {
		
		persistAccounts();
		
		Account a1 = this.accountDataService.findById(this.account1.getAccountId());
		
		assertNotNull(a1);
		
		assertEquals(a1.getAccountName(), this.account1.getAccountName());
		assertEquals(a1.getAccountNumber(), this.account1.getAccountNumber());
		assertEquals(a1.getAccountType(), this.account1.getAccountType());
		assertEquals(a1.getCompany(), this.account1.getCompany());
		assertEquals(a1.getOwner(), this.account1.getOwner());
		
		removeData();
	}
	
	@Test
	public void testFindAllByOrderByAccountNameAsc() throws Exception {
		
		int numAccounts = persistAccounts();
		
		List<Account> accounts = this.accountDataService.findAllByOrderByAccountNameAsc();
		
		assertNotNull(accounts);
		assertEquals(numAccounts, accounts.size());
		
		Account a1 = accounts.get(0);
		
		assertEquals(a1.getAccountName(), this.account1.getAccountName());
		assertEquals(a1.getAccountNumber(), this.account1.getAccountNumber());
		assertEquals(a1.getAccountType(), this.account1.getAccountType());
		assertEquals(a1.getCompany(), this.account1.getCompany());
		assertEquals(a1.getOwner(), this.account1.getOwner());
		
		Account a2 = accounts.get(1);
		
		assertEquals(a2.getAccountName(), this.account2.getAccountName());
		assertEquals(a2.getAccountNumber(), this.account2.getAccountNumber());
		assertEquals(a2.getAccountType(), this.account2.getAccountType());
		assertEquals(a2.getCompany(), this.account2.getCompany());
		assertEquals(a2.getOwner(), this.account2.getOwner());
		
		removeData();
	}
	
	@Test
	public void testFindByAccountNumber() throws Exception {
		
		persistAccounts();
		
		Account a2 = this.accountDataService.findByAccountNumber(this.account2.getAccountNumber());
		
		assertNotNull(a2);
		assertEquals(a2.getAccountName(), this.account2.getAccountName());
		assertEquals(a2.getAccountNumber(), this.account2.getAccountNumber());
		assertEquals(a2.getAccountType(), this.account2.getAccountType());
		assertEquals(a2.getCompany(), this.account2.getCompany());
		assertEquals(a2.getOwner(), this.account2.getOwner());
		
		removeData();
	}
	
	@Test
	public void testDelete() throws Exception {
		
		int numAccounts = persistAccounts();
		
		this.accountDataService.delete(this.account2.getAccountId());
		
		List<Account> accounts = this.accountDataService.findAll();
		
		Account a = accounts.get(0);
		
		assertEquals(numAccounts -1, accounts.size());
		assertNotEquals(a.getAccountId(), this.account2.getAccountId());
		assertNotEquals(a.getAccountName(), this.account2.getAccountName());
		assertNotEquals(a.getAccountNumber(), this.account2.getAccountNumber());
		assertNotEquals(a.getCompany(), this.account2.getCompany());
		
		removeData();
	}
}
