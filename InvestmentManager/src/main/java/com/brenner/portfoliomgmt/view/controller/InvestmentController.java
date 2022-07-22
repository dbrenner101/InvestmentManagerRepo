package com.brenner.portfoliomgmt.view.controller;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.exception.QuoteRetrievalException;
import com.brenner.portfoliomgmt.quotes.retrievalservice.QuoteRetrievalService;
import com.brenner.portfoliomgmt.service.InvestmentsService;
import com.brenner.portfoliomgmt.service.QuotesService;

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
    /*@RequestMapping("/getInvestmentsAndMostRecentQuoteDate")
    public String getInvestmentsAndMostRecentQuoteDate(Model model) {
    	logger.info("Entering getInvestmentsAndMostRecentQuoteDate()");
    	
    	// retrieve investments and associated quotes
    	List<Investment> investments = this.investmentsService.findInvestmentsAndQuotesAssociatedWithAHolding();
    	List<Investment> viewInvestments = new ArrayList<>(investments.size());
    	
    	logger.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
    	
    	// loop through investments and evaluate quotes
    	Iterator<Investment> iter = investments.iterator();
    	while (iter.hasNext()) {
    		Investment investment = iter.next();
    		Investment investmentValue = new Investment(investment);
    		List<Quote> quotes = investment.getQuotes();
			
    		if (quotes != null && ! quotes.isEmpty()) {
				// sort the collection which puts the most recent first
				Collections.sort(quotes);
				QuoteDTO maxDateQuote = quotes.get(0);
				investmentValue.setQuoteValue(new Quote(maxDateQuote));
	    		
				logger.debug("Max quote date: {}", maxDateQuote);
    		}
    		
    		viewInvestments.add(investmentValue);
    	}
    	
    	model.addAttribute("investments", viewInvestments);
    	
    	logger.info("Forwarding to investments/listInvestmentsAndQuoteDate");
    	
    	return "investments/listInvestmentsAndQuoteDate";
    }*/
	
	
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
	public String addInvestment(@ModelAttribute Investment investment, Model model) {
		
		logger.info("Entering addInvestment()");
		logger.debug("Request parameters: investment: {});", 
				investment);
		
		Optional<Investment> optInvestment = this.investmentsService.findInvestmentBySymbol(investment.getSymbol());
		if (optInvestment.isPresent()) {
			investment = optInvestment.get();
			logger.debug("Retrieved investment: {}", investment);
		}
		else {
			investment = this.investmentsService.saveInvestment(investment);
		}
		
		model.addAttribute("investment", investment);
		
		List<Investment> allInvestments = this.investmentsService.findAllInvestments("symbol");
		
		model.addAttribute("investments", allInvestments);
		
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
		
		List<Investment> allInvestments = this.investmentsService.findAllInvestments("symbol");
		
		model.addAttribute("investments", allInvestments);
		
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
		
		List<Investment> allInvestments = this.investmentsService.findAllInvestments("symbol");
		
		model.addAttribute("investments", allInvestments);
		
		for (Investment investment : allInvestments) {
			Optional<Date> optDate = this.quotesService.findGreatestQuoteDateForSymbol(investment.getSymbol());
			if (optDate.isPresent()) {
				Quote quoteValue = new Quote();
				quoteValue.setDate(optDate.get());
				investment.setMostRecentQuote(quoteValue);
			}
		}
		
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
		
		Optional<Investment> optInvestment = this.investmentsService.findInvestmentByInvestmentId(Long.valueOf(investmentId));
		
		if (! optInvestment.isPresent()) {
			throw new NotFoundException("Investment with id " + investmentId + " does not exist.");
		}
		
	    Investment investment = optInvestment.get();
	    
	    logger.debug("Retrieved investment: {}", investment);
	    
		model.addAttribute("investment", investment);
		
		logger.info("Forwarding to investments/editInvestment");
		
		return "investments/editInvestment";
		
	}
	
	/**
	 *
	 * Method to update details on an investment
	 *
	 * @param model - container to put the updated investment
	 * @return redirect:/getAllInvestments
	 * @param investment Details on the investment to update. Must include an investmentId.
	 * @param model
	 * @return
	 */
	@RequestMapping("/updateInvestment")
	public String updateInvestment(
			@ModelAttribute(name="investment") Investment investment,
			Model model) {
		logger.info("Entering updateInvestment()");
		logger.debug("Param: investment: {}", investment);
		
		investment = this.investmentsService.saveInvestment(investment);
		
		logger.debug("Saved investment: {}", investment);
		logger.info("Redirecting to getAllInvestments");
		
		return "redirect:getAllInvestments";
	}
	
	@GetMapping(path="/deleteInvestment")
	public String deleteInvestment(@RequestParam(name="investmentId", required=true) Long investmentId) {
		
		logger.info("Entering deleteInvestment()");
		logger.debug("Param: investmentId: {}", investmentId);
		
		Optional<Investment> optInvestment = this.investmentsService.findInvestmentByInvestmentId(investmentId);
		
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
