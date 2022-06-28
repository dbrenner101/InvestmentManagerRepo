/**
 * 
 */
package com.brenner.investments.service;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

import com.brenner.investments.data.InvestmentsRepository;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.test.TestDataHelper;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest
@DirtiesContext
@TestInstance(Lifecycle.PER_METHOD)
public class InvestmentsServiceTests {

	@MockBean
	InvestmentsRepository investmentRepo;
	
	@Autowired
	InvestmentsService service;
	
	Investment aapl = TestDataHelper.getInvestmentAAPL();
	Investment ge = TestDataHelper.getInvestmentGE();
	Investment pvtl = TestDataHelper.getInvestmentPVTL();
	Investment fb = TestDataHelper.getInvestmentFB();
	
	List<Investment> allInvestments = TestDataHelper.getAllInvestments();
	
	@Test
	public void testGetInvestmentsBySymbolAlpha_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findLikeSymbol(ge.getSymbol())).thenReturn(allInvestments);
		
		List<Investment> inv = this.service.getInvestmentsBySymbolAlpha(ge.getSymbol());
		assertNotNull(inv);
		assertEquals(allInvestments, inv);
	}
	
	@Test
	public void testGetInvestmentsBySymbolAlpha_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.getInvestmentsBySymbolAlpha(null);
		});
		
		assertEquals("Search string must be non-null.", e.getMessage());
	}
	
	@Test
	public void testDeleteInvestment_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findById(aapl.getInvestmentId())).thenReturn(Optional.of(aapl));
		
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
	public void testGetInvestmentsAndQuotesAssociatedWithAHolding_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.getInvestmentsAllForHoldingsWithQuotesOrderedBySymbol()).thenReturn(allInvestments);
		
		List<Investment> investments = this.service.getInvestmentsForHoldingsWithQuotes();
		
		assertNotNull(investments);
		assertEquals(allInvestments, investments);
	}
	
	@Test
	public void getInvestmentsOrderedBySymbol_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findAll(Sort.by(Sort.Direction.ASC, "symbol"))).thenReturn(allInvestments);
		
		List<Investment> investments = this.service.getInvestmentsOrderedBySymbolAsc();
		
		assertNotNull(investments);
		assertEquals(allInvestments, investments);
	}
	
	@Test
	public void testGetInvestmentBySymbol_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findBySymbol(pvtl.getSymbol())).thenReturn(Optional.of(pvtl));
		
		Investment i = this.service.getInvestmentBySymbol(pvtl.getSymbol());
		
		assertNotNull(i);
		assertEquals(pvtl, i);
	}
	
	@Test
	public void testGetInvestmentBySymbolNullSymbol_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.getInvestmentBySymbol(null);
		});
		
		assertEquals("symbol must not be null.", e.getMessage());
	}
	
	@Test
	public void testGetInvestmentBySymbolNotFound_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findBySymbol("FOO")).thenReturn(Optional.empty());
		
		Investment i = this.service.getInvestmentBySymbol(pvtl.getSymbol());
		
		assertNull(i);
	}
	
	@Test
	public void testSaveInvestment_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.save(aapl)).thenReturn(aapl);
		
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
	public void testSaveInvestments_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.save(aapl)).thenReturn(aapl);
		Mockito.when(this.investmentRepo.save(pvtl)).thenReturn(pvtl);
		Mockito.when(this.investmentRepo.save(ge)).thenReturn(ge);
		Mockito.when(this.investmentRepo.save(fb)).thenReturn(fb);
		
		assertThatNoException().isThrownBy(() -> {
			this.service.saveInvestments(allInvestments);
		});
	}
	
	@Test
	public void testGetAllInvestments() throws Exception {
		
		Mockito.when(this.investmentRepo.findAll(Sort.by(Sort.Direction.ASC, "symbol"))).thenReturn(allInvestments);
		
		List<Investment> invs = this.service.getAllInvestmentsSortedBySymbol();
		
		assertNotNull(invs);
		assertEquals(allInvestments, invs);
	}
	
	@Test
	public void testGetInvestmentByInvestmentId_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findById(ge.getInvestmentId())).thenReturn(Optional.of(ge));
		
		Investment i = this.service.getInvestmentByInvestmentId(ge.getInvestmentId());
		
		assertNotNull(i);
		assertEquals(ge, i);
	}
	
	@Test
	public void testGetInvestmentByInvestmentIdNotFound_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findById(ge.getInvestmentId())).thenReturn(Optional.empty());
		
		Investment i = this.service.getInvestmentByInvestmentId(ge.getInvestmentId());
		
		assertNull(i);
	}
	
	@Test
	public void testGetInvestmentByInvestmentIdNullId_Fail() throws Exception {
		
		Exception e = assertThrows(InvalidRequestException.class, () -> {
			this.service.getInvestmentByInvestmentId(null);
		});
		
		assertEquals("investmentId must not be null", e.getMessage());
	}
	
	@Test
	public void testGetInvestmentsForHoldingsWithQuotes_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.getInvestmentsAllForHoldingsWithQuotesOrderedBySymbol()).thenReturn(allInvestments);
		
		List<Investment> invs = this.service.getInvestmentsForHoldingsWithQuotes();
		
		assertNotNull(invs);
		assertEquals(allInvestments, invs);
	}
	
	@Test
	public void testfindUniqueInvestmentsForHoldingsOrderBySymbol_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findUniqueInvestmentsForHoldingsOrderBySymbol()).thenReturn(allInvestments);
		
		List<Investment> invs = this.service.findUniqueInvestmentsForHoldingsOrderBySymbol();
		
		assertNotNull(invs);
		assertEquals(allInvestments, invs);
	}
	
	@Test
	public void testFindInvestmentsForHoldingsOrderedBySymbol_Success() throws Exception {
		
		Mockito.when(this.investmentRepo.findInvestmentsForHoldingsOrderedBySymbol()).thenReturn(allInvestments);
		
		List<Investment> invs = this.service.findInvestmentsForHoldingsOrderedBySymbol();
		
		assertNotNull(invs);
		assertEquals(allInvestments, invs);
	}

}
