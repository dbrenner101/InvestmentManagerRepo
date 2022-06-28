/**
 * 
 */
package com.brenner.investments.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Transaction;
import com.brenner.investments.service.AccountsService;
import com.brenner.investments.service.HoldingsService;
import com.brenner.investments.service.InvestmentsService;
import com.brenner.investments.service.TransactionsService;
import com.brenner.investments.test.TestDataHelper;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		HoldingsService.class,
		AccountsService.class,
		InvestmentsService.class,
		TransactionsService.class,
		HoldingsController.class
})
@AutoConfigureMockMvc
@DirtiesContext
public class HoldingsControllerTests {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean HoldingsService holdingsService;
	@MockBean AccountsService accountsService;
	@MockBean InvestmentsService investmentsService;
	@MockBean TransactionsService transactionsService;
	
	Holding h1 = TestDataHelper.getHolding1();
	Holding h2 = TestDataHelper.getHolding2();
	Holding h3 = TestDataHelper.getHolding3();
	List<Holding> allHoldings = new ArrayList<>(Arrays.asList(h1, h2, h3));
	
	Account a1 = TestDataHelper.getAccount1();
	Investment i1 = TestDataHelper.getInvestmentAAPL();
	
	List<Account> allAccounts = TestDataHelper.getAllAccountsList();
	List<Investment> allInvestments = TestDataHelper.getAllInvestments();
	
	@Test
	@WithMockUser
	public void testGetAccountsForHoldings_Success() throws Exception {
		
		Mockito.when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(allAccounts);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/getAccountsForHoldings"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("accounts"))
			.andExpect(model().attribute("accounts", allAccounts))
			.andExpect(view().name("holdings/chooseAccountAjax"));
	} 
	
	@Test
	@WithMockUser
	public void testPrepAddHolding_Success() throws Exception {
		
		Mockito.when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(allAccounts);
		Mockito.when(this.investmentsService.getInvestmentsOrderedBySymbolAsc()).thenReturn(allInvestments);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/prepAddHolding"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("accounts"))
			.andExpect(model().attribute("accounts", allAccounts))
			.andExpect(model().attributeExists("investments"))
			.andExpect(model().attribute("investments", allInvestments))
			.andExpect(view().name("holdings/addHolding"));
	}
	
	@Test
	@WithMockUser
	public void testAddHolding_Success() throws Exception {
		
		Map<String, String> requestParams = new HashMap<>(5);
		requestParams.put("accountId", "123");
		requestParams.put("investmentId", "1");
		requestParams.put("tradeQuantity", "100");
		requestParams.put("tradePrice", "275.5");
		requestParams.put("transactionDate", "2020-01-21");
		
		MultiValueMap<String, String> springMap = new LinkedMultiValueMap<>();
		springMap.setAll(requestParams);
		
		Mockito.when(this.investmentsService.getInvestmentByInvestmentId(1L)).thenReturn(i1);
		Mockito.when(this.holdingsService.addHolding(Mockito.any(Transaction.class), Mockito.any(Holding.class))).thenReturn(h1);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/addHolding")
				.params(springMap))
			.andExpect(view().name("redirect:prepAddHolding"))
			.andExpect(status().is3xxRedirection());
	}
	
	@Test
	@WithMockUser
	public void testGetHoldingsAjax_Success() throws Exception {
		
		Mockito.when(this.holdingsService.getHoldingsForAccount(1L)).thenReturn(allHoldings);
		Mockito.when(this.accountsService.getAccountAndCash(1L)).thenReturn(a1);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/getHoldingsAjax")
				.param("accountId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", notNullValue()))
			.andExpect(jsonPath("$", hasSize(allHoldings.size())))
			.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	@WithMockUser
	public void testGetHoldingsAjaxNoHoldings_Success() throws Exception {
		
		Mockito.when(this.holdingsService.getHoldingsForAccount(1L)).thenReturn(null);
		Mockito.when(this.accountsService.getAccountAndCash(1L)).thenReturn(a1);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/getHoldingsAjax")
				.param("accountId", "1"))
			.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser
	public void testEditHolding_Success() throws Exception {
		
		h1.setAccount(a1);
		h1.setInvestment(i1);
		
		Mockito.when(this.holdingsService.getHoldingByHoldingId(1L)).thenReturn(h1);
		Mockito.when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(allAccounts);
		Mockito.when(this.investmentsService.getInvestmentsOrderedBySymbolAsc()).thenReturn(allInvestments);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/editHolding")
				.param("holdingId", "1"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("holding"))
			.andExpect(model().attribute("holding", h1))
			.andExpect(model().attributeExists("accounts"))
			.andExpect(model().attribute("accounts", this.allAccounts))
			.andExpect(model().attributeExists("investments"))
			.andExpect(model().attribute("investments", this.allInvestments))
			.andExpect(view().name("holdings/editHoldingForm"));
	}

}
