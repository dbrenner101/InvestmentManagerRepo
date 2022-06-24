package com.brenner.investments.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.brenner.investments.InvestmentsProperties;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.entities.constants.InvestmentTypeEnum;
import com.brenner.investments.quote.service.QuoteRetrievalException;
import com.brenner.investments.quote.service.QuoteRetrievalService;
import com.brenner.investments.service.InvestmentsService;

/**
 * MVC controller for investment related requests. 
 * 
 * @author dbrenner
 *
 */
@Controller
public class InvestmentController implements WebMvcConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(InvestmentController.class);
	
	@Autowired
	InvestmentsService investmentsService;
	
	@Autowired
	InvestmentsProperties props;
    
    @Autowired
    QuoteRetrievalService quoteService;
    
    /**
     * Retrieves the investments along with their associated quotes.
     * 
     * @param model - container to return the results
     * @return /investments/listInvestmentsAndQuoteDate
     */
    @RequestMapping("/getInvestmentsAndMostRecentQuoteDate")
    public String getInvestmentsAndMostRecentQuoteDate(Model model) {
    	logger.info("Entering getInvestmentsAndMostRecentQuoteDate()");
    	
    	// retrieve investments and associated quotes
    	List<Investment> investments = this.investmentsService.getInvestmentsAndQuotesAssociatedWithAHolding();
    	logger.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
    	model.addAttribute(this.props.getInvestmentsListAttributeKey(), investments);
    	
    	// loop through investments and evaluate quotes
    	Iterator<Investment> iter = investments.iterator();
    	while (iter.hasNext()) {
    		Investment investment = iter.next();
    		List<Quote> quotes = investment.getQuotes();
			
    		if (quotes != null && ! quotes.isEmpty()) {
				// sort the collection which puts the most recent first
				Collections.sort(quotes);
				Quote maxDateQuote = quotes.get(0);
	    			
				List<Quote> singleQuote = new ArrayList<>();
				singleQuote.add(maxDateQuote);
				investment.setQuotes(singleQuote);
	    		
				logger.debug("Max quote date: {}", maxDateQuote);
				logger.debug("Using quote: {}", singleQuote);
    		}
    	}
    	
    	logger.info("Forwarding to investments/listInvestmentsAndQuoteDate");
    	return "investments/listInvestmentsAndQuoteDate";
    }
	
	
	/**
	 * Handles getting details for a specific investment
	 * 
	 * @param symbol - unique investment identifier
	 * @param model - container for the investment details
	 * @return the path to new investment template
	 * @throws QuoteRetrievalException 
	 */
	@GetMapping("/lookupInvestment")
	public String lookupInvestment(@RequestParam(name="symbol", required=true) String symbol, 
			Model model) throws QuoteRetrievalException {
		logger.info("Entering lookupInvestment()");
		logger.debug("Request parameter: symbol: {}", symbol);
		
		Quote quote = this.quoteService.getQuote(symbol);
		logger.debug("Quote: {}", quote);
		
		Investment investment = new Investment();
		investment.setSymbol(symbol.toUpperCase());
		investment.setCompanyName(quote != null ? quote.getInvestment().getCompanyName() : "");
		investment.setExchange(quote != null ? quote.getInvestment().getExchange() : "NA");
		investment.setSector(quote != null ? quote.getInvestment().getSector() : "NA");
		logger.debug("Investment: {}", investment);
				
		model.addAttribute(props.getInvestmentAttributeKey(), investment);
		
		logger.info("Forwarding to investments/newInvestment");
		return "investments/newInvestment";
	}
	
	/**
	 * Method to handle request to add a new investment.
	 * 
	 * @param companyName - name of the company
	 * @param symbol - unique investment identifier
	 * @param model - container to put the newly added investment in
	 * @return investments/addInvestmentEntryPage
	 */
	@GetMapping("/addInvestment")
	public String addInvestment(
	        @RequestParam(name="companyName", required=true) String companyName, 
	        @RequestParam(name="symbol", required=true) String symbol,
	        @RequestParam(name="exchange", required=false) String exchange,
	        @RequestParam(name="sector", required=false) String sector,
	        @RequestParam(name="investmentType", required=true) String investmentType,
	        Model model) {
		logger.info("Entering addInvestment()");
		logger.debug("Request parameters: companyName: {}); symbol: {}; exchange: {}; sector: {}", 
				companyName, symbol, exchange, sector);
		
		Investment investment = this.investmentsService.getInvestmentBySymbol(symbol);
		logger.debug("Retrieved investment: {}", investment);
		
		if (investment == null) {
			investment = new Investment();
			investment.setSymbol(symbol.toUpperCase());
			investment.setCompanyName(companyName);
			investment.setExchange(exchange);
			investment.setSector(sector);
			investment.setInvestmentType(InvestmentTypeEnum.valueOf(investmentType));
			this.investmentsService.saveInvestment(investment);
		}
		model.addAttribute(this.props.getInvestmentAttributeKey(), investment);
		
		logger.info("Forwarding to investments/addInvestmentEntryPage");
		return "investments/addInvestmentEntryPage";
	}
	
	/**
	 * Entry point for the add investment request
	 * 
	 * @return investments/addInvestmentEntryPage
	 */
	@RequestMapping("/addInvestmentEntry")
	public String addInvestmentEntry() {
		
		return "investments/addInvestmentEntryPage";
	}
	
	/**
	 * Entry point to get a list of all investments.
	 * 
	 * @param model - container to place the investments list
	 * @return investments/listAllInvestments"
	 */
	@RequestMapping("/getAllInvestments")
	public String getAllInvestments(Model model) {
		logger.info("Entering getAllInvestments()");
		
		model.addAttribute(
				props.getInvestmentsListAttributeKey(), 
				this.investmentsService.getInvestmentsOrderedBySymbolAsc());
		
		logger.info("Forwarding to investments/listAllInvestments\"");
		return "investments/listAllInvestments";
	}
	
	/**
	 * Method to get details on a specific investment.
	 * 
	 * @param investmentId - unique investment identifier
	 * @param model - container to add the investment details
	 * @return /investments/editInvestment
	 * @throws IOException 
	 */
	@RequestMapping("/getInvestmentDetails")
	public String getInvestmentDetails(
			@RequestParam(name="investmentId", required=true) String investmentId,
			HttpServletResponse response,
			Model model) throws IOException {
		logger.info("Entering getInvestmentDetails()");
		logger.debug("Request parameter: investmentId: {}", investmentId);
		
	    Investment investment = this.investmentsService.getInvestmentByInvestmentId(Long.valueOf(investmentId));
	    logger.debug("Retrieved investment: {}", investment);
		
	    if (investment == null) {
	    	response.sendError(HttpStatus.BAD_REQUEST.value(), "The investment cannot be located. Id: " + investmentId);
	    }
	    
		model.addAttribute(props.getInvestmentAttributeKey(), investment);
		
		logger.info("Forwarding to investments/editInvestment");
		return "investments/editInvestment";
		
	}
	
	/**
	 * Method to update details on an investment
	 * 
	 * @param investmentId - unique investment identifier
	 * @param symbol - common investment identifier
	 * @param companyName - company name
	 * @param exchange - exchange that hosts the investment
	 * @param sector - sector assigned to the investment
	 * @param model - container to put the updated investment
	 * @return redirect:/getAllInvestments
	 */
	@RequestMapping("/updateInvestment")
	public String updateInvestment(
			@ModelAttribute(name="investment") Investment investment,
			Model model) {
		logger.info("Entering updateInvestment()");
		logger.debug("Param: investment: {}", investment);
		
		Investment savedInvestment = this.investmentsService.saveInvestment(investment);
		logger.debug("Saved investment: {}", savedInvestment);
		
		logger.info("Redirecting to getAllInvestments");
		return "redirect:getAllInvestments";
	}
	

}
