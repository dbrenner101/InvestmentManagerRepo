/**
 * 
 */
package com.brenner.investments.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.quote.service.QuoteRetrievalService;
import com.brenner.investments.service.InvestmentsService;
import com.brenner.investments.test.TestDataHelper;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		QuoteRetrievalService.class,
		InvestmentsService.class,
		InvestmentController.class
})
@AutoConfigureMockMvc
@DirtiesContext
public class InvestmentControllerTests {
	
	@Autowired MockMvc mockMvc;
	
	@MockBean QuoteRetrievalService quoteService;
	
	@MockBean InvestmentsService investmentsService;
	
	Investment aapl = TestDataHelper.getInvestmentAAPL();
	Investment fb = TestDataHelper.getInvestmentFB();
	Investment ge = TestDataHelper.getInvestmentGE();
	Investment pvtl = TestDataHelper.getInvestmentPVTL();
	
	Quote aaplQuote = TestDataHelper.getQuoteAAPL();
	Quote fbQuote = TestDataHelper.getQuoteFB();
	
	@Test
	@WithMockUser
	public void testGetInvestmentsAndMostRecentQuoteDate_Success() throws Exception {
		
		aapl.addQuote(aaplQuote);
		fb.addQuote(fbQuote);
		
		List<Investment> investments = new ArrayList<>(Arrays.asList(aapl, fb, ge, pvtl));
		
		Mockito.when(this.investmentsService.getInvestmentsAndQuotesAssociatedWithAHolding()).thenReturn(investments);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/getInvestmentsAndMostRecentQuoteDate"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("investments"))
			.andExpect(model().attribute("investments", investments))
			.andExpect(view().name("investments/listInvestmentsAndQuoteDate"));
	}
	
	@Test
	@WithMockUser
	public void testLookupInvestment_Success() throws Exception {
		
		Mockito.when(this.quoteService.getQuote("FB")).thenReturn(fbQuote);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/lookupInvestment")
				.param("symbol", "FB"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("investment"))
			.andExpect(view().name("investments/newInvestment"));
	}
	
	@Test
	@WithMockUser
	public void testAddInvestment_Success() throws Exception {
		
		Mockito.when(this.investmentsService.getInvestmentBySymbol(aapl.getSymbol())).thenReturn(aapl);
		Mockito.when(this.investmentsService.saveInvestment(aapl)).thenReturn(aapl);
		
		MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
		requestParams.add("companyName", aapl.getCompanyName());
		requestParams.add("symbol", aapl.getSymbol());
		requestParams.add("exchange", aapl.getExchange());
		requestParams.add("sector", aapl.getSector());
		requestParams.add("investmentType", aapl.getInvestmentType().getDisplayName());
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/addInvestment")
				.params(requestParams))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("investment"))
			.andExpect(model().attribute("investment", aapl))
			.andExpect(view().name("investments/addInvestmentEntryPage"));
	}
	
	@Test
	@WithMockUser
	public void testAddInvestmentNotFound_Success() throws Exception {
		
		Mockito.when(this.investmentsService.getInvestmentBySymbol(aapl.getSymbol())).thenReturn(null);
		
		MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
		requestParams.add("companyName", aapl.getCompanyName());
		requestParams.add("symbol", aapl.getSymbol());
		requestParams.add("exchange", aapl.getExchange());
		requestParams.add("sector", aapl.getSector());
		requestParams.add("investmentType", aapl.getInvestmentType().getDisplayName());
		
		Investment newInv = new Investment();
		newInv.setCompanyName(aapl.getCompanyName());
		newInv.setSymbol(aapl.getSymbol());
		newInv.setExchange(aapl.getExchange());
		newInv.setSector(aapl.getSector());
		newInv.setInvestmentType(aapl.getInvestmentType());
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/addInvestment")
				.params(requestParams))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("investment"))
			.andExpect(model().attribute("investment", newInv))
			.andExpect(view().name("investments/addInvestmentEntryPage"));
	}
	
	@Test
	@WithMockUser
	public void testAddInvestmentEntry_Success() throws Exception {
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/addInvestmentEntry"))
			.andExpect(status().isOk())
			.andExpect(view().name("investments/addInvestmentEntryPage"));
	}
	
	@Test
	@WithMockUser
	public void testGetAllInvestments_Success() throws Exception {
		
		List<Investment> investments = new ArrayList<>(Arrays.asList(aapl, fb, ge, pvtl));
		
		Mockito.when(this.investmentsService.getInvestmentsOrderedBySymbolAsc()).thenReturn(investments);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/getAllInvestments"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("investments"))
			.andExpect(model().attribute("investments", investments))
			.andExpect(view().name("investments/listAllInvestments"));
	}
	
	@Test
	@WithMockUser
	public void testGetInvestmentDetails_Success() throws Exception {
		
		Mockito.when(this.investmentsService.getInvestmentByInvestmentId(ge.getInvestmentId())).thenReturn(ge);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/getInvestmentDetails")
				.param("investmentId", ge.getInvestmentId().toString()))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("investment"))
			.andExpect(model().attribute("investment", ge))
			.andExpect(view().name("investments/editInvestment"));
	}
	
	@Test
	@WithMockUser
	public void testUpdateInvestment_Success() throws Exception {
		
		Mockito.when(this.investmentsService.saveInvestment(pvtl)).thenReturn(pvtl);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/updateInvestment")
				.flashAttr("investment", pvtl)
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:getAllInvestments"));
	}
}