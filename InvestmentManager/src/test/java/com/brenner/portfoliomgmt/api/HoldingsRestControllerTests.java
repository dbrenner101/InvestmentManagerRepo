/**
 * 
 */
package com.brenner.portfoliomgmt.api;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
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

import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.domain.Holding;
import com.brenner.portfoliomgmt.service.AccountsService;
import com.brenner.portfoliomgmt.service.HoldingsService;
import com.brenner.portfoliomgmt.test.DomainTestData;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		HoldingsRestController.class,
		HoldingsService.class,
		AccountsService.class
})
@AutoConfigureMockMvc
@EnableWebMvc
public class HoldingsRestControllerTests {
	
	@Autowired MockMvc mockMvc;
	
	@MockBean HoldingsService holdingsService;
	@MockBean AccountsService accountsService;
	
	@Test @WithMockUser
	public void testGetAllHoldings_Success() throws Exception {
		
		List<Holding> holdings = DomainTestData.generateHoldingsList(4, DomainTestData.getAccount1());
		Mockito.when(this.holdingsService.findAllHoldingsOrderedBySymbol()).thenReturn(holdings);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/api/holdings")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", notNullValue()))
			.andExpect(jsonPath("$", hasSize(holdings.size())));
	}
	
	@Test @WithMockUser
	public void testGetAllHoldingsForAccount_Success() throws Exception {
		Account account = DomainTestData.getAccount2();
		List<Holding> holdings = DomainTestData.generateHoldingsList(4, account);
		
		Mockito.when(this.accountsService.findAccountByAccountId(account.getAccountId())).thenReturn(Optional.of(account));
		Mockito.when(this.holdingsService.findHoldingsForAccount(account.getAccountId())).thenReturn(holdings);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/api/holdings/account/" + account.getAccountId())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", notNullValue()))
			.andExpect(jsonPath("$", hasSize(holdings.size())));
	}
}
