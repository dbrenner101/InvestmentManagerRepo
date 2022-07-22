package com.brenner.portfoliomgmt.view.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.brenner.portfoliomgmt.domain.Holding;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.exception.InvestmentManagerServiceException;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.service.HoldingsService;
import com.brenner.portfoliomgmt.service.InvestmentsService;
import com.brenner.portfoliomgmt.service.QuotesService;
import com.brenner.portfoliomgmt.util.CommonUtils;

/**
 * Controller for working with quotes
 * 
 * @author dbrenner
 *
 */
@Controller
public class QuotesController implements WebMvcConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(QuotesController.class);
    
    @Autowired QuotesService quotesService;
    
    @Autowired InvestmentsService investmentsService;
    
    @Autowired HoldingsService holdingsService;
    
    @RequestMapping(path="/editQuotesStart")
    public String editQuotesStart(Model model) {
    	
    	model.addAttribute("investments", this.investmentsService.findAllInvestments("symbol"));
    	
    	return "quotes/listQuotesForInvestment";
    }
    
    @RequestMapping(path="/editQuote")
    public String editQuote(@RequestParam(name="quoteId") Long quoteId, Model model) {
    	
    	Optional<Quote> optQuote = this.quotesService.findQuoteByQuoteId(quoteId);
    	if (! optQuote.isPresent()) {
    		throw new NotFoundException("Quote with quoteId " + quoteId + " was not found.");
    	}
    	
    	model.addAttribute("quote", optQuote.get());
    	
    	return "quotes/editQuote";
    }
    
    @RequestMapping(path="updateManualQuote")
    public String updateManualQuote(@ModelAttribute Quote quote, @RequestParam(name="investmentId") Long investmentId, BindingResult result, ModelMap model) {
    	
    	Optional<Investment> optInv = this.quotesService.findInvestmentForQuote(investmentId);
    	if(! optInv.isPresent()) {
    		throw new NotFoundException("Investment for quote " + quote + " was not found");
    	}
    	
    	Investment investment = optInv.get();
    	quote.setInvestment(investment);
    	
    	this.quotesService.updateQuote(quote);
    	
    	return "redirect:/editQuotesStart";
    }
    
    @RequestMapping(path="/startManualQuote")
    public String startManualQuote(@RequestParam(name="investmentId", required=true) Long investmentId, Model model) {
    	
    	Optional<Investment> optInvestment = this.investmentsService.findInvestmentByInvestmentId(investmentId);
    	if (! optInvestment.isPresent()) {
    		throw new NotFoundException("Investment with id " + investmentId + "does not exist");
    	}
    	
    	Investment investment = optInvestment.get();
    	model.addAttribute("investment", investment);
    	model.addAttribute("quote", new Quote());
    	
    	return "quotes/addManualQuote";
    }
    
    
    /**
     * Loops through all investments associated with a holding and updates the persisted set of quotes.
     * 
     * @return redirect:/getInvestmentsAndMostRecentQuoteDate
     * @throws ParseException - passed along from chain
     * @throws IOException - passed along from chain
     */
    @RequestMapping("/updateQuotesForAllInvestments")
    public String updateQuotesForAllInvestments() throws ParseException, IOException {
    	logger.info("Entering updateQuotesForAllInvestments()");
    	
    	this.quotesService.updateQuotesForAllInvestments();
    	
    	logger.info("Redirecting to: getInvestmentsAndMostRecentQuoteDate");
    	return "redirect:getInvestmentsAndMostRecentQuoteDate";
    }
    
    
    /**
     * Retrieves and stored quotes for a specific investment. Ensures it doesn't duplicate the saved quotes.
     * 
     * @param investmentIdStr - string representation of the unique investment identifier
     * @return "redirect:/getInvestmentsAndMostRecentQuoteDate"
     * @throws IOException - underlying IO issues
     * @throws InvestmentManagerServiceException  - communicates issues from the service layer
     * @throws NumberFormatException  - inability to convert data
     */
    @RequestMapping("/updateQuoteForInvestment")
    public String updateQuoteForInvestment(
    		@RequestParam(name="investmentId", required=true) String investmentIdStr) 
    throws IOException, NumberFormatException, InvestmentManagerServiceException {
    	logger.info("Entering updateQuoteForInvestment()");
    	logger.debug("Request parameter: investmentId: {}", investmentIdStr);
    	
    	this.quotesService.updateQuoteForInvestment(Long.valueOf(investmentIdStr));
    	
    	logger.info("Redirecting to getInvestmentsAndMostRecentQuoteDate");
    	return "redirect:getInvestmentsAndMostRecentQuoteDate";
    }
    
    /**
     * Entry point to get quotes for holdings
     * 
     * @param model - add all holdings ordered by symbol
     * @return /quotes/getQuotes
     */
    @RequestMapping("/prepGetQuotes")
    public String prepGetQuotes(Model model) {
    	logger.info("Entering prepGetQuotes()");
    	
    	List<Holding> holdings = this.holdingsService.findAllHoldingsOrderedBySymbol();
    	logger.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
        model.addAttribute("holdings", holdings);
        
        logger.info("Forwarding to quotes/getQuotes");
        return "quotes/getQuotes";
    }
    
    /**
     * Calls the service layer to handle the quote data submitted. 
     * 
     * @param quote - quote object to save
     * @param symbol - investment identifier
     * @param model - container for the objects
     * @return redirect:/prepAddManualQuoteForm
     * @throws ParseException - thrown when unable to parse data string
     */
    @RequestMapping("/saveManualQuote")
    public ModelAndView saveManualQuote(
    		@ModelAttribute(name="quote") Quote quote,  
            @RequestParam(name="symbol", required=true) String symbol, 
            Model model) throws ParseException {
    	logger.info("Entering saveManualQuote()");
    	logger.debug("Request parameters: quote {}; symbol: {}", quote, symbol);
    	
        quote = this.quotesService.addManualQuote(quote, symbol);
        logger.debug("Saved quote: {}", quote);
        model.addAttribute("quote", quote);
        
        logger.info("Redirecting to getAllInvestments");
        return new ModelAndView("redirect:getAllInvestments");
   }
    
    /**
     * Requests 30 days of quotes for a specific investment form the servide layer.
     * 
     * @param investmentIdStr - Unique investment identifier
     * @param response - The response object to write back to
     * @throws InvestmentManagerServiceException - Thrown from the service layer for general issues retrieving quotes
     * @throws IOException - Thrown for errors writing to the response stream
     */
    @RequestMapping("/load30DaysQuotesForHoldingAjax")
    public void load30DaysQuotesForHoldingAjax(
    		@RequestParam(name="investmentId", required=true) String investmentIdStr, 
    		HttpServletResponse response) throws InvestmentManagerServiceException, IOException {
    	logger.info("Entered load30DaysQuotesForHoldingAjax()");
    	logger.debug("Param: investmentIdStr: {}", investmentIdStr);
    	
    	Investment inv = this.investmentsService.findInvestmentByInvestmentId(Long.valueOf(investmentIdStr)).get();
    	
    	List<Quote> quotes = null;
    	
    	if (inv != null) {
    		quotes = this.quotesService.findAllQuotesBySymbol(inv.getSymbol());
    	}
    		
		if (quotes == null || quotes.isEmpty()) {
			quotes = this.quotesService.updateQuoteForInvestment(inv.getInvestmentId());
		}
		
		//Collections.sort(quotes);
    	
    	CommonUtils.serializeObjectToJson(response.getOutputStream(), quotes);
    	
    	logger.info("Exiting load30DaysQuotesForHoldingAjax()");
    }
    
    /**
     * Async method for retrieving quotes.
     * 
     * @param model add all holdings ordered by symbol
     * @return /quotes/getQuotesAjax
     */
    @RequestMapping("/prepGetQuotesAjax")
    public String prepGetQuotesAjax(Model model) {
    	logger.info("Entering prepGetQuotesAjax()");
    	
    	List<Investment> investments = this.holdingsService.findAllInvestmentsCurrentlyHeldOrderedBySymbol();
    	logger.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
        model.addAttribute(
        		"investments", 
        		investments);
        
        logger.info("Forwarding to quotes/getQuotesAjax");
        return "quotes/getQuotesAjax";
    }
    
}
