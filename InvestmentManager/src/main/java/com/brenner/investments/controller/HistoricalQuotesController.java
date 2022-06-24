package com.brenner.investments.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.brenner.investments.InvestmentsProperties;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.service.InvestmentManagerServiceException;
import com.brenner.investments.service.InvestmentsService;
import com.brenner.investments.service.QuotesService;
import com.brenner.investments.service.TransactionsService;
import com.brenner.investments.util.CommonUtils;

/**
 * Controller for working with quotes
 * 
 * @author dbrenner
 *
 */
@Controller
public class HistoricalQuotesController implements WebMvcConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(HistoricalQuotesController.class);
    
    @Autowired
    QuotesService quotesService;
    
    @Autowired
    TransactionsService transactionsService;
    
    @Autowired
    InvestmentsService investmentsService;
    
    @Autowired
    InvestmentsProperties props;
    
    
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
    	
    	List<Holding> holdings = this.transactionsService.getAllHoldingsOrderedBySymbol();
    	logger.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
        model.addAttribute(props.getHoldingsListAttributeKey(), holdings);
        
        logger.info("Forwarding to quotes/getQuotes");
        return "quotes/getQuotes";
    }
    
    /**
     * prepares the data for the entry of a quote
     * 
     * @param model - container for the attributes
     * @return /quotes/addManualQuote
     */
    @RequestMapping("/prepAddManualQuoteForm")
    public String prepAddManualQuote(Model model) {
    	logger.info("Entering prepAddManualQuote()");
    	
    	List<Holding> holdings = this.transactionsService.getAllHoldingsOrderedBySymbol();
    	logger.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
        model.addAttribute(
        		this.props.getHoldingsListAttributeKey(), 
        		holdings);
        
        logger.info("Forwarding to quotes/addManualQuote");
        return "quotes/addManualQuote";
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
    public ModelAndView saveManualQuote(@ModelAttribute(name="quote") Quote quote,  
            @RequestParam(name="symbol", required=true) String symbol, 
            Model model) throws ParseException {
    	logger.info("Entering saveManualQuote()");
    	logger.debug("Request parameters: quote {}; symbol: {}", quote, symbol);
    	
        quote = this.quotesService.addManualQuote(quote, symbol);
        logger.debug("Saved quote: {}", quote);
        model.addAttribute("quote", quote);
        
        logger.info("Redirecting to prepAddManualQuoteForm");
        return new ModelAndView("redirect:prepAddManualQuoteForm", model.asMap());
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
    	
    	Investment inv = this.investmentsService.getInvestmentByInvestmentId(Long.valueOf(investmentIdStr));
    	
    	List<Quote> quotes = null;
    	
    	if (inv != null) {
    		quotes = this.quotesService.getAllQuotesBySymbol(inv.getSymbol());
    	}
    		
		if (quotes == null || quotes.isEmpty()) {
			quotes = this.quotesService.updateQuoteForInvestment(inv.getInvestmentId());
		}
		
		Collections.sort(quotes);
    	
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
    	
    	List<Investment> investments = this.transactionsService.getAllInvestmentsCurrentlyHeldOrderedBySymbol();
    	logger.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
        model.addAttribute(
        		props.getInvestmentsListAttributeKey(), 
        		investments);
        
        logger.info("Forwarding to quotes/getQuotesAjax");
        return "quotes/getQuotesAjax";
    }
    
}
