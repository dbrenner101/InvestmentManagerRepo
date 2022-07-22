/**
 * 
 */
package com.brenner.portfoliomgmt.view.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.brenner.portfoliomgmt.api.TransactionsRestController;
import com.brenner.portfoliomgmt.data.entities.TransactionDTO;
import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.domain.Holding;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Transaction;
import com.brenner.portfoliomgmt.domain.TransactionTypeEnum;
import com.brenner.portfoliomgmt.service.AccountsService;
import com.brenner.portfoliomgmt.service.HoldingsService;
import com.brenner.portfoliomgmt.service.InvestmentsService;
import com.brenner.portfoliomgmt.test.DomainTestData;
import com.brenner.portfoliomgmt.util.CommonUtils;
import com.brenner.portfoliomgmt.view.controller.TransactionsController;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		AccountsService.class,
		InvestmentsService.class,
		HoldingsService.class,
		TransactionsController.class,
		TransactionsRestController.class
})
@AutoConfigureMockMvc
@DirtiesContext
@EnableWebMvc
public class TransactionsControllerTests {
	
	@Autowired MockMvc mockMvc;
	
	@MockBean AccountsService accountsService;
	
	@MockBean InvestmentsService investmentsService;
	
	@MockBean HoldingsService holdingsService;
	
	
	@Test
	@WithMockUser
	public void testPrepSplitEntryForm_Success() throws Exception {
		
		List<Investment> investmentsList = DomainTestData.getAllInvestments();
		
		Mockito.when(this.investmentsService.findInvestments("symbol")).thenReturn(investmentsList);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/prepSplitEntryForm"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("investments"))
			//.andExpect(model().attribute("investments", investmentsList))
			.andExpect(view().name("trades/defineSplit"));
	}
	
	@Test
	@WithMockUser
	public void testPrepForTransactionsList_Success() throws Exception {
		
		List<Account> accountsList = DomainTestData.getAllAccountsList();
		Mockito.when(this.accountsService.findAllAccounts("accountName")).thenReturn(accountsList);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/prepForTransactionsList"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("accounts"))
			.andExpect(model().attribute("accounts", accountsList))
			.andExpect(view().name("transactions/chooseAccountForTransactionsList"));
	}
	
	@Test
	@WithMockUser
	public void testPrepGetTrades_Success() throws Exception {
		
		List<Account> accountsList = DomainTestData.getAllAccountsList();
		Mockito.when(this.accountsService.findAllAccounts("accountName")).thenReturn(accountsList);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/prepGetTrades"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("accounts"))
			.andExpect(model().attribute("accounts", accountsList))
			.andExpect(view().name("trades/chooseAccountForTransactionsList"));
	}
	
	@Test
	@WithMockUser
	public void testEditTradeGetDetails_Success() throws Exception {
		
		Account a = DomainTestData.getAccount1();
		Transaction t = DomainTestData.getBuyTransaction1();
		t.setAccount(a);
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(2);
		params.add("accountId", a.getAccountId().toString());
		params.add("transactionId", t.getTransactionId().toString());
		
		Mockito.when(this.holdingsService.findTransaction(t.getTransactionId())).thenReturn(Optional.of(t));
		Mockito.when(this.accountsService.findAllAccounts("accountName")).thenReturn(DomainTestData.getAllAccountsList());
		Mockito.when(this.investmentsService.findInvestments("symbol")).thenReturn(DomainTestData.getAllInvestments());
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/editTradeGetDetails")
				.params(params))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("transaction"))
			.andExpect(model().attribute("transaction", t))
			.andExpect(model().attributeExists("accounts"))
			.andExpect(model().attributeExists("investments"))
			.andExpect(view().name("trades/editTrade"));
	}
	
	@Test
	@WithMockUser
	public void testRetrieveHoldingDetails_Success() throws Exception {
		
		Holding h = DomainTestData.getHolding1();
		h.setAccount(DomainTestData.getAccount1());
		h.setInvestment(DomainTestData.getInvestmentGE());
		
		Mockito.when(this.holdingsService.findHoldingByHoldingIdWithBuyTransaction(h.getHoldingId())).thenReturn(Optional.of(h));
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/retrieveHoldingDetails")
				.param("holdingId", h.getHoldingId().toString()))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("holding"))
			.andExpect(model().attribute("holding", h))
			.andExpect(view().name("trades/transactionsForHolding"));
		
	}
	
	@Test
	@WithMockUser
	public void testSellHolding_Success() throws Exception {
		Long holdingId = 1L;
		Long transactionId = 2L; 
		String transactionDate = "2021-10-10";
		Float tradeQuantity = 10F;
		Float tradePrice = 5.5F;
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(5);
		params.add("holdingId", holdingId.toString());
		params.add("transactionId", transactionId.toString());
		params.add("transactionDate", transactionDate);
		params.add("tradeQuantity", tradeQuantity.toString());
		params.add("tradePrice", tradePrice.toString());
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/sellHolding")
				.params(params))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:getAccountsForHoldings"));
	}
	
	@Test
	@WithMockUser
	public void testPrepareDefineTrade_Success() throws Exception {
		
		List<Account> accounts = DomainTestData.getAllAccountsList();
		List<Investment> investments = DomainTestData.getAllInvestments();
		
		Mockito.when(this.accountsService.findAllAccounts("accountName")).thenReturn(accounts);
		Mockito.when(this.investmentsService.findInvestments("symbol")).thenReturn(investments);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/prepareDefineTrade"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("accounts"))
			.andExpect(model().attribute("accounts", accounts))
			.andExpect(model().attributeExists("investments"))
			.andExpect(model().attribute("investments", investments))
			.andExpect(view().name("trades/defineTrade"));
	}
	
	@Test
	@WithMockUser
	public void testBuyInvestment_Success() throws Exception {
		
		String tradeDate = "1950-01-20";
		String price = "12.55";
		String quantity = "228";
		String investmentId = "12"; 
		String accountId = "2221";
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(5);
		params.add("tradeDate", tradeDate);
		params.add("price", price);
		params.add("quantity", quantity);
		params.add("investmentId", investmentId);
		params.add("accountId", accountId);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/buyInvestment")
				.params(params)
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:prepareDefineTrade"));
	}
	
	@Test
	@WithMockUser
	public void testPrepTransferCashForm_Success() throws Exception {
		
		List<Account> accounts = DomainTestData.getAllAccountsList();
		Mockito.when(this.accountsService.findAllAccounts("accountName")).thenReturn(accounts);
		
		List<Account> cashAccounts = new ArrayList<>();
		cashAccounts.addAll(accounts);
		
		List<Transaction> cashTransactions = new ArrayList<>(2);
		cashTransactions.add(DomainTestData.getCashTransaction1());
		cashTransactions.add(DomainTestData.getCashTransaction2());
		
		Mockito.when(this.accountsService.findAllAccounts("")).thenReturn(cashAccounts);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/prepTransferCashForm"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("accounts"))
			.andExpect(model().attribute("accounts", accounts))
			.andExpect(model().attributeExists("accountsCash"))
			.andExpect(model().attribute("accountsCash", cashAccounts))
			.andExpect(view().name("accounts/transferCash"));
	}
	
	@Test
	@WithMockUser
	public void testListTransactionsForAccount_Success() throws Exception {
		
		Transaction t1 = DomainTestData.getCashTransaction1();
		t1.setAccount(DomainTestData.getAccount1());
		t1.setHolding(DomainTestData.getHolding1());
		
		Transaction t2 = DomainTestData.getBuyTransaction1();
		t2.setAccount(DomainTestData.getAccount2());
		t2.setHolding(DomainTestData.getHolding2());
		
		List<Transaction> transactions = new ArrayList<>(2);
		transactions.add(t1);
		transactions.add(t2);
		
		int validateTransactionId = transactions.get(0).getTransactionId().intValue();
		
		Mockito.when(this.holdingsService.findTradesForAccount(Mockito.any(Account.class))).thenReturn(transactions);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/api/transactions/account/33"))
			 .andExpect(status().isOk())
			 .andExpect(jsonPath("$", notNullValue()))
			 .andExpect(jsonPath("$", hasSize(transactions.size())))
			 .andExpect(jsonPath("$[0].transactionId", is(validateTransactionId)))
			 .andExpect(jsonPath("$[1].transactionType", is("Buy")))
			 .andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	@WithMockUser
	public void testTransferBetweenAccounts_Success() throws Exception {
		
		String dateString = "12/1/1921";
		BigDecimal transferAmount = BigDecimal.valueOf(121.21);
		Long fromAccountId = 12L;
		Long toAccountId = 21L;
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(4);
		params.add("transferDate", dateString);
		params.add("transferAmount", transferAmount.toString());
		params.add("fromAccountId", fromAccountId.toString());
		params.add("toAccountId", toAccountId.toString());
		
		Mockito.spy(this.holdingsService);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/transferCashBetweeAccounts")
				.params(params))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:prepTransferCashForm"));
		
		Mockito.verify(this.holdingsService).transferCash(CommonUtils.convertCommonDateFormatStringToDate(dateString), transferAmount, fromAccountId, toAccountId);
	}
	
	@Test
	@WithMockUser
	public void testPrepDividenForm_Success() throws Exception {
		
		List<Account> accounts = DomainTestData.getAllAccountsList();
		List<Investment> investments = DomainTestData.getAllInvestments();
		
		Mockito.when(this.accountsService.findAllAccounts("accountName")).thenReturn(accounts);
		Mockito.when(this.investmentsService.findInvestments("symbol")).thenReturn(investments);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/prepDividendForm"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("accounts"))
			.andExpect(model().attribute("accounts", accounts))
			.andExpect(model().attributeExists("investments"))
			.andExpect(model().attribute("investments", investments))
			.andExpect(view().name("accounts/dividendPayment"));
	}
	
	@Test
	@WithMockUser
	public void testApplyDividen_Success() throws Exception {
		
		String tradeDate = "03/22/1985";
		Float amount = 22.50F;
		Long investmentId = 33L;
		Long accountId = 15L;
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(4);
		params.add("tradeDate", tradeDate);
		params.add("amount", amount.toString());
		params.add("investmentId", investmentId.toString()); 
		params.add("accountId", accountId.toString()); 
		
		Mockito.when(this.accountsService.findAccountByAccountId(accountId)).thenReturn(Optional.of(DomainTestData.getAccount1()));
		
		Mockito.spy(this.holdingsService);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/dividendReceived")
				.params(params))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:prepDividendForm"));
		
		Mockito.doAnswer(invocation -> {
			TransactionDTO t = (TransactionDTO) invocation.getArgument(0);
			assertNotNull(t);
			assertNotNull(t.getAccount());
			assertEquals(accountId, t.getAccount().getAccountId());
			assertEquals(tradeDate, t.getTransactionDate());
			assertEquals(amount, t.getTradeQuantity());
			assertEquals(1F, t.getTradePrice());
			assertEquals(TransactionTypeEnum.Dividend, t.getTransactionType());
			
			return null;
		}).when(this.holdingsService).saveTransaction(Mockito.any(Transaction.class));
		
	}
	
	@Test
	@WithMockUser
	public void testPrepareDepositForm_Success() throws Exception {
		
		List<Account> accounts = DomainTestData.getAllAccountsList();
		Mockito.when(this.accountsService.findAllAccounts("")).thenReturn(accounts);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/prepareDepositForm"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("accounts"))
			//.andExpect(model().attribute("accounts", accounts))
			.andExpect(view().name("accounts/depositForm"));
	}
	
	@Test
	@WithMockUser
	public void testDepositCash_Success() throws Exception {
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(3);
		params.add("amount", "50.50");
		params.add("depositDate", "07/02/1901");
		params.add("accountId", "1");
		
		Mockito.when(this.accountsService.findAccountByAccountId(1L)).thenReturn(Optional.of(DomainTestData.getAccount1()));
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/depositCash")
				.params(params))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:prepareDepositForm"));
		
		Mockito.doAnswer(invocation -> {
	        Object arg0 = invocation.getArgument(0);
	        TransactionDTO t = (TransactionDTO) arg0;
	        
	        assertEquals(TransactionTypeEnum.Cash, t.getTransactionType());
	        assertNotNull(t.getAccount());
	        assertEquals(1L, t.getAccount().getAccountId());
	        assertEquals(1F, t.getTradePrice());
	        assertEquals(50.50F, t.getTradeQuantity());
	        
	        return null;
	    }).when(this.holdingsService).saveTransaction(Mockito.any(Transaction.class));
	}

}
