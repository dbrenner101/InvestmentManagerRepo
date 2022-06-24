package com.brenner.investments.controller.unit.test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.brenner.investments.InvestmentsProperties;
import com.brenner.investments.controller.TransactionsController;
import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Transaction;
import com.brenner.investments.entities.constants.TransactionTypeEnum;
import com.brenner.investments.service.AccountsService;
import com.brenner.investments.service.InvestmentsService;
import com.brenner.investments.service.TransactionsService;
import com.brenner.investments.test.TestDataHelper;
import com.brenner.investments.util.CommonUtils;

@ActiveProfiles("test")
public class TransactionControllerTests {

	private MockMvc mockMvc;
	@Mock TransactionsService transactionsService;
	@Mock AccountsService accountsService;
	@Mock InvestmentsService investmentsService;
	@Mock InvestmentsProperties props;
	
	@InjectMocks TransactionsController transactionController;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.transactionController).build();
	}
	
	@After
	public void cleanup() throws Exception {
		
	}
	
	@Test
	public void testPrepForTransactionsList() throws Exception {
		
		List<Account> accounts = TestDataHelper.getAllAccountsList();
		
		when(this.props.getAccountsListAttributeKey()).thenReturn("accounts");
		when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(accounts);
		
		this.mockMvc.perform(get("/prepForTransactionsList"))
			.andExpect(status().isOk())
			.andExpect(view().name("transactions/chooseAccountForTransactionsList"))
			.andExpect(model().attribute("accounts", hasSize(accounts.size())));
	}
	
	@Test
	public void testListTransactionsForAccount() throws Exception {
		
		Account a = TestDataHelper.getAccount1();
		List<Transaction> transactions = TestDataHelper.getTransactionForAccount(a);
		Transaction t2 = transactions.get(2);
		
		when(this.transactionsService.getTradesForAccount(a)).thenReturn(transactions);
		
		this.mockMvc.perform(get("/listTransactionsForAccount")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("accountId", a.getAccountId().toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(transactions.size())))
			.andExpect(jsonPath("$[2].account.accountName", is(t2.getAccount().getAccountName())))
			.andExpect(jsonPath("$[2].transactionId").value(t2.getTransactionId().toString()))
			.andExpect(jsonPath("$[2].transactionType").value(t2.getTransactionType().toString()));
	}
	
	@Test
	public void testListTransactionsForAccountNullTransactions() throws Exception {
		
		Account a = TestDataHelper.getAccount1();
		when(this.transactionsService.getTradesForAccount(a)).thenReturn(null);
		
		this.mockMvc.perform(get("/listTransactionsForAccount")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("accountId", "123"))
			.andExpect(status().isOk());
	}
	
	@Test
	public void testPrepareDefineTrade() throws Exception {
		
		List<Account> accounts = TestDataHelper.getAllAccountsList();
		List<Investment> investments = TestDataHelper.getAllInvestments();
		
		when(props.getAccountsListAttributeKey()).thenReturn("accounts");
		when(props.getInvestmentsListAttributeKey()).thenReturn("investments");
		
		when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(accounts);
		when(this.investmentsService.getInvestmentsOrderedBySymbolAsc()).thenReturn(investments);
		
		this.mockMvc.perform(get("/prepareDefineTrade"))
			.andExpect(status().isOk())
			.andExpect(view().name("trades/defineTrade"))
			.andExpect(model().attribute("accounts", hasSize(accounts.size())))
			.andExpect(model().attribute("investments", hasSize(investments.size())));
	}
	
	@Test
	public void testBuyInvestment() throws Exception {
		
		Date date = new Date();
		
		doAnswer((invocation) -> {
			return null;
		}).when(this.transactionsService).persistBuy(Mockito.any(Date.class), anyFloat(), anyFloat(), anyLong(), anyLong());
		
		this.mockMvc.perform(post("/buyInvestment")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("tradeDate", CommonUtils.convertDateToMMDDYYYYString(date))
				.param("price", "15")
				.param("quantity", "10")
				.param("investmentId", "99")
				.param("accountId", "1234"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("prepareDefineTrade"));
	}
	
	@Test
	public void testPrepareDepositForm() throws Exception {
		
		List<Account> accounts = TestDataHelper.getAllAccountsList();
		
		when(this.accountsService.getAccountsAndCash()).thenReturn(accounts);
		when(props.getAccountsListAttributeKey()).thenReturn("accounts");
		
		this.mockMvc.perform(get("/prepareDepositForm"))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts/depositForm"))
			.andExpect(model().attribute("accounts", hasSize(accounts.size())));
	}
	
	@Test
	public void testDepositCash() throws Exception {
		
		String amount = "100"; 
		String depositDate = "02/20/1965";
		String accountId = "1234";
		
		ArgumentCaptor<Transaction> argCap = ArgumentCaptor.forClass(Transaction.class);
		
		when(this.transactionsService.saveTransaction(argCap.capture())).thenReturn(null);
		
		this.mockMvc.perform(post("/depositCash")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("amount", amount)
				.param("depositDate", depositDate)
				.param("accountId", accountId))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("prepareDepositForm"));
		
		assertEquals(Float.valueOf(amount), argCap.getValue().getTradeQuantity());
		assertEquals(Long.valueOf(accountId), argCap.getValue().getAccount().getAccountId());
		assertEquals(CommonUtils.convertCommonDateFormatStringToDate(depositDate), argCap.getValue().getTransactionDate());
		
	}
	
	@Test
	public void testPrepTransferCashForm() throws Exception {
		
		List<Account> accounts = TestDataHelper.getAllAccountsList();
		
		when(this.props.getAccountsListAttributeKey()).thenReturn("accounts");
		when(this.props.getAccountsAndCashAttributeKey()).thenReturn("accountsandcash");
		
		when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(accounts);
		when(this.accountsService.getAccountsAndCash()).thenReturn(accounts);
		
		this.mockMvc.perform(get("/prepTransferCashForm"))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts/transferCash"))
			.andExpect(model().attribute("accounts", is(accounts)))
			.andExpect(model().attribute("accountsandcash", is(accounts)));
	}
	
	@Test
	public void testTransferCashBetweeAccounts() throws Exception {
		
		doAnswer((invocation) -> {
			return null;
		}).when(this.transactionsService).transferCash(Mockito.any(Date.class), anyFloat(), anyLong(), anyLong());
		
		this.mockMvc.perform(post("/transferCashBetweeAccounts")
				.content(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("transferDate", "02/20/1965")
				.param("transferAmount", "100")
				.param("fromAccountId", "1234")
				.param("toAccountId", "4321"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("prepTransferCashForm"));
	}
	
	@Test
	public void testPrepDividendForm() throws Exception {
		
		List<Account> accounts = TestDataHelper.getAllAccountsList();
		List<Investment> investments = TestDataHelper.getAllInvestments();
		when(props.getAccountsListAttributeKey()).thenReturn("accounts");
		when(this.accountsService.getAllAccountsOrderByAccountNameAsc()).thenReturn(accounts);
		
		when(props.getInvestmentsListAttributeKey()).thenReturn("investments");
		when(this.investmentsService.getInvestmentsOrderedBySymbolAsc()).thenReturn(investments);
		
		this.mockMvc.perform(get("/prepDividendForm"))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts/dividendPayment"))
			.andExpect(model().attribute("accounts", is(accounts)))
			.andExpect(model().attribute("investments", is(investments)));
		
	}
	
	@Test
	public void testApplyDividend() throws Exception {
		
		ArgumentCaptor<Transaction> argCapTran = ArgumentCaptor.forClass(Transaction.class);
		
		when(this.transactionsService.saveTransaction(argCapTran.capture())).thenReturn(null);
		
		String tradeDate = "02/20/1965"; 
		String amount = "100"; 
		String investmentId = "1234"; 
		String accountId = "4321";
		
		this.mockMvc.perform(post("/dividendReceived")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("tradeDate", tradeDate)
				.param("amount", amount)
				.param("investmentId", investmentId)
				.param("accountId", accountId))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("prepDividendForm"));
		
		Transaction t = argCapTran.getValue();
		assertEquals(CommonUtils.convertCommonDateFormatStringToDate(tradeDate), t.getTransactionDate());
		assertEquals(Float.valueOf(amount), t.getTradeQuantity());
		assertEquals(Float.valueOf(1), t.getTradePrice());
		assertNotNull(t.getInvestment());
		assertEquals(Long.valueOf(investmentId), t.getInvestment().getInvestmentId());
		assertNotNull(t.getAccount());
		assertEquals(Long.valueOf(accountId), t.getAccount().getAccountId());
		assertEquals(TransactionTypeEnum.Dividend, t.getTransactionType());
	}
}
