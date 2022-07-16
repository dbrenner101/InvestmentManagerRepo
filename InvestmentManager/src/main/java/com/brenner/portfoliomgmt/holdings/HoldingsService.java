/**
 * 
 */
package com.brenner.portfoliomgmt.holdings;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.portfoliomgmt.accounts.Account;
import com.brenner.portfoliomgmt.exception.InvalidDataRequestException;
import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.investments.InvestmentsService;
import com.brenner.portfoliomgmt.quotes.QuotesService;
import com.brenner.portfoliomgmt.transactions.Transaction;
import com.brenner.portfoliomgmt.transactions.TransactionTypeEnum;
import com.brenner.portfoliomgmt.transactions.TransactionsService;

/**
 *
 * @author dbrenner
 * 
 */
@Service
public class HoldingsService {
	
	private static final Logger log = LoggerFactory.getLogger(HoldingsService.class);

	@Autowired
	InvestmentsService investmentsService;
	
	@Autowired
	HoldingsRepository holdingsRepo;
	
	@Autowired
	TransactionsService transactionsService;
	
	@Autowired
	QuotesService quotesService;
    
    /**
     * Saves a new holding instance
     * 
     * @param investment - the investment for the holding
     * @param trade - trade associated with the investment
     * @param holding - the holding to save
     */
    @Transactional
    public Holding addHolding(Transaction trade, Holding holding) {
    	
    	if (trade == null || holding == null || holding.getInvestment() == null) {
    		throw new InvalidRequestException("Required attributes are null");
    	}
        
        // persist the objects
        holding = this.holdingsRepo.save(holding);
		
        if (trade.getTransactionType() == null) {
        	trade.setTransactionType(TransactionTypeEnum.Buy);
        }
        
        trade.setInvestment(holding.getInvestment());
        trade.setHolding(holding);
        this.transactionsService.saveTransaction(trade);
        
        return holding;
    }
    

    
    /**
     * Persists a holding
     * 
     * @param holding - object to persist
     * @return {@link Holding}
     */
    @Transactional
    public Holding saveHolding(Holding holding) {
    	
    	if (holding == null) {
    		throw new InvalidRequestException("Holding must not be null");
    	}
        
        return this.holdingsRepo.save(holding);
    }
    
    /**
     * Retrieves a list of Holdings for a given account
     * 
     * @param accountId - unique account identifier
     * @return List<Holding>
     */
    public List<Holding> getHoldingsForAccount(Long accountId) {
    	
    	if (accountId == null) {
    		throw new InvalidRequestException("accountId must be non-null");
    	}
    	
    	List<Holding> holdings = this.holdingsRepo.findByAccountAccountId(accountId);
    	
    	for (Holding holding : holdings) {
    		holding.setQuotes(this.quotesService.findMostRecentQuotesForInvestment(holding.getInvestment()));
    		Optional<Date> optBuyDate = this.transactionsService.findBuyDateForHolding(holding.getHoldingId());
    		if (optBuyDate.isPresent()) {
    			holding.setPurchaseDate(optBuyDate.get());
    		}
    	}
    	
    	return holdings;
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
    	
    	if (accountId == null) {
    		throw new InvalidRequestException("accountId must be non-null");
    	}
        
        List<Holding> holdings = this.holdingsRepo.findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol(accountId);
        
        log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
        
        holdings.forEach((holding) -> {
        	holding.setQuotes(this.quotesService.findMostRecentQuotesForInvestment(holding.getInvestment()));
        });
        
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
    public void sellHolding(Long transactionId, Date saleDate, BigDecimal tradeQuantity, BigDecimal tradePrice) {
    	
    	if (transactionId == null || saleDate == null || tradeQuantity == null || tradePrice == null) {
    		throw new InvalidRequestException("Required attributes are null");
    	}
    	
		// retrieve the existing transaction to determine the change from the sale
		Transaction existingTransaction = this.transactionsService.getTransaction(transactionId);
		
		// build the sale transaction off of the existing transaction
		Transaction saleTransaction = new Transaction();
		saleTransaction.setAccount(existingTransaction.getAccount());
		saleTransaction.setHolding(existingTransaction.getHolding());
		saleTransaction.setInvestment(existingTransaction.getInvestment());
		saleTransaction.setTradePrice(tradePrice);
		saleTransaction.setTradeQuantity(tradeQuantity);
		saleTransaction.setTransactionDate(saleDate);
		saleTransaction.setTransactionType(TransactionTypeEnum.Sell);
		
		// build a cash transaction that is the result of the sale
		Transaction cashTransaction = new Transaction();
		cashTransaction.setAccount(existingTransaction.getAccount());
		cashTransaction.setTradePrice(saleTransaction.getTradePrice());
		cashTransaction.setTradeQuantity(saleTransaction.getTradeQuantity());
		cashTransaction.setTransactionDate(saleTransaction.getTransactionDate());
		cashTransaction.setTransactionType(TransactionTypeEnum.Cash);
		
		// grab the holding since it may be set to 0 or only decremented.
		Holding holding = this.getHoldingByHoldingId(existingTransaction.getHolding().getHoldingId()).get();
		
		BigDecimal quantityChange = holding.getQuantity().subtract(saleTransaction.getTradeQuantity());
		holding.setQuantity(quantityChange);
		
    	this.holdingsRepo.save(holding);
    	Transaction t = this.transactionsService.saveTransaction(cashTransaction);
    	saleTransaction.setAssociatedCashTransactionId(t.getTransactionId());
    	this.transactionsService.saveTransaction(saleTransaction);
    }
    
    /**
     * Retrieves a specific holding for an account and investment
     * 
     * @param accountId - unique account identifier
     * @param investmentId - unique investment identifier
     * @return List<Holding>
     */
    public List<Holding> getHoldingByAccountAccountIdAndInvestmentInvestmentId(Long accountId, Long investmentId) {
    	
    	if (accountId == null || investmentId == null) {
    		throw new InvalidRequestException("accountId and investmentId must be non-null");
    	}
        
        return this.holdingsRepo.findByAccountAccountIdAndInvestmentInvestmentId(accountId, investmentId);
    }
    
    /**
     * Retrieve a specific holding based on a holding identifier
     * 
     * @param holdingId - unique holding identifier
     * @return {@link Holding}
     */
    public Optional<Holding> getHoldingByHoldingId(Long holdingId) {
    	
    	if (holdingId == null) {
    		throw new InvalidRequestException("holding id must not be null");
    	}
        
    	Holding h = null;
    	
    	Optional<Holding> optHolding = this.holdingsRepo.findById(holdingId);
    	if (optHolding.isPresent()) {
    		h = optHolding.get();
	    	List<Transaction> transactions = this.transactionsService.getAllTransactionsForAccountAndInvestment(
	    			h.getAccount(), 
	    			h.getInvestment());
	    	h.setTransactions(transactions);
    	}
    	
        return optHolding;
    }
    
    public Optional<Holding> getHoldingByHoldingIdWithBuyTransaction(Long holdingId) {
    	
    	if (holdingId == null) {
    		throw new InvalidRequestException("holding id must not be null");
    	}
        
    	Holding h = null;
    	
    	Optional<Holding> optHolding = this.holdingsRepo.findById(holdingId);
    	
    	if (optHolding.isPresent()) {
    		h = optHolding.get();
    		Optional<Transaction> optTransaction = this.transactionsService.getBuyTransactionsForHoldingId(holdingId);
        	if (optTransaction.isPresent()) {
        		h.addTransaction(optTransaction.get());
        	}
    	}
    	
        return optHolding;
    }
    
    /**
     * Updates a holding and the associated trade
     * 
     * @param holding - holding to update
     * @param trade - trade associated with the holding
     */
    @Transactional
    public void updateHoldingAndTrade(Holding holding, Transaction trade) {
    	
    	if (holding == null || trade == null) {
    		throw new InvalidRequestException("holding and trade must be non-null");
    	}
        
        holding = this.holdingsRepo.save(holding);
    	trade.setHolding(holding);

        this.transactionsService.saveTransaction(trade);
    }
    
    /**
     * Access to the update holding persistence.
     * 
     * @param holding
     * @return
     */
    @Transactional
    public Holding updateHolding(Holding holding) {
    	if (holding == null || holding.getHoldingId() == null) {
    		throw new InvalidRequestException("Holding and holdingId must be non-null");
    	}
    	
    	Optional<Transaction> optTransactionOptional = this.transactionsService.getPurchaseTransactionForHolding(holding);
    	if (optTransactionOptional.isPresent()) {
    		Transaction buyTransaction = optTransactionOptional.get();
    		buyTransaction.setTradePrice(holding.getPurchasePrice());
    		buyTransaction.setTradeQuantity(holding.getQuantity());
    		this.transactionsService.saveTransaction(buyTransaction);
    	}
    	
    	return this.holdingsRepo.save(holding);
    }
    
    /**
     * Retrieves all holdings
     * 
     * @return {@link List}<Holding>
     */
    public List<Holding> getAllHoldings() {
    	
        return this.holdingsRepo.findAll();
    }
    
    /**
     * Retrieves all holdings ordered by their associated investment symbol
     * 
     * @return {@link List}<Holding>
     */
    public List<Holding> getAllHoldingsOrderedBySymbol() {
        
        return this.holdingsRepo.findAll(Sort.by(Sort.Direction.ASC, "investment.symbol"));
    }
    
    
    public List<Investment> getAllInvestmentsCurrentlyHeldOrderedBySymbol() {
    	
    	return this.investmentsService.findUniqueInvestmentsForHoldingsOrderBySymbol();
    }
	
	/**
	 * Retrieves the holdings associated with an investment
	 * 
	 * @param investmentId - investment unique identifier
	 * @return A {@link List} of holdings associated with the investment
	 */
	public List<Holding> getHoldingsByInvestmentId(Long investmentId) {
		log.info("Entered getHoldingsByInvestmentId()");
		
		if (investmentId == null) {
			throw new InvalidRequestException("investmentId must be non-null");
		}
		
		List<Holding> holdings = this.holdingsRepo.findHoldingsByInvestmentInvestmentId(investmentId);
		
		log.info("Exiting getHoldingsByInvestmentId()");
		return holdings;
	}
	
	/**
	 * Entry point for saving a {@link List} of holding objects. Each is saved individually - not batch
	 * 
	 * @param holdings - the {@link List} of holdings to save. 
	 */
	@Transactional
	public void saveAll(List<Holding> holdings) {
		log.info("Entered saveAll()");
		
		if (holdings != null) {
			Iterator<Holding> iter = holdings.iterator();
			while (iter.hasNext()) {
				this.saveHolding(iter.next());
			}
		}
		
		log.info("Exiting saveAll()");
	}
	
	/**
	 * Rerieves all holdings
	 * 
	 * @return A {@link List} of {@link Holding}s
	 */
	public List<Holding> findAll() {
		log.info("Entered findAll()");
		
		List<Holding> holdings = this.holdingsRepo.findAll();
				
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findAll()");
		return holdings;
	}
	
	/**
	 * Retrieves all holdings ordered by most recent market value
	 * 
	 * @return A {@link List} of {@link Holding}s
	 */
	/*
	 * public List<Holding> findHoldingsByMarketValueOrderedDesc(String sortOrder) {
	 * log.info("Entered findHoldingsByMarketValueOrderedDesc()");
	 * log.debug("Param: sortOrder: {}", sortOrder);
	 * 
	 * List<Holding> holdings =
	 * this.holdingsRepo.getAllHoldingsOrderedByMarketValue(sortOrder);
	 * log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
	 * 
	 * log.info("Exiting findHoldingsByMarketValueOrderedDesc()"); return holdings;
	 * }
	 */
	
	/**
	 * Retrieves holdings ordered by a change in value
	 * 
	 * @return A {@link List} of {@link Holding}s
	 */
	/*
	 * public List<Holding> findHoldingByChangeInValueOrderedDesc() {
	 * log.info("Entered findHoldingByChangeInValueOrderedDesc()");
	 * 
	 * List<Holding> holdings =
	 * this.holdingsRepo.getAllHoldingsOrderedByChangeInValue();
	 * log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
	 * 
	 * log.info("Exiting findHoldingByChangeInValueOrderedDesc()"); return holdings;
	 * }
	 */
	
	/**
     * Retrieves all holdings ordered by their associated investment symbol
     * 
     * @return The {@link List} of {@link Holding}s
     */
	/*
	 * public List<Holding>
	 * findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol(Long accountId) {
	 * log.
	 * info("Entered findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol)");
	 * log.debug("Param: accountId: {}", accountId);
	 * 
	 * List<Holding> holdings =
	 * this.holdingsRepo.findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol
	 * (accountId);
	 * 
	 * log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
	 * 
	 * log.
	 * info("Exiting findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol()"
	 * ); return holdings; }
	 */
	
	/**
	 * Retrieves all holdings ordered by their associated investment symbol
	 * 
	 * @return The {@link List} of {@link Holding}s
	 */
	/*
	 * public List<Holding> findHoldingsOrderByInvestmentSymbol() {
	 * log.info("Entered findHoldingsOrderByInvestmentSymbol()");
	 * 
	 * List<Holding> holdings =
	 * this.holdingsRepo.findAll(Sort.by(Sort.Direction.ASC, "investment.symbol"));
	 * 
	 * log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
	 * 
	 * log.info("Exiting findHoldingsOrderByInvestmentSymbol()"); return holdings; }
	 */
	
	/**
	 * Deletes a specific {@link Holding}
	 * 
	 * @param holding - Holding to delete, must contain a holdingId
	 */
	@Transactional
	public void delete(Holding holding) {
		log.info("Entered delete()");
		log.debug("Param: holdingId: {}", holding);
		
		if (holding.getHoldingId() == null) {
			throw new InvalidDataRequestException("holdingId must be non-null");
		}
		
		this.holdingsRepo.delete(holding);
		
		log.info("Exiting delete()");
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
			BigDecimal price,
			BigDecimal quantity,
			Long investmentId,
			Long accountId) {
    	
    	if (tradeDate == null || price == null || quantity == null || investmentId == null || accountId == null) {
    		throw new InvalidRequestException("required attributes are null");
    	}

		// create the Account object that is associate with the purchase
		Account purchaseAccount = new Account(accountId);
		
		// create the investment being purchased
		Investment investment = new Investment(investmentId);
		
		BigDecimal purchaseQuantity = quantity;
		BigDecimal purchasePrice = price;
		
		// add the new holding - this represents a lot
		Holding holding =  new Holding();
		holding.setAccount(purchaseAccount);
		holding.setInvestment(investment);
		holding.setQuantity(purchaseQuantity);
		holding.setPurchasePrice(purchasePrice);
		
		// build the initial transaction object
		Transaction trade = new Transaction();
		trade.setTransactionDate(tradeDate);
		trade.setTradePrice(price);
		trade.setTransactionType(TransactionTypeEnum.Buy);
		trade.setTradeQuantity(quantity);
		trade.setInvestment(investment);
		trade.setAccount(purchaseAccount);
			
		Transaction cashTransaction = new Transaction();
		cashTransaction.setAccount(trade.getAccount());
		cashTransaction.setTradePrice(new BigDecimal(-1));
		cashTransaction.setTradeQuantity(trade.getTradePrice().multiply(trade.getTradeQuantity()));
		cashTransaction.setTransactionDate(trade.getTransactionDate());
		cashTransaction.setTransactionType(TransactionTypeEnum.Cash);
        
        holding = this.holdingsRepo.save(holding);
        trade.setHolding(holding);
        Transaction t = this.transactionsService.saveTransaction(cashTransaction);
        trade.setAssociatedCashTransactionId(t.getTransactionId());
        this.transactionsService.saveTransaction(trade);
        
    }

}
