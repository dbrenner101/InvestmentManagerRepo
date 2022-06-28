/**
 * 
 */
package com.brenner.investments.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.investments.data.QuotesRepository;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.quote.service.QuoteRetrievalException;
import com.brenner.investments.quote.service.QuoteRetrievalService;
import com.brenner.investments.util.CommonUtils;

/**
 * Data service specific to quotes data
 * 
 * @author dbrenner
 *
 */
@Service
public class QuotesService {
    
    @Autowired
    QuotesRepository quotesRepo;
    
    @Autowired
    InvestmentsService investmentsService;
    
    @Autowired
    QuoteRetrievalService quotesRetrievalService;
    
    public String findMaxQuoteDate() {
    	return this.quotesRepo.getMaxQuoteDate();
    }

	/**
     * Deletes a specific quote
     * 
     * @param quote - quote to delete
     */
    public void deleteQuote(Quote quote) {
    	this.quotesRepo.delete(quote);
    }
    
    /**
     * Persists a list of quotes
     * 
     * @param quotes - the list of quotes to save
     */
    private void saveAllQuotes(List<Quote> quotes) {
    	this.quotesRepo.saveAll(quotes);
    }
    
    public Quote addManualQuote(Quote quote, String symbol) {
    	
        Investment investment = this.investmentsService.getInvestmentBySymbol(symbol);
        quote.setInvestment(investment);
        
        return this.saveQuote(quote);
    }
    
    
    public void updateQuotesForAllInvestments() throws ParseException, IOException {
    	
    	List<Investment> investments = this.investmentsService.getInvestmentsAndQuotesAssociatedWithAHolding();
    	
    	if (investments != null && ! investments.isEmpty()) {
    		Iterator<Investment> iter = investments.iterator();
    		while (iter.hasNext()) {
    			Investment investment = iter.next();
    			Date maxQuoteDate = this.findGreatestQuoteDateForSymbol(investment.getSymbol());
    			List<Quote> existingQuotes = this.getAllQuotesBySymbol(investment.getSymbol());
    		
    			List<Quote> newQuotes = this.quotesRetrievalService.getNewQuotesForInvestment(
        			investment, maxQuoteDate, existingQuotes);
        	
    			this.saveAllQuotes(newQuotes);
    		}
    	}
    }
    
    
    public List<Quote> updateQuoteForInvestment(Long investmentId) throws InvestmentManagerServiceException {
    	
    	Investment investment = this.investmentsService.getInvestmentByInvestmentId(investmentId);
    	Date maxQuoteDate;
		try {
			maxQuoteDate = this.findGreatestQuoteDateForSymbol(investment.getSymbol());
		} catch (ParseException pe) {
			throw new InvestmentManagerServiceException("Unable to parse date.", pe);
		}
		
		Long today = new Date().getTime();
    	
    	List<Quote> newQuotes = new ArrayList<>();
		
		if (maxQuoteDate == null) {
			try {
				Quote quote = this.quotesRetrievalService.getQuote(investment.getSymbol());
				quote.setInvestment(investment);
				newQuotes.add(quote);
			} catch (QuoteRetrievalException e) {
				throw new InvestmentManagerServiceException("Error retrieving quote", e);
			}
		}
		else {
			Long maxDateTime = maxQuoteDate.getTime();
			if (maxDateTime > today) {
				Long timeDiffHours = TimeUnit.MILLISECONDS.toHours(maxDateTime - today);
				if (timeDiffHours > 24) {
					Quote quote;
					try {
						quote = this.quotesRetrievalService.getQuote(investment.getSymbol());
						newQuotes.add(quote);
					} catch (QuoteRetrievalException e) {
						throw new InvestmentManagerServiceException("Error retrieving quote", e);
					}
				}
			}
		}
		
		this.saveAllQuotes(newQuotes);
		
		return newQuotes;
    	
    	/*List<Quote> existingQuotes = this.getAllQuotesBySymbol(investment.getSymbol());
    	
    	// sort the existingQuotes to put the most recent at the top
    	Collections.sort(existingQuotes);
    	Quote mostRecentQuote = existingQuotes.get(0);
    	Date mostRecentQuoteDate = mostRecentQuote.getDate();
    	
    	int missingQuoteDays = CommonUtils.workdayDiff(mostRecentQuoteDate, new Date());
    	
    	if (missingQuoteDays > 0) {
			try {
				newQuotes = this.quotesRetrievalService.getNewQuotesForInvestment(
						investment, 
						maxQuoteDate, 
						existingQuotes);
			} catch (IOException ioe) {
				throw new InvestmentManagerServiceException("Quote retrieval exception for " + investment, ioe);
			}
			
			if (newQuotes == null) {
				throw new InvestmentManagerServiceException("Unable to retrieve quotes for " + investment);
			}
	    	
	    	this.saveAllQuotes(newQuotes);
    	}
    	
    	return newQuotes;*/
    }
    
    
    /**
     * Locates all quotes for an investment by its unique identifier.
     * 
     * @param investmentId - investment unique id
     * @return {@link List}<Quote>
     */
    public List<Quote> findAllByInvestmentId(Long investmentId) {
    	
    	return this.quotesRepo.findAllByInvestmentInvestmentId(investmentId);
    }
    
    /**
     * Retrieves a quote for a specific investment on a specific date
     * 
     * @param symbol - investment identifier
     * @param date - quote date
     * @return {@link Quote}
     */
    public Quote getQuoteBySymbolAndDate(String symbol, Date date) {
    	
    	Quote quote = null;
        
        Optional<Quote> optQuote = this.quotesRepo.findByInvestmentSymbolAndDate(symbol, date);
        
        if (optQuote.isPresent()) {
        	quote = optQuote.get();
        }
        
        return quote;
    }
    
    /**
     * Retrieves the most recent quote date for a given investment. Returns null is it can't be found
     * 
     * @param symbol - investment identifier
     * @return {@link Date}
     * @throws ParseException 
     */
    public Date findGreatestQuoteDateForSymbol(String symbol) throws ParseException {
        
        String dateString = this.quotesRepo.getMaxQuoteDateForInvestmentSymbol(symbol);
        
        if (dateString != null) {
            return CommonUtils.convertDatePickerDateFormatStringToDate(dateString);
        }
        
        return null;
    }
    
    /**
     * Retrieves a list of Quotes for a given investment
     * 
     * @param symbol - investment identifier
     * @return List<Quote> 
     */
    public List<Quote> getAllQuotesBySymbol(String symbol) {
        
        return this.quotesRepo.findAllByInvestmentSymbol(symbol);
    }
    
    /**
     * Persists the quote
     * 
     * @param quote - the object to persist
     */
    @Transactional
    private Quote saveQuote(Quote quote) {
        return this.quotesRepo.save(quote);
    }

	@Transactional
	public void addQuotes(List<Quote> quotes) {
		
		List<Quote> quotesToSave = new ArrayList<Quote>(quotes.size());
		
		Iterator<Quote> iter = quotes.iterator();
		while (iter.hasNext()) {
			Quote q = iter.next();
			
			if (this.getQuoteBySymbolAndDate(q.getInvestment().getSymbol(), q.getDate()) == null) {
				quotesToSave.add(q);
			}
		}
		
		this.quotesRepo.saveAll(quotesToSave);
	}
    
}
