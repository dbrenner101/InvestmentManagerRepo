package com.brenner.investments.controller.unit.test;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import com.brenner.investments.InvestmentsProperties;
import com.brenner.investments.controller.AccountController;
import com.brenner.investments.entities.Account;
import com.brenner.investments.service.AccountsService;
import com.brenner.investments.test.TestDataHelper;

@ActiveProfiles("test")
public class AccountControllerTests {

    private MockMvc mockMvc;
    
    @InjectMocks
    AccountController accountController;
    
    @Mock
    AccountsService accountsService;
    
    @Mock
    InvestmentsProperties props;
    
    @Mock Model model;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	    this.mockMvc = MockMvcBuilders
                .standaloneSetup(accountController)
                .build();
	}
	
	@Test
	public void testGetNewAccountForm() throws Exception{
		
		Account a1 = TestDataHelper.getAccount1();
		a1.setCashOnAccount(200F);
		Account a2 = TestDataHelper.getAccount2();
		a2.setCashOnAccount(100F);
		Account a3 = TestDataHelper.getAccount3();
		a3.setCashOnAccount(0F);
		
		List<Account> accounts = new ArrayList<>();
		accounts.add(a1);
		accounts.add(a2);
		accounts.add(a3);
		
		when(this.accountsService.getAccountsAndCash()).thenReturn(accounts);
		
		when(this.props.getAccountsAndCashAttributeKey()).thenReturn("accounts");
		
	    this.mockMvc.perform(get("/getNewAccountForm"))
	    	.andExpect(
	    			status().isOk())
	    	.andExpect(
	    			view().name("accounts/addAccountForm"))
	    	.andExpect(
	    			model().attribute("accounts", hasSize(accounts.size())))
	    	.andExpect(
	    			model().attribute("accounts", hasItem(
	    					allOf(
	    							hasProperty("accountId", is(a1.getAccountId())),
	    							hasProperty("cashOnAccount", is(a1.getCashOnAccount()))
	    						)
	    	)))
	    	.andExpect(
	    			model().attribute("accounts", hasItem(
	    					allOf(
	    							hasProperty("accountId", is(a2.getAccountId())),
	    							hasProperty("cashOnAccount", is(a2.getCashOnAccount()))
	    	))))
	    	.andExpect(
	    			model().attribute("accounts", hasItem(
	    					allOf(
	    							hasProperty("accountId", is(a3.getAccountId())),
	    							hasProperty("cashOnAccount", is(a3.getCashOnAccount()))
	    	))));
	}
	
	@Test
	public void testAddAccount() throws Exception {
		
		Account a1 = TestDataHelper.getAccount1();
		
		when(this.accountsService.addNewAccount(Mockito.any(Account.class)))
			.thenReturn(a1);
		
		when(this.props.getAccountAttributeKey()).thenReturn("account");
		
		this.mockMvc.perform(post("/addAccount")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("accountName", a1.getAccountName())
                .param("company", a1.getCompany())
                .param("accountNumber", a1.getAccountNumber())
                .param("owner", a1.getOwner())
                .param("type", a1.getAccountType()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("getNewAccountForm"))
			.andExpect(
					model().attribute("account", is(a1))
			);
	}
	
	@Test
	public void testEditAccountPrep() throws Exception {
		
		Account a1 = TestDataHelper.getAccount1();
		a1.setCashOnAccount(200F);
		Account a2 = TestDataHelper.getAccount2();
		a2.setCashOnAccount(100F);
		Account a3 = TestDataHelper.getAccount3();
		a3.setCashOnAccount(0F);
		
		List<Account> accounts = new ArrayList<>();
		accounts.add(a1);
		accounts.add(a2);
		accounts.add(a3);
		
		when(this.accountsService.getAllAccounts()).thenReturn(accounts);
		when(this.props.getAccountsListAttributeKey()).thenReturn("accounts");
		
		this.mockMvc.perform(get("/editAccountPrep"))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts/chooseAccountToEdit"));
	}
	
	@Test
	public void testGetAccountDetailsForEdit() throws Exception {
		
		Account a = TestDataHelper.getAccount1();
		
		when(this.props.getAccountAttributeKey()).thenReturn("account");
		when(this.accountsService.getAccountByAccountId(anyLong())).thenReturn(a);
		
		this.mockMvc.perform(post("/getAccountDetailsForEdit")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("accountId", a.getAccountId().toString()))
		.andExpect(status().isOk())
		.andExpect(view().name("accounts/editAccountForm"))
		.andExpect(model().attribute("account", is(a)));
	}
	
	@Test
	public void testUpdateAccount() throws Exception {
		
		when(this.props.getAccountAttributeKey()).thenReturn("account");
		when(this.accountsService.updateAccount(Mockito.any(Account.class)))
			.thenReturn(TestDataHelper.getAccount1());
		
		this.mockMvc.perform(post("/updateAccount")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("accountId", "1")
                .param("accountName", "Name")
                .param("company", "company")
                .param("accountNumber", "number")
                .param("owner", "owner")
                .param("type", "type"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("editAccountPrep"));
	}
}