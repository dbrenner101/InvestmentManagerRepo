/**
 * 
 */
package com.brenner.investments.service.implementation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.investments.data.HoldingsDataService;
import com.brenner.investments.data.InvestmentsDataService;
import com.brenner.investments.data.TransactionsDataService;
import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.entities.Transaction;
import com.brenner.investments.entities.constants.TransactionTypeEnum;
import com.brenner.investments.quote.service.QuoteRetrievalService;
import com.brenner.investments.service.QuotesService;
import com.brenner.investments.service.TransactionsService;

/**
 * Data service specific to transactions
 * 
 * @author dbrenner
 *
 */
@Service
public class PersistentTransactionsService implements TransactionsService {
	
	private static final Logger log = LoggerFactory.getLogger(PersistentTransactionsService.class);
    
    @Autowired
    TransactionsDataService transactionsDataService;
    
    @Autowired
    HoldingsDataService holdingsDataService;
    
    @Autowired
    InvestmentsDataService investmentDataService;
    
    @Autowired
    QuotesService quotesService;
    
    @Autowired
    QuoteRetrievalService quoteRetrievalService;
    
    
    public Map<String, List<Holding>> modelSplit(Date transactionDate, Long investmentId, Float splitRatio) {
    	
    	log.info("Entering modelSplit()");
    	
    	List<Holding> holdingsBeforeSplit = this.holdingsDataService.getHoldingsByInvestmentId(investmentId);
    	List<Holding> holdingsAfterSplit = this.applySplit(holdingsBeforeSplit, splitRatio);
    	
    	Map<String, List<Holding>> beforeAfterSplitMap = new HashMap<>(2);
    	beforeAfterSplitMap.put("beforeSplit", holdingsBeforeSplit);
    	beforeAfterSplitMap.put("afterSplit", holdingsAfterSplit);
    	
    	log.info("Exiting modelSplit()");
    	
    	return beforeAfterSplitMap;
    }
    
    public void saveSplit(Date transactionDate, Long investmentId, Float splitRatio) {
    	
    	List<Holding> holdingsBeforeSplit = this.holdingsDataService.getHoldingsByInvestmentId(investmentId);
    	List<Holding> holdingsAfterSplit = this.applySplit(holdingsBeforeSplit, splitRatio);
    	List<Transaction> transactions = new ArrayList<>(holdingsAfterSplit.size());
    	
    	Iterator<Holding> iter = holdingsAfterSplit.iterator();
    	while(iter.hasNext()) {
    		Holding h = iter.next();
    		
    		Transaction t = new Transaction();
    		t.setAccount(h.getAccount());
    		t.setHoldingId(h.getHoldingId());
    		t.setInvestment(h.getInvestment());
    		t.setTradeQuantity(h.getQuantity());
    		t.setTradePrice(0F);
    		t.setTransactionDate(transactionDate);
    		t.setTransactionType(TransactionTypeEnum.Split);
    		transactions.add(t);
    	}
    	
    	this.holdingsDataService.saveAll(holdingsAfterSplit);
    	this.transactionsDataService.saveAll(transactions);
    	
    }
    
    private List<Holding> applySplit(List<Holding> preSplitHoldings, Float splitRatio) {
    	
    	List<Holding> holdingsAfterSplit = new ArrayList<>(preSplitHoldings.size());
    	
    	Iterator<Holding> beforeIter = preSplitHoldings.iterator();
    	while (beforeIter.hasNext()) {
    		Holding before = beforeIter.next();
    		Holding after = before.shallowClone(before);
    		after.setQuantity(before.getQuantity() * splitRatio);
    		holdingsAfterSplit.add(after);
    	}
    	
    	return holdingsAfterSplit;
    }
    
    public List<Transaction> getTotalDividendsForInvestments() {
    	
    	log.info("Entered getDividendsForInvestments()");
    	
    	List<Transaction> dividends = this.transactionsDataService.findTotalDividendsForInvestments();
    	
    	log.debug("Returning {} dividends", dividends != null ? dividends.size() : 0);
    	log.info("Exiting getDividendsForInvestments()");
    	
    	return dividends;
    }
    
    public Transaction getTotalDividendsForInvestment(Long investmentId) {
    	
    	log.info("Entered getDividendsForInvestment()");
    	
    	Transaction dividend = this.transactionsDataService.findTotalDividendsForInvestment(investmentId);
    	
    	log.debug("Returning {} dividends", dividend);
    	log.info("Exiting getDividendsForInvestment()");
    	
    	return dividend;
    }
    
    public Map<Long, Transaction> getTotalDidivendsForAllInvestments() {
    	
    	List<Transaction> dividends = this.getTotalDividendsForInvestments();
    	
    	Map<Long, Transaction> dividendsMap = null;
    	
    	if (dividends != null) {
    		dividendsMap = new HashMap<>(dividends.size());
    		
    		Iterator<Transaction> iter = dividends.iterator();
    		while (iter.hasNext()) {
    			Transaction div = iter.next();
    			dividendsMap.put(div.getInvestment().getInvestmentId(), div);
    		}
    	}
    	
    	return dividendsMap;
    }
    
    /**
     * Injections of the TransactionRepository to support testing
     * @param transactionsRepo
     */
    public void setTransactionsRepo(TransactionsDataService transactionsRepo) {
		this.transactionsDataService = transactionsRepo;
	}

    /**
     * Injection of the HoldingsRepo to support testing
     * @param holdingsRepo
     */
	public void setHoldingsRepo(HoldingsDataService holdingsRepo) {
		this.holdingsDataService = holdingsRepo;
	}

	/**
	 * Injection of the InvestmentsRepository to support testing
	 * @param investmentRepo
	 */
	public void setInvestmentRepo(InvestmentsDataService investmentRepo) {
		this.investmentDataService = investmentRepo;
	}

	/**
	 * Injection of the QuotesService to support testing
	 * @param quotesService
	 */
	public void setQuotesService(QuotesService quotesService) {
		this.quotesService = quotesService;
	}

	/**
	 * Injection of the QuoteRetrievalService to support testing
	 * @param quoteRetrievalService
	 */
	public void setQuoteRetrievalService(QuoteRetrievalService quoteRetrievalService) {
		this.quoteRetrievalService = quoteRetrievalService;
	}


	public void deleteHolding(Holding holding) {
    	this.holdingsDataService.delete(holding);
    }
    
    
    public void deleteTransaction(Transaction transaction) {
    	this.transactionsDataService.delete(transaction);
    }
    
    /*public void saveBulkTransaction(List<Transaction> transactions) {
    	this.transactionsRepo.saveAll(transactions);
    }*/
    

    /**
     * Retrieves transactions for an account and holding.
     * 
     * @param accountId - unique account identifier
     * @param holdingId - unique holding identifier
     * @return List<Transaction>
     */
    public List<Transaction> getAllTransactionsForAccountAndHolding(Account account, Holding holding) {
    	
    	return this.transactionsDataService.findAllByAccountAccountIdAndHoldingHoldingId(account, holding);
    }
    

    /**
     * Retrieves transactions for an account and holding.
     * 
     * @param accountId - unique account identifier
     * @param holdingId - unique holding identifier
     * @return List<Transaction>
     */
    public List<Transaction> getAllTransactionsForAccountAndInvestment(Account account, Investment investment) {
    	
    	return this.transactionsDataService.findAllByAccountAccountIdAndInvestmentInvestmentId(account, investment);
    }
    
    
    public Transaction getBuyTransactionsForHoldingId(Long holdingId) {
    	
    	return this.transactionsDataService.findBuyTransactionforHoldingId(holdingId);
    }
    
    /**
     * Persists a holding
     * 
     * @param holding - object to persist
     * @return {@link Holding}
     */
    @Transactional
    public Holding saveHolding(Holding holding) {
        
        return this.holdingsDataService.save(holding);
    }
    
    /**
     * Retrieves a list of Holdings for a given account
     * 
     * @param accountId - unique account identifier
     * @return List<Holding>
     */
    public List<Holding> getHoldingsForAccount(Long accountId) {
        
        return this.holdingsDataService.findByAccountAccountId(accountId);
    }
    
    /**
     * Retrieves the holdings associated with an account where the quantity > 0
     * 
     * @param accountId - account unique identifier
     * @return List<Holding>
     * @throws ParseException 
     */
    public List<Holding> getHoldingsForAccountAndQuantityGreaterThanOrderedBySymbol(Long accountId) throws ParseException {
    	log.info("Entered getHoldingsForAccountAndQuantityGreaterThanOrderedBySymbol(Long)");
    	log.debug("Param: accountId: {}", accountId);
        
        List<Holding> holdings = this.holdingsDataService.findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol(accountId);
        log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
        
        // merge duplicates
        Map<Long, Holding> deDupMap = new HashMap<>(holdings.size());
        Iterator<Holding> iter = holdings.iterator();
        while (iter.hasNext()) {
        	Holding h = iter.next();
        	
        	Holding existingHolding = deDupMap.get(h.getHoldingId());
        	if (existingHolding != null && ! existingHolding.getHoldingId().equals(h.getHoldingId())) {
        		existingHolding.setQuantity(h.getQuantity() + existingHolding.getQuantity());
        	}
        	else {
        		Date maxDate = this.quotesService.findGreatestQuoteDateForSymbol(h.getInvestment().getSymbol());
        		if (maxDate != null) {
        			Quote q = this.quotesService.getQuoteBySymbolAndDate(h.getInvestment().getSymbol(), maxDate);
        			h.addQuote(q);
        		}
        		deDupMap.put(h.getHoldingId(), h);
        	}
        }
        
        holdings = new ArrayList<>();
        holdings.addAll(deDupMap.values());
        
        log.info("Exiting getHoldingsForAccountAndQuantityGreaterThanOrderedBySymbol(Long)");
        return holdings;
    }
    
    /**
     * Executes the logic to handle the sale of a holding. This includes modifying the Holding, persisting a trade and updating cash position for the account
     * 
     * @param holding - the holding that represents investment being sold
     * @param saleTransaction - the transaction that wraps the sale
     * @param cashTransaction - the cash result of the trade
     * @param realized - the actual gain or loss from the transaction
     */
    @Transactional
    public void sellHolding(Long transactionId, Date saleDate, Float tradeQuantity, Float tradePrice) {
    	
		// retrieve the existing transaction to determine the change from the sale
		Transaction existingTransaction = this.getTransaction(transactionId);
		
		// build the sale transaction off of the existing transaction
		Transaction saleTransaction = new Transaction();
		saleTransaction.setAccount(existingTransaction.getAccount());
		saleTransaction.setHoldingId(existingTransaction.getHoldingId());
		saleTransaction.setInvestment(existingTransaction.getInvestment());
		saleTransaction.setTradePrice(tradePrice);
		saleTransaction.setTradeQuantity(tradeQuantity);
		saleTransaction.setTransactionDate(saleDate);
		saleTransaction.setTransactionType(TransactionTypeEnum.Sell);
		
		// build a cash transaction that is the result of the sale
		Transaction cashTransaction = new Transaction();
		cashTransaction.setAccount(existingTransaction.getAccount());
		cashTransaction.setTradePrice(1F);
		cashTransaction.setTradeQuantity(saleTransaction.getTradePrice() * saleTransaction.getTradeQuantity());
		cashTransaction.setTransactionDate(saleTransaction.getTransactionDate());
		cashTransaction.setTransactionType(TransactionTypeEnum.Cash);
		
		// grab the holding since it may be set to 0 or only decremented.
		Holding holding = this.getHoldingByHoldingId(existingTransaction.getHoldingId());
		
		Float quantityChange = holding.getQuantity() - saleTransaction.getTradeQuantity();
		holding.setQuantity(quantityChange);
		
		if (quantityChange < 0){
			// throw exception
		}
		
    	this.holdingsDataService.save(holding);
    	Transaction t = this.transactionsDataService.save(cashTransaction);
    	saleTransaction.setAssociatedCashTransactionId(t.getTransactionId());
    	this.transactionsDataService.save(saleTransaction);
    }
    
    /**
     * Saves a new holding instance
     * 
     * @param investment - the investment for the holding
     * @param trade - trade associated with the investment
     * @param holding - the holding to save
     */
    @Transactional
    public Holding addHolding(Transaction trade) {
    	
    	// 1 check if the investment exists and if not add it
		Investment investment = this.investmentDataService.findById(trade.getInvestment().getInvestmentId());
		
		trade.setTransactionType(TransactionTypeEnum.Buy);
		
		
		//add it to holdings
		Holding holding = new Holding();
		holding.setAccount(trade.getAccount());
		holding.setInvestment(investment);
		holding.setPurchasePrice(trade.getTradePrice());
		holding.setQuantity(trade.getTradeQuantity());
        
        trade.setInvestment(investment);
        
        // persist the objects
        holding = this.saveHolding(holding);
        trade.setHoldingId(holding.getHoldingId());
        this.saveTransaction(trade);
        
        return holding;
    }
    
    /**
     * Retrieves a specific holding for an account and investment
     * 
     * @param accountId - unique account identifier
     * @param investmentId - unique investment identifier
     * @return List<Holding>
     */
    public List<Holding> getHoldingByAccountAccountIdAndInvestmentInvestmentId(Long accountId, Long investmentId) {
        
        return this.holdingsDataService.findByAccountIdAndInvestmentId(accountId, investmentId);
    }
    
    /**
     * Retrieve a specific holding based on a holding identifier
     * 
     * @param holdingId - unique holding identifier
     * @return {@link Holding}
     */
    public Holding getHoldingByHoldingId(Long holdingId) {
        
    	Holding h = this.holdingsDataService.findById(holdingId);
    	if (h!= null) {
	    	List<Transaction> transactions = this.getAllTransactionsForAccountAndInvestment(
	    			h.getAccount(), 
	    			h.getInvestment());
	    	h.setTransactions(transactions);
    	}
    	
        return h;
    }
    
    public Holding getHoldingByHoldingIdWithBuyTransactions(Long holdingId) {
        
    	Holding h = this.holdingsDataService.findById(holdingId);
    	Transaction transaction = this.getBuyTransactionsForHoldingId(holdingId);
    	h.addTransaction(transaction);
    	
        return h;
    }
    
    /**
     * Updates a holding and the associated trade
     * 
     * @param holding - holding to update
     * @param trade - trade associated with the holding
     */
    @Transactional
    public void updateHoldingAndTrade(Holding holding, Transaction trade) {
        
        if (holding!= null) {
            holding = this.holdingsDataService.save(holding);
            trade.setHoldingId(holding.getHoldingId());
        }
        this.transactionsDataService.save(trade);
    }
    
    /**
     * Retrieves all holdings
     * 
     * @return {@link Iterable}<Holding>
     */
    public Iterable<Holding> getAllHoldings() {
        return this.holdingsDataService.findAll();
    }
    
    /**
     * Retrieves all holdings ordered by their associated investment symbol
     * 
     * @return {@link List}<Holding>
     */
    public List<Holding> getAllHoldingsOrderedBySymbol() {
        
        return this.holdingsDataService.findHoldingsOrderByInvestmentSymbol();
    }
    
    
    public List<Investment> getAllInvestmentsCurrentlyHeldOrderedBySymbol() {
    	
    	return this.investmentDataService.findUniqueInvestmentsForHoldingsOrderBySymbol();
    }
    
    
    
    // End Holdings Methods
    
    
    
    
    // Trade Methods
    
    /**
     * Persists the specified trade
     * 
     * @param transaction - transaction to save
     * @return {@link Transaction}
     */
    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        
        return this.transactionsDataService.save(transaction);
    }
    
    
    /**
     * Retrieves all the transactions of type cash for a particular account.
     * 
     * @param accountId - unique account identifier
     * @return List<Transaction>
     */
    public List<Transaction> findAllCashTransactionsForAccount(Long accountId) {
        
        return this.transactionsDataService.findAllByAccountAccountIdWithCash(accountId);
    }
    
    /**
     * Updates the holding and associated trade
     * 
     * @param trade - trade to update
     * @param holding - holding to update
     */
    @Transactional
    public void updateTrade(Transaction changedTrade, TransactionTypeEnum previousTradeType) {
	    
	    TransactionTypeEnum newTradeTypeValue = changedTrade.getTransactionType();
	    
	    Transaction currentTransaction = this.getTransaction(changedTrade.getTransactionId());
	    
	    Holding holding = null;
	    
	    if (! previousTradeType.equals(newTradeTypeValue)) {
	        
	    }
	    else {
	        if (currentTransaction.getHoldingId() != null) {
	        	holding = this.getHoldingByHoldingId(currentTransaction.getHoldingId());
	        	
		        if (holding != null) {
	    	        holding.setAccount(changedTrade.getAccount());
	    	        holding.setInvestment(changedTrade.getInvestment());
	    	        holding.setQuantity(changedTrade.getTradeQuantity());
	    	        holding.setPurchasePrice(changedTrade.getTradePrice());
		        }
	        }
	        
	        this.updateHoldingAndTrade(holding, changedTrade);
	    }
    }
    
    /**
     * Saves the details of a purchase transaction
     * 
     * @param trade - trade to save
     * @param holding - holding
     * @param cashTransaction - cash position changes
     */
    @Transactional
    public void persistBuy(
			Date tradeDate, 
			Float price,
			Float quantity,
			Long investmentId,
			Long accountId) {

		// create the Account object that is associate with the purchase
		Account purchaseAccount = new Account(accountId);
		
		// create the investment being purchased
		Investment investment = new Investment(investmentId);
		
		Float purchaseQuantity = Float.valueOf(quantity);
		Float purchasePrice = Float.valueOf(price);
		
		// add the new holding - this represents a lot
		Holding holding =  new Holding();
		holding.setAccount(purchaseAccount);
		holding.setInvestment(investment);
		holding.setQuantity(purchaseQuantity);
		holding.setPurchasePrice(purchasePrice);
		
		// build the initial transaction object
		Transaction trade = new Transaction();
		trade.setTransactionDate(tradeDate);
		trade.setTradePrice(Float.valueOf(price));
		trade.setTransactionType(TransactionTypeEnum.Buy);
		trade.setTradeQuantity(Float.valueOf(quantity));
		trade.setInvestment(investment);
		trade.setAccount(purchaseAccount);
			
		Transaction cashTransaction = new Transaction();
		cashTransaction.setAccount(trade.getAccount());
		cashTransaction.setTradePrice(-1F);
		cashTransaction.setTradeQuantity(trade.getTradePrice() * trade.getTradeQuantity());
		cashTransaction.setTransactionDate(trade.getTransactionDate());
		cashTransaction.setTransactionType(TransactionTypeEnum.Cash);
        
        holding = this.holdingsDataService.save(holding);
        trade.setHoldingId(holding.getHoldingId());
        Transaction t = this.transactionsDataService.save(cashTransaction);
        trade.setAssociatedCashTransactionId(t.getTransactionId());
        this.transactionsDataService.save(trade);
        
    }
    
    /**
     * Retrieves the trades associated with an account
     * 
     * @param accountId - unique account identifier
     * @return {@link List}<Trade>
     */
    public List<Transaction> getTradesForAccount(Account account) {
        
        return this.transactionsDataService.findAllByAccountAccountIdOrderByTransactionDateDesc(account);
    }
    
    /**
     * Retrieve a specific trade
     * 
     * @param transactionId - unique transaction identifier
     * @return {@link Transactions}
     */
    public Transaction getTransaction(Long transactionId) {
        
        Transaction trade = this.transactionsDataService.findById(transactionId);
        trade.setTransactionDate(new Date(trade.getTransactionDate().getTime()));
        return trade;
    }
    
    
    // End Trade Methods
    
    
    // Start Realized gain or loss methods
    
    
 
    
    
    
    // End Realized Gain/Loss Methods
    
    
    
// Cash Transaction Methods
    
    /**
     * Transaction method to transfer cash from one account to another
     * 
     * @param transferDate - date of the transfer
     * @param transferAmount - transfer amount
     * @param fromAccountId - unique account identifier for the account to debit
     * @param toAccountId - unique account identifier for the account to credit
     */
    @Transactional
    public void transferCash(Date transferDate, Float transferAmount, Long fromAccountId, Long toAccountId) {
        
        Transaction debitTransaction = new Transaction();
        debitTransaction.setAccount(new Account(fromAccountId));
        debitTransaction.setTradePrice(-1F);
        debitTransaction.setTradeQuantity(transferAmount);
        debitTransaction.setTransactionDate(transferDate);
        debitTransaction.setTransactionType(TransactionTypeEnum.Cash);
        
        Transaction creditTransaction = new Transaction();
        creditTransaction.setAccount(new Account(toAccountId));
        creditTransaction.setTradePrice(1F);
        creditTransaction.setTradeQuantity(transferAmount);
        creditTransaction.setTransactionDate(transferDate);
        creditTransaction.setTransactionType(TransactionTypeEnum.Cash);
        
        this.transactionsDataService.save(debitTransaction);
        this.transactionsDataService.save(creditTransaction);
    }
    // End Cash Transaction Methods
}
