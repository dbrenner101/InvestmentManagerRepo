/**
 * 
 */
package com.brenner.portfoliomgmt.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.portfoliomgmt.data.entities.AccountDTO;
import com.brenner.portfoliomgmt.data.entities.HoldingDTO;
import com.brenner.portfoliomgmt.data.entities.InvestmentDTO;
import com.brenner.portfoliomgmt.data.entities.TransactionDTO;
import com.brenner.portfoliomgmt.data.repo.AccountsRepository;
import com.brenner.portfoliomgmt.data.repo.HoldingsRepository;
import com.brenner.portfoliomgmt.data.repo.InvestmentsRepository;
import com.brenner.portfoliomgmt.data.repo.TransactionsRepository;
import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.domain.Holding;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.domain.Transaction;
import com.brenner.portfoliomgmt.domain.TransactionTypeEnum;
import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.util.ObjectMappingUtil;

/**
 *
 * @author dbrenner
 * 
 */
@Service
public class HoldingsService {
	
	private static final Logger log = LoggerFactory.getLogger(HoldingsService.class);

	@Autowired
	InvestmentsRepository investmentsRepo;
	
	@Autowired
	HoldingsRepository holdingsRepo;
    
    @Autowired
    TransactionsRepository transactionsRepo;
	
	@Autowired
	QuotesService quotesService;
	
	@Autowired
	AccountsRepository accountsRepo;
	
	@Transactional
	public void deleteHolding(Long holdingId) {
		log.info("Entered delete()");
		log.debug("Param: holdingId: {}", holdingId);
		
		if (holdingId == null) {
			throw new InvalidRequestException("holdingId must be non-null");
		}
		this.transactionsRepo.deleteTransactionForHolding(holdingId);
		this.holdingsRepo.deleteById(holdingId);
		
		log.info("Exiting delete()");
	}
    
    /**
     * Saves a new holding instance
     * 
     * @param investment - the investment for the holding
     * @param trade - trade associated with the investment
     * @param holding - the holding to save
     */
    @Transactional
    public Holding addHolding(Transaction trade, Holding holding, Account account, Investment investment) {
    	
    	if (trade == null || holding == null || account == null || account.getAccountId() == null || investment == null || investment.getInvestmentId() == null) {
    		throw new InvalidRequestException("Required attributes are null");
    	}
		
        Optional<AccountDTO> optAccount = this.accountsRepo.findById(account.getAccountId());
        if (optAccount.isEmpty()) {
        	throw new InvalidRequestException("Unable to locate account with id " + account.getAccountId());
        }
        
        Optional<InvestmentDTO> optInvestment = this.investmentsRepo.findById(investment.getInvestmentId());
        if (optInvestment.isEmpty()) {
        	throw new InvalidRequestException("Unable to locate investment with id " + investment.getInvestmentId());
        }
        
        HoldingDTO holdingData = ObjectMappingUtil.mapHoldingToHoldingDto(holding);
        holdingData.setInvestment(optInvestment.get());
        holdingData.setAccount(optAccount.get());
        holdingData = this.holdingsRepo.save(holdingData);
        
        if (trade.getTransactionType() == null) {
        	trade.setTransactionType(TransactionTypeEnum.Buy);
        }
        
        TransactionDTO transactionData = ObjectMappingUtil.mapTransactionToTransactionDTO(trade);
        transactionData.setAccount(optAccount.get());
        transactionData.setHolding(holdingData);
        this.transactionsRepo.save(transactionData);
        
        return ObjectMappingUtil.mapHoldingDtoToHolding(holdingData);
    }
    

    
    /**
     * Persists a holding
     * 
     * @param holding - object to persist
     * @return {@link HoldingDTO}
     */
    @Transactional
    public Holding saveHolding(Holding holding) {
    	
    	if (holding == null || holding.getAccount() == null) {
    		throw new InvalidRequestException("Holding and Holding.Account must not be null");
    	}
    	
    	HoldingDTO holdingData = ObjectMappingUtil.mapHoldingToHoldingDto(holding);
    	holdingData = this.holdingsRepo.save(holdingData);
        
        return ObjectMappingUtil.mapHoldingDtoToHolding(holdingData);
    }
    
    /**
     * Retrieves a list of Holdings for a given account
     * 
     * @param accountId - unique account identifier
     * @return List<Holding>
     */
    public List<Holding> findHoldingsForAccount(Long accountId) {
    	
    	if (accountId == null) {
    		throw new InvalidRequestException("accountId must be non-null");
    	}
    	
    	List<HoldingDTO> holdingsData = this.holdingsRepo.findByAccountAccountId(accountId);
    	List<Holding> holdings = ObjectMappingUtil.mapHoldingDtoList(holdingsData);
    	
    	int loopCount = 0;
    	for (Holding holding : holdings) {
    		HoldingDTO holdingData = holdingsData.get(loopCount);
    		List<Quote> quotes = this.quotesService.findMostRecentQuotesForInvestment(holdingData.getInvestment());
    		if (quotes != null && quotes.size() > 0) {
    			holding.setMostRecentQuote(quotes.get(0));
    		}
    		++loopCount;
    	}
    	
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
		Optional<TransactionDTO> optTrans = this.transactionsRepo.findById(transactionId);
		TransactionDTO existingTransaction = optTrans.get();
		
		// build the sale transaction off of the existing transaction
		TransactionDTO saleTransaction = new TransactionDTO();
		saleTransaction.setTradePrice(tradePrice);
		saleTransaction.setTradeQuantity(tradeQuantity);
		saleTransaction.setTransactionDate(saleDate);
		saleTransaction.setTransactionType(TransactionTypeEnum.Sell);
		saleTransaction.setAccount(existingTransaction.getAccount());
		
		// build a cash transaction that is the result of the sale
		TransactionDTO cashTransaction = new TransactionDTO();
		cashTransaction.setTradePrice(saleTransaction.getTradePrice());
		cashTransaction.setTradeQuantity(saleTransaction.getTradeQuantity());
		cashTransaction.setTransactionDate(saleTransaction.getTransactionDate());
		cashTransaction.setTransactionType(TransactionTypeEnum.Cash);
		cashTransaction.setAccount(existingTransaction.getAccount());
		
		// grab the holding since it may be set to 0 or only decremented.
		Long holdingId = existingTransaction.getHolding().getHoldingId();
		HoldingDTO holding = this.holdingsRepo.findById(holdingId).get();
		saleTransaction.setHolding(holding);
		
		BigDecimal quantityChange = holding.getQuantity().subtract(saleTransaction.getTradeQuantity());
		holding.setQuantity(quantityChange);
		
    	this.holdingsRepo.save(holding);
    	TransactionDTO t = this.transactionsRepo.save(cashTransaction);
    	saleTransaction.setAssociatedCashTransactionId(t.getTransactionId());
    	this.transactionsRepo.save(saleTransaction);
    }
    
    /**
     * Retrieves a specific holding for an account and investment
     * 
     * @param accountId - unique account identifier
     * @param investmentId - unique investment identifier
     * @return List<Holding>
     */
    public List<Holding> findHoldingByAccountAccountIdAndInvestmentInvestmentId(Long accountId, Long investmentId) {
    	
    	if (accountId == null || investmentId == null) {
    		throw new InvalidRequestException("accountId and investmentId must be non-null");
    	}
        
        List<HoldingDTO> holdingsData = this.holdingsRepo.findByAccountAccountIdAndInvestmentInvestmentId(accountId, investmentId);
        
        return ObjectMappingUtil.mapHoldingDtoList(holdingsData);
    }
    
    /**
     * Retrieve a specific holding based on a holding identifier
     * 
     * @param holdingId - unique holding identifier
     * @return {@link HoldingDTO}
     */
    public Optional<Holding> findHoldingByHoldingId(Long holdingId) {
    	
    	if (holdingId == null) {
    		throw new InvalidRequestException("holding id must not be null");
    	}
        
    	HoldingDTO holding = null;
    	
    	Optional<HoldingDTO> optHolding = this.holdingsRepo.findById(holdingId);
    	if (optHolding.isPresent()) {
    		holding = optHolding.get();
	    	List<TransactionDTO> transactions = this.transactionsRepo.findAllByAccountAndHolding(holding.getAccount(), holding);
	    	holding.setTransactions(transactions);
    	}
    	
        return optHolding.isEmpty() ?
        		Optional.empty() : 
        			Optional.of(ObjectMappingUtil.mapHoldingDtoToHolding(optHolding.get()));
    }
    
    public Optional<Holding> findHoldingByHoldingIdWithBuyTransaction(Long holdingId) {
    	
    	if (holdingId == null) {
    		throw new InvalidRequestException("holding id must not be null");
    	}
        
    	HoldingDTO h = null;
    	
    	Optional<HoldingDTO> optHolding = this.holdingsRepo.findById(holdingId);
    	
    	if (optHolding.isPresent()) {
    		h = optHolding.get();
    		Optional<TransactionDTO> optTransaction = this.transactionsRepo.findBuyTransactionforHoldingId(holdingId);
        	if (optTransaction.isPresent()) {
        		h.addTransaction(optTransaction.get());
        	}
    	}
    	
        return optHolding.isEmpty() ?
        		Optional.empty() : 
        			Optional.of(ObjectMappingUtil.mapHoldingDtoToHolding(optHolding.get()));
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
        
        HoldingDTO holdingData = ObjectMappingUtil.mapHoldingToHoldingDto(holding);
        holdingData = this.holdingsRepo.save(holdingData);
        
        TransactionDTO transactionData = ObjectMappingUtil.mapTransactionToTransactionDTO(trade);
    	transactionData.setHolding(holdingData);

        this.transactionsRepo.save(transactionData);
    }
    
    /**
	 * 
	 * @param holding 
	 */
	private Optional<TransactionDTO> findBuyTransactionForHolding(Long holdingId) {
		
		List<TransactionDTO> transactions = this.transactionsRepo.findByHoldingHoldingId(holdingId);
		Optional<TransactionDTO> buyTransaction = transactions.stream().findFirst().filter(t -> t.getTransactionType().equals(TransactionTypeEnum.Buy));
		
		return buyTransaction;
		
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
    	
    	Optional<TransactionDTO> optTransactionOptional = this.findBuyTransactionForHolding(holding.getHoldingId());
    	if (optTransactionOptional.isPresent()) {
    		TransactionDTO buyTransaction = optTransactionOptional.get();
    		buyTransaction.setTradePrice(holding.getPurchasePrice());
    		buyTransaction.setTradeQuantity(holding.getQuantity());
    		this.transactionsRepo.save(buyTransaction);
    	}
    	
    	HoldingDTO holdingData = ObjectMappingUtil.mapHoldingToHoldingDto(holding);
    	holdingData = this.holdingsRepo.save(holdingData);
    	
    	return ObjectMappingUtil.mapHoldingDtoToHolding(holdingData);
    }
    
    /**
     * Retrieves all holdings
     * 
     * @return {@link List}<Holding>
     */
    public List<Holding> findAllHoldings() {
    	
        List<HoldingDTO> holdings = this.holdingsRepo.findAll();
        
        return ObjectMappingUtil.mapHoldingDtoList(holdings);
    }
    
    /**
     * Retrieves all holdings ordered by their associated investment symbol
     * 
     * @return {@link List}<Holding>
     */
    public List<Holding> findAllHoldingsOrderedBySymbol() {
        
        List<HoldingDTO> holdingsData = this.holdingsRepo.findAll(Sort.by(Sort.Direction.ASC, "investment.symbol"));
        
        return ObjectMappingUtil.mapHoldingDtoList(holdingsData);
    }
    
    
    public List<Investment> findAllInvestmentsCurrentlyHeldOrderedBySymbol() {
    	
    	List<InvestmentDTO> investmentsData = this.investmentsRepo.findUniqueInvestmentsForHoldingsOrderBySymbol();
    	
    	return ObjectMappingUtil.mapInvestmentDtoList(investmentsData);
    }
	
	/**
	 * Retrieves the holdings associated with an investment
	 * 
	 * @param investmentId - investment unique identifier
	 * @return A {@link List} of holdings associated with the investment
	 */
	public List<Holding> findHoldingsByInvestmentId(Long investmentId) {
		log.info("Entered getHoldingsByInvestmentId()");
		
		if (investmentId == null) {
			throw new InvalidRequestException("investmentId must be non-null");
		}
		
		List<HoldingDTO> holdings = this.holdingsRepo.findHoldingsByInvestmentInvestmentId(investmentId);
		
		log.info("Exiting getHoldingsByInvestmentId()");
		
		return ObjectMappingUtil.mapHoldingDtoList(holdings);
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
			for (Holding holding : holdings) {
				this.saveHolding(holding);
			}
		}
		
		log.info("Exiting saveAll()");
	}
	
	/**
	 * Rerieves all holdings
	 * 
	 * @return A {@link List} of {@link HoldingDTO}s
	 */
	public List<Holding> findAll() {
		log.info("Entered findAll()");
		
		List<HoldingDTO> holdings = this.holdingsRepo.findAll();
				
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		log.info("Exiting findAll()");
		
		return ObjectMappingUtil.mapHoldingDtoList(holdings);
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
		AccountDTO purchaseAccount = new AccountDTO(accountId);
		
		// create the investment being purchased
		InvestmentDTO investment = new InvestmentDTO(investmentId);
		
		BigDecimal purchaseQuantity = quantity;
		BigDecimal purchasePrice = price;
		
		// add the new holding - this represents a lot
		HoldingDTO holding =  new HoldingDTO();
		holding.setAccount(purchaseAccount);
		holding.setInvestment(investment);
		holding.setQuantity(purchaseQuantity);
		holding.setPurchasePrice(purchasePrice);
		holding = this.holdingsRepo.save(holding);
		
		// build the initial transaction object
		TransactionDTO trade = new TransactionDTO(holding);
		trade.setTransactionDate(tradeDate);
		trade.setTradePrice(price);
		trade.setTransactionType(TransactionTypeEnum.Buy);
		trade.setTradeQuantity(quantity);
			
		TransactionDTO cashTransaction = new TransactionDTO(purchaseAccount);
		cashTransaction.setTradePrice(new BigDecimal(-1));
		cashTransaction.setTradeQuantity(trade.getTradePrice().multiply(trade.getTradeQuantity()));
		cashTransaction.setTransactionDate(trade.getTransactionDate());
		cashTransaction.setTransactionType(TransactionTypeEnum.Cash);
        
        TransactionDTO t = this.transactionsRepo.save(cashTransaction);
        trade.setAssociatedCashTransactionId(t.getTransactionId());
        this.transactionsRepo.save(trade);
        
    }
    
    /**
     * Transaction method to transfer cash from one account to another
     * 
     * @param transferDate - date of the transfer
     * @param transferAmount - transfer amount
     * @param fromAccountId - unique account identifier for the account to debit
     * @param toAccountId - unique account identifier for the account to credit
     */
    @Transactional
    public void transferCash(Date transferDate, BigDecimal transferAmount, Long fromAccountId, Long toAccountId) {
    	
    	if (transferDate == null || transferAmount == null || fromAccountId == null || toAccountId == null) {
    		throw new InvalidRequestException("required attributes are null");
    	}
        
        TransactionDTO debitTransaction = new TransactionDTO(new AccountDTO(fromAccountId));
        debitTransaction.setTradePrice(BigDecimal.valueOf(-1));
        debitTransaction.setTradeQuantity(transferAmount);
        debitTransaction.setTransactionDate(transferDate);
        debitTransaction.setTransactionType(TransactionTypeEnum.Cash);
        
        TransactionDTO creditTransaction = new TransactionDTO(new AccountDTO(toAccountId));
        creditTransaction.setTradePrice(BigDecimal.valueOf(1));
        creditTransaction.setTradeQuantity(transferAmount);
        creditTransaction.setTransactionDate(transferDate);
        creditTransaction.setTransactionType(TransactionTypeEnum.Cash);
        
        this.transactionsRepo.save(debitTransaction);
        this.transactionsRepo.save(creditTransaction);
    }
    
    /**
     * Retrieve a specific trade
     * 
     * @param transactionId - unique transaction identifier
     * @return {@link Transactions}
     */
    public Optional<Transaction> findTransaction(Long transactionId) {
    	
    	if (transactionId == null) {
    		throw new InvalidRequestException("transactionId must be non-null");
    	}
        
        Optional<TransactionDTO> optTransaction = this.transactionsRepo.findById(transactionId);
        
        return optTransaction.isEmpty() ?
        		Optional.empty() :
        			Optional.of(ObjectMappingUtil.mapTransactionDtoToTransaction(optTransaction.get()));
    }
    
    /**
     * Retrieves transactions for an account and holding.
     * 
     * @param accountId - unique account identifier
     * @param holdingId - unique holding identifier
     * @return List<Transaction>
     */
    public List<Transaction> findAllTransactionsForAccountAndHolding(Account account, Holding holding) {
    	
    	if (holding == null || account == null || holding.getHoldingId() == null || account.getAccountId() == null) {
    		throw new InvalidRequestException("required attributes are null");
    	}
    	
    	AccountDTO accountData = ObjectMappingUtil.mapAccountToAccountDTO(account);
    	HoldingDTO holdingData = ObjectMappingUtil.mapHoldingToHoldingDto(holding);
    	
    	List<TransactionDTO> transactionsData = this.transactionsRepo.findAllByAccountAndHolding(accountData, holdingData);
    	
    	return ObjectMappingUtil.mapTransactionDtoList(transactionsData);
    }
    
    public Optional<Date> findBuyDateForHolding(Long holdingId) {
    	if (holdingId == null) {
    		throw new InvalidRequestException("holdingId must be non-null");
    	}
    	
    	return this.transactionsRepo.findBuyDateForHolding(holdingId);
    }
    
    
    public Optional<Transaction> findBuyTransactionsForHoldingId(Long holdingId) {
    	
    	if (holdingId == null) {
    		throw new InvalidRequestException("holdingId must be non-null");
    	}
    	
    	Optional<TransactionDTO> transaction = this.transactionsRepo.findBuyTransactionforHoldingId(holdingId);
    	
    	return transaction.isEmpty() ?
    			Optional.empty() : 
    				Optional.of(ObjectMappingUtil.mapTransactionDtoToTransaction(transaction.get()));
    }
    
    
    
    
    // Trade Methods
    
    /**
     * Persists the specified trade
     * 
     * @param transaction - transaction to save
     * @return {@link TransactionDTO}
     */
    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
    	
    	if (transaction == null) {
    		throw new InvalidRequestException("transaction must be non-null");
    	}
    	
    	TransactionDTO transactionData = ObjectMappingUtil.mapTransactionToTransactionDTO(transaction);
    	transactionData = this.transactionsRepo.save(transactionData);
    	
    	return ObjectMappingUtil.mapTransactionDtoToTransaction(transactionData);
    }
    
    
    /**
     * Retrieves all the transactions of type cash for a particular account.
     * 
     * @param accountId - unique account identifier
     * @return List<Transaction>
     */
    public List<Transaction> findAllCashTransactionsForAccount(Long accountId) {
    	
    	if (accountId == null) {
    		throw new InvalidRequestException("accountId must be non-null");
    	}
        
        List<TransactionDTO> transactions =  this.transactionsRepo.findAllByAccountAccountIdWithCash(accountId);
        
        return ObjectMappingUtil.mapTransactionDtoList(transactions);
    }
    
    /**
     * @TODO IMPLEMENT
     * 
     * Updates the holding and associated trade
     * 
     * @param trade - trade to update
     * @param holding - holding to update
     */
    @Transactional
    public void updateTrade(Transaction changedTrade) {
	    
	    TransactionTypeEnum newTradeTypeValue = changedTrade.getTransactionType();
	    
	    Optional<TransactionDTO> optCurrentTransaction = this.transactionsRepo.findById(changedTrade.getTransactionId());
	    TransactionDTO currentTransaction = optCurrentTransaction.get();
	    
	    HoldingDTO holding = currentTransaction.getHolding();
	    if (! currentTransaction.getTransactionType().equals(newTradeTypeValue)) {
	        throw new InvalidRequestException("Updating the transaction type on a trade is not supported");
	    }
	    else {
	        if (currentTransaction.getHolding().getHoldingId() != null && holding != null) {
    	        holding.setQuantity(changedTrade.getTradeQuantity());
    	        holding.setPurchasePrice(changedTrade.getTradePrice());
		    }
	    }
	   
	   this.holdingsRepo.save(holding);
	   this.transactionsRepo.save(ObjectMappingUtil.mapTransactionToTransactionDTO(changedTrade));
    }
    
    /**
     * Retrieves the trades associated with an account
     * 
     * @param accountId - unique account identifier
     * @return {@link List}<Trade>
     */
    public List<Transaction> findTradesForAccount(Account account) {
    	
    	if (account == null || account.getAccountId() == null) {
    		throw new InvalidRequestException("account and accountId must be non-null");
    	}
        
    	AccountDTO accountData = ObjectMappingUtil.mapAccountToAccountDTO(account);
        List<TransactionDTO> transactionsData = this.transactionsRepo.findAllByAccountAccountIdOrderByTransactionDateDesc(accountData);
        
        return ObjectMappingUtil.mapTransactionDtoList(transactionsData);
    }
    
    public List<Transaction> findTotalDividendsForHoldings() {
    	
    	log.info("Entered getDividendsForHoldings()");
    	
    	List<TransactionDTO> dividends = this.transactionsRepo.findTotalDividendsForHoldings();
    	
    	log.debug("Returning {} dividends", dividends != null ? dividends.size() : 0);
    	log.info("Exiting getDividendsForHoldings()");
    	
    	return ObjectMappingUtil.mapTransactionDtoList(dividends);
    }
    
    public Optional<Transaction> findTotalDividendsForHolding(Long holdingId) {
    	
    	if (holdingId == null) {
    		throw new InvalidRequestException("investmentId must be non-null");
    	}
    	
    	log.info("Entered getTotalDividendsForHolding()");
    	
    	Optional<TransactionDTO> dividend = this.transactionsRepo.findTotalDividendsForHolding(holdingId);
    	
    	log.debug("Returning {} dividends", dividend);
    	log.info("Exiting getTotalDividendsForHolding()");
    	
    	return dividend.isEmpty() ?
    			Optional.empty() : 
    				Optional.of(ObjectMappingUtil.mapTransactionDtoToTransaction(dividend.get()));
    }
    
    public Map<Long, Transaction> findTotalDidivendsForAllHoldings() {
    	
    	List<Transaction> dividends = this.findTotalDividendsForHoldings();
    	
    	Map<Long, Transaction> dividendsMap = null;
    	
    	if (dividends != null) {
    		dividendsMap = new HashMap<>(dividends.size());
    		
    		Iterator<Transaction> iter = dividends.iterator();
    		while (iter.hasNext()) {
    			Transaction div = iter.next();
    			dividendsMap.put(div.getHolding().getHoldingId(), div);
    		}
    	}
    	
    	return dividendsMap;
    }
    
    
    public void deleteTransaction(Transaction transaction) {
    	this.transactionsRepo.deleteById(transaction.getTransactionId());
    }

}
