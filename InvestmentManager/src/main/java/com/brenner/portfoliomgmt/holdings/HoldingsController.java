package com.brenner.portfoliomgmt.holdings;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.brenner.portfoliomgmt.accounts.Account;
import com.brenner.portfoliomgmt.accounts.AccountsService;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.exception.QuoteRetrievalException;
import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.investments.InvestmentsService;
import com.brenner.portfoliomgmt.transactions.Transaction;
import com.brenner.portfoliomgmt.util.CommonUtils;

/**
 * MVC controller for actions related to Holdings
 * 
 * @author dbrenner
 *
 */
@Controller
@Secured("ROLE_USER")
public class HoldingsController implements WebMvcConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(HoldingsController.class); 
	
	@Autowired
	AccountsService accountsService;
	
	@Autowired
	InvestmentsService investmentsService;
	
	@Autowired
	HoldingsService holdingsService;
    
    
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
		model.addAttribute("accounts", allAccounts);
		
		logger.debug("Retrieved {} accounts",  allAccounts.size());
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
		model.addAttribute("accounts", allAccounts);

		logger.debug("Retrieved {} accounts",  allAccounts != null ? allAccounts.size() : 0);
		
		List<Investment> allInvestments = this.investmentsService.getInvestmentsOrderedBySymbolAsc();
		model.addAttribute("investments", allInvestments);

		logger.debug("Retrieved {} investments", allInvestments != null ? allInvestments.size() : 0);
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
    	
		Optional<Investment> optInvestment = this.investmentsService.getInvestmentByInvestmentId(trade.getInvestment().getInvestmentId());
		if (! optInvestment.isPresent()) {
			throw new NotFoundException("Investment with id " + investmentIdStr + " does not exist");
		}
		Investment investment = optInvestment.get();
		
		//add it to holdings
		Holding holding = new Holding();
		holding.setAccount(trade.getAccount());
		holding.setInvestment(investment);
		holding.setPurchasePrice(trade.getTradePrice());
		holding.setQuantity(trade.getTradeQuantity());
		
		this.holdingsService.addHolding(trade, holding);
		
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
        		
        List<Holding> holdings = this.holdingsService.getHoldingsForAccount(Long.valueOf(accountId));
        
        if (holdings != null) {
        	Collections.sort(holdings);
        }
        
        logger.debug("Retrieved {} accounts", holdings != null ? holdings.size() : 0);
        
        Optional<Account> optAccount = this.accountsService.getAccountAndCash(Long.valueOf(accountId));
        
        if (! optAccount.isPresent()) {
        	throw new NotFoundException("Account with id " + accountId + " does not exist.");
        }
        
        Account account = optAccount.get();
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
    	
    	Optional<Holding> optHolding = this.holdingsService.getHoldingByHoldingId(Long.valueOf(holdingId));
    	
    	if (! optHolding.isPresent()) {
    		throw new NotFoundException("Holding with holding id " + holdingId + " does not exist.");
    	}
        
    	Holding holding = optHolding.get();
    	List<Account> accounts = this.accountsService.getAllAccountsOrderByAccountNameAsc();
    	List<Investment> investments = this.investmentsService.getInvestmentsOrderedBySymbolAsc();
    	
    	logger.debug("Retrived holding: {}", holding);
    	logger.debug("Retrieved {} accounts", accounts != null ? accounts.size() : 0);
    	logger.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
    	
        model.addAttribute("holding", holding);
        model.addAttribute("accounts", accounts);
        model.addAttribute("investments", investments);
        
        logger.info("Forwarding to holdings/editHoldingForm");
        
        return "holdings/editHoldingForm";
    }
}