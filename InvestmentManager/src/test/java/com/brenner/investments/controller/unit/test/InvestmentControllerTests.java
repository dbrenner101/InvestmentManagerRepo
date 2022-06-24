package com.brenner.investments.controller.unit.test;

import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.brenner.investments.InvestmentsProperties;
import com.brenner.investments.controller.InvestmentController;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.quote.service.QuoteRetrievalService;
import com.brenner.investments.service.InvestmentsService;
import com.brenner.investments.test.TestDataHelper;
import com.brenner.investments.util.CommonUtils;

@ActiveProfiles("test")
public class InvestmentControllerTests {
	
	private MockMvc mockMvc;
	@InjectMocks InvestmentController investmentController;
	@Mock InvestmentsService investmentsService;
	@Mock InvestmentsProperties props;
	@Mock QuoteRetrievalService quoteService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.investmentController).build();
	}
	
	@Test
	public void testAddInvesetmentEntryStartPage() throws Exception {
		
		this.mockMvc.perform(get("/addInvestmentEntry"))
			.andExpect(status().isOk())
			.andExpect(view().name("investments/addInvestmentEntryPage"));
	}
	
	@Test
	public void testLookupInvestment() throws Exception {
		
		Quote q = TestDataHelper.getQuoteAAPL();
		
		when(this.quoteService.getQuote("AAPL")).thenReturn(q);
		when(this.props.getInvestmentAttributeKey()).thenReturn("investment");
		
		this.mockMvc.perform(get("/lookupInvestment")
				.param("symbol", "AAPL"))
			.andExpect(status().isOk())
			.andExpect(view().name("investments/newInvestment"))
			.andExpect(model().attribute("investment", is(instanceOf(Investment.class))))
			.andExpect(model().attribute("investment", hasProperty("symbol", is(q.getInvestment().getSymbol()))))
			.andExpect(model().attribute("investment", hasProperty("companyName", is(q.getInvestment().getCompanyName()))));
	}
	
	@Test
	public void testLookupInvestmentNoQuote() throws Exception {
		
		when(this.quoteService.getQuote("ZZZZ")).thenReturn(null);
		when(this.props.getInvestmentAttributeKey()).thenReturn("investment");
		
		this.mockMvc.perform(get("/lookupInvestment")
				.param("symbol", "ZZZZ"))
			.andExpect(status().isOk())
			.andExpect(view().name("investments/newInvestment"))
			.andExpect(model().attribute("investment", is(instanceOf(Investment.class))))
			.andExpect(model().attribute("investment", hasProperty("companyName", is(""))))
			.andExpect(model().attribute("investment", hasProperty("symbol", is("ZZZZ"))))
			.andExpect(model().attribute("investment", hasProperty("exchange", is("NA"))))
			.andExpect(model().attribute("investment", hasProperty("sector", is("NA"))));
	}
	
	@Test
	public void testAddInvestmentAddDuplicate() throws Exception {
		
		Investment i1 = TestDataHelper.getInvestmentFB();
		
		when(this.investmentsService.getInvestmentBySymbol(i1.getSymbol())).thenReturn(i1);
		when(this.props.getInvestmentAttributeKey()).thenReturn("investment");
		
		this.mockMvc.perform(get("/addInvestment")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("companyName", i1.getCompanyName())
				.param("symbol", i1.getSymbol())
				.param("exchange", i1.getExchange())
				.param("sector", i1.getSector())
				.param("investmentType", i1.getInvestmentType().getDisplayName()))
		.andExpect(status().isOk())
		.andExpect(view().name("investments/addInvestmentEntryPage"))
		.andExpect(model().attribute("investment", is(instanceOf(Investment.class))))
		.andExpect(model().attribute("investment", is(i1)))
		.andExpect(model().attribute("investment", hasProperty("symbol", is(i1.getSymbol()))));
	}
	
	@Test
	public void testGetAllInvestments() throws Exception {
		
		Investment fb = TestDataHelper.getInvestmentFB();
		Long fbId = fb.getInvestmentId();
		
		List<Investment> investments = new ArrayList<>();
		investments.add(TestDataHelper.getInvestmentAAPL());
		investments.add(fb);
		investments.add(TestDataHelper.getInvestmentGE());
		investments.add(TestDataHelper.getInvestmentPVTL());
		
		when(this.props.getInvestmentsListAttributeKey()).thenReturn("investments");
		when(this.investmentsService.getInvestmentsOrderedBySymbolAsc()).thenReturn(investments);
		
		this.mockMvc.perform(get("/getAllInvestments"))
			.andExpect(status().isOk())
			.andExpect(view().name("investments/listAllInvestments"))
			.andExpect(model().attribute("investments", hasItem(
					allOf(hasProperty("symbol", is("FB")), 
					hasProperty("investmentId", is(fbId))))));
	}
	
	@Test
	public void testGetInvestmentDetailsNoParam() throws Exception {
		
		this.mockMvc.perform(get("/getInvestmentDetails"))
			.andExpect(status().is(400))
			.andExpect(status().reason(containsString("\'investmentId\' is not present")));
	}
	
	@Test
	public void testGetInvestmentDetailsKnownInvestment() throws Exception {
		
		Investment fb = TestDataHelper.getInvestmentFB();
		
		when(this.investmentsService.getInvestmentByInvestmentId(fb.getInvestmentId()))
			.thenReturn(fb);
		when(props.getInvestmentAttributeKey()).thenReturn("investment");
		
		this.mockMvc.perform(get("/getInvestmentDetails")
				.param("investmentId", fb.getInvestmentId().toString()))
			.andExpect(status().isOk())
			.andExpect(view().name("investments/editInvestment"))
			.andExpect(model().attribute("investment", is(fb)));
	}
	
	@Test
	public void testGetInvestmentDetailsNewInvestment() throws Exception {
		
		when(this.investmentsService.getInvestmentByInvestmentId(anyLong()))
			.thenReturn(null);
		when(props.getInvestmentAttributeKey()).thenReturn("investment");
		
		this.mockMvc.perform(get("/getInvestmentDetails")
				.param("investmentId", "4"))
			.andExpect(status().is(400))
			.andExpect(status().reason(containsString("The investment cannot be located. Id: " + 4)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetInvestmentsAndMostRecentQuoteDate() throws Exception {
		
		List<Investment> investments = TestDataHelper.getAllInvestmentsAndQuotes();
		Investment i1 = investments.get(0);
		Quote q = i1.getQuotes().get(0);
		Date maxDate = CommonUtils.convertCommonDateFormatStringToDate("01/01/3001");
		q.setDate(maxDate);
		
		when(this.investmentsService.getInvestmentsAndQuotesAssociatedWithAHolding())
			.thenReturn(investments);
		
		when(this.props.getInvestmentsListAttributeKey()).thenReturn("investments");
		
		this.mockMvc.perform(get("/getInvestmentsAndMostRecentQuoteDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("investments/listInvestmentsAndQuoteDate"))
			.andExpect(model().attribute("investments", is(investments)))
			.andExpect(model().attribute("investments", hasItem(
					allOf(
							hasProperty("symbol", is(i1.getSymbol())),
							hasProperty("quotes", hasItem(
									allOf(
											hasProperty("date", is(maxDate))
									)
							))
					))));
	}

}
