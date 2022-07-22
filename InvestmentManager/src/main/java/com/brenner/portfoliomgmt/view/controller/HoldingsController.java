package com.brenner.portfoliomgmt.view.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.domain.BucketEnum;
import com.brenner.portfoliomgmt.domain.Holding;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Transaction;
import com.brenner.portfoliomgmt.domain.TransactionTypeEnum;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.service.AccountsService;
import com.brenner.portfoliomgmt.service.HoldingsService;
import com.brenner.portfoliomgmt.service.InvestmentsService;
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
	
	@RequestMapping("/deleteHolding")
	public String deleteHolding(@RequestParam Long holdingId) {
		
		this.holdingsService.deleteHolding(holdingId);
		
		return "redirect:getAccountsForHoldings";
	}
    
    
	/**
	 * Adds the list of accounts, ordered by account name, to the model and returns to the choose account Ajax template.
	 * 
	 * @param model - model containing the list of account to display
	 * @return /holdings/chooseAccountAjax
	 */
	@RequestMapping("/getAccountsForHoldings")
	public String getAccountsForHoldings(Model model) {
		logger.info("Entering getAccountsForHoldings()");
		
		List<Account> allAccounts = this.accountsService.findAllAccounts("accountName");
		model.addAttribute("accounts", allAccounts);
		
		logger.debug("Retrieved {} accounts",  allAccounts.size());
		logger.info("Forwarding request to: holdings/chooseAccountAjax");
		
		return "holdings/chooseAccountAjax";
	}
	
	/**
	 * 
	 * 
	 * @param holding
	 * @return
	 */
	@PostMapping(path="/updateHolding")
	public String updateHolding(@ModelAttribute(name="holding") Holding holding) {
				
		this.holdingsService.updateHolding(holding);
		
		return "redirect:/editHolding?holdingId=" + holding.getHoldingId();
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
		
		List<Account> allAccounts = this.accountsService.findAllAccounts("accountName");
		model.addAttribute("accounts", allAccounts);

		logger.debug("Retrieved {} accounts",  allAccounts != null ? allAccounts.size() : 0);
		
		List<Investment> allInvestments = this.investmentsService.findInvestments("symbol");
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
			@RequestParam(name="accountId") Long accountId, 
			@RequestParam(name="investmentId") Long investmentId, 
			@RequestParam(name="tradeQuantity") String tradeQuantity, 
			@RequestParam(name="tradePrice") String tradePrice, 
			@RequestParam(name="transactionDate") String transactionDate, 
			@RequestParam(name="transactionType") String transactionType, 
			@RequestParam(name="bucketEnum") String bucketType) throws ParseException {
		
		logger.info("Entering addHolding()");
		logger.debug("Received method parameters: accountId: {}; investmentId: {}; tradeQuantity: {}; tradePrice: {}; transactionDate: {}.", 
				accountId, investmentId, tradeQuantity, tradePrice, transactionDate);
		
    	Investment investment = new Investment(investmentId);
		Account account = new Account(accountId);
		Date purchaseDate = CommonUtils.convertDatePickerDateFormatStringToDate(transactionDate);
		
		// create the trade as a type: transfer
		Transaction trade = new Transaction();
		trade.setTradePrice(new BigDecimal(tradePrice));
		trade.setTradeQuantity(new BigDecimal(tradeQuantity));
		trade.setTransactionDate(CommonUtils.convertDatePickerDateFormatStringToDate(transactionDate));
		trade.setTransactionType(TransactionTypeEnum.valueOf(transactionType));
		trade.setTransactionDate(purchaseDate);
		
		//add it to holdings
		Holding holding = new Holding();
		holding.setPurchasePrice(trade.getTradePrice());
		holding.setQuantity(trade.getTradeQuantity());
		holding.setPurchaseDate(purchaseDate);
		holding.setBucketEnum(BucketEnum.valueOf(bucketType));
		
		this.holdingsService.addHolding(trade, holding, account, investment);
		
		logger.debug("Added holding {}", trade);
		logger.info("Redirecting to prepAddHolding");
		
		return "redirect:prepAddHolding";
	}
    
    /**
     * Entry point to edit a specific holding for an account
     * 
     * @param holdingId - unique holding identifier
     * @param model - container for the holding, accounts list and investments list
     * @return /holdings/editHoldingForm
     */
    @RequestMapping("/editHolding")
    public String editHolding(@RequestParam(name="holdingId", required=true) String holdingId, Model model) {
    	logger.info("Entering editHolding()");
    	logger.debug("Request param holdingId: {}", holdingId);
    	
    	Optional<Holding> optHolding = this.holdingsService.findHoldingByHoldingId(Long.valueOf(holdingId));
    	
    	if (! optHolding.isPresent()) {
    		throw new NotFoundException("Holding with holding id " + holdingId + " does not exist.");
    	}
        
    	Holding holding = optHolding.get();
    	
    	List<Account> accounts = this.accountsService.findAllAccounts("accountName");
    	
    	List<Investment> investments = this.investmentsService.findInvestments("symbol");
    	
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