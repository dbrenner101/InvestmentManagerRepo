/**
 * 
 */
package com.brenner.portfoliomgmt.accounts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.test.TestDataHelper;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		AccountsService.class,
		AccountController.class
})
@AutoConfigureMockMvc
@DirtiesContext
public class AccountControllerTests {

	
	@MockBean AccountsService accountsService;
	
	@Autowired
	MockMvc mockMvc;
	
	Account a1 = TestDataHelper.getAccount1();
	Account a2 = TestDataHelper.getAccount2();
	Account a3 = TestDataHelper.getAccount3();
	
	@Test
	@WithMockUser
	public void testGetNewAccountForm_Success() throws Exception {
		
		List<Account> accounts = TestDataHelper.getAllAccountsList();
		Mockito.when(this.accountsService.getAccountsAndCash()).thenReturn(accounts);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/getNewAccountForm"))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts/addAccountForm"))
			.andExpect(model().attributeExists("accountsandcash"))
			.andExpect(model().attribute("accountsandcash", accounts));
	}
	
	@Test
	@WithMockUser
	public void testAddAccount_Success() throws Exception {
		
		Mockito.when(this.accountsService.addNewAccount(a2)).thenReturn(a2);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/addAccount")
				.flashAttr("account", a2)
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:getNewAccountForm"))
			.andExpect(model().attributeExists("account"))
			.andExpect(model().attribute("account", a2));
	}
	
	@Test
	@WithMockUser
	public void testPrepEditPage_Success() throws Exception {
		
		List<Account> allAccounts = TestDataHelper.getAllAccountsList();
		
		Mockito.when(this.accountsService.getAllAccounts()).thenReturn(allAccounts);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/editAccountPrep"))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts/chooseAccountToEdit"))
			.andExpect(model().attributeExists("accounts"))
			.andExpect(model().attribute("accounts", allAccounts));
	}
	
	@Test
	@WithMockUser
	public void testGetAccountDetailForEdit_Success() throws Exception {
		
		Mockito.when(this.accountsService.getAccountByAccountId(a3.getAccountId())).thenReturn(Optional.of(a3));
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/getAccountDetailsForEdit")
				.param("accountId", a3.getAccountId().toString()))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts/editAccountForm"))
			.andExpect(model().attributeExists("account"))
			.andExpect(model().attribute("account", a3));
	}
	
	@Test
	@WithMockUser
	public void testGetAccountDetailForEditNotFound_Fail() throws Exception {
		
		Mockito.when(this.accountsService.getAccountByAccountId(a3.getAccountId())).thenReturn(Optional.empty());
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/getAccountDetailsForEdit")
				.param("accountId", a3.getAccountId().toString()))
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
			.andExpect(result -> assertEquals(
					"Account with accountId " + a3.getAccountId() + " does not exist.", 
					result.getResolvedException().getMessage()));
	}
	
	@Test
	@WithMockUser
	public void testUpdateAccount_Success() throws Exception {
		
		Mockito.when(this.accountsService.save(a2)).thenReturn(a2);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/updateAccount")
				.flashAttr("account", a2)
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:editAccountPrep"))
			.andExpect(model().attributeExists("account"))
			.andExpect(model().attribute("account", a2));
	}

}
