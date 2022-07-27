/**
 * 
 */
package com.brenner.portfoliomgmt.service;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import com.brenner.portfoliomgmt.data.entities.AccountDTO;
import com.brenner.portfoliomgmt.data.repo.AccountsRepository;
import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.test.DomainTestData;
import com.brenner.portfoliomgmt.test.EntityTestData;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		AccountsRepository.class,
		AccountsService.class
})
@DirtiesContext
@TestInstance(Lifecycle.PER_METHOD)
public class AccountsServiceTests {

	@MockBean AccountsRepository accountsRepo;
	
	@Autowired AccountsService service;
	
	AccountDTO a1 = EntityTestData.getAccount1();
	AccountDTO a2 = EntityTestData.getAccount2();
	AccountDTO a3 = EntityTestData.getAccount3();
	
	@Test
	public void testFindAccountByAccountName_Success() throws Exception {
		
		Mockito.when(this.accountsRepo.findAccountByAccountName(a2.getAccountName())).thenReturn(Optional.of(a2));
		
		Optional<Account> optAccount = this.service.findAccountByAccountName(a2.getAccountName());
		assertTrue(optAccount.isPresent());
		evalAccountEquality(this.a2, optAccount.get());
	}
	
	@Test
	public void testFindAccountByAccountNameNotFound_Success() throws Exception {
		Mockito.when(this.accountsRepo.findAccountByAccountName(Mockito.anyString())).thenReturn(Optional.empty());
		
		Optional<Account> optAccount = this.service.findAccountByAccountName("foobar");
		assertTrue(optAccount.isEmpty());
	}
	
	@Test
	public void testFindAccountByAccountNumber_Success() throws Exception {
		
		Mockito.when(this.accountsRepo.findAccountByAccountNumber(a1.getAccountNumber())).thenReturn(Optional.of(a1));
		
		Optional<Account> optAccount = service.findAccountByAccountNumber(a1.getAccountNumber());
		assertTrue(optAccount.isPresent());
		evalAccountEquality(this.a1, optAccount.get());
	}
	
	@Test
	public void testFindAccountByNumberUnknownAccount_Success() throws Exception {
		
		Optional<Account> optAccount = service.findAccountByAccountNumber(a1.getAccountNumber());
		assertFalse(optAccount.isPresent());
	}
	
	@Test
	public void testFindAccountByNumberNullValue_Fail() throws Exception {

		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.findAccountByAccountNumber(null);
		});
		
		assertEquals("Account number must be non-null", e.getMessage());
	}
	
	@Test
	public void testDeleteAccount_Success() throws Exception {
		
		Mockito.when(this.accountsRepo.findById(a2.getAccountId())).thenReturn(Optional.of(a2));
		
		assertThatNoException().isThrownBy(() -> this.service.deleteAccount(DomainTestData.getAccount2()));
	}
	
	@Test
	public void testDeleteAccountNullAccount_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.deleteAccount(null);
		});
		
		assertEquals("Account must be non-null", e.getMessage());
	}
	
	@Test
	public void testDeleteAccountNotFound_Fail() throws Exception {
		
		Mockito.when(this.accountsRepo.findById(a2.getAccountId())).thenReturn(Optional.empty());
		
		Exception e = assertThrows(NotFoundException.class, () -> {
			this.service.deleteAccount(DomainTestData.getAccount2());
		});
		
		assertEquals("Account with accountId " + a2.getAccountId() + " does not exist.", e.getMessage());
	}
	
	@Test 
	public void testSaveAccount_Success() throws Exception {
		
		Mockito.when(this.accountsRepo.save(Mockito.any(AccountDTO.class))).thenReturn(a1);
		
		Account account = this.service.save(DomainTestData.getAccount1());
		evalAccountEquality(this.a1, account);
	}
	
	@Test
	public void testSaveAccountNullAccount_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.save(null);
		});
		
		assertEquals("Account must be non-null.", e.getMessage());
	}
	
	@Test
	public void testAddNewAccountNullAccount_Success() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.save(null);
		});
		
		assertEquals("Account must be non-null.", e.getMessage());
	}
	
	@Test
	public void testFindAllAccounts_Success() throws Exception {
		
		List<AccountDTO> accountDTOS = EntityTestData.getAllAccountsList();
		Mockito.when(this.accountsRepo.findAll()).thenReturn(accountDTOS);
		
		List<Account> accounts = this.service.findAllAccounts();
		
		int loopCount = 0;
		for (AccountDTO accountDTO : accountDTOS) {
			evalAccountEquality(accountDTO, accounts.get(loopCount));
			++loopCount;
		}
	}
	
	@Test
	public void testFindAllAccountsSort_Success() throws Exception {
		
		List<AccountDTO> accountDTOS = EntityTestData.getAllAccountsList();
		Mockito.when(this.accountsRepo.findAll(Sort.by(Sort.Direction.ASC, "accountName"))).thenReturn(accountDTOS);
		
		List<Account> accounts = this.service.findAllAccounts("accountName");
		
		int loopCount = 0;
		for (AccountDTO accountDTO : accountDTOS) {
			evalAccountEquality(accountDTO, accounts.get(loopCount));
			++loopCount;
		}
	}
	
	@Test
	public void testFindAllAccountsSortNullEmptyParam_Success() throws Exception {
		
		List<AccountDTO> accountDTOS = EntityTestData.getAllAccountsList();
		Mockito.when(this.accountsRepo.findAll(Sort.by(Sort.Direction.ASC, "accountName"))).thenReturn(accountDTOS);
		
		List<Account> accounts = this.service.findAllAccounts(" ");
		
		int loopCount = 0;
		for (AccountDTO accountDTO : accountDTOS) {
			evalAccountEquality(accountDTO, accounts.get(loopCount));
			++loopCount;
		}
	}
	
	@Test
	public void testFindAccountByAccountId_Success() throws Exception {
		
		Mockito.when(this.accountsRepo.findById(a3.getAccountId())).thenReturn(Optional.of(a3));
		
		Optional<Account> optAccount = this.service.findAccountByAccountId(a3.getAccountId());
		assertTrue(optAccount.isPresent());
		evalAccountEquality(this.a3, optAccount.get());
	}
	
	@Test
	public void testFindAccountByAccountIdNullId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.findAccountByAccountId(null);
		});
		
		assertEquals("Account id must be non-null.", e.getMessage());
	}
	
	@Test
	public void testFindAccountByAccountIdUnknownId_Success() throws Exception {
		
		Optional<Account> a = this.service.findAccountByAccountId(123L);
		assertFalse(a.isPresent());
	}
	
	
	private void evalAccountEquality(AccountDTO accountDTO, Account account) {
		assertNotNull(account);
		assertNotNull(accountDTO);
		assertEquals(accountDTO.getAccountNumber(), account.getAccountNumber());
		assertEquals(accountDTO.getAccountId(), account.getAccountId());
		assertEquals(accountDTO.getAccountName(), account.getAccountName());
		assertEquals(accountDTO.getAccountType(), account.getAccountType());
		assertEquals(accountDTO.getCompany(), account.getCompany());
		assertEquals(accountDTO.getOwner(), account.getOwner());
	}

}
