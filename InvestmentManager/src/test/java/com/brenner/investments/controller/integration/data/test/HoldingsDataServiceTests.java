package com.brenner.investments.controller.integration.data.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import com.brenner.investments.data.AccountDataService;
import com.brenner.investments.data.HoldingsReportingDataService;
import com.brenner.investments.data.InvestmentsDataService;
import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.pojo.HoldingsReport;
import com.brenner.investments.test.TestDataHelper;

@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles("test")
public class HoldingsDataServiceTests {
	
	@Autowired
	HoldingsReportingDataService holdingDataService;
	
	@Autowired
	AccountDataService accountDataService;
	
	@Autowired
	InvestmentsDataService investmentsDataService;
	
	Holding h1;
	Holding h2;
	Holding h3;
	Holding zeroQtyHolding;
	
	Account a1;
	Account a2;
	Account a3;
	
	Investment aapl;
	Investment fb;
	Investment ge;
	Investment pvtl;
	
	@Before
	public void setup() throws Exception {
		
		this.a1 = TestDataHelper.getAccount1();
		this.a1.setAccountId(null);
		this.a2 = TestDataHelper.getAccount2();
		this.a2.setAccountId(null);
		this.a3 = TestDataHelper.getAccount3();
		this.a3.setAccountId(null);
		
		this.aapl = TestDataHelper.getInvestmentAAPL();
		this.aapl.setInvestmentId(null);
		this.fb = TestDataHelper.getInvestmentFB();
		this.fb.setInvestmentId(null);
		this.ge = TestDataHelper.getInvestmentGE();
		this.ge.setInvestmentId(null);
		this.pvtl = TestDataHelper.getInvestmentPVTL();
		this.pvtl.setInvestmentId(null);
		
		this.h1 = TestDataHelper.getHolding1();
		this.h1.setHoldingId(null);
		this.h1.setAccount(this.a1);
		this.h1.setInvestment(this.aapl);
		
		this.h2 = TestDataHelper.getHolding2();
		this.h2.setHoldingId(null);
		this.h2.setAccount(this.a2);
		this.h2.setInvestment(this.fb);
		
		this.h3 = TestDataHelper.getHolding3();
		this.h3.setHoldingId(null);
		this.h3.setAccount(this.a3);
		this.h3.setInvestment(this.pvtl);
		
		this.zeroQtyHolding = TestDataHelper.getZeroQuantityHolding();
		this.zeroQtyHolding.setHoldingId(null);
		this.zeroQtyHolding.setAccount(this.a2);
		this.zeroQtyHolding.setInvestment(this.pvtl);
	}
	
	private int persistData() throws Exception {
		
		this.a1 = this.accountDataService.save(this.a1);
		this.a2 = this.accountDataService.save(this.a2);
		this.a3 = this.accountDataService.save(this.a3);
		
		this.aapl = this.investmentsDataService.save(this.aapl);
		this.fb = this.investmentsDataService.save(this.fb);
		this.ge = this.investmentsDataService.save(this.ge);
		this.pvtl = this.investmentsDataService.save(this.pvtl);
				
		this.h1 = this.holdingDataService.save(this.h1);
		this.h2 = this.holdingDataService.save(this.h2);
		this.h3 = this.holdingDataService.save(this.h3);
		this.zeroQtyHolding = this.holdingDataService.save(this.zeroQtyHolding);
		
		return 4;
	}
	
	private void deleteData() throws Exception {
		
		this.holdingDataService.delete(this.h1.getHoldingId());
		this.holdingDataService.delete(this.h2.getHoldingId());
		this.holdingDataService.delete(this.h3.getHoldingId());
		this.holdingDataService.delete(this.zeroQtyHolding.getHoldingId());
		
		this.accountDataService.delete(this.a1.getAccountId());
		this.accountDataService.delete(this.a2.getAccountId());
		
		this.investmentsDataService.delete(this.aapl);
		this.investmentsDataService.delete(this.fb);
		this.investmentsDataService.delete(this.ge);
		this.investmentsDataService.delete(this.pvtl);
	}
	
	@Test
	public void testUpdateHolding() throws Exception {
		
		persistData();
		
		try {
			Holding testHolding = this.holdingDataService.findById(this.h1.getHoldingId());
			
			assertNotNull(testHolding);
			assertEquals(this.h1.getHoldingId(), testHolding.getHoldingId());
			
			testHolding.setPurchasePrice(100F);
			testHolding.setAccount(this.a2);
			testHolding.setInvestment(this.ge);
			testHolding.setQuantity(9F);
			
			this.holdingDataService.save(testHolding);
			
			Holding newHolding = this.holdingDataService.findById(this.h1.getHoldingId());
			
			assertNotNull(newHolding);
			assertEquals(newHolding.getAccount().getAccountId(), testHolding.getAccount().getAccountId());
			assertEquals(newHolding.getHoldingId(), testHolding.getHoldingId());
			assertEquals(newHolding.getPurchasePrice(), testHolding.getPurchasePrice());
			assertEquals(newHolding.getQuantity(), testHolding.getQuantity());
			assertEquals(newHolding.getInvestment().getInvestmentId(), testHolding.getInvestment().getInvestmentId());
		}
		finally {
		
			deleteData();
		}
	}
	
	@Test
	public void testFindAll() throws Exception {
		
		int num = persistData();
		
		try {
			List<Holding> holdings = this.holdingDataService.findAll();
			
			assertNotNull(holdings);
			assertEquals(num, holdings.size());
		}
		finally {
		
			deleteData();
		}
	}
	
	@Test
	public void testFindById() throws Exception {
		
		persistData();
		
		try {
			Holding h = this.holdingDataService.findById(this.h2.getHoldingId());
			
			assertNotNull(h);
			assertEquals(h.getHoldingId(), this.h2.getHoldingId());
			assertEquals(h.getAccount().getAccountId(), this.h2.getAccount().getAccountId());
			assertEquals(h.getInvestment().getInvestmentId(), this.h2.getInvestment().getInvestmentId());
			assertEquals(h.getPurchasePrice(), this.h2.getPurchasePrice());
			assertEquals(h.getQuantity(), this.h2.getQuantity());
		}
		finally {
		
			deleteData();
		}
	}
	
	@Test
	public void testFindHoldingsByMarketValueOrderedDesc() throws Exception {
		
		int num = persistData();
		
		try {
			List<Holding> holdings = new ArrayList<>(num);
			holdings.add(this.h1);
			holdings.add(this.h2);
			holdings.add(this.h3);
			
			Collections.sort(holdings);
			
			List<HoldingsReport> retHoldings = this.holdingDataService.findHoldingsByMarketValueOrderedDesc(null);
			
			assertNotNull(retHoldings);
			assertEquals(3, retHoldings.size());
			
			//TODO once quotes are complete
		}
		finally {
		
			deleteData();
		}
	}
	
	@Test 
	public void testFindHoldingByChangeInValueOrderedDesc() throws Exception {
		//TODO once quotes is complete
	}
	
	@Test
	public void testFindByAccountIdAndInvestmentId() throws Exception {
		
		persistData();
		
		try {
			Long accountId = this.h3.getAccount().getAccountId();
			Long investmentId = this.h3.getInvestment().getInvestmentId();
			
			List<Holding> holdings = this.holdingDataService.findByAccountIdAndInvestmentId(accountId, investmentId);
			
			assertNotNull(holdings);
			
			Holding h = holdings.get(0);
			assertEquals(accountId, h.getAccount().getAccountId());
			assertEquals(h.getInvestment().getInvestmentId(), investmentId);
		}
		finally {
		
			deleteData();
		}
	}
}

class HoldingComparator implements Comparator<Holding> {

	@Override
	public int compare(Holding o1, Holding o2) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}