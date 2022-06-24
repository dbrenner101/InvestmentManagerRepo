package com.brenner.investments.service.unit.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import com.brenner.investments.data.InvestmentsDataService;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.service.implementation.PersistentInvestmentsService;
import com.brenner.investments.test.TestDataHelper;

@ActiveProfiles("test")
public class InvestmentRepositoryTests {
	
	@Mock
	InvestmentsDataService investmentsRepo;
	
	private PersistentInvestmentsService investmentsDataService;
	
	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		this.investmentsDataService = new PersistentInvestmentsService();
		this.investmentsDataService.setInvestmentRepo(this.investmentsRepo);
	}
	
	@Test()
	public void testGetInvestmentsOrderedBySymbolAsc() throws Exception {
		
		List<Investment> returnList = new ArrayList<>(4);
		returnList.add(TestDataHelper.getInvestmentAAPL());
		returnList.add(TestDataHelper.getInvestmentFB());
		returnList.add(TestDataHelper.getInvestmentGE());
		returnList.add(TestDataHelper.getInvestmentPVTL());
		
		when(this.investmentsRepo.findAllByOrderBySymbolAsc()).thenReturn(returnList);
		
		List<Investment> allInvestments = this.investmentsDataService.getInvestmentsOrderedBySymbolAsc();
		assertNotNull(allInvestments);
		assertTrue(! allInvestments.isEmpty());
		assertEquals(4, allInvestments.size());
		
		assertTrue(allInvestments.get(0).getSymbol().equals("AAPL"));
		assertTrue(allInvestments.get(1).getSymbol().equals("FB"));
		assertTrue(allInvestments.get(2).getSymbol().equals("GE"));
		assertTrue(allInvestments.get(3).getSymbol().equals("PVTL"));
	}
	
	@Test()
	public void testGetInvestmentBySymbol() throws Exception {
		
		Investment investment = TestDataHelper.getInvestmentGE();
		
		when(this.investmentsRepo.findBySymbol("GE")).thenReturn(investment);
		
		
		Investment inv = this.investmentsDataService.getInvestmentBySymbol("GE");
		assertNotNull(inv);
		assertTrue(inv.getSymbol().equals("GE"));
		
		Investment inv2 = this.investmentsDataService.getInvestmentBySymbol("ZZZ");
		assertNull(inv2);;
	}
	
	@Test()
	public void testGetAllInvestments() throws Exception {
		
		List<Investment> returnInv = new ArrayList<>(4);
		returnInv.add(TestDataHelper.getInvestmentAAPL());
		returnInv.add(TestDataHelper.getInvestmentFB());
		returnInv.add(TestDataHelper.getInvestmentGE());
		returnInv.add(TestDataHelper.getInvestmentPVTL());
		
		when(this.investmentsRepo.findAllByOrderBySymbolAsc()).thenReturn(returnInv);
		
		Iterable<Investment> allInvestments = this.investmentsDataService.getAllInvestments();
		assertNotNull(allInvestments);
		
		Iterator<Investment> invIter = allInvestments.iterator();
		List<Investment> allInvList = new ArrayList<>();
		while (invIter.hasNext()) {
			allInvList.add(invIter.next());
		}
		
		assertEquals(4, allInvList.size());
		
	}
	
	@Test
	public void testGetInvestmentByInvestmentId() {
		
		Investment inv = TestDataHelper.getInvestmentGE();
		when(this.investmentsRepo.findById(TestDataHelper.getInvestmentGE().getInvestmentId())).thenReturn(inv);
		
		Investment i = this.investmentsDataService.getInvestmentByInvestmentId(TestDataHelper.getInvestmentGE().getInvestmentId());
		assertNotNull(i);
		assertEquals(TestDataHelper.getInvestmentGE().getInvestmentId(), i.getInvestmentId());
		assertEquals("GE", i.getSymbol());
	}

}
