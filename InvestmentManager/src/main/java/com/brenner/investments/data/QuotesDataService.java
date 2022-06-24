package com.brenner.investments.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.investments.data.mapping.QuoteRowMapper;
import com.brenner.investments.data.props.QuotesSqlProperties;
import com.brenner.investments.entities.Quote;

/**
 * Data Service for Quotes
 * 
 * @author dbrenner
 *
 */
@Component
public class QuotesDataService {
	
	private static final Logger log = LoggerFactory.getLogger(QuotesDataService.class);
	
	@Autowired
	QuotesRepository quotesRepo;
	
	/**
	 * Retrieves all stored quotes
	 * 
	 * @return The list of {@link Quote}s
	 */
	public List<Quote> findAll() {
		log.info("Entered findAll()");
		
		List<Quote> quotes = this.quotesRepo.findAll();
		log.debug("Returning {} quotes", quotes != null ? quotes.size() : 0);
		
		log.info("Exiting findAll()");
		return quotes;
	}
	
	/**
	 * Retrieves a specific quote based on the identifier
	 * 
	 * @param quoteId - unique identifier for quote
	 * @return The quote specified by the unique key or null of not found
	 */
	public Quote findById(Long quoteId) {
		log.info("Entered findById()");
		log.debug("Param: quoteId: {}", quoteId);
		
		Quote quote = this.quotesRepo.findById(quoteId).get();
		log.debug("Returning quote: {}", quote);
		
		log.info("Exiting findById()");
		return quote;
	}
	
	/**
	 * Entry point to save or update quote data. Save are individual and not done in batch
	 * 
	 * @param quotes - {@link List} of quotes to save.
	 * @return The updated list of quotes saved.
	 */
	@Transactional
	public List<Quote> saveAll(List<Quote> quotes) {
		log.info("Entered saveAll()");
		log.debug("Param: {} quotes", quotes != null ? quotes.size() : 0);
		
		List<Quote> savedQuotes = new ArrayList<>(quotes.size());
		
		Iterator<Quote> iter = quotes.iterator();
		while (iter.hasNext()) {
			savedQuotes.add(this.save(iter.next()));
		}
		log.debug("Returning {} quotes", savedQuotes.size());
		
		log.info("Exiting saveAll()");
		return savedQuotes;
	}
	
	/**
	 * Entry point to add or update a single quote.
	 * 
	 * @param quote The quote data to persist
	 * @return The updated quote.
	 */
	@Transactional
	public Quote save(Quote quote) {
		log.info("Entered save()");
		log.debug("Param: quote: {}", quote);
		
		Quote q = this.quotesRepo.save(quote);
		
		log.info("Exiting save()");
		return q;
	}
	
	/**
	 * Delete a quote
	 * 
	 * @param quote - Quote to delete. quoteId is the required attribute.
	 */
	@Transactional
	public void delete(Quote quote) {
		log.info("Entered delete()");
		log.debug("Param: quote: {}", quote);
		
		this.quotesRepo.delete(quote);
		
		log.info("Exiting delete()");
	}
	
	/**
     * Locates a quote based on an investment symbol and date
     * 
     * @param symbol - investment identifier
     * @param date - quote date
     * @return Optional<Quote>
     */
    public Quote findByInvestmentSymbolAndDate(String symbol, Date date) {
    	log.info("Entered findByInvestmentSymbolAndDate()");
    	log.debug("Params: symbol: {}; date: {}", symbol, date);
    	
    	Optional<Quote> optQuote = this.quotesRepo.findByInvestmentSymbolAndDate(symbol, date);
    	
    	Quote quote = null;
    	
    	if (optQuote.isPresent()) {
    		quote = optQuote.get();
    	}
    	
    	log.info("Exiting findByInvestmentSymbolAndDate()");
    	return quote;
    }
	
    /**
     * Uses a native query to get the max quote date for a given investment
     * 
     * @param symbol - investment identifier
     * @return String
     */
    public String findMaxDateBySymbol(String symbol) {
    	log.info("Entered findMaxDateBySymbol()");
    	log.debug("Param: symbol: {}", symbol);
    	
    	String date = this.quotesRepo.getMaxQuoteDateForInvestmentSymbol(symbol);
    	
    	log.debug("Retrieved date: {}", date);
    	
    	log.info("Exiting findMaxDateBySymbol()");
    	return date;
    }
    
    /**
     * Retrieves the most recent quote data across all quotes.
     * 
     * @return The {@link String} representation of the max date
     */
    public String findMaxQuoteDate() {
    	log.info("Entered findMaxQuoteDate()");
    	
    	String date = this.quotesRepo.getMaxQuoteDate();
    	
    	log.debug("Retrieved date: {}", date);
    	
    	log.info("Exiting findMaxQuoteDate()");
    	return date;
    }
    
    /**
     * Retrieves all quotes for a given investment
     * 
     * @param symbol - investment identifier
     * @return List<Quote>
     */
    public List<Quote> findAllByInvestmentSymbol(String symbol) {
    	log.info("Entered findAllByInvestmentSymbol()");
    	log.debug("Param: symbol: {}", symbol);
    	
    	List<Quote> quotes = this.quotesRepo.findAllByInvestmentSymbol(symbol);
    	
		log.debug("Returning {} quotes", quotes != null ? quotes.size() : 0);
    	
		log.info("Exiting findAllByInvestmentSymbol()");
    	return quotes;
    }
    
    /**
     * Retrieves all quotes based on the investment identifier
     * 
     * @param investmentId - unique investment identifier
     * @return List<Quote>
     */
    public List<Quote> findAllByInvestmentInvestmentId(Long investmentId) {
    	log.info("Entered findAllByInvestmentInvestmentId()");
    	log.debug("Param: investmentId: {}", investmentId);
    	
    	List<Quote> quotes = this.findAllByInvestmentInvestmentId(investmentId);
    	
		log.debug("Returning {} quotes", quotes != null ? quotes.size() : 0);
    	
		log.info("Exiting findAllByInvestmentInvestmentId()");
    	return quotes;
    }

}
