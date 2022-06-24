package com.brenner.investments.service;

import java.util.Date;
import java.util.List;

import com.brenner.investments.entities.PortfolioRollup;
import com.brenner.investments.entities.reporting.PortfolioReport;
import com.brenner.investments.pojo.HoldingsReport;
import com.brenner.investments.quote.service.QuoteRetrievalException;

public interface ReportsService {

	/**
     * Gets the changes in the portfolio over time with optional symbol and date
     * 
     * @param symbol - investment identifier
     * @param date - start date to rollup data
     * @return List<PortfolioRollup>
     */
	public List<PortfolioRollup> getChangeInPortfolioOverTime(String symbol, Date date);
    
	/**
	 * Retrieves the total change in the porfolio over time. This method does the roll up in Java rather than leveraging the database
	 * 
	 * @return {@link PortfolioReport}
	 * @throws QuoteRetrievalException  thrown when the quote retrieval service fails
	 */
    public PortfolioReport getTotalChangeInPortfolio() throws QuoteRetrievalException;
    
    public List<HoldingsReport> retrieveHoldings(String primaryDataSet, String sortOrder);
    
    public List<HoldingsReport> getHoldingsByTypeAndSector();
    
}
