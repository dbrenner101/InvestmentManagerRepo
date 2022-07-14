package com.brenner.portfoliomgmt.transactions;

import java.io.IOException;
import java.text.ParseException;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.brenner.portfoliomgmt.accounts.Account;
import com.brenner.portfoliomgmt.accounts.AccountsService;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.holdings.Holding;
import com.brenner.portfoliomgmt.holdings.HoldingsService;
import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.investments.InvestmentsService;
import com.brenner.portfoliomgmt.util.CommonUtils;

/**
 * MVC controller for cash transaction retrieval and changes
 * 
 * @author dbrenner
 *
 */
@Controller
@Secured("ROLE_USER")
public class TransactionsController implements WebMvcConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(TransactionsController.class);
	
	@Autowired
	TransactionsService transactionsService;
	
	@Autowired
	AccountsService accountsService;
	
	@Autowired
	InvestmentsService investmentsService;
	
	@Autowired
	HoldingsService holdingsService;
	
	/**
	 * Entry point to the enter split transaction flow
	 * 
	 * @param model - container for interacting with UI layer
	 * @return trades/defineSplit
	 */
	@RequestMapping("prepSplitEntryForm")
	public String prepSplitEntryForm(Model model) {
		
		logger.info("Entered prepSplitEntryForm()");
		
		model.addAttribute("investments", this.investmentsService.getInvestmentsOrderedBySymbolAsc());
		
		logger.info("Exiting prepSplitEntryForm()");
		
		return "trades/defineSplit";
	}
	/**
	 * Step 2 in the split flow. Takes the data defining the split and passes to service layer for modeling.
	 * 
	 * @param transactionDateStr - Date of the split
	 * @param investmentIdStr - Investment identifier 
	 * @param splitRatioStr - Split ratio (fractions identify a reverse split)
	 * @param model - Container for interacting with the UI layer
	 * @return trades/modelSplitChanges
	 * @throws ParseException - throw when date string can't be parsed
	 */
	/*@RequestMapping("modelSplit")
	public String modelSplitChange(
			@RequestParam(name="transactionDate", required=true) String transactionDateStr, 
			@RequestParam(name="investmentId", required=true) String investmentIdStr, 
			@RequestParam(name="splitRatio", required=true) String splitRatioStr, 
			Model model) throws ParseException {
		logger.info("Entering modelSplitChange()");
		logger.debug("Params: transactionDateStr: {}; investmentIdStr: {}; splitRatioStr: {}", 
				transactionDateStr, investmentIdStr, splitRatioStr);
		
		Map<String, List<Holding>> beforeAfterSplitMap = this.transactionsService.modelSplit(
				CommonUtils.convertCommonDateFormatStringToDate(transactionDateStr), 
				Long.valueOf(investmentIdStr), 
				Float.valueOf(splitRatioStr));
		
		model.addAttribute("beforeSplit", beforeAfterSplitMap.get("beforeSplit"));
		model.addAttribute("afterSplit", beforeAfterSplitMap.get("afterSplit"));
		model.addAttribute("transactionDate", transactionDateStr);
		model.addAttribute("investmentId", investmentIdStr);
		model.addAttribute("splitRatio", splitRatioStr);
		
		logger.info("Exiting modelSplitChange()");
		return "trades/modelSplitChanges";
	}*/
	
	/**
	 * Step 3 in the split flow - calls service layer to save the split
	 * 
	 * @param transactionDateStr - Date of the split
	 * @param investmentIdStr - Investment identifier 
	 * @param splitRatioStr - Split ratio (fractions identify a reverse split)
	 * @param model - Container for interacting with the UI layer
	 * @return index
	 * @throws ParseException - thrown when the date string can't be parsed to a {@link Date} object
	 */
	/*@RequestMapping("saveSplit")
	public String saveSplit(
			@RequestParam(name="transactionDate", required=true) String transactionDateStr, 
			@RequestParam(name="investmentId", required=true) String investmentIdStr, 
			@RequestParam(name="splitRatio", required=true) String splitRatioStr) throws ParseException {
		logger.info("Entering saveSplit()");
		logger.debug("Params: transactionDateStr: {}; investmentIdStr: {}; splitRatioStr: {}", 
				transactionDateStr, investmentIdStr, splitRatioStr);
		
		Date transactionDate = CommonUtils.convertCommonDateFormatStringToDate(transactionDateStr);
		Long investmentId = Long.valueOf(investmentIdStr);
		Float splitRatio = Float.valueOf(splitRatioStr);
		
		this.transactionsService.saveSplit(transactionDate, investmentId, splitRatio);
		
		logger.info("Exiting saveSplit()");
		return "index";
	}
    
    
    /**
     * Entry point for the list transactions screen
     * 
     * @param model - container to hold the list of accounts
     * @return transactions/chooseAccountForTransactionsList
     */
    @RequestMapping("/prepForTransactionsList")
    public String prepForTransactionsList(Model model) {
    	
    	logger.info("Entering prepForTransactionsList()");
    	
    	List<Account> accounts = this.accountsService.getAllAccountsOrderByAccountNameAsc();
    	
    	logger.debug("Retrieved {} accounts", accounts != null ? accounts.size() : 0);
        
    	model.addAttribute("accounts", accounts);
        
        logger.info("Forwarding to transactions/chooseAccountForTransactionsList");
        
        return "transactions/chooseAccountForTransactionsList";
    }
	
	
	/**
	 * Entry point for listing trades by account
	 * 
	 * @param model - container to hold the list of accounts ordered by account name
	 * @return /trades/chooseAccountForTransactionsList
	 */
	@RequestMapping("/prepGetTrades")
	public String prepGetTrades(Model model) {
		
		logger.info("Entering prepGetTrades()");
		
		List<Account> accounts = this.accountsService.getAllAccountsOrderByAccountNameAsc();
		
		logger.debug("Retrieved {} accounts", accounts != null ? accounts.size() : 0);
	    
		model.addAttribute("accounts", accounts);
	    
	    logger.info("Forwarding to trades/chooseAccountForTransactionsList");
	    
	    return "trades/chooseAccountForTransactionsList";
	}
	
	/**
	 * Retrieves a list of trades for a specific account
	 * 
	 * @param accountId - unique account identifier
	 * @param model - container to add the trades lit
	 * @return /trades/chooseTradeForEdit
	 */
	/*@RequestMapping("/tradesForAccount")
	public String getTradesForAccount(@RequestParam(name="accountId", required=true) String accountId, Model model) {
		
		logger.info("Entering getTradesForAccount()");
		logger.debug("Request parameter: accountId: {}", accountId);
		
		Account account = new Account();
		account.setAccountId(Long.valueOf(accountId));
		List<Transaction> transactions = this.transactionsService.getTradesForAccount(account);
		
		logger.debug("Retrieved {} transactions", transactions != null ? transactions.size() : 0);
	    
		model.addAttribute("transactions", transactions);
	    
	    logger.info("Forwarding to trades/chooseTradeForEdit");
	    
	    return "trades/chooseTradeForEdit";
	}*/
	
	/**
	 * Retrieves the details of a specific trade
	 * 
	 * @param tradeId - unique trade identifier
	 * @param accountId - unique account identifier
	 * @param model - container for the result
	 * @return /trades/editTrade
	 */
	@RequestMapping("/editTradeGetDetails")
	public String editTradeGetDetails(
	        @RequestParam(name="transactionId", required=true) String transactionId, 
	        @RequestParam(name="accountId", required=true) String accountId,
	        Model model) {
		
		logger.info("Entering editTradeGetDetails()");
		logger.debug("Request parameters: transactionId: {}); accountId: {}", transactionId, accountId);
		
		Transaction transaction = this.transactionsService.getTransaction(Long.valueOf(transactionId));
		
		logger.debug("Retrieved transaction: {}", transaction);
		
		model.addAttribute("transaction", transaction);
		
		List<Account> accounts = this.accountsService.getAllAccountsOrderByAccountNameAsc();
		
		logger.debug("Retrieved {} accounts", accounts != null ? accounts.size() : 0);
		
		model.addAttribute("accounts", accounts);
		
		List<Investment> investments = this.investmentsService.getInvestmentsOrderedBySymbolAsc();
		
		logger.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		
		model.addAttribute("investments", investments);
	    
	    logger.info("Forwarding to trades/editTrade");
	    
	    return "trades/editTrade";
	}
	
	/**
	 * @TODO IMPLEMENT 
	 * Method to update details of a trade.
	 * 
	 * @TODO update for when the trade types don't change
	 * 
	 * @param transactionDate - date of the trade
	 * @param transactionId - unique identifier for the trade
	 * @param accountId - unique identifier for the account
	 * @param investmentId - unique identifier for the investment
	 * @param tradePrice - price of the trade
	 * @param tradeQuantity - number or shares
	 * @param transactionType - type of trade
	 * @param previousTransactionType - previous trade type (used to manage logic about updating from buy to sell, etc.)
	 * @return redirect:/prepForTransactionsList
	 * @throws ParseException is thrown is the tradeDate string can't be parsed to a date
	 */
	@PostMapping("/updateTrade")
	public String updateTrade(
	        @ModelAttribute(name="transaction") Transaction transaction) throws ParseException {
		logger.info("Entering updateTrade()");
        logger.debug("Updating trade: {}", transaction);
        
        this.transactionsService.updateTrade(transaction);
	    
        logger.info("Redirecting to prepForTransactionsList");
        return "redirect:prepForTransactionsList";
	}
	
	/**
	 * Entry point to get details for a specific {@link Holding}
	 * 
	 * @param holdingId - holding unique identifier
	 * @param model - container to interact with UI layer
	 * @return trades/transactionsForHolding
	 */
	@RequestMapping("/retrieveHoldingDetails")
	public String retrieveHoldingDetails(@RequestParam(name="holdingId", required=true) String holdingId, Model model) {
		
		logger.info("Entering retrieveHoldingDetails()");
		logger.debug("Request parameter: holdingId: {}", holdingId);
		
		Optional<Holding> optHolding = this.holdingsService.getHoldingByHoldingIdWithBuyTransaction(Long.valueOf(holdingId));
		if (! optHolding.isPresent()) {
			throw new NotFoundException("Holding with holding id " + holdingId + " does not exist.");
		}
		
		Holding holding = optHolding.get();
		
		logger.debug("Retrieved holding: {}", holding);
		
		model.addAttribute("holding", holding);
		
		logger.info("Forwarding to trades/transactionsForHoldin");
		
		return "trades/transactionsForHolding";
	}
	
	/**
	 * Method to handle a sell holding request
	 * 
	 * @param transactionId - unique transaction identifier
	 * @param tradeDate - date of the trade
	 * @param saleQuantity - number of shares
	 * @param price - price received per share
	 * @param holdingId - unique holding identifier
	 * @return redirect:/getAccounts
	 * @throws ParseException is thrown is the tradeDate string can't be parsed into a date
	 */
	@RequestMapping("/sellHolding")
	public String sellHolding(
			@RequestParam(name="holdingId", required=true) String holdingIdStr,
			@RequestParam(name="transactionId", required=true) String transactionIdStr, 
			@RequestParam(name="transactionDate", required=true) String tradeDateStr, 
			@RequestParam(name="tradeQuantity", required=true) String tradeQuantityStr, 
			@RequestParam(name="tradePrice", required=true) String tradePriceStr) throws ParseException {
		
		logger.info("Entering sellHoling()");
		logger.debug("Request parameters: holdingId: {}; transactionId: {}; transactionDate: {}; tradeQuantity: {}; tradePrice: {}", 
				holdingIdStr, transactionIdStr, tradeDateStr, tradeQuantityStr, tradePriceStr);
		
		Long transactionId = Long.valueOf(transactionIdStr);
		Date saleDate = CommonUtils.convertDatePickerDateFormatStringToDate(tradeDateStr);
		Float tradeQuantity = Float.valueOf(tradeQuantityStr);
		Float tradePrice = Float.valueOf(tradePriceStr);
		
		this.holdingsService.sellHolding(transactionId, saleDate, tradeQuantity, tradePrice);
		
		logger.info("Redirecting to getAccountsForHoldings");
		
		return "redirect:getAccountsForHoldings";
	}

	/**
	 * Entry point for the buy template
	 * 
	 * @param model - container to put the list of accounts and investments to choose from
	 * @return the path to the buy template
	 */
	@RequestMapping("/prepareDefineTrade")
	public String prepareDefineTrade(Model model) {
		
		logger.info("Entering prepareDefineTrade()");
		
		List<Account> accounts = this.accountsService.getAllAccountsOrderByAccountNameAsc();
		
		logger.debug("Retrieved {} accounts", accounts != null ? accounts.size() : 0);
		
		model.addAttribute("accounts", accounts);
		
		List<Investment> investments = this.investmentsService.getInvestmentsOrderedBySymbolAsc();
		
		logger.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		
		model.addAttribute("investments", investments);
		
		logger.info("Forwarding to trades/defineTrade");
		
		return "trades/defineTrade";
	}
	
	/**
	 * Entry point to capture purchase details.
	 * 
	 * @param tradeDate - date of the trade
	 * @param price - price paid per share
	 * @param quantity - total number of shares
	 * @param investmentId - investment unique identifier
	 * @param accountId - account unique identifier
	 * @return redirect:/prepareDefineTrade
	 * @throws ParseException when the tradeDate  string can't be parsed to a date
	 */
	@RequestMapping("/buyInvestment")
	public String buyInvestment(
			@RequestParam(name="tradeDate", required=true) String tradeDate, 
			@RequestParam(name="price", required=true) String price,
			@RequestParam(name="quantity", required=true) String quantity,
			@RequestParam(name="investmentId", required=true) String investmentId, 
			@RequestParam(name="accountId", required=true) String accountId) throws ParseException {
		
		logger.info("Entering buyInvestment()");
		logger.debug("Request parameters: tradeDate: {}; price: {}; quantity: {}; investmentId: {}; accountId: {}", 
				tradeDate, price, quantity, investmentId, accountId);
		
		this.holdingsService.persistBuy(
				CommonUtils.convertDatePickerDateFormatStringToDate(tradeDate), 
				Float.valueOf(price), 
				Float.valueOf(quantity), 
				Long.valueOf(investmentId), 
				Long.valueOf(accountId));
		
		logger.info("Redirecting to prepareDefineTrade");
		
		return "redirect:prepareDefineTrade";
	}
	
	
	/**
	 * Entry point for the transfer cash form. 
	 * 
	 * @param model - add the list of accounts to the model as well as the accounts with their cash position to display
	 * @return /accounts/transferCash
	 */
	@RequestMapping("/prepTransferCashForm")
	public String prepTransferCashForm(Model model) {
		
		logger.info("Entering prepTransferCashForm()");
	    
		List<Account> accounts = this.accountsService.getAllAccountsOrderByAccountNameAsc();
		
		logger.debug("Retrieved {} accounts", accounts != null ? accounts.size() : 0);
		
		model.addAttribute("accounts", accounts);
        
	    List<Account> accountsCash = this.accountsService.getAccountsAndCash();
	    
	    logger.debug("Retrieved {} accounts with cash", accountsCash != null ? accountsCash.size() : 0);
	    
	    model.addAttribute("accountsCash", accountsCash);
	    
	    logger.info("Forwarding to accounts/transferCash");
	    
	    return "accounts/transferCash";
	}
    
    
    /**
     * Retrieves the list of trades for the supplied account
     *  
     * @param accountIdStr - unique account identifier
     * @param response - object containing the stream to write to
     * @throws IOException
     */
    @RequestMapping("/listTransactionsForAccount")
    public void listTransactionsForAccount(
            @RequestParam(name="accountId", required=true) String accountIdStr, 
            HttpServletResponse response) throws IOException {
    	
    	logger.info("Entering listTransactionsForAccount()");
    	logger.debug("Request parameter: accountId: {}", accountIdStr);
    	
    	Account account = new Account();
    	account.setAccountId(Long.valueOf(accountIdStr));
    	
        List<Transaction> trades = this.transactionsService.getTradesForAccount(account);
        
        logger.debug("Retrieved {} transactions", trades != null ? trades.size() : 0);
        logger.info("Writing transactions list to JSON");
        
        CommonUtils.serializeObjectToJson(response.getOutputStream(), trades);
    }
	
	/**
	 * Method to take the transfer form parameters and convert data, calling the data service to persist the changes.
	 * 
	 * @param transferDate - date of the cash transfer
	 * @param transferAmount - amount of the transfer
	 * @param fromAccountId - unique identifier for the debit account
	 * @param toAccountId - unique identifier for the credit account
	 * @param model - add the account information to be redisplayed
	 * @return redirect:/prepTransferCashForm
	 * @throws ParseException - exception thrown if date string can't be parsed to a date
	 */
	@RequestMapping("/transferCashBetweeAccounts")
	public String transferCashBetweeAccounts(
	        @RequestParam(name="transferDate", required=true) String transferDate, 
	        @RequestParam(name="transferAmount", required=true) String transferAmount, 
	        @RequestParam(name="fromAccountId", required=true) String fromAccountId, 
	        @RequestParam(name="toAccountId", required=true) String toAccountId, 
	        Model model) throws ParseException {
		
		logger.info("Entering transferCashBetweeAccounts()");
		logger.debug("Request parameters: transferDate: {}; transferAmount: {}; fromAccountId: {}; toAccountId: {}", 
				transferDate, transferAmount, fromAccountId, toAccountId);
	    
	    Date dateOfTransfer = CommonUtils.convertCommonDateFormatStringToDate(transferDate);
	    Float transferAmountDouble = Float.valueOf(transferAmount);
	    Long fromAccount = Long.parseLong(fromAccountId);
	    Long toAccount = Long.parseLong(toAccountId);
	    
	    this.transactionsService.transferCash(dateOfTransfer, transferAmountDouble, fromAccount, toAccount);
	    
	    logger.info("Redirecting to prepTransferCashForm");
	    
	    return "redirect:prepTransferCashForm";
	}
	
	
	/**
	 * Entry point for the dividend submission form.
	 * 
	 * @param model two attributes added: all accounts (ordered by account name) and all investments (ordered by symbol)
	 * @return
	 */
	@RequestMapping("/prepDividendForm")
	public String prepDividendForm(Model model) {
		
		model.addAttribute("accounts", this.accountsService.getAllAccountsOrderByAccountNameAsc());
		model.addAttribute("investments", this.investmentsService.getInvestmentsOrderedBySymbolAsc());
		
		return "accounts/dividendPayment";
	}
	
	/**
	 * Method to handle processing a new dividend.
	 * 
	 * @param tradeDate - date the trade was processed
	 * @param amount - the amount of the dividend
	 * @param investmentId - unique identifier for the security that generated the dividend
	 * @param accountId - unique identifier for the account
	 * @return redirect:/prepDividendForm
	 * @throws ParseException
	 */
	@RequestMapping("/dividendReceived")
	public String applyDividend(
			@RequestParam(name="tradeDate", required=true) String tradeDate, 
			@RequestParam(name="amount", required=true) String amount, 
			@RequestParam(name="investmentId", required=true) String investmentId, 
			@RequestParam(name="accountId", required=true) String accountId) throws ParseException {
		
		logger.info("Entering applyDividend()");
		logger.debug("Request parameters: tradeDate: {}; amount: {}; investmentId: {}; accountId: {}", 
				tradeDate, amount, investmentId, accountId);
		
	    // build a new CashTransaction object
		Transaction cash = new Transaction();
		cash.setAccount(new Account(Long.valueOf(accountId)));
		cash.setTradePrice(1F);
		cash.setTradeQuantity(Float.valueOf(amount));
		cash.setInvestment(new Investment(Long.valueOf(investmentId)));
		cash.setTransactionDate(CommonUtils.convertCommonDateFormatStringToDate(tradeDate));
		cash.setTransactionType(TransactionTypeEnum.Dividend);
		
		logger.debug("Cash transaction: {}", cash);
		
		this.transactionsService.saveTransaction(cash);
		
		logger.info("Redirecting to prepDividendForm");
		
		return "redirect:prepDividendForm";
	}
	
	/**
	 * Entry point for the new deposit form.
	 * 
	 * @param model - add all accounts and their cash to the model
	 * @return
	 */
	@RequestMapping("/prepareDepositForm")
	public String prepareDepositForm(Model model) {
		logger.info("Entering prepareDepositForm()");
		
		List<Account> accounts = this.accountsService.getAccountsAndCash();
		
		logger.debug("Retrieved {} accounts", accounts != null ? accounts.size() : 0);
		
		model.addAttribute("accounts", accounts);
		
		logger.info("Forwarding to accounts/depositForm");
		
		return "accounts/depositForm";
	}
	
	/**
	 * Method to process the deposit form details.
	 * 
	 * @param amount - the amount of the deposit
	 * @param depositDate - the date of the deposit
	 * @param accountId - unique account identifier
	 * @return redirect:/prepareDepositForm
	 * @throws ParseException Exception thrown what the date string cannot be converted to a date object
	 */
	@RequestMapping("/depositCash")
	public String depositCash(
			@RequestParam(name="amount", required=true) String amount, 
			@RequestParam(name="depositDate", required=true) String depositDate,
			@RequestParam(name="accountId", required=true) String accountId) throws ParseException {
		
		logger.info("Entering depositCash()");
		logger.debug("Request parameters: amount: {}; depositDate: {}; accountId: {}", 
				amount, depositDate, accountId);
		
		Transaction cashTransaction = new Transaction();
		cashTransaction.setTransactionDate(CommonUtils.convertCommonDateFormatStringToDate(depositDate));
		cashTransaction.setAccount(new Account(Long.parseLong(accountId)));
		cashTransaction.setTradePrice(1F);
		cashTransaction.setTradeQuantity(Float.valueOf(amount));
		cashTransaction.setTransactionType(TransactionTypeEnum.Cash);
		
		logger.debug("Saving cash transaction: " + cashTransaction);
		
		this.transactionsService.saveTransaction(cashTransaction);
		
		logger.info("Redirecting to prepareDepositForm");
		
		return "redirect:prepareDepositForm";
	}
}
