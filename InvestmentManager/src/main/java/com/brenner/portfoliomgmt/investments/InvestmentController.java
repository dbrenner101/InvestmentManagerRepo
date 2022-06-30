package com.brenner.portfoliomgmt.investments;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.exception.QuoteRetrievalException;
import com.brenner.portfoliomgmt.quotes.Quote;
import com.brenner.portfoliomgmt.quotes.QuotesService;
import com.brenner.portfoliomgmt.quotes.retrievalservice.QuoteRetrievalService;

/**
 * MVC controller for investment related requests. 
 * 
 * @author dbrenner
 *
 */
@Controller
@Secured("ROLE_USER")
public class InvestmentController implements WebMvcConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(InvestmentController.class);
	
	@Autowired
	InvestmentsService investmentsService;
    
    @Autowired
    QuoteRetrievalService quoteRetrievalService;
    
    @Autowired
    QuotesService quotesService;
    
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
    	
    	model.addAttribute("investments", investments);
    	
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
	public String lookupInvestment(
			@RequestParam(name="symbol", required=true) String symbol, 
			@RequestParam(name="lookupInvestmentFlag", required=false) boolean lookupInvestmentFlag,
			Model model) throws QuoteRetrievalException {
		logger.info("Entering lookupInvestment()");
		logger.debug("Request parameter: symbol: {}", symbol);
		
		Quote quote = null;
		
		if (lookupInvestmentFlag) {
			quote = this.quoteRetrievalService.getQuote(symbol);
		}
		
		logger.debug("Quote: {}", quote);
		
		Investment investment = new Investment();
		investment.setSymbol(symbol.toUpperCase());
		investment.setCompanyName(quote != null ? quote.getInvestment().getCompanyName() : null);
		investment.setExchange(quote != null ? quote.getInvestment().getExchange() : null);
		investment.setSector(quote != null ? quote.getInvestment().getSector() : null);
		
		logger.debug("Investment: {}", investment);
				
		model.addAttribute("investment", investment);
		
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
		
		Optional<Investment> optInvestment = this.investmentsService.getInvestmentBySymbol(symbol);
		
		if (! optInvestment.isPresent()) {
			throw new NotFoundException("Investment with symbol " + symbol + " does not exist.");
		}
		
		Investment investment = optInvestment.get();
		
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
		model.addAttribute("investment", investment);
		model.addAttribute("investments", this.investmentsService.getAllInvestmentsSortedBySymbol());
		
		logger.info("Forwarding to investments/addInvestmentEntryPage");
		
		return "investments/addInvestmentEntryPage";
	}
	
	/**
	 * Entry point for the add investment request
	 * 
	 * @return investments/addInvestmentEntryPage
	 */
	@RequestMapping("/addInvestmentEntry")
	public String addInvestmentEntry(Model model) {
		
		model.addAttribute("investments", this.investmentsService.getAllInvestmentsSortedBySymbol());
		
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
		
		List<Investment> investments = this.investmentsService.getInvestmentsOrderedBySymbolAsc();
		model.addAttribute("investments", investments);
		
		Map<Long, Date> quotesForInvestments = new HashMap<>(investments.size());
		
		for (Investment investment : investments) {
			Optional<Date> optDate = this.quotesService.findGreatestQuoteDateForSymbol(investment.getSymbol());
			if (optDate.isPresent()) {
				quotesForInvestments.put(investment.getInvestmentId(), optDate.get());
			}
		}
		
		model.addAttribute("quoteDatesForInvestmentsMap", quotesForInvestments);
		
		logger.info("Forwarding to investments/listAllInvestments");
		
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
		
		Optional<Investment> optInvestment = this.investmentsService.getInvestmentByInvestmentId(Long.valueOf(investmentId));
		
		if (! optInvestment.isPresent()) {
			throw new NotFoundException("Investment with id " + investmentId + " does not exist.");
		}
		
	    Investment investment = optInvestment.get();
	    
	    logger.debug("Retrieved investment: {}", investment);
		
	    if (investment == null) {
	    	response.sendError(HttpStatus.BAD_REQUEST.value(), "The investment cannot be located. Id: " + investmentId);
	    }
	    
		model.addAttribute("investment", investment);
		
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
	
	@GetMapping(path="/deleteInvestment")
	public String deleteInvestment(@RequestParam(name="investmentId", required=true) Long investmentId) {
		
		logger.info("Entering deleteInvestment()");
		logger.debug("Param: investmentId: {}", investmentId);
		
		Optional<Investment> optInvestment = this.investmentsService.getInvestmentByInvestmentId(investmentId);
		
		if (! optInvestment.isPresent()) {
			throw new NotFoundException("Investment with id " + investmentId + " does not exist.");
		}
		
		Investment i = optInvestment.get();
		
		this.investmentsService.deleteInvestment(i);
		
		logger.debug("Deleted investment: {}", i);
		logger.info("Redirecting to getAllInvestments");
		
		return "redirect:getAllInvestments";
	}
	
	

}
