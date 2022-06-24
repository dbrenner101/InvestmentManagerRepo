package com.brenner.investments.service.unit.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import com.brenner.investments.data.QuotesDataService;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.quote.service.QuoteRetrievalService;
import com.brenner.investments.service.implementation.PersistentInvestmentsService;
import com.brenner.investments.service.implementation.PersistentQuotesService;
import com.brenner.investments.test.TestDataHelper;
import com.brenner.investments.util.CommonUtils;

@ActiveProfiles("test")
public class QuotesDataServiceTest {

	@Mock
	QuotesDataService quoteRepo;
	
	PersistentQuotesService quotesDataService;
	
	@Mock
	PersistentInvestmentsService investmentsDataService;
	
	@Mock
	QuoteRetrievalService quoteRetrievalService;
	
	/*private static final Float Q1_CLOSE = 9.99f;
	private static final DateTime Q1_DATE = new DateTime();
	private static final Float Q1_HIGH = 12.34f;
	private static final Float Q1_LOW = 7.5f;
	private static final Float Q1_OPEN = 9.50f;
	private static final Float Q1_PRICE_CHANGE = 1.25f;
	private static final Integer Q1_VOLUME = 153475;
	
	private static final Float Q2_CLOSE = .5f;
	private static final DateTime Q2_DATE = QuotesDataServiceTest.Q1_DATE.minusDays(2);
	private static final Float Q2_HIGH = 1.34f;
	private static final Float Q2_LOW = .5f;
	private static final Float Q2_OPEN = 3.50f;
	private static final Float Q2_PRICE_CHANGE = 1.25f;
	private static final Integer Q2_VOLUME = 75;
	
	private static final String I1_SYMBOL = "AAPL";
	private static final String I1_COMPANY = "APPLE";
	
	private static final String I2_SYMBOL = "GE";
	private static final String I2_COMPANY = "General Electric";
	
	private Investment i1 = new Investment();
	private Investment i2 = new Investment();*/
	
	
	@Before
	public void setup() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		this.quotesDataService = new PersistentQuotesService();
		this.quotesDataService.setQuoteRepo(this.quoteRepo);
		this.quotesDataService.setQuotesService(this.quoteRetrievalService);
		this.quotesDataService.setInvestmentsService(this.investmentsDataService);
		
		/*i1.setCompanyName(QuotesDataServiceTest.I1_COMPANY);
		i1.setSymbol(QuotesDataServiceTest.I1_SYMBOL);
		i1 = this.investmentsDataService.saveInvestment(i1);
		
		i2.setCompanyName(QuotesDataServiceTest.I2_COMPANY);
		i2.setSymbol(QuotesDataServiceTest.I2_SYMBOL);
		i2 = this.investmentsDataService.saveInvestment(i2);
		
		Quote q1 = new Quote();
		q1.setClose(Q1_CLOSE);
		q1.setDate(Q1_DATE.toDate());
		q1.setHigh(Q1_HIGH);
		q1.setLow(Q1_LOW);
		q1.setOpen(Q1_OPEN);
		q1.setPriceChange(Q1_PRICE_CHANGE);
		q1.setVolume(Q1_VOLUME);
		this.quotesDataService.addManualQuote(q1, i1.getSymbol());
		
		Quote q2 = new Quote();
		q2.setClose(Q2_CLOSE);
		q2.setDate(Q2_DATE.toDate());
		q2.setHigh(Q2_HIGH);
		q2.setLow(Q2_LOW);
		q2.setOpen(Q2_OPEN);
		q2.setPriceChange(Q2_PRICE_CHANGE);
		q2.setVolume(Q2_VOLUME);
		this.quotesDataService.addManualQuote(q2, i2.getSymbol());*/
	}
	
	@Test
	public void testGetQuoteBySymbolAndDate() throws Exception {
		
		Quote quote = TestDataHelper.getQuoteAAPL();
		
		when(this.quoteRepo.findByInvestmentSymbolAndDate(
				quote.getInvestment().getSymbol(), 
				quote.getDate()))
			.thenReturn(quote);
		
		Quote q = this.quotesDataService.getQuoteBySymbolAndDate(
				quote.getInvestment().getSymbol(), quote.getDate());
		
		assertNotNull(q);
		assertEquals(quote.getClose(), q.getClose());
		assertEquals(quote.getDate(), q.getDate());
		assertEquals(quote.getHigh(), q.getHigh());
		assertEquals(quote.getLow(), q.getLow());
		assertEquals(quote.getOpen(), q.getOpen());
		assertEquals(quote.getPriceChange(), q.getPriceChange());
		assertEquals(quote.getVolume(), q.getVolume());
		assertEquals(quote.getInvestment().getSymbol(), q.getInvestment().getSymbol());
	}
	
	@Test
	public void testFindGreatestQuoteDateForSymbol() throws Exception {
		
		String dateString = "2018-08-28 00:00:00";
		
		Date testDate = CommonUtils.convertDatePickerDateFormatStringToDate(dateString);
		
		when(this.quoteRepo.findMaxDateBySymbol("GE")).thenReturn(dateString);
		
		Date date = this.quotesDataService.findGreatestQuoteDateForSymbol("GE");
		assertNotNull(date);
		assertEquals(date, testDate);
	}
}
