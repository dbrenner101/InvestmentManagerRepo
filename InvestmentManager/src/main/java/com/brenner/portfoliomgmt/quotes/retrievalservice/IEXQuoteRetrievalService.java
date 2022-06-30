package com.brenner.portfoliomgmt.quotes.retrievalservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.brenner.portfoliomgmt.exception.QuoteRetrievalException;
import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.news.News;
import com.brenner.portfoliomgmt.quotes.BatchHistoricalQuotes;
import com.brenner.portfoliomgmt.quotes.BatchQuotes;
import com.brenner.portfoliomgmt.quotes.HistoricalQuotes;
import com.brenner.portfoliomgmt.quotes.Quote;
import com.brenner.portfoliomgmt.quotes.QuoteConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service class to handle retrieving and handling quotes from IEX
 * 
 * @author dbrenner
 *
 */
@Service
public class IEXQuoteRetrievalService implements QuoteRetrievalService {
    
    private static final Logger log = LoggerFactory.getLogger(IEXQuoteRetrievalService.class);
    
    private IEXQuoteRetrievalService() {}
    
    private static final String API_TOKEN = "pk_4d08a1499c5a44cdba45db1bc6063211";
    
    private static String singleQuoteUrlBase = "https://cloud.iexapis.com/stable/stock/%S%/quote?token=" + API_TOKEN;
	
	private static String batchQuoteUrlBase = "https://api.iextrading.com/1.0/stock/market/batch?symbols=%S%&types=quote";
	
	private static String historicalQuotesOneMonth = "https://cloud.iexapis.com/stable/stock/%S%/chart/1m";
	
	private static String historicalQuotesThreeMonths = "https://cloud.iexapis.com/stable/stock/%S%/chart/3m";
	
	private static String historicalQuotesSixMonths = "https://cloud.iexapis.com/stable/stock/%S%/chart/6m";
	
	private static String batchHistoricalQuotes = "https://api.iextrading.com/1.0/stock/market/batch?symbols=%S%&types=chart&range=6m";
	
	private static final String GENERAL_NEWS = "https://api.iextrading.com/1.0/stock/market/news/";
	
	private static String companyNews = "https://api.iextrading.com/1.0/stock/%S%/news";
	
	
	/**
	 * Retrieves a list of news articles for today.
	 * 
	 * @return The {@link List} of {@link News} objects obtained from the service.
	 */
	public List<News> getCurrentNews() {
	    log.info("Entered getCurrentNews()");
	    
	    List<News> news = getNews(GENERAL_NEWS);
	    log.debug("Retrieved {} news articles", news != null ? news.size() : 0);
	    
	    log.info("Exiting getCurrentNews()");
	    return news;
	}
	
	/**
	 * Retrieves news for a specific company
	 * 
	 * @param companySymbol - Company's stock symbol
	 * @return The list of news articles
	 */
	public List<News> getCompanyNews(String companySymbol) {
	    log.info("Entered getCompanyNews()");
	    
	    String url = companyNews.replace("%S%", companySymbol);
	    log.debug("Using url: {}", url);
	    List<News> news = getNews(url);
	    log.debug("Retrieved {} articles", news != null ? news.size() : 0);
	    
	    log.info("Exiting getCompanyNews()");
	    return news;
	}
	
	/**
	 * Shared method to retrieve news articles
	 * 
	 * @param url - Service URL to use.
	 * @return The list of news articles.
	 */
	private List<News> getNews(String url) {
	    
	    RestTemplate template = new RestTemplate();
	    
	    ResponseEntity<List<News>> rateResponse =
                template.exchange(url,
                            HttpMethod.GET, null, new ParameterizedTypeReference<List<News>>() {});
        return rateResponse.getBody();
        
	}
	
	
	/**
	 * Updates the quotes for an investment. Goes back 1 month and the most. Contains logic
	 * to not duplicate quotes already stored in the data store.
	 * 
	 * @param investment - the investment to get quotes for
	 * @param maxQuoteDate - the oldest current quote
	 * @param existingQuotes - the list of quotes currently persisted
	 * @return {@link List}<Quote>
	 * @throws IOException
	 */
	public List<Quote> getNewQuotesForInvestment(
			Investment investment, 
			Date maxQuoteDate, 
			List<Quote> existingQuotes) throws IOException {
		
		
		// determine how far back to go to start retrieving quotes
		DateTime startPeriod = null;
    	
		// container for the existing quote dates
    	List<Date> existingQuoteDates = new ArrayList<>();
    	
    	// loop through the existing quotes and extract the date from each quote
    	if (existingQuotes != null && ! existingQuotes.isEmpty()) {
    		Iterator<Quote> quoteIter = existingQuotes.iterator();
    		while (quoteIter.hasNext()) {
    			Quote q = quoteIter.next();
    			if (q != null) {
    				existingQuoteDates.add(q.getDate());
    			}
    		}
    	}
    	
    	// if there is no max quote date, initialize
    	if (maxQuoteDate == null) {
    		startPeriod = new DateTime(QuoteConstants.MIN_QUOTE_DATE);
    	}
    	else {
    		startPeriod = new DateTime(maxQuoteDate);
    	}
    	
    	// get 1 months of quotes for the investment
    	HistoricalQuotes quotes = null;
    	
    	try {
    		quotes = this.getHistoricalQuotesForSymbol(investment.getSymbol(), 1);
    	}
    	catch (HttpClientErrorException hcee) {
    		// eat failed retrieves - comes from non-exchange traded securities
    	}
    	
    	List<Quote> newQuotes = new ArrayList<>();
    	
    	if (quotes != null) {
    		
    		// get the historical quotes returned by the service and pull the date keys
    		Map<Date, Quote> quotesMap = quotes.getQuotes();
    		Set<Date> dates = quotesMap.keySet();
    		
    		// Loop through each date key
    		Iterator<Date> dateIter = dates.iterator();
    		while(dateIter.hasNext()) {
    			DateTime quoteDate = new DateTime(dateIter.next());
    			
    			// if the quote date is after the start period and it's not in the existing quotes list (from database) add it to the return list
    			if (quoteDate.getMillis() > startPeriod.getMillis() && ! existingQuoteDates.contains(quoteDate.toDate())) {
    				Quote q = quotesMap.get(quoteDate.toDate());
    				q.setInvestment(investment);
    				newQuotes.add(q);
    			}
    		}
    	}
    	
    	return newQuotes;
	}
	
	/**
	 * Retrieves a quote for the supplied symbol
	 * 
	 * @param symbol
	 * @return {@link Quote}
	 */
	public Quote getQuote(String symbol) {

		RestTemplate restTemplate = new RestTemplate();
		
		String url = IEXQuoteRetrievalService.singleQuoteUrlBase.replace("%S%", symbol.toLowerCase());
		
		return restTemplate.getForObject(url, Quote.class);
	}
	
	/**
	 * Retrieves a batch of quotes for the supplied symbols
	 * 
	 * @param symbols - list of investments to get quotes for
	 * @return {@link BatchQuotes}
	 * @throws QuoteRetrievalException
	 */
	public BatchQuotes getBatchQuotes(List<String> symbols) throws QuoteRetrievalException {
		
		StringBuilder buffer = new StringBuilder();
		int numSymbols = symbols.size();
		int count = 1;
		for (String symbol : symbols) {
			buffer.append(symbol);
			if (count < numSymbols) {
				buffer.append(",");
			}
			++count;
		}
		
		String url = IEXQuoteRetrievalService.batchQuoteUrlBase.replace("%S%", buffer.toString().toLowerCase());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(
                new MappingJackson2HttpMessageConverter());
		
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		ObjectMapper mapper = new ObjectMapper();
		
		try {
            return mapper.readValue(response.getBody(), BatchQuotes.class);
        } catch (IOException e) {
            throw new QuoteRetrievalException(e);
        }
	}
	
	/**
	 * Gets historical quotes for the supplied symbol and number of months
	 * 
	 * @param symbol - investment to get quotes for
	 * @param numMonths - number of months back to request (1, 3 or 6 are supported - 1 is the default)
	 * @return {@link HistoricalQuotes}
	 * @throws IOException
	 */
	public HistoricalQuotes getHistoricalQuotesForSymbol(String symbol, int numMonths) throws IOException {
	    
	    String url = "";
	    
	    if (numMonths == 6) {
	        url = IEXQuoteRetrievalService.historicalQuotesSixMonths.replace("%S%", symbol);
	    }
	    else if (numMonths == 3) {
	        url = IEXQuoteRetrievalService.historicalQuotesThreeMonths.replace("%S%", symbol);
	    }
	    else {
	        url = IEXQuoteRetrievalService.historicalQuotesOneMonth.replace("%S%", symbol);
	        
	    }
	    
	    RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(
                new MappingJackson2HttpMessageConverter());
        
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper mapper = new ObjectMapper();
	    
	    return mapper.readValue(response.getBody(), HistoricalQuotes.class);
	    
	}
	
	/**
	 * Gets a batch of quotes for the list of symbols
	 * 
	 * @param symbols - investment identifiers to use for batch quotes
	 * @return {@link BatchHistoricalQuotes}
	 * @throws IOException
	 */
	public BatchHistoricalQuotes getSixMonthsHistoricalChartsForSymbols(List<String> symbols) throws IOException {
	    
	    StringBuilder builder = new StringBuilder();
	    int numSymbols = symbols.size();
	    
	    for (int i=0; i<numSymbols; i++) {
	        builder.append(symbols.get(i));
	        if (i<numSymbols-1) {
	            builder.append(",");
	        }
	    }
	    
	    String url = IEXQuoteRetrievalService.batchHistoricalQuotes.replace("%S%", builder.toString());
	    
	    RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(
                new MappingJackson2HttpMessageConverter());
        
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ObjectMapper mapper = new ObjectMapper();
        
        return mapper.readValue(response.getBody(), BatchHistoricalQuotes.class);
	}

}
