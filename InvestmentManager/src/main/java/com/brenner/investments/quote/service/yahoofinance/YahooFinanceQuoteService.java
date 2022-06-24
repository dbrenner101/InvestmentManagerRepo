/**
 * 
 */
package com.brenner.investments.quote.service.yahoofinance;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.brenner.investments.entities.BatchHistoricalQuotes;
import com.brenner.investments.entities.BatchQuotes;
import com.brenner.investments.entities.HistoricalQuotes;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.News;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.quote.service.QuoteRetrievalException;
import com.brenner.investments.quote.service.QuoteRetrievalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 *
 * @author dbrenner
 * 
 */
@Service
@Primary
public class YahooFinanceQuoteService implements QuoteRetrievalService {
	
	private static final Logger logger = LoggerFactory.getLogger(YahooFinanceQuoteService.class);
	
	static HttpHeaders headers;
	
	private static final String QUOTE_URL = "https://yh-finance.p.rapidapi.com/market/v2/get-quotes?region={region}&symbols={symbols}";
	
	public YahooFinanceQuoteService() {
		YahooFinanceQuoteService.init();
	}
	
	private static void init() {
		headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("X-RapidAPI-Key", "969134fa9cmsh771faa7485c0f7bp18e7c2jsnffa0bd6ee3a8");
		headers.set("X-RapidAPI-Host", "yh-finance.p.rapidapi.com");
	}

	@Override
	public List<Quote> getNewQuotesForInvestment(Investment investment, Date maxQuoteDate, List<Quote> existingQuotes)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<News> getCurrentNews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<News> getCompanyNews(String companySymbol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quote getQuote(String symbol) throws QuoteRetrievalException {
		
		BatchQuotes quotes = getBatchQuotes(Arrays.asList(symbol));
		
		if (quotes != null) {
			return quotes.getQuotes().get(symbol);
		}
		
		return null;
	}

	@Override
	public BatchQuotes getBatchQuotes(List<String> symbols) throws QuoteRetrievalException {
		
		HttpEntity<String> httpEntity = new HttpEntity<>("", headers);
		
		final Map<String, String> uriParameters = new HashMap<>(2);
		
		StringBuilder builder = new StringBuilder();
		Iterator<String> symbolsIter = symbols.iterator();
		while (symbolsIter.hasNext()) {
			builder.append(symbolsIter.next());
			if (symbolsIter.hasNext()) {
				builder.append(",");
			}
		}
		
		logger.debug("Adding symbils to URI: " + builder.toString());
		
		uriParameters.put("symbols", builder.toString());
		uriParameters.put("region", "US");
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(QUOTE_URL, HttpMethod.GET, httpEntity, String.class, uriParameters);
		
		SimpleModule module = new SimpleModule("QuoteDeserializer", new Version(1, 0, 0, null, null, null));
		module.addDeserializer(BatchQuotes.class, new YahooFinanceBatchQuotesDeserializer());
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);
		
		BatchQuotes quotes = null;
		
		try {
			quotes = mapper.readValue(response.getBody(), BatchQuotes.class);
		} catch (JsonMappingException e) {
			throw new QuoteRetrievalException(e.getMessage());
		} catch (JsonProcessingException e) {
			throw new QuoteRetrievalException(e.getMessage());
		}
		
		if (quotes != null) {
			return quotes;
		}
		
		return null;
	}

	@Override
	public HistoricalQuotes getHistoricalQuotesForSymbol(String symbol, int numMonths) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchHistoricalQuotes getSixMonthsHistoricalChartsForSymbols(List<String> symbols) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
