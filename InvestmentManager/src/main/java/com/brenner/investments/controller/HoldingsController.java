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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.brenner.investments.InvestmentsProperties;
import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Transaction;
import com.brenner.investments.quote.service.QuoteRetrievalException;
import com.brenner.investments.service.AccountsService;
import com.brenner.investments.service.InvestmentsService;
import com.brenner.investments.service.TransactionsService;
import com.brenner.investments.util.CommonUtils;

/**
 * MVC controller for actions related to Holdings
 * 
 * @author dbrenner
 *
 */
@Controller
public class HoldingsController implements WebMvcConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(HoldingsController.class); 
	
	@Autowired
	TransactionsService transactionsService;
	
	@Autowired
	AccountsService accountsService;
	
	@Autowired
	InvestmentsService investmentsService;
    
    @Autowired
    InvestmentsProperties props;
    
    
	/**
	 * Adds the list of accounts, ordered by account name, to the model and returns to the choose account Ajax template.
	 * 
	 * @param model - model containing the list of account to display
	 * @return /holdings/chooseAccountAjax
	 */
	@RequestMapping("/getAccountsForHoldings")
	public String getAccountsForHoldings(Model model) {
		logger.info("Entering getAccountsForHoldings()");
		
		List<Account> allAccounts = this.accountsService.getAllAccountsOrderByAccountNameAsc();
		logger.debug("Retrieved {} accounts",  allAccounts.size());
		model.addAttribute(props.getAccountsListAttributeKey(), allAccounts);
		
		logger.info("Forwarding request to: holdings/chooseAccountAjax");
		return "holdings/chooseAccountAjax";
	}
	
	
	/**
	 * Entry point for the add holding form. 
	 * 
	 * @param model - container for the accounts list ordered by account name
	 * @return /holdings/addHolding
	 */
	@RequestMapping("/prepAddHolding")
	public String prepAddHolding(Model model) {
		logger.info("Entering prepAddHolding()");
		
		List<Account> allAccounts = this.accountsService.getAllAccountsOrderByAccountNameAsc();
		logger.debug("Retrieved {} accounts",  allAccounts != null ? allAccounts.size() : 0);
		model.addAttribute(props.getAccountsListAttributeKey(), allAccounts);
		
		List<Investment> allInvestments = this.investmentsService.getInvestmentsOrderedBySymbolAsc();
		logger.debug("Retrieved {} investments", allInvestments != null ? allInvestments.size() : 0);
		model.addAttribute(this.props.getInvestmentsListAttributeKey(), allInvestments);
		
		logger.info("Forwarding to holdings/addHolding");
		return "holdings/addHolding";
	}
	
	/**
	 * Method to add a new holding. 
	 * 
	 * @param accountId - unique account identifier to associate with the holding
	 * @param symbol - investment identifier
	 * @param tradeQuantity - purchase quantity
	 * @param tradePrice - price of the holding at the time it is added
	 * @param transactionDate - date the holding was added to the account
	 * @return redirect:/prepAddHolding
	 * @throws ParseException is thrown is the tradeDate string can't be parsed into a date
	 */
	@RequestMapping("/addHolding")
	public String addHolding(
			@RequestParam(name="accountId", required=true) String accountId, 
			@RequestParam(name="investmentId", required=true) String investmentIdStr, 
			@RequestParam(name="tradeQuantity", required=true) String tradeQuantity, 
			@RequestParam(name="tradePrice", required=true) String tradePrice, 
			@RequestParam(name="transactionDate", required=true) String transactionDate) throws ParseException {
		logger.info("Entering addHolding()");
		logger.debug("Received method parameters: accountId: {}; investmentId: {}; tradeQuantity: {}; tradePrice: {}; transactionDate: {}.", 
				accountId, investmentIdStr, tradeQuantity, tradePrice, transactionDate);
		
		// create the trade as a type: transfer
		Transaction trade = new Transaction();
		trade.setAccount(new Account(Long.valueOf(accountId)));
		trade.setTradePrice(Float.valueOf(tradePrice));
		trade.setTradeQuantity(Float.valueOf(tradeQuantity));
		trade.setTransactionDate(CommonUtils.convertDatePickerDateFormatStringToDate(transactionDate));
		trade.setInvestment(new Investment(Long.valueOf(investmentIdStr)));
		
		this.transactionsService.addHolding(trade);
		logger.debug("Added holding {}", trade);
		
		logger.info("Redirecting to prepAddHolding");
		return "redirect:prepAddHolding";
	}
    
	/**
	 * Method to support retrieving holdings for an Ajax template
     * 
     * @param accountId - unique account identifier
     * @param response - Http response object to get the output stream to write the response
     * @return the path to the list holdings template
     * @throws IOException is thrown is the message fails to be written to the response
     * @throws QuoteRetrievalException is thrown is the service us unable to retrieve a quote
	 * @throws ParseException thrown if there is an error parsing a date string to a date object
	 */
    @RequestMapping("/getHoldingsAjax")
    public void getHoldingsAjax(
            @RequestParam(name="accountId", required=true) String accountId, 
            HttpServletResponse response, 
            Model model) throws IOException, QuoteRetrievalException, ParseException {
    	logger.info("Entering getHoldingsAjax");
    	logger.debug("Request parameter: {}", accountId);
        		
        List<Holding> holdings = this.transactionsService.getHoldingsForAccount(Long.valueOf(accountId));
        Collections.sort(holdings);
        logger.debug("Retrieved {} accounts", holdings != null ? holdings.size() : 0);
        
        Account account = this.accountsService.getAccountAndCash(Long.valueOf(accountId));
        Double totalValueChange = 0D;
        Double totalStockValue = 0D;
        
        if (holdings != null && ! holdings.isEmpty()) {
            holdings.get(0).setAccount(account);
            
            for(Holding holding : holdings) {
            	logger.debug("Calc changes in holding: {}", holding);
                totalValueChange += holding.getChangeInValue() == null ? 0 : holding.getChangeInValue();
                totalStockValue += holding.getCurrentValue() == null ? 0 : holding.getCurrentValue();
            }
        }
        
        logger.debug("Total value change: {}", totalValueChange);
        logger.debug("Total stock value: {}", totalStockValue);
        
        logger.info("Serializing holdings to JSON");
        CommonUtils.serializeObjectToJson(response.getOutputStream(), holdings);
    }
    
    /**
     * Entry point to edit a specific holding for an account
     * 
     * @param holdingId - unique holding identifier
     * @param model - container for the holding, accounts list and investments list
     * @return /holdings/editHoldingForm
     */
    @RequestMapping("/editHolding")
    public String editHolding(
    		@RequestParam(name="holdingId", required=true) String holdingId, 
    		Model model) {
    	logger.info("Entering editHolding()");
    	logger.debug("Request param holdingId: {}", holdingId);
        
    	Holding holding = this.transactionsService.getHoldingByHoldingId(Long.valueOf(holdingId));
    	List<Account> accounts = this.accountsService.getAllAccountsOrderByAccountNameAsc();
    	List<Investment> investments = this.investmentsService.getInvestmentsOrderedBySymbolAsc();
    	
    	logger.debug("Retrived holding: {}", holding);
    	logger.debug("Retrieved {} accounts", accounts != null ? accounts.size() : 0);
    	logger.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
    	
        model.addAttribute(this.props.getHoldingAttributeKey(), holding);
        model.addAttribute(this.props.getAccountsListAttributeKey(), accounts);
        model.addAttribute(this.props.getInvestmentsListAttributeKey(), investments);
        
        logger.info("Forwarding to holdings/editHoldingForm");
        return "holdings/editHoldingForm";
    }
}