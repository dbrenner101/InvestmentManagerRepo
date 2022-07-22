/**
 * 
 */
package com.brenner.portfoliomgmt.service;

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

import com.brenner.portfoliomgmt.data.entities.InvestmentDTO;
import com.brenner.portfoliomgmt.data.entities.QuoteDTO;
import com.brenner.portfoliomgmt.data.repo.QuotesRepository;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.exception.InvestmentManagerServiceException;
import com.brenner.portfoliomgmt.exception.QuoteRetrievalException;
import com.brenner.portfoliomgmt.quotes.retrievalservice.QuoteRetrievalService;
import com.brenner.portfoliomgmt.util.CommonUtils;
import com.brenner.portfoliomgmt.util.ObjectMappingUtil;

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
    
    public Optional<Investment> findInvestmentForQuote(Long investmentId) {
    	
    	return this.investmentsService.findInvestmentByInvestmentId(investmentId);
    }
    
    public Optional<Quote> findQuoteByQuoteId(Long quoteId) {
    	Optional<QuoteDTO> optQuote = this.quotesRepo.findById(quoteId);
    	
    	return optQuote.isEmpty() ?
    			Optional.empty() :
    				Optional.of(ObjectMappingUtil.mapQuoteDtoToQuote(optQuote.get()));
    }
    
    public List<Quote> findMostRecentQuotesForInvestment(InvestmentDTO investment) {
    	if (investment == null || investment.getInvestmentId() == null) {
    		throw new InvalidRequestException("investment.investmentId must not be null.");
    	}
    	
    	List<QuoteDTO> quotesData = this.quotesRepo.findMostRecentQuotesForInvestmentId(investment.getInvestmentId());
    	
    	return ObjectMappingUtil.mapQuoteDtoList(quotesData);
    }
    
    public String findMaxQuoteDate() {
    	return this.quotesRepo.getMaxQuoteDate();
    }

	/**
     * Deletes a specific quote
     * 
     * @param quote - quote to delete
     */
    public void deleteQuote(QuoteDTO quote) {
    	this.quotesRepo.delete(quote);
    }
    
    /**
     * Persists a list of quotes
     * 
     * @param quotes - the list of quotes to save
     */
    private void saveAllQuotes(List<Quote> quotes) {
    	for (Quote quote : quotes) {
    		this.quotesRepo.save(ObjectMappingUtil.mapQuoteToQuoteDTO(quote));
    	}
    }
    
    public Quote addManualQuote(Quote quote, String symbol) {
    	
        Investment investment = this.investmentsService.findInvestmentBySymbol(symbol).get();
        
        QuoteDTO quoteData = ObjectMappingUtil.mapQuoteToQuoteDTO(quote);
        quoteData.setInvestment(ObjectMappingUtil.mapInvestmentToInvestmentDTO(investment));
        
        quoteData = this.quotesRepo.save(quoteData);
        
        return ObjectMappingUtil.mapQuoteDtoToQuote(quoteData);
    }
    
    
    public void updateQuotesForAllInvestments() throws ParseException, IOException {
    	
    	List<Investment> investments = this.investmentsService.findInvestmentsAndQuotesAssociatedWithAHolding();
    	
    	if (investments != null && ! investments.isEmpty()) {
    		Iterator<Investment> iter = investments.iterator();
    		while (iter.hasNext()) {
    			Investment investment = iter.next();
    			Optional<Date> optQuoteDate = this.findGreatestQuoteDateForSymbol(investment.getSymbol());
    			if (optQuoteDate.isPresent()) {
    				Date maxQuoteDate = optQuoteDate.get();
    				List<QuoteDTO> existingQuoteDTOs = this.quotesRepo.findAllByInvestmentSymbol(investment.getSymbol());
    				List<Quote> existingQuotes = ObjectMappingUtil.mapQuoteDtoList(existingQuoteDTOs);
    		
    				List<Quote> newQuotes = this.quotesRetrievalService.getNewQuotesForInvestment(
    						investment, maxQuoteDate, existingQuotes);
        	
    				this.saveAllQuotes(newQuotes);
    			}
    		}
    	}
    }
    
    
    public List<Quote> updateQuoteForInvestment(Long investmentId) throws InvestmentManagerServiceException {
    	
    	Investment investment = this.investmentsService.findInvestmentByInvestmentId(investmentId).get();
    	
    	Optional<Date> optQuoteDate = this.findGreatestQuoteDateForSymbol(investment.getSymbol());
		Long today = new Date().getTime();
    	List<Quote> newQuotes = new ArrayList<>();
		Date maxQuoteDate = optQuoteDate.get();
		
		if (optQuoteDate.isPresent()) {
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
    }
    
    public Quote updateQuote(Quote quote) {
    	QuoteDTO quoteData = ObjectMappingUtil.mapQuoteToQuoteDTO(quote);
    	quoteData = this.quotesRepo.save(quoteData);
    	
    	return ObjectMappingUtil.mapQuoteDtoToQuote(quoteData);
    }
    
    
    /**
     * Locates all quotes for an investment by its unique identifier.
     * 
     * @param investmentId - investment unique id
     * @return {@link List}<Quote>
     */
    public List<QuoteDTO> findAllByInvestmentId(Long investmentId) {
    	
    	return this.quotesRepo.findAllByInvestmentInvestmentId(investmentId);
    }
    
    /**
     * Retrieves a quote for a specific investment on a specific date
     * 
     * @param symbol - investment identifier
     * @param date - quote date
     * @return {@link QuoteDTO}
     */
    public Optional<QuoteDTO> findQuoteBySymbolAndDate(String symbol, Date date) {
        
        Optional<QuoteDTO> optQuote = this.quotesRepo.findByInvestmentSymbolAndDate(symbol, date);
        
        return optQuote;
    }
    
    /**
     * Retrieves the most recent quote date for a given investment. Returns null is it can't be found
     * 
     * @param symbol - investment identifier
     * @return {@link Date}
     * @throws ParseException 
     */
    public Optional<Date> findGreatestQuoteDateForSymbol(String symbol) {
        
    	Optional<String> optDateString = this.quotesRepo.getMaxQuoteDateForInvestmentSymbol(symbol);
        
        if (optDateString.isPresent()) {
            try {
				return Optional.of(
						CommonUtils.convertDatePickerDateFormatStringToDate(optDateString.get()));
			} catch (ParseException e) {
				//eat it
				e.printStackTrace();
			}
        }
        
        return Optional.empty();
    }
    
    /**
     * Retrieves a list of Quotes for a given investment
     * 
     * @param symbol - investment identifier
     * @return List<Quote> 
     */
    public List<Quote> findAllQuotesBySymbol(String symbol) {
        
        List<QuoteDTO> quotesData = this.quotesRepo.findAllByInvestmentSymbol(symbol);
        
        return ObjectMappingUtil.mapQuoteDtoList(quotesData);
    }
    
    /**
     * Persists the quote
     * 
     * @param quote - the object to persist
     */
    @Transactional
    public Quote saveQuote(Quote quote) {
    	
    	QuoteDTO quoteData = ObjectMappingUtil.mapQuoteToQuoteDTO(quote);
    	
        quoteData = this.quotesRepo.save(quoteData);
        
        return ObjectMappingUtil.mapQuoteDtoToQuote(quoteData);
    }

	@Transactional
	public void addQuotes(List<Quote> quotes) {
		
		List<QuoteDTO> quotesToSave = new ArrayList<QuoteDTO>(quotes.size());
		
		for (Quote quote : quotes) {
			if (this.findQuoteBySymbolAndDate(quote.getInvestment().getSymbol(), quote.getDate()) == null) {
				quotesToSave.add(ObjectMappingUtil.mapQuoteToQuoteDTO(quote));
			}
		}
		
		this.quotesRepo.saveAll(quotesToSave);
	}
    
}
