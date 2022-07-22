/**
 * 
 */
package com.brenner.portfoliomgmt.api;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.brenner.portfoliomgmt.data.repo.AccountsRepository;
import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.service.AccountsService;
import com.brenner.portfoliomgmt.test.DomainTestData;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for methods on the Accounts REST API
 * 
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		AccountsRestController.class,
		AccountsService.class,
		AccountsRepository.class
})
@AutoConfigureMockMvc
@EnableWebMvc
public class AccountsRestControllerTests {
	
	@Autowired MockMvc mockMvc;
	
	@MockBean AccountsService accountsService;
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Test @WithMockUser
	public void testAddAccount_Success() throws Exception {
		
		Account account = DomainTestData.getAccount1();
		Mockito.when(this.accountsService.save(account)).thenReturn(account);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/api/account")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(account))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", notNullValue()))
            .andExpect(jsonPath("$.accountName", is(account.getAccountName())));
	}
	
	@Test @WithMockUser
	public void testDeleteAccount_Success() throws Exception {
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.delete("/api/account/99")
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()))
			.andExpect(status().isOk());
	}
	
	@Test @WithMockUser
	public void testGetAccount_Success() throws Exception {
		
		Account account = DomainTestData.getAccount2();
		Mockito.when(this.accountsService.findAccountByAccountId(account.getAccountId())).thenReturn(Optional.of(account));
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/api/account/" + account.getAccountId())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", notNullValue()))
			.andExpect(jsonPath("$.accountName", is(account.getAccountName())));
	}
	
	@Test @WithMockUser
	public void testGetAccountNotFound_Fail() throws Exception {
		
		Mockito.when(this.accountsService.findAccountByAccountId(Mockito.anyLong())).thenReturn(Optional.empty());
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/api/account/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}
	
	@Test @WithMockUser
	public void testGetAccounList_Success() throws Exception {
		
		List<Account> accounts = DomainTestData.getAllAccountsList();
		Mockito.when(this.accountsService.findAllAccounts(Mockito.anyString())).thenReturn(accounts);
		
	    this.mockMvc.perform(MockMvcRequestBuilders
	    		.get("/api/account")
	    		.contentType(MediaType.APPLICATION_JSON))
	    	.andExpect(status().isOk())
	    	.andExpect(jsonPath("$", notNullValue()))
	    	.andExpect(jsonPath("$", hasSize(accounts.size())))
	    	.andExpect(jsonPath("$[0].accountName", is(accounts.get(0).getAccountName())));
	    	
	}
	
	@Test @WithMockUser
	public void testUpdateAccount_Success() throws Exception {
		
		Account account = DomainTestData.getAccount3();
		Mockito.when(this.accountsService.save(account)).thenReturn(account);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.put("/api/account/" + account.getAccountId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(account))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", notNullValue()))
			.andExpect(jsonPath("$.accountName", is(account.getAccountName())));
	}

}
