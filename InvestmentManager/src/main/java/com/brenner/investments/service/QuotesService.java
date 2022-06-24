package com.brenner.investments.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.brenner.investments.entities.Quote;

public interface QuotesService {

	/**
     * Deletes a specific quote
     * 
     * @param quote - quote to delete
     */
    public void deleteQuote(Quote quote);
    
    /**
     * Method definition for adding a quote to the system manually.
     * 
     * @param quote - {@link Quote} to add
     * @param symbol - Investment identifier
     * @return {@link Quote}
     */
    public Quote addManualQuote(Quote quote, String symbol);
    
    /**
     * Method definition for the bulk upload of quotes.
     * 
     * @throws ParseException Errors parsing dates
     * @throws IOException Parsing Errors
     */
    public void updateQuotesForAllInvestments() throws ParseException, IOException;
    
    /**
     * Method definition for updating the quote associated with an investment.
     * 
     * @param investmentId - unique investment identifier
     * @throws ParseException Errors parsing dates
     * @throws IOException Parsing errors
     */
    public List<Quote> updateQuoteForInvestment(Long investmentId) throws InvestmentManagerServiceException;
    
    
    /**
     * Locates all quotes for an investment by its unique identifier.
     * 
     * @param investmentId - investment unique id
     * @return {@link List} of Quote
     */
    public List<Quote> findAllByInvestmentId(Long investmentId);
    
    /**
     * Retrieves a quote for a specific investment on a specific date
     * 
     * @param symbol - investment identifier
     * @param date - quote date
     * @return {@link Quote}
     */
    public Quote getQuoteBySymbolAndDate(String symbol, Date date);
    
    /**
     * Retrieves the most recent quote date for a given investment. Returns null is it can't be found
     * 
     * @param symbol - investment identifier
     * @return {@link Date}
     * @throws ParseException Errors parsing dates
     */
    public Date findGreatestQuoteDateForSymbol(String symbol) throws ParseException;
    
    /**
     * Retrieves a list of Quotes for a given investment
     * 
     * @param symbol - investment identifier
     * @return A List of Quotes
     */
    public List<Quote> getAllQuotesBySymbol(String symbol);
    
    /** 
     * Saves a list of quote objects
     * 
     * @param quotes - list of quotes
     */
    public void addQuotes(List<Quote> quotes);
}
