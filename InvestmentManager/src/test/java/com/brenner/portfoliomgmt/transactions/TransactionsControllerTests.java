/**
 * 
 */
package com.brenner.portfoliomgmt.transactions;

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

import com.brenner.portfoliomgmt.accounts.Account;
import com.brenner.portfoliomgmt.accounts.AccountsService;
import com.brenner.portfoliomgmt.holdings.Holding;
import com.brenner.portfoliomgmt.holdings.HoldingsService;
import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.investments.InvestmentsService;
import com.brenner.portfoliomgmt.test.TestDataHelper;
import com.brenner.portfoliomgmt.util.CommonUtils;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		TransactionsService.class,
		AccountsService.class,
		InvestmentsService.class,
		HoldingsService.class,
		TransactionsController.class
})
@AutoConfigureMockMvc
@DirtiesContext
public class TransactionsControllerTests {
	
	@Autowired MockMvc mockMvc;
	
	@MockBean TransactionsService transactionsService;
	
	@MockBean AccountsService accountsService;
	
	@MockBean InvestmentsService investmentsService;
	
	@MockBean HoldingsService holdingsService;
	
	
	@Test @WithMockUser public void testPrepSplitEntryForm_Success() throws Exception {
		
		List<Investment> investmentsList = TestDataHelper.getAllInvestments();
		
		Mockito.when(this.investmentsService.getInvestmentsOrderedBySymbolAsc()).thenReturn(investmentsList);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/prepSplitEntryForm"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("investments"))
			.andExpect(model().attribute("investments", investmentsList))
			.andExpect(view().name("trades/defineSplit"));
	}
	
	@Test @WithMockUser public void testPrepForTransactionsList_Success() throws Exception {
		
		List<Account> accountsList = TestDataHelper.getAllAccountsList();
		Mockito.when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(accountsList);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/prepForTransactionsList"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("accounts"))
			.andExpect(model().attribute("accounts", accountsList))
			.andExpect(view().name("transactions/chooseAccountForTransactionsList"));
	}
	
	@Test @WithMockUser public void testPrepGetTrades_Success() throws Exception {
		
		List<Account> accountsList = TestDataHelper.getAllAccountsList();
		Mockito.when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(accountsList);
		
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
		
		Account a = TestDataHelper.getAccount1();
		Transaction t = TestDataHelper.getBuyTransaction1();
		t.setAccount(a);
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(2);
		params.add("accountId", a.getAccountId().toString());
		params.add("transactionId", t.getTransactionId().toString());
		
		Mockito.when(this.transactionsService.getTransaction(t.getTransactionId())).thenReturn(t);
		Mockito.when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(TestDataHelper.getAllAccountsList());
		Mockito.when(this.investmentsService.getInvestmentsOrderedBySymbolAsc()).thenReturn(TestDataHelper.getAllInvestments());
		
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
		
		Holding h = TestDataHelper.getHolding1();
		h.setAccount(TestDataHelper.getAccount1());
		h.setInvestment(TestDataHelper.getInvestmentGE());
		
		Mockito.when(this.holdingsService.getHoldingByHoldingIdWithBuyTransaction(h.getHoldingId())).thenReturn(Optional.of(h));
		
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
		
		List<Account> accounts = TestDataHelper.getAllAccountsList();
		List<Investment> investments = TestDataHelper.getAllInvestments();
		
		Mockito.when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(accounts);
		Mockito.when(this.investmentsService.getInvestmentsOrderedBySymbolAsc()).thenReturn(investments);
		
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
		
		List<Account> accounts = TestDataHelper.getAllAccountsList();
		Mockito.when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(accounts);
		
		List<Account> cashAccounts = new ArrayList<>();
		cashAccounts.addAll(accounts);
		
		List<Transaction> cashTransactions = new ArrayList<>(2);
		cashTransactions.add(TestDataHelper.getCashTransaction1());
		cashTransactions.add(TestDataHelper.getCashTransaction2());
		
		for (Account a : cashAccounts) {
			a.setTransactions(cashTransactions);
		}
		
		Mockito.when(this.accountsService.getAccountsAndCash()).thenReturn(cashAccounts);
		
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
		
		List<Transaction> transactions = new ArrayList<>(2);
		transactions.add(TestDataHelper.getCashTransaction1());
		transactions.add(TestDataHelper.getBuyTransaction1());
		
		int validateTransactionId = transactions.get(0).getTransactionId().intValue();
		
		Mockito.when(this.transactionsService.getTradesForAccount(Mockito.any(Account.class))).thenReturn(transactions);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/listTransactionsForAccount")
				.param("accountId", "33"))
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
		Float transferAmount = 121.21F;
		Long fromAccountId = 12L;
		Long toAccountId = 21L;
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(4);
		params.add("transferDate", dateString);
		params.add("transferAmount", transferAmount.toString());
		params.add("fromAccountId", fromAccountId.toString());
		params.add("toAccountId", toAccountId.toString());
		
		Mockito.spy(this.transactionsService);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/transferCashBetweeAccounts")
				.params(params))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:prepTransferCashForm"));
		
		Mockito.verify(this.transactionsService).transferCash(CommonUtils.convertCommonDateFormatStringToDate(dateString), transferAmount, fromAccountId, toAccountId);
	}
	
	@Test
	@WithMockUser
	public void testPrepDividenForm_Success() throws Exception {
		
		List<Account> accounts = TestDataHelper.getAllAccountsList();
		List<Investment> investments = TestDataHelper.getAllInvestments();
		
		Mockito.when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(accounts);
		Mockito.when(this.investmentsService.getInvestmentsOrderedBySymbolAsc()).thenReturn(investments);
		
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
		
		Mockito.spy(this.transactionsService);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/dividendReceived")
				.params(params))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:prepDividendForm"));
		
		Mockito.doAnswer(invocation -> {
			Transaction t = (Transaction) invocation.getArgument(0);
			assertNotNull(t);
			assertNotNull(t.getAccount());
			assertEquals(accountId, t.getAccount().getAccountId());
			assertEquals(tradeDate, t.getTransactionDate());
			assertEquals(amount, t.getTradeQuantity());
			assertEquals(1F, t.getTradePrice());
			assertEquals(TransactionTypeEnum.Dividend, t.getTransactionType());
			assertEquals(investmentId, t.getInvestment().getInvestmentId());
			
			return null;
		}).when(this.transactionsService).saveTransaction(Mockito.any(Transaction.class));
		
	}
	
	@Test
	@WithMockUser
	public void testPrepareDepositForm_Success() throws Exception {
		
		List<Account> accounts = TestDataHelper.getAllAccountsList();
		Mockito.when(this.accountsService.getAccountsAndCash()).thenReturn(accounts);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/prepareDepositForm"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("accounts"))
			.andExpect(model().attribute("accounts", accounts))
			.andExpect(view().name("accounts/depositForm"));
	}
	
	@Test
	@WithMockUser
	public void testDepositCash_Success() throws Exception {
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(3);
		params.add("amount", "50.50");
		params.add("depositDate", "07/02/1901");
		params.add("accountId", "1");
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/depositCash")
				.params(params))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:prepareDepositForm"));
		
		Mockito.doAnswer(invocation -> {
	        Object arg0 = invocation.getArgument(0);
	        Transaction t = (Transaction) arg0;
	        
	        assertEquals(TransactionTypeEnum.Cash, t.getTransactionType());
	        assertNotNull(t.getAccount());
	        assertEquals(1L, t.getAccount().getAccountId());
	        assertEquals(1F, t.getTradePrice());
	        assertEquals(50.50F, t.getTradeQuantity());
	        
	        return null;
	    }).when(this.transactionsService).saveTransaction(Mockito.any(Transaction.class));
	}

}
