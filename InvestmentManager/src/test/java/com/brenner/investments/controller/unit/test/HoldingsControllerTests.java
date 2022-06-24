package com.brenner.investments.controller.unit.test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.brenner.investments.InvestmentsProperties;
import com.brenner.investments.controller.HoldingsController;
import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.entities.Transaction;
import com.brenner.investments.service.AccountsService;
import com.brenner.investments.service.InvestmentsService;
import com.brenner.investments.service.TransactionsService;
import com.brenner.investments.test.TestDataHelper;
import com.brenner.investments.util.CommonUtils;

@ActiveProfiles("test")
public class HoldingsControllerTests {

	private MockMvc mockMvc;
	@Mock TransactionsService transactionsService;
	@Mock AccountsService accountsService;
	@Mock InvestmentsService investmentsService;
	@Mock InvestmentsProperties props;
	
	@InjectMocks HoldingsController holdingsController;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.holdingsController).build();
	}
	
	@Test
	public void testGetAccountsForHoldings() throws Exception {
		
		when(props.getAccountsListAttributeKey()).thenReturn("accounts");
		when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(TestDataHelper.getAllAccountsList());
		
		this.mockMvc.perform(get("/getAccountsForHoldings"))
			.andExpect(status().isOk())
			.andExpect(view().name("holdings/chooseAccountAjax"));
	}
	
	@Test
	public void testGetHoldingsAjax() throws Exception {
		
		String accountIdStr = "1234";
		
		Account a = TestDataHelper.getAccount1();
		a.setAccountId(Long.valueOf(accountIdStr));
		a.setCashOnAccount(Float.valueOf(150));
		List<Holding> holdings = TestDataHelper.getHoldingsForAccount(a);
		Holding h1 = holdings.get(0);
		h1.setInvestment(TestDataHelper.getInvestmentGE());
		
		Double totalValueChange = 0D;
        Double totalStockValue = 0D;
    
	    if (holdings != null && ! holdings.isEmpty()) {
	        for(Holding holding : holdings) {
	            totalValueChange += holding.getChangeInValue() == null ? 0 : holding.getChangeInValue();
	            totalStockValue += holding.getCurrentValue() == null ? 0 : holding.getCurrentValue();
	        }
	    }
		
		when(this.transactionsService
        		.getHoldingsForAccountAndQuantityGreaterThanOrderedBySymbol(
        				a.getAccountId())).thenReturn(holdings);
		
		when(this.accountsService.getAccountAndCash(a.getAccountId())).thenReturn(a);
		
		when(props.getHoldingsListAttributeKey()).thenReturn("holdings");
		when(props.getAccountAttributeKey()).thenReturn("account");
		
		this.mockMvc.perform(get("/getHoldingsAjax").param("accountId", accountIdStr))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(holdings.size())))
			.andExpect(jsonPath("$[0].investment.investmentId").value(h1.getInvestment().getInvestmentId()))
			.andExpect(jsonPath("$[0].account.accountId").value(h1.getAccount().getAccountId()))
			.andExpect(jsonPath("$[0].quantity").value(h1.getQuantity()))
			.andExpect(jsonPath("$[0].purchasePrice").value(h1.getPurchasePrice()));
	}
	
	@Test
	public void testPrepAddHolding() throws Exception {
		
		List<Account> accounts = TestDataHelper.getAllAccountsList();
		
		when(props.getAccountsListAttributeKey()).thenReturn("accounts");
		when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(accounts);
		
		this.mockMvc.perform(get("/prepAddHolding"))
			.andExpect(status().isOk())
			.andExpect(view().name("holdings/addHolding"))
			.andExpect(model().attribute("accounts", is(accounts)));
	}
	
	@Test
	public void testAddHolding() throws Exception {
		
		String accountId = "1234";
		String symbol = "PVTL"; 
		String quantity = "20"; 
		String price = "19.555"; 
		String tradeDate = "02/20/1965";
		
		ArgumentCaptor<Transaction> capture = ArgumentCaptor.forClass(Transaction.class);
		
		when(this.transactionsService.addHolding(capture.capture())).thenReturn(null);
		
		this.mockMvc.perform(post("/addHolding")
				.content(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("accountId", accountId)
				.param("symbol", symbol)
				.param("quantity", quantity)
				.param("price", price)
				.param("tradeDate", tradeDate))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("prepAddHolding"));
		
		Transaction t = capture.getValue();
		
		assertNotNull(t);
		assertNotNull(t.getAccount());
		assertEquals(Long.valueOf(accountId), t.getAccount().getAccountId());
		assertEquals(Float.valueOf(quantity), t.getTradeQuantity());
		assertEquals(Float.valueOf(price), t.getTradePrice());
		assertEquals(CommonUtils.convertCommonDateFormatStringToDate(tradeDate), t.getTransactionDate());
	}

}
