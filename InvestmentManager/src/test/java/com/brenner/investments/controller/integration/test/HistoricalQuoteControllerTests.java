package com.brenner.investments.controller.integration.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.brenner.investments.InvestmentsProperties;
import com.brenner.investments.data.InvestmentsDataService;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.test.TestDataHelper;

@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles(profiles= {"test"})
public class HistoricalQuoteControllerTests {
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	InvestmentsProperties props;
	
	@Autowired
	InvestmentsDataService investmentsDataService;
	
	Investment aapl;
	
	@Autowired
    private MockMvc mockMvc;
	
	@Before
	public void setup() throws Exception {
		
		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
		
		this.aapl = TestDataHelper.getInvestmentAAPL();
		this.aapl.setInvestmentId(null);
		this.aapl = this.investmentsDataService.save(this.aapl);
	}
	
	@Test
	public void testAddManualQuote() throws Exception {
		
		Quote q = TestDataHelper.getQuoteAAPL();
		q.setQuoteId(null);
		q.setInvestment(null);
		
		MockHttpServletRequestBuilder requestBuilder =
          MockMvcRequestBuilders.get("/saveManualQuote")
            .param("date", q.getDate().toString())
			.param("open", q.getOpen().toString())
			.param("close",q.getClose().toString())
			.param("symbol", TestDataHelper.getInvestmentAAPL().getSymbol());
		
		MvcResult result = this.mockMvc.perform(requestBuilder)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("prepAddManualQuoteForm"))
			.andReturn();
		
		ModelAndView mandv = result.getModelAndView();
		Quote quote = (Quote) mandv.getModel().get("quote");
		assertNotNull(quote);
		assertNotNull(quote.getQuoteId());
		assertEquals(q.getClose(), quote.getClose());
		assertEquals(q.getOpen(), quote.getOpen());
	}

}
