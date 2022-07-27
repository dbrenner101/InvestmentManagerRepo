package com.brenner.portfoliomgmt.quotes.retrievalservice;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.brenner.portfoliomgmt.domain.BatchQuotes;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.domain.reporting.BatchHistoricalQuotes;
import com.brenner.portfoliomgmt.domain.reporting.HistoricalQuotes;
import com.brenner.portfoliomgmt.exception.QuoteRetrievalException;

public interface QuoteRetrievalService {

	public List<Quote> getNewQuotesForInvestment(
			Investment investment, 
			Date maxQuoteDate, 
			List<Quote> existingQuotes) throws IOException;
	
	/**
	 * Retrieves a quote for the supplied symbol
	 * 
	 * @param symbol
	 * @return {@link QuoteDTO}
	 */
	public Quote getQuote(String symbol) throws QuoteRetrievalException;
	
	/**
	 * Retrieves a batch of quotes for the supplied symbols
	 * 
	 * @param symbols - list of investments to get quotes for
	 * @return {@link BatchQuotes}
	 * @throws QuoteRetrievalException
	 */
	public BatchQuotes getBatchQuotes(List<String> symbols) throws QuoteRetrievalException;
	
	/**
	 * Gets historical quotes for the supplied symbol and number of months
	 * 
	 * @param symbol - investment to get quotes for
	 * @param numMonths - number of months back to request (1, 3 or 6 are supported - 1 is the default)
	 * @return {@link HistoricalQuotes}
	 * @throws IOException
	 */
	public HistoricalQuotes getHistoricalQuotesForSymbol(String symbol, int numMonths) throws IOException;
	
	/**
	 * Gets a batch of quotes for the list of symbols
	 * 
	 * @param symbols - investment identifiers to use for batch quotes
	 * @return {@link BatchHistoricalQuotes}
	 * @throws IOException
	 */
	public BatchHistoricalQuotes getSixMonthsHistoricalChartsForSymbols(List<String> symbols) throws IOException;
}
