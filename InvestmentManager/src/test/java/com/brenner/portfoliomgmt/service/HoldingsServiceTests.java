/**
 * 
 */
package com.brenner.portfoliomgmt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.brenner.portfoliomgmt.data.repo.AccountsRepository;
import com.brenner.portfoliomgmt.exception.InvalidDataRequestException;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;

import com.brenner.portfoliomgmt.data.entities.AccountDTO;
import com.brenner.portfoliomgmt.data.entities.HoldingDTO;
import com.brenner.portfoliomgmt.data.entities.InvestmentDTO;
import com.brenner.portfoliomgmt.data.entities.QuoteDTO;
import com.brenner.portfoliomgmt.data.entities.TransactionDTO;
import com.brenner.portfoliomgmt.data.repo.HoldingsRepository;
import com.brenner.portfoliomgmt.data.repo.InvestmentsRepository;
import com.brenner.portfoliomgmt.data.repo.TransactionsRepository;
import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.domain.Holding;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Transaction;
import com.brenner.portfoliomgmt.domain.TransactionTypeEnum;
import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.service.HoldingsService;
import com.brenner.portfoliomgmt.service.InvestmentsService;
import com.brenner.portfoliomgmt.service.QuotesService;
import com.brenner.portfoliomgmt.test.DomainTestData;
import com.brenner.portfoliomgmt.test.EntityTestData;
import com.brenner.portfoliomgmt.util.CommonUtils;
import com.brenner.portfoliomgmt.util.ObjectMappingUtil;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		HoldingsRepository.class,
		TransactionsRepository.class,
		QuotesService.class,
		HoldingsService.class,
		InvestmentsRepository.class,
		AccountsRepository.class
})
@DirtiesContext
@TestInstance(Lifecycle.PER_METHOD)
public class HoldingsServiceTests {
	
	@MockBean HoldingsRepository holdingsRepo;
	@MockBean TransactionsRepository transactionsRepo;
	@MockBean InvestmentsRepository investmentsRepository;
	@MockBean QuotesService quotesService;
	@MockBean AccountsRepository accountsRepo;
	
	@Autowired HoldingsService holdingsService;
	
	@Test
	public void testAddHolding_Success() throws Exception {
		
		AccountDTO accountDTO = EntityTestData.getAccount1();
		Account account = ObjectMappingUtil.mapAccountDtoToAccount(accountDTO);
		Mockito.when(this.accountsRepo.findById(accountDTO.getAccountId())).thenReturn(Optional.of(accountDTO));
		
		InvestmentDTO investmentDTO = EntityTestData.getInvestmentAAPL();
		Investment investment = ObjectMappingUtil.mapInvestmentDtoToInvestment(investmentDTO);
		Mockito.when(this.investmentsRepository.findById(investmentDTO.getInvestmentId())).thenReturn(Optional.of(investmentDTO));
		
		HoldingDTO holdingData = EntityTestData.getHolding1();
		Holding holding = ObjectMappingUtil.mapHoldingDtoToHolding(holdingData);
		holdingData.setAccount(accountDTO);
		holdingData.setInvestment(investmentDTO);
		
		TransactionDTO transactionData = EntityTestData.getBuyTransaction1();
		Transaction transaction = ObjectMappingUtil.mapTransactionDtoToTransaction(transactionData);
		transactionData.setTransactionType(null);
		
		Mockito.when(this.holdingsRepo.save(Mockito.any(HoldingDTO.class))).thenAnswer(hd -> {
			HoldingDTO h = hd.getArgument(0, HoldingDTO.class);
			assertEquals(holding.getHoldingId(), h.getHoldingId());
			assertEquals(holding.getQuantity(), h.getQuantity());
			assertEquals(holding.getPurchasePrice(), h.getPurchasePrice());
			assertEquals(holding.getPurchaseDate(), h.getPurchaseDate());
			assertEquals(account.getAccountId(), h.getAccount().getAccountId());
			return holdingData;
		});
		
		Mockito.when(this.transactionsRepo.save(Mockito.any(TransactionDTO.class))).thenAnswer(t -> {
			TransactionDTO trans = t.getArgument(0, TransactionDTO.class);
			assertEquals(TransactionTypeEnum.Buy, trans.getTransactionType());
			assertEquals(transaction.getTransactionId(), trans.getTransactionId());
			assertEquals(transaction.getTransactionDate(), trans.getTransactionDate());
			assertEquals(transaction.getTradePrice(), trans.getTradePrice());
			assertEquals(account.getAccountId(), trans.getAccount().getAccountId());
			assertEquals(holding.getHoldingId(), trans.getHolding().getHoldingId());
			return null;
		});
		
		this.holdingsService.addHolding(transaction, holding, account, investment);
	}
	
	@Test
	public void testAddHoldingNullValues_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.addHolding(null, null, null, null);
		});
		
		assertEquals("Required attributes are null", e.getMessage());
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.addHolding(
					DomainTestData.getBuyTransaction1(), 
					DomainTestData.getHolding1(), 
					DomainTestData.getAccount1(), 
					DomainTestData.getInvestmentAAPL());
		});
		
		assertEquals("Unable to locate account with id 1", e.getMessage());
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.addHolding(
					DomainTestData.getBuyTransaction1(), 
					null, 
					DomainTestData.getAccount1(), 
					DomainTestData.getInvestmentAAPL());
		});
		
		assertEquals("Required attributes are null", e.getMessage());
	}
	
	@Test
	public void testSaveHolding_Success() throws Exception {
		
		HoldingDTO holdingData = EntityTestData.getHolding1();
		AccountDTO accountDTO = EntityTestData.getAccount1();
		holdingData.setAccount(accountDTO);
		Holding holding = ObjectMappingUtil.mapHoldingDtoToHolding(holdingData);
		
		Mockito.when(this.holdingsRepo.save(holdingData)).thenReturn(holdingData);
		
		holding = this.holdingsService.saveHolding(holding);
		
		assertNotNull(holding);
		assertEquals(holdingData.getHoldingId(), holding.getHoldingId());
		assertEquals(holdingData.getQuantity(), holding.getQuantity());
		assertEquals(holdingData.getPurchasePrice(), holding.getPurchasePrice());
		assertEquals(holdingData.getPurchaseDate(), holding.getPurchaseDate());
		assertEquals(accountDTO.getAccountId(), holding.getAccount().getAccountId());
	}
	
	@Test
	public void testDeleteHoldingNullValue_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.deleteHolding(null);
		});
		
		assertEquals("holdingId must be non-null", e.getMessage());
	}
	
	@Test
	public void testSaveHoldingNullHolding_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.saveHolding(null);
		});
		
		assertEquals("Holding and Holding.Account must not be null", e.getMessage());
	}
	
	@Test
	public void testFindHoldingsForAccount_Success() throws Exception {
		
		AccountDTO a = EntityTestData.getAccount1();
		List<HoldingDTO> returnVal = EntityTestData.getHoldingsForAccount(a);
		Mockito.when(this.holdingsRepo.findByAccountAccountId(a.getAccountId())).thenReturn(returnVal);
		
		List<Holding> holdings = this.holdingsService.findHoldingsForAccount(a.getAccountId());
		
		assertNotNull(holdings);
		assertEquals(returnVal.size(), holdings.size());
		assertEquals(returnVal.get(0).getHoldingId(), holdings.get(0).getHoldingId());
	}
	
	@Test
	public void testFindHoldingsForAccountNullAccountId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.findHoldingsForAccount(null);
		});
		
		assertEquals("accountId must be non-null", e.getMessage());
	}
	
	@Test
	public void testSellHolding_Success() throws Exception {
		
		AccountDTO a = EntityTestData.getAccount1();
		InvestmentDTO i = EntityTestData.getInvestmentAAPL();
		HoldingDTO h = EntityTestData.getHolding1();
		h.setAccount(a);
		h.setInvestment(i);
		
		Long transactionId = 1L;
		Date saleDate = new Date();
		BigDecimal tradeQuantity = BigDecimal.valueOf(10);
		BigDecimal tradePrice = BigDecimal.valueOf(25);
		
		TransactionDTO existingTransaction = EntityTestData.getBuyTransaction1();
		existingTransaction.setAccount(a);
		existingTransaction.setHolding(h);
		existingTransaction.setTransactionId(transactionId);
		existingTransaction.setHolding(h);
		
		Mockito.when(this.transactionsRepo.findById(existingTransaction.getTransactionId())).thenReturn(Optional.of(existingTransaction));
		Mockito.when(this.holdingsRepo.findById(h.getHoldingId())).thenReturn(Optional.of(h));
		
		Mockito.when(this.transactionsRepo.save(Mockito.any(TransactionDTO.class))).thenAnswer(invocation ->  {
			Object[] args = invocation.getArguments();
			TransactionDTO t = (TransactionDTO) args[0];
			if (t.getTransactionType().equals(TransactionTypeEnum.Sell)) {
	        	assertEquals(h, t.getHolding());
	        	assertEquals(tradePrice, t.getTradePrice());
	        	assertEquals(tradeQuantity, t.getTradeQuantity());
	        	assertEquals(saleDate, t.getTransactionDate());
	        	assertNotNull(t.getAssociatedCashTransactionId());
	        }
	        else if (t.getTransactionType().equals(TransactionTypeEnum.Cash)) {
	        	t.setTransactionId(2L);
	        	assertEquals(a, t.getAccount());
	        	assertEquals(tradePrice, t.getTradePrice());
	        	assertEquals(tradeQuantity, t.getTradeQuantity());
	        	assertEquals(saleDate, t.getTransactionDate());
	        }
	        return t;
		});
		
		this.holdingsService.sellHolding(transactionId, saleDate, tradeQuantity, tradePrice);
		assertEquals(BigDecimal.valueOf(0), h.getQuantity());
		
	}
	
	@Test
	public void testSellHoldingNulls_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.sellHolding(null, null, null, null);
		});
		
		assertEquals("Required attributes are null", e.getMessage());
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.sellHolding(1L, null, null, null);
		});
		
		assertEquals("Required attributes are null", e.getMessage());
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.sellHolding(1L, new Date(), null, null);
		});
		
		assertEquals("Required attributes are null", e.getMessage());
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.sellHolding(1L, new Date(), BigDecimal.valueOf(1), null);
		});
		
		assertEquals("Required attributes are null", e.getMessage());
	}
	
	@Test
	public void testFindHoldingByAccountAccountIdAndInvestmentInvestmentId_Success() throws Exception {
		
		AccountDTO a = EntityTestData.getAccount1();
		InvestmentDTO i = EntityTestData.getInvestmentAAPL();
		List<HoldingDTO> holdings = EntityTestData.getHoldingsForAccount(a);
		
		Mockito.when(this.holdingsRepo.findByAccountAccountIdAndInvestmentInvestmentId(a.getAccountId(), i.getInvestmentId())).thenReturn(holdings);
		
		List<Holding> returnedList = this.holdingsService.findHoldingByAccountAccountIdAndInvestmentInvestmentId(a.getAccountId(), i.getInvestmentId());
		
		assertNotNull(returnedList);
		assertEquals(holdings.size(), returnedList.size());
		assertEquals(holdings.get(0).getHoldingId(), returnedList.get(0).getHoldingId());
	}
	
	@Test
	public void testFindHoldingByAccountAccountIdAndInvestmentInvestmentIdNulls_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.findHoldingByAccountAccountIdAndInvestmentInvestmentId(null, null);
		});
		
		assertEquals("accountId and investmentId must be non-null", e.getMessage());
	}
	
	@Test
	public void testFindHoldingByHoldingId_Success() throws Exception {
		
		HoldingDTO h = EntityTestData.getHolding2();
		h.setAccount(EntityTestData.getAccount3());
		h.setInvestment(EntityTestData.getInvestmentPVTL());
		
		List<TransactionDTO> transactions = new ArrayList<>(Arrays.asList(
				EntityTestData.getBuyTransaction1(), 
				EntityTestData.getBuyTransaction2(), 
				EntityTestData.getCashTransaction1()));
		
		Mockito.when(this.holdingsRepo.findById(h.getHoldingId())).thenReturn(Optional.of(h));
		Mockito.when(this.transactionsRepo.findAllByAccountAndHolding(h.getAccount(), h)).thenReturn(transactions);
		
		Optional<Holding> optHolding = this.holdingsService.findHoldingByHoldingId(h.getHoldingId());
		assertTrue(optHolding.isPresent());
		
		Holding holding = optHolding.get();
		
		assertNotNull(holding);
		assertNotNull(holding.getTransactions());
		assertEquals(transactions.size(), holding.getTransactions().size());
	}
	
	@Test
	public void testFindHoldingByHoldingIdNullId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.findHoldingByHoldingId(null);
		});
		
		assertEquals("holding id must not be null", e.getMessage());
	}
	
	@Test
	public void testFindHoldingByHoldingIdNotFound_Success() throws Exception {
		
		Mockito.when(this.holdingsService.findHoldingByHoldingId(1L)).thenReturn(Optional.empty());
		
		Optional<Holding> optHolding =  this.holdingsService.findHoldingByHoldingId(1L);
		
		assertFalse(optHolding.isPresent());
	}
	
	@Test
	public void testFindHoldingByHoldingIdWithBuyTransactions_Success() throws Exception {
		
		HoldingDTO h = EntityTestData.getHolding2();
		h.setAccount(EntityTestData.getAccount3());
		h.setInvestment(EntityTestData.getInvestmentPVTL());
		
		TransactionDTO transaction = EntityTestData.getBuyTransaction1();
		
		Mockito.when(this.holdingsRepo.findById(h.getHoldingId())).thenReturn(Optional.of(h));
		Mockito.when(this.transactionsRepo.findBuyTransactionforHoldingId(h.getHoldingId())).thenReturn(Optional.of(transaction));
		
		Optional<Holding> optHolding = this.holdingsService.findHoldingByHoldingIdWithBuyTransaction(h.getHoldingId());
		assertTrue(optHolding.isPresent());
		
		Holding holding = optHolding.get();
		
		assertNotNull(holding);
		assertNotNull(holding.getTransactions());
		assertEquals(1, holding.getTransactions().size());
		assertEquals(TransactionTypeEnum.Buy, holding.getTransactions().get(0).getTransactionType());
	}
	
	@Test
	public void testFindHoldingByHoldingIdWithBuyTransactionsNullId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.findHoldingByHoldingIdWithBuyTransaction(null);
		});
		
		assertEquals("holding id must not be null", e.getMessage());
	}
	
	@Test
	public void testUpdateHoldingAndTrade_Success() throws Exception {
		
		HoldingDTO holdingData = EntityTestData.getHolding1();
		Holding holding = ObjectMappingUtil.mapHoldingDtoToHolding(holdingData);
		TransactionDTO transactionData = EntityTestData.getBuyTransaction1();
		Transaction transaction = ObjectMappingUtil.mapTransactionDtoToTransaction(transactionData);
		
		Mockito.when(this.holdingsRepo.save(holdingData)).thenReturn(holdingData);
		Mockito.when(this.transactionsRepo.save(transactionData)).thenReturn(transactionData);
		
		assertNotNull(transactionData.getHolding());
		
		this.holdingsService.updateHoldingAndTrade(holding, transaction);
		
		assertEquals(holdingData, transactionData.getHolding());
	}
	
	@Test
	public void testUpdateHoldingAndTradeNullId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.updateHoldingAndTrade(null, null);
		});
		
		assertEquals("holding and trade must be non-null", e.getMessage());
		
		e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.updateHoldingAndTrade(new Holding(), null);
		});
		
		assertEquals("holding and trade must be non-null", e.getMessage());
	}
	
	@Test
	public void testFindAllHoldings_Success() throws Exception {
		
		List<HoldingDTO> holdings = EntityTestData.getHoldingsForAccount(EntityTestData.getAccount1());
		
		Mockito.when(this.holdingsRepo.findAll()).thenReturn(holdings);
		
		List<Holding> values = this.holdingsService.findAllHoldings();
		
		assertNotNull(values);
		assertEquals(holdings.size(), values.size());
	}
	
	@Test
	public void testFindAllHoldingsOrderedBySymbol_Success() throws Exception {
		
		List<HoldingDTO> holdings = EntityTestData.getHoldingsForAccount(EntityTestData.getAccount1());
		
		Mockito.when(this.holdingsRepo.findAll(Sort.by(Sort.Direction.ASC, "investment.symbol"))).thenReturn(holdings);
		
		List<Holding> values = this.holdingsService.findAllHoldingsOrderedBySymbol();
		
		assertNotNull(values);
		assertEquals(holdings.size(), values.size());
	}
	
	@Test
	public void testFindHoldingsByInvesmentId_Success() throws Exception {
		
		InvestmentDTO i = EntityTestData.getInvestmentAAPL();
		AccountDTO a = EntityTestData.getAccount1();
		List<HoldingDTO> holdings = EntityTestData.getHoldingsForAccount(a);
		
		Mockito.when(this.holdingsRepo.findHoldingsByInvestmentInvestmentId(i.getInvestmentId())).thenReturn(holdings);
		
		List<Holding> values = this.holdingsService.findHoldingsByInvestmentId(i.getInvestmentId());
		
		assertNotNull(values);
		assertEquals(holdings.size(), values.size());
	}
	
	@Test
	public void testFindHoldingsByInvesmentIdNullId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.findHoldingsByInvestmentId(null);
		});
		
		assertEquals("investmentId must be non-null", e.getMessage());
	}
	
	@Test
	public void testPersistBuy_Success() throws Exception {
		Date tradeDate = new Date(); 
		Float price = 12.25F;
		Float quantity = 100F;
		Long investmentId = 1L;
		Long accountId = 1L;
		
		AccountDTO a = new AccountDTO(accountId);
		InvestmentDTO i = new InvestmentDTO(investmentId);
		
		HoldingDTO h = new HoldingDTO();
		h.setAccount(a);
		h.setInvestment(i);
		h.setHoldingId(12L);
		
		Mockito.when(this.holdingsRepo.save(Mockito.any(HoldingDTO.class))).thenReturn(h);
		Mockito.when(this.transactionsRepo.save(Mockito.any(TransactionDTO.class))).thenAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			TransactionDTO t = (TransactionDTO) args[0];
			if (t.getTransactionType().equals(TransactionTypeEnum.Cash)) {
				t.setTransactionId(1L);
				assertEquals(a, t.getAccount());
				assertEquals(-1F, t.getTradePrice());
				assertEquals(price * quantity, t.getTradeQuantity());
				assertEquals(tradeDate, t.getTransactionDate());
			}
			else {
				assertEquals(tradeDate, t.getTransactionDate());
				assertEquals(price, t.getTradePrice());
				assertEquals(quantity, t.getTradeQuantity());
				assertEquals(a, t.getAccount());
				assertNotNull(t.getAssociatedCashTransactionId());
				assertEquals(1L, t.getTransactionId());
			}
			return t;
		});
	}
	
	@Test
	public void testPersistBuyNulls_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.holdingsService.persistBuy(null, null, null, null, null);
		});
		
		assertEquals("required attributes are null", e.getMessage());
	}

}
