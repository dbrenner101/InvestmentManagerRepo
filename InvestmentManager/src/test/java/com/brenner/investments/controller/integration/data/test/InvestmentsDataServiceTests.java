package com.brenner.investments.controller.integration.data.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.brenner.investments.data.InvestmentsDataService;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.constants.InvestmentTypeEnum;
import com.brenner.investments.test.TestDataHelper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles("test")
public class InvestmentsDataServiceTests {
	
	@Autowired
	InvestmentsDataService investmentsDataService;
	
	Investment aapl;
	Investment fb;
	Investment pvtl;
	Investment ge;
	
	@Before
	public void setUp() throws Exception {
		
		this.aapl = TestDataHelper.getInvestmentAAPL();
		this.aapl.setInvestmentId(null);
		this.fb = TestDataHelper.getInvestmentFB();
		this.fb.setInvestmentId(null);
		this.pvtl = TestDataHelper.getInvestmentPVTL();
		this.pvtl.setInvestmentId(null);
		this.ge = TestDataHelper.getInvestmentGE();
		this.ge.setInvestmentId(null);
	}
	
	private int persistData() throws Exception {
		
		this.aapl = this.investmentsDataService.save(this.aapl);
		this.fb = this.investmentsDataService.save(this.fb);
		this.pvtl = this.investmentsDataService.save(this.pvtl);
		this.ge = this.investmentsDataService.save(this.ge);
		
		return 4;
	}
	
	private void deleteData() throws Exception {
		
		this.investmentsDataService.delete(this.aapl);
		this.investmentsDataService.delete(this.fb);
		this.investmentsDataService.delete(this.pvtl);
		this.investmentsDataService.delete(this.ge);
	}
	
	@Test
	public void testFindBySymbol() throws Exception {
		
		persistData();
		
		Investment p = this.investmentsDataService.findBySymbol(TestDataHelper.getInvestmentPVTL().getSymbol());
		
		assertNotNull(p);
		assertEquals(this.pvtl.getInvestmentId(), p.getInvestmentId());
		assertEquals(this.pvtl.getCompanyName(), p.getCompanyName());
		assertEquals(this.pvtl.getExchange(), p.getExchange());
		assertEquals(this.pvtl.getInvestmentType(), p.getInvestmentType());
		assertEquals(this.pvtl.getSector(), p.getSector());
		assertEquals(this.pvtl.getSymbol(), p.getSymbol());
		
		deleteData();
	}
	
	@Test
	public void testFindAllByOrderByCompanyNameAsc() throws Exception {
		
		int num = persistData();
		
		List<Investment> invest = this.investmentsDataService.findAllByOrderByCompanyNameAsc();
		
		assertNotNull(invest);
		assertEquals(num, invest.size());
		assertEquals(this.aapl.getSymbol(), invest.get(0).getSymbol());
		assertEquals(this.fb.getSymbol(), invest.get(1).getSymbol());
		assertEquals(this.ge.getSymbol(), invest.get(2).getSymbol());
		assertEquals(this.pvtl.getSymbol(), invest.get(3).getSymbol());
		
		deleteData();
	}
	
	@Test
	public void testFindAllByOrderBySymbolAsc() throws Exception {
		
		int num = persistData();
		
		List<Investment> invest = this.investmentsDataService.findAllByOrderByCompanyNameAsc();
		
		assertNotNull(invest);
		assertEquals(num, invest.size());
		assertEquals(this.aapl.getSymbol(), invest.get(0).getSymbol());
		assertEquals(this.fb.getSymbol(), invest.get(1).getSymbol());
		assertEquals(this.ge.getSymbol(), invest.get(2).getSymbol());
		assertEquals(this.pvtl.getSymbol(), invest.get(3).getSymbol());
		
		deleteData();
	}
	
	@Test
	public void testFindIvestmentsForHoldingsOrderedBySymbol() throws Exception {
		//TODO update test after holdings data persistence is available
	}
	
	@Test
	public void testGetInvestmentsForHoldingsWithQuotes() throws Exception {
		//TODO update test after holdings data persistence is available
	}
	
	@Test
	public void testFindUniqueInvestmentsForHoldingsOrderBySymbol() throws Exception {
		//TODO update test after holdings data persistence is available
	}
	
	@Test
	public void testDelete() throws Exception {
		
		int num = persistData();
		
		List<Investment> allInv = this.investmentsDataService.findAllByOrderBySymbolAsc();
		
		assertEquals(num, allInv.size());
		
		this.investmentsDataService.delete(this.aapl);
		allInv = this.investmentsDataService.findAllByOrderBySymbolAsc();
		
		assertEquals(num - 1, allInv.size());
		assertTrue(! allInv.contains(this.aapl));
		
		deleteData();
	}
	
	@Test
	public void testUpdate() throws Exception {
		
		persistData();
		
		final String NAME = "FooBar";
		final String EXCHANGE = "Nothing";
		final InvestmentTypeEnum ENUM = InvestmentTypeEnum.Bond;
		final String SECTOR = "TUESDAY";
		final String SYMBOL = "YELL";
		
		this.aapl.setCompanyName(NAME);
		this.aapl.setExchange(EXCHANGE);
		this.aapl.setInvestmentType(ENUM);
		this.aapl.setSector(SECTOR);
		this.aapl.setSymbol(SYMBOL);
		
		this.investmentsDataService.save(this.aapl);
		
		Investment i = this.investmentsDataService.findBySymbol(SYMBOL);
		
		assertNotNull(i);
		assertEquals(this.aapl.getInvestmentId(), i.getInvestmentId());
		assertEquals(NAME, i.getCompanyName());
		assertEquals(EXCHANGE, i.getExchange());
		assertEquals(ENUM, i.getInvestmentType());
		assertEquals(SECTOR, i.getSector());
		assertEquals(SYMBOL, i.getSymbol());
		
		deleteData();
	}

}
