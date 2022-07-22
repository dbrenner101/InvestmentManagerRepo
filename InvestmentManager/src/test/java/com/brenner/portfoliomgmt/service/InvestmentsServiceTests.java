/**
 * 
 */
package com.brenner.portfoliomgmt.service;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;

import com.brenner.portfoliomgmt.data.entities.InvestmentDTO;
import com.brenner.portfoliomgmt.data.repo.InvestmentsRepository;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.test.DomainTestData;
import com.brenner.portfoliomgmt.test.EntityTestData;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		InvestmentsService.class,
		InvestmentsRepository.class
})
@DirtiesContext
@TestInstance(Lifecycle.PER_METHOD)
public class InvestmentsServiceTests {

	@MockBean
	InvestmentsRepository investmentRepo;
	
	@Autowired
	InvestmentsService service;
	
	Investment aapl = DomainTestData.getInvestmentAAPL();
	Investment ge = DomainTestData.getInvestmentGE();
	Investment pvtl = DomainTestData.getInvestmentPVTL();
	Investment fb = DomainTestData.getInvestmentFB();
	
	List<InvestmentDTO> allInvestments = EntityTestData.getAllInvestments();
	
	@Test
	public void testDeleteInvestment_Success() throws Exception {
		InvestmentDTO investmentData = EntityTestData.getInvestmentAAPL();
		
		Mockito.when(this.investmentRepo.findById(aapl.getInvestmentId())).thenReturn(Optional.of(investmentData));
		
		assertThatNoException().isThrownBy(() -> this.service.deleteInvestment(aapl));
	}
	
	@Test
	public void testDeleteInvestmentNullValue_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.deleteInvestment(null);
		});
		
		assertEquals("investment and investmentId must not be null", e.getMessage());
	}
	
	@Test
	public void testDeleteInvestmentNullId_Fail() throws Exception {
		
		Investment inv = new Investment();
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.deleteInvestment(inv);
		});
		
		assertEquals("investment and investmentId must not be null", e.getMessage());
	}
	
	@Test
	public void testDeleteInvestmentNotFound_Fail() throws Exception {
		
		Mockito.when(this.investmentRepo.findById(ge.getInvestmentId())).thenReturn(Optional.empty());
		
		Exception e = assertThrows(NotFoundException.class, () -> {
			this.service.deleteInvestment(ge);
		});
		
		assertEquals("Investment with id " + ge.getInvestmentId() + " does not exist.", e.getMessage());
	}
	
	@Test
	public void testFindInvestmentsAndQuotesAssociatedWithAHolding_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.getInvestmentsAllForHoldingsWithQuotesOrderedBySymbol()).thenReturn(allInvestments);
		
		List<Investment> investments = this.service.findInvestmentsForHoldingsWithQuotes();
		
		assertNotNull(investments);
		assertEquals(allInvestments.size(), investments.size());
		assertEquals(allInvestments.get(0).getInvestmentId(), investments.get(0).getInvestmentId());
	}
	
	@Test
	public void testFindtInvestmentsOrderedBySymbol_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findAll(Sort.by(Sort.Direction.ASC, "symbol"))).thenReturn(allInvestments);
		
		List<Investment> investments = this.service.findInvestments("symbol");
		
		assertNotNull(investments);
		assertEquals(allInvestments.size(), investments.size());
		assertEquals(allInvestments.get(0).getInvestmentId(), investments.get(0).getInvestmentId());
	}
	
	@Test
	public void testFindInvestmentBySymbol_Success() throws Exception {
		
		InvestmentDTO investmentData = EntityTestData.getInvestmentPVTL();
		
		Mockito.when(this.investmentRepo.findBySymbol(pvtl.getSymbol())).thenReturn(Optional.of(investmentData));
		
		Optional<Investment> optInvestment = this.service.findInvestmentBySymbol(pvtl.getSymbol());
		assertTrue(optInvestment.isPresent());
		
		Investment i = optInvestment.get();
		
		assertNotNull(i);
		assertEquals(pvtl, i);
	}
	
	@Test
	public void testFindInvestmentBySymbolNullSymbol_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.findInvestmentBySymbol(null);
		});
		
		assertEquals("symbol must not be null.", e.getMessage());
	}
	
	@Test
	public void testFindInvestmentBySymbolNotFound_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findBySymbol("FOO")).thenReturn(Optional.empty());
		
		Optional<Investment> optInvestment = this.service.findInvestmentBySymbol(pvtl.getSymbol());
		assertFalse(optInvestment.isPresent());
	}
	
	@Test
	public void testSaveInvestment_Success() throws Exception {
		
		InvestmentDTO investmentData = EntityTestData.getInvestmentAAPL();
		
		Mockito.when(this.investmentRepo.save(investmentData)).thenReturn(investmentData);
		
		Investment i = this.service.saveInvestment(aapl);
		
		assertNotNull(i);
		assertEquals(aapl, i);
	}
	
	@Test
	public void testSaveInvestmentNull_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.saveInvestment(null);
		});
		
		assertEquals("investment must not be null", e.getMessage());
	}
	
	@Test
	public void testFindAllInvestments() throws Exception {
		
		Mockito.when(this.investmentRepo.findAll(Sort.by(Sort.Direction.ASC, "symbol"))).thenReturn(allInvestments);
		
		List<Investment> invs = this.service.findAllInvestments("symbol");
		
		assertNotNull(invs);
		assertEquals(allInvestments.size(), invs.size());
		assertEquals(allInvestments.get(0).getInvestmentId(), invs.get(0).getInvestmentId());
	}
	
	@Test
	public void testFindInvestmentByInvestmentId_Success() throws Exception {
		
		InvestmentDTO ge = EntityTestData.getInvestmentGE();
		
		Mockito.when(this.investmentRepo.findById(ge.getInvestmentId())).thenReturn(Optional.of(ge));
		
		Optional<Investment> optInvestment = this.service.findInvestmentByInvestmentId(ge.getInvestmentId());
		assertTrue(optInvestment.isPresent());
		
		Investment i = optInvestment.get();
		
		assertNotNull(i);
		assertEquals(ge.getInvestmentId(), i.getInvestmentId());
		assertEquals(ge.getSymbol(), i.getSymbol());
		assertEquals(ge.getInvestmentType(), i.getInvestmentType());
		assertEquals(ge.getExchange(), i.getExchange());
		assertEquals(ge.getCompanyName(), i.getCompanyName());
	}
	
	@Test
	public void testFindInvestmentByInvestmentIdNotFound_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findById(ge.getInvestmentId())).thenReturn(Optional.empty());
		
		Optional<Investment> optInvestment = this.service.findInvestmentByInvestmentId(ge.getInvestmentId());
		assertFalse(optInvestment.isPresent());
	}
	
	@Test
	public void testFindInvestmentByInvestmentIdNullId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.findInvestmentByInvestmentId(null);
		});
		
		assertEquals("investmentId must not be null", e.getMessage());
	}
	
	@Test
	public void testFindInvestmentsForHoldingsWithQuotes_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.getInvestmentsAllForHoldingsWithQuotesOrderedBySymbol()).thenReturn(allInvestments);
		
		List<Investment> invs = this.service.findInvestmentsForHoldingsWithQuotes();
		
		assertNotNull(invs);
		assertEquals(allInvestments.size(), invs.size());
		assertEquals(allInvestments.get(0).getInvestmentId(), invs.get(0).getInvestmentId());
	}
	
	@Test
	public void testFindUniqueInvestmentsForHoldingsOrderBySymbol_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findUniqueInvestmentsForHoldingsOrderBySymbol()).thenReturn(allInvestments);
		
		List<Investment> invs = this.service.findUniqueInvestmentsForHoldingsOrderBySymbol();
		
		assertNotNull(invs);
		assertEquals(allInvestments.size(), invs.size());
		assertEquals(allInvestments.get(0).getInvestmentId(), invs.get(0).getInvestmentId());
	}
	
	@Test
	public void testFindInvestmentsForHoldingsOrderedBySymbol_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findInvestmentsForHoldingsOrderedBySymbol()).thenReturn(allInvestments);
		
		List<Investment> invs = this.service.findInvestmentsForHoldingsOrderedBySymbol();
		
		assertNotNull(invs);
		assertEquals(allInvestments.size(), invs.size());
		assertEquals(allInvestments.get(0).getInvestmentId(), invs.get(0).getInvestmentId());
	}

}
