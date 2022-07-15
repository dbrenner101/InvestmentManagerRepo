package com.brenner.portfoliomgmt.reporting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.brenner.portfoliomgmt.InvestmentsProperties;
import com.brenner.portfoliomgmt.exception.QuoteRetrievalException;
import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.investments.InvestmentsService;
import com.brenner.portfoliomgmt.quotes.Quote;
import com.brenner.portfoliomgmt.quotes.retrievalservice.QuoteRetrievalService;
import com.brenner.portfoliomgmt.transactions.Transaction;
import com.brenner.portfoliomgmt.transactions.TransactionsService;
import com.brenner.portfoliomgmt.util.CommonUtils;

/**
 * MVC controller to handle report centic requests.
 * 
 * @author dbrenner
 *
 */
@Controller
@Secured("ROLE_USER")
public class ReportsController implements WebMvcConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);
	
	@PersistenceContext
	EntityManager em;
    
    @Autowired
    ReportsService reportsService;
    
    @Autowired
    InvestmentsService investmentsService;
    
    @Autowired
    QuoteRetrievalService quoteService;
    
    @Autowired
    TransactionsService transactionService;
    
    @Autowired
    InvestmentsProperties props;
    
    /**
     * Request method to retrieve a summation of holdings by type and sector
     * 
     * @param model - container to interact with UI layer
     * @return reports/holdingsView
     */
    @GetMapping("/holdingsByTypeAndSector")
    public String holdingsByTypeAndSector(Model model) {
    	logger.info("Entered holdingsByTypeAndSector()");
    	
    	List<HoldingsReport> report = this.reportsService.getHoldingsByTypeAndSector();
    	logger.debug("Retrieved {} objects", report != null ? report.size() : 0);
    	model.addAttribute("holdingsReport", report);
    	
    	logger.info("Exiting holdingsByTypeAndSector()");
    	return "reports/holdingsView";
    }
    
    /**
     * Retrieves a summation of {@link HoldingsReport} objects ordered by market value or change in value based on parameters
     * 
     * @param sortOrder - ASC or DESC marker
     * @param dataSet - market value or change in value
     * @param model - container to interact with UI layer
     * @return reports/rollup
     */
    @GetMapping("/retrieveRollupData")
    public String retrieveRollupData(
    		@RequestParam(name="sortOrder", required=false) String sortOrder, 
    		@RequestParam(name="dataSet", required=false) String dataSet,
    		Model model) {
    	logger.info("Entering retrieveRollupData()");
    	logger.debug("Request parameters: orderStr: {}); orderByStr: {}", sortOrder, dataSet);
    	
    	if (sortOrder == null || sortOrder.trim().length() == 0) {
    		sortOrder = "DESC";
    	}
    	
    	List<HoldingsReport> holdings = this.reportsService.retrieveHoldings(dataSet, sortOrder);
    	logger.debug("Retrieved {} holdings", holdings.size());
    	model.addAttribute(this.props.getHoldingsByMarketValueAttributeKey(), holdings);
    	
    	Map<Long, Transaction> dividendsMap = this.transactionService.getTotalDidivendsForAllInvestments();
    	
    	Float totalChangeInValue = 0F;
    	Integer losers = 0;
    	Integer gainers = 0;
    	Integer unchanged = 0;
    	
    	if (holdings != null) {
    		Iterator<HoldingsReport> iter = holdings.iterator();
    		while (iter.hasNext()) {
    			HoldingsReport h = iter.next();
    			h.setTotalValue(h.getMarketValue());
    			Transaction dividendTrans = dividendsMap.get(h.getInvestmentId());
    			if (dividendTrans != null) {
    				Float dividend = dividendTrans.getDividend();
    				h.setTotalDividends(dividend);
    				if (h.getMarketValue() != null) {
    					h.setTotalValue(h.getMarketValue() + dividend);
    				}
    			}
    			totalChangeInValue += h.getChangeInValue();
    			if (h.getChangeInValue() > 0) {
    				++gainers;
    			}
    			else if (h.getChangeInValue() < 0) {
    				++losers;
    			}
    			else {
    				++unchanged;
    			}
    		}
    	}
    	
    	logger.debug("Total change in value: {}", totalChangeInValue);
    	logger.debug("Gainers: {}", gainers);
    	logger.debug("Losers: {}", losers);
    	logger.debug("Unchanged: {}", unchanged);
    	
    	model.addAttribute(this.props.getTotalMarketValueChangeAttribute(), totalChangeInValue);
    	model.addAttribute("gainers", gainers);
    	model.addAttribute("losers", losers);
    	model.addAttribute("unchanged", unchanged);
    	
    	if (sortOrder.equals("DESC")) {
    		sortOrder = "ASC";
    	}
    	else {
    		sortOrder = "DESC";
    	}
    	
    	model.addAttribute("sortOrder", sortOrder);
    	
    	logger.info("Forwarding to reports/rollup");
    	return "reports/rollup";
    }

    
    /**
     * Method to get a specific quote.
     * 
     * @param symbol - investment identifier
     * @param response - object containing the output stream to write to
     * @throws IOException - thrown if there is an issue retrieving the quote or writing back to the output stream
     * @throws QuoteRetrievalException 
     */
    @RequestMapping("/getQuotexxx")
    public void getQuote(
            @RequestParam(name="symbol", required=true) String symbol,
            HttpServletResponse response) throws IOException, QuoteRetrievalException {
    	logger.info("Entering getQuote()");
    	logger.debug("Request param: symbol: {}", symbol);
        
        Quote quote1 = this.quoteService.getQuote(symbol);
        logger.debug("Retrieved quote: {}", quote1);
        
        List<Quote> quotes = new ArrayList<>(2);
        quotes.add(quote1);
        
        logger.info("Serializing quote object to JSON : {}", quotes);
        CommonUtils.serializeObjectToJson(response.getOutputStream(), quotes);
        
    }
    
    /**
     * Entry point for the reports template
     * 
     * @param model - container to add the list of investments ordered by symbol
     * @return /reports/reportSelector
     */
    @RequestMapping("/prepForMostRecentQuote")
    public String prepReports(Model model) {
    	logger.info("Entering prepReports()");
        
    	List<Investment> investments = this.investmentsService.getInvestmentsOrderedBySymbolAsc();
    	logger.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
    	model.addAttribute(props.getInvestmentsListAttributeKey(), investments);
        
        logger.info("Forwarding to reports/reportSelector");
        return "reports/reportSelector";
    }
    
    /**
     * Retrieves the total change in the portfolio and serializes the response to JSON
     * 
     * @param response - object containing the output stream to write the result
     * @throws IOException - thrown if there is an issue writing the data back to the output stream
     * @throws QuoteRetrievalException 
     */
    @RequestMapping("/getTotalChangeInPortfolioAjax")
    public void getTotalChangeInPortfolioAjax(HttpServletResponse response) 
    		throws IOException, QuoteRetrievalException {
    	logger.info("Entering getTotalChangeInPortfolioAjax()");
        
    	PortfolioReport totalChange = this.reportsService.getTotalChangeInPortfolio();
    	logger.debug("Portfolio change report: {}", totalChange);
    	
    	logger.info("Writing object to JSON");
    	CommonUtils.serializeObjectToJson(response.getOutputStream(), totalChange);
        
    }
    
    /**
     * Entry point to the view historical performance template
     * 
     * @param model - container to add the list of investments ordered by symbol
     * @return /reports/historicalView
     */
    @RequestMapping("/visualizeHistoricalPerformance")
    public String visualizeHistoricalPerformance(Model model) {
    	logger.info("Entering visualizeHistoricalPerformance()");
        
    	List<Investment> investments = this.investmentsService.getInvestmentsOrderedBySymbolAsc();
    	logger.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
    	model.addAttribute(this.props.getInvestmentsListAttributeKey(), investments);
        
        logger.info("Forwarding to reports/historicalView");
        return "reports/historicalView";
    }
    
    /**
     * Entry point to the visualize portfolio performance
     * 
     * @param model - container to hold the list of investments ordered by symbol
     * @return /reports/portfolioPerformance
     */
    @RequestMapping("/visualizePortfolioPerformance")
    public String visualizePortfolioPerformance(Model model) {
    	logger.info("Entering visualizePortfolioPerformance()");
        
    	List<Investment> investments = this.investmentsService.getInvestmentsOrderedBySymbolAsc();
    	logger.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
        model.addAttribute(this.props.getInvestmentsListAttributeKey(), investments);
        
        logger.info("Forwarding to reports/portfolioPerformance");
        return "reports/portfolioPerformance";
    }
    
    /**
     * Method to get historical performance information for specific investments
     * 
     * @param response - object containing the output stream to write the result to
     * @param symbol - investment identifier
     * @param numDays - length of time to build performance data
     * @throws IOException is thrown if there are issues writing to the output steam
     */
    @RequestMapping("getPortfolioPerformanceAjax")
    public void getPortfolioPerformanceAjax(
            HttpServletResponse response, 
            @RequestParam(name="symbol", required=false) String symbol, 
            @RequestParam(name="numDays", required=false) String numDays) throws IOException {
    	logger.info("Entering getPortfoiloChangeOverTimeAjax()");
    	logger.debug("Request parameters: symbol: {}); numDays: {}", symbol, numDays);
    	
    	if (numDays == null || numDays.equals("0")) {
    		numDays = "30";
    	}
        
        Date pastDate = CommonUtils.getDateSomeNumMonthsAgo(Integer.valueOf(numDays));
        logger.debug("pastDate: {}", pastDate);
        
        List<PortfolioRollup> rollup = this.reportsService.getChangeInPortfolioOverTime(symbol, pastDate);
        logger.debug("Retrieved {} PortfolioRollup", rollup != null ? rollup.size() : 0);
        
        logger.info("Serizing object to JSON");
        CommonUtils.serializeObjectToJson(response.getOutputStream(), rollup);
    }
}
