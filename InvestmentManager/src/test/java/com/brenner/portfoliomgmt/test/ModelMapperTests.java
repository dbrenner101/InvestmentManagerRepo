/**
 * 
 */
package com.brenner.portfoliomgmt.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import com.brenner.portfoliomgmt.data.entities.AccountDTO;
import com.brenner.portfoliomgmt.data.entities.HoldingDTO;
import com.brenner.portfoliomgmt.data.entities.InvestmentDTO;
import com.brenner.portfoliomgmt.data.entities.TransactionDTO;
import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.domain.Holding;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Transaction;
import com.brenner.portfoliomgmt.util.ObjectMappingUtil;

/**
 * Tests to ensure the mapping from Entity to Domain and Domain to Entity works as expected.
 * 
 * Private evaluation methods below are chained carefully to prevent recursion issues. Review their interdependencies
 * before adding new tests.
 *
 * @author dbrenner
 * 
 */
@Testable
public class ModelMapperTests {

	@Test
	public void testAccountToAccountDtoMapping() throws Exception {
		Account account = DomainTestData.generateAccount(1, 5);
		AccountDTO accountData = ObjectMappingUtil.mapAccountToAccountDTO(account);
		
		evalAccountAndAccountDTOEqual(account, accountData);
		evalHoldingAndHoldingDTOListsEqual(account.getHoldings(), accountData.getHoldings());
	}
	
	@Test
	public void testAccountDTOToAccountMapping() throws Exception {
		AccountDTO accountData = EntityTestData.generateAccount(4, 10);
		Account account = ObjectMappingUtil.mapAccountDtoToAccount(accountData);
		evalAccountAndAccountDTOEqual(account, accountData);
		evalHoldingAndHoldingDTOListsEqual(account.getHoldings(), accountData.getHoldings());
	}
	
	@Test
	public void testInvestmentToInvestmentDtoMapping() throws Exception {
		Investment investment = DomainTestData.generateInvestment(1);
		InvestmentDTO investmentData = ObjectMappingUtil.mapInvestmentToInvestmentDTO(investment);
		evalInvestmentAndInvestmentDTOEqual(investment, investmentData);
	}
	
	@Test
	public void testInvestmentDtoToInvestmentMapping() throws Exception {
		InvestmentDTO investmentData = EntityTestData.generateInvestment(1, 5);
		Investment investment = ObjectMappingUtil.mapInvestmentDtoToInvestment(investmentData);
		evalInvestmentAndInvestmentDTOEqual(investment, investmentData);
	}
	
	
	/*
	 * private void evalQuoteAndQuoteDtoEqual(Quote quote, QuoteDTO quoteData) {
	 * 
	 * if (quote == null || quoteData == null) { assertEquals(quote, quoteData); }
	 * else { assertEquals(quote.getDate(), quoteData.getDate());
	 * assertEquals(quote.getOpen(), quoteData.getOpen());
	 * assertEquals(quote.getClose(), quoteData.getClose());
	 * assertEquals(quote.getHigh(), quoteData.getHigh());
	 * assertEquals(quote.getLow(), quoteData.getLow());
	 * assertEquals(quote.getVolume(), quoteData.getVolume());
	 * assertEquals(quote.getPriceChange(), quoteData.getPriceChange());
	 * assertEquals(quote.getWeek52High(), quoteData.getWeek52High());
	 * assertEquals(quote.getWeek52Low(), quoteData.getWeek52Low());
	 * evalInvestmentAndInvestmentDTOEqual(quote.getInvestment(),
	 * quoteData.getInvestment()); } }
	 */
	
	
	private void evalInvestmentAndInvestmentDTOEqual(Investment investment, InvestmentDTO investmentData) {
		
		if (investment == null || investmentData == null) {
			assertEquals(investment, investmentData);
		} else {
			assertEquals(investment.getInvestmentId(), investmentData.getInvestmentId());
			assertEquals(investment.getSymbol(), investmentData.getSymbol());
			assertEquals(investment.getCompanyName(), investmentData.getCompanyName());
			assertEquals(investment.getExchange(), investmentData.getExchange());
			assertEquals(investment.getSector(), investmentData.getSector());
			assertEquals(investment.getInvestmentType(), investmentData.getInvestmentType());
		}
	}
	
	private void evalHoldingAndHoldingDTOEqual(Holding holding, HoldingDTO holdingData) {
		
		if (holding == null || holdingData == null) {
			assertEquals(holding, holdingData);
		} else {
			assertEquals(holding.getBucketEnum(), holdingData.getBucketEnum());
			assertEquals(holding.getHoldingId(), holdingData.getHoldingId());
			assertEquals(holding.getPurchaseDate(), holdingData.getPurchaseDate());
			assertEquals(holding.getPurchasePrice(), holdingData.getPurchasePrice());
			assertEquals(holding.getQuantity(), holdingData.getQuantity());
			assertEquals(holding.getTotalDividends(), holdingData.getTotalDividends());
		}
	}
	
	private void evalHoldingAndHoldingDTOListsEqual(List<Holding> holdings, List<HoldingDTO> holdingDatas) {
		if (holdings == null || holdingDatas == null) {
			assertEquals(holdings, holdingDatas);
		} else {
			assertEquals(holdings.size(), holdingDatas.size());
			
			int loopCount = 0;
			for (Holding holding : holdings) {
				HoldingDTO holdingData = holdingDatas.get(loopCount);
				evalHoldingAndHoldingDTOEqual(holding, holdingData);
				evalTransactionAndTransactionDTOListsEqual(holding.getTransactions(), holdingData.getTransactions());
				++ loopCount;
			}
		}
	}
	
	private void evalAccountAndAccountDTOEqual(Account account, AccountDTO accountData) {
		
		if (account == null || accountData == null) {
			assertEquals(account, accountData);
		}
		else {
			assertEquals(account.getAccountId(), accountData.getAccountId());
			assertEquals(account.getAccountName(), accountData.getAccountName());
			assertEquals(account.getAccountNumber(), accountData.getAccountNumber());
			assertEquals(account.getAccountType(), accountData.getAccountType());
			assertEquals(account.getCashOnAccount(), accountData.getCashOnAccount());
			assertEquals(account.getCompany(), accountData.getCompany());
			assertEquals(account.getOwner(), accountData.getOwner());
		}
	}
	
	private void evalTransactionAndTransactionDTOEqual(Transaction transaction, TransactionDTO transactionData) {
		
		if (transaction == null || transactionData == null) {
			assertEquals(transaction, transactionData);
		} else {
			assertEquals(transaction.getTransactionId(), transactionData.getTransactionId());
			assertEquals(transaction.getAssociatedCashTransactionId(), transactionData.getAssociatedCashTransactionId());
			assertEquals(transaction.getTransactionDate(), transactionData.getTransactionDate());
			assertEquals(transaction.getTradePrice(), transactionData.getTradePrice());
			assertEquals(transaction.getTradeQuantity(), transactionData.getTradeQuantity());
			assertEquals(transaction.getTransactionType(), transactionData.getTransactionType());
			assertEquals(transaction.getDividend(), transactionData.getDividend());
		}
	}
	
	private void evalTransactionAndTransactionDTOListsEqual(List<Transaction> transactions, List<TransactionDTO> transactionDatas) {
		
		if (transactions == null || transactionDatas == null) {
			assertEquals(transactions, transactionDatas);
		} else {
			assertEquals(transactions.size(), transactionDatas.size());
			
			int loopCounter = 0;
			for (Transaction transaction : transactions) {
				evalTransactionAndTransactionDTOEqual(transaction, transactionDatas.get(loopCounter));
				++loopCounter;
			}
		}
	}
}
