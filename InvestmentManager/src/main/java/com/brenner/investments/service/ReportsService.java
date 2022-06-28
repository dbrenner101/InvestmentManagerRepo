package com.brenner.investments.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brenner.investments.data.HoldingsReportingDataService;
import com.brenner.investments.data.PortfolioRollupRepository;
import com.brenner.investments.entities.BatchQuotes;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.entities.PortfolioRollup;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.entities.reporting.PortfolioReport;
import com.brenner.investments.pojo.HoldingsReport;
import com.brenner.investments.quote.service.QuoteRetrievalException;
import com.brenner.investments.quote.service.iex.IEXQuoteRetrievalService;
import com.brenner.investments.util.CommonUtils;

/**
 * Data service class to abstract the various JPA repositories
 * 
 * @author dbrenner
 *
 */
@Service
public class ReportsService {
	
	private static final Logger log = LoggerFactory.getLogger(ReportsService.class);
	
	@Autowired
	PortfolioRollupRepository rollupRepo;
    
    @Autowired
    HoldingsService holdingsService;
    
    @Autowired
    IEXQuoteRetrievalService quoteService;
    
    @Autowired
    HoldingsReportingDataService reportingDataService;
    
    
    public List<HoldingsReport> getHoldingsByTypeAndSector() {
    	
    	log.info("Entered getHoldingsByTypeAndSector()");
    	
    	List<HoldingsReport> report = this.reportingDataService.getHoldingsByInvestmentTypeAndSector();
    	
    	log.info("Exiting getHoldingsByTypeAndSector()");
    	
    	return report;
    }
    
    
    public List<HoldingsReport> retrieveHoldings(String primaryDataSet, String sortOrder) {
    	
    	log.info("Entered retrieveHoldings()");
    	log.debug("Params: orderBy: {}; order: {}", primaryDataSet, sortOrder);
    	
    	List<HoldingsReport> holdings = new ArrayList<>();
    	
    	if (primaryDataSet == null || primaryDataSet.equals("marketValue")) {
    		holdings = this.reportingDataService.findHoldingsByMarketValueOrderedDesc(sortOrder);
    	}
    	else if (primaryDataSet.equals("changeInValue")) {
    		holdings = this.reportingDataService.findHoldingByChangeInValueOrderedDesc(sortOrder);
    	}
    	
    	log.debug("Returning {} items", holdings.size());
    	
    	return holdings;
    }
	
	
	
	// Start roll-up methods
	
    /**
     * Gets the changes in the portfolio over time with optional symbol and date
     * 
     * @param symbol - investment identifier
     * @param date - start date to rollup data
     * @return List<PortfolioRollup>
     */
	public List<PortfolioRollup> getChangeInPortfolioOverTime(String symbol, Date date) {
	    
	    List<PortfolioRollup> rollup = null;
	    String minDateString = date == null ? null : CommonUtils.convertDateToMMYYDDString(date);
	    
	    if (symbol != null && symbol.equalsIgnoreCase("all")) {
	        if (minDateString != null) {
	            rollup = this.rollupRepo.getChangeInPortfolioValueByMonths(minDateString);
	        }
	        else {
	            rollup = this.rollupRepo.getChangeInPortfolioValue();
	        }
	    }
	    else if (symbol != null && date != null) {
	        
	        rollup =  this.rollupRepo.getChangeInPortfolioValueBySymbolAndDate(symbol, minDateString);
	    }
	    else {
	        rollup = this.rollupRepo.getChangeInPortfolioValue();
	    }
        
        return rollup;
    }
    
	/**
	 * Retrieves the total change in the porfolio over time. This method does the roll up in Java rather than leveraging the database
	 * 
	 * @return {@link PortfolioReport}
	 * @throws QuoteRetrievalException  thrown when the quote retrieval service fails
	 */
    public PortfolioReport getTotalChangeInPortfolio() throws QuoteRetrievalException {
        
        
        Iterable<Holding> holdingsIter = this.holdingsService.getAllHoldings();
        
        List<String> symbols = new ArrayList<>();
        
        if (holdingsIter != null) {
            Iterator<Holding> iter = holdingsIter.iterator();
            while (iter.hasNext()) {
                Holding holding = iter.next();
                symbols.add(holding.getInvestment().getSymbol());
            }
        }
        else {
            return null;
        }
        
        BatchQuotes batchQuotes = this.quoteService.getBatchQuotes(symbols);
        
        Double totalChangeInValue = 0D;
        Double totalValueAtPurchase = 0D;
        Double totalCurrentValue = 0D;
        Double totalChangeInPrice = 0D;
        
        if (batchQuotes != null) {
            Iterator<Holding> iter = holdingsIter.iterator();
            while (iter.hasNext()) {
                Holding holding = iter.next();
                String symbol = holding.getInvestment().getSymbol();
                Quote quote = batchQuotes.getQuotes().get(symbol);
                if (quote != null) {
                    //holding.setQuote(batchQuotes.getQuotes().get(symbol));
                }
                totalChangeInValue += holding.getChangeInValue() != null ? holding.getChangeInValue() : 0D;
                totalValueAtPurchase += holding.getValueAtPurchase() != null ? holding.getValueAtPurchase() : 0D;
                totalCurrentValue += holding.getCurrentValue() != null ? holding.getCurrentValue() : 0D;
                //totalChangeInPrice += holding.getChangeInPrice() != null ? holding.getChangeInPrice() : 0D;
            }
        }
        
        PortfolioReport report = new PortfolioReport();
        report.setTotalChangeInValue(totalChangeInValue);
        report.setTotalChangeInPrice(totalChangeInPrice);
        report.setTotalCurrentValue(totalCurrentValue);
        report.setTotalValueAtPurchase(totalValueAtPurchase);
        
        
        return report;
    }

}
