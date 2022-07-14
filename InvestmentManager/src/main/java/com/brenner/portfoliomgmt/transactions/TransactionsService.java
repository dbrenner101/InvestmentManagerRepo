/**
 * 
 */
package com.brenner.portfoliomgmt.transactions;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.portfoliomgmt.accounts.Account;
import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.holdings.Holding;
import com.brenner.portfoliomgmt.holdings.HoldingsRepository;
import com.brenner.portfoliomgmt.investments.Investment;

/**
 * Data service specific to transactions
 * 
 * @author dbrenner
 *
 */
@Service
public class TransactionsService {
	
	private static final Logger log = LoggerFactory.getLogger(TransactionsService.class);
    
    @Autowired
    TransactionsRepository transactionsRepo;
    
    @Autowired
    HoldingsRepository holdingsRepository;
    
    
    /*public Map<String, List<Holding>> modelSplit(Date transactionDate, Long investmentId, Float splitRatio) {
    	
    	log.info("Entering modelSplit()");
    	
    	List<Holding> holdingsBeforeSplit = this.holdingsService.getHoldingsByInvestmentId(investmentId);
    	List<Holding> holdingsAfterSplit = this.applySplit(holdingsBeforeSplit, splitRatio);
    	
    	Map<String, List<Holding>> beforeAfterSplitMap = new HashMap<>(2);
    	beforeAfterSplitMap.put("beforeSplit", holdingsBeforeSplit);
    	beforeAfterSplitMap.put("afterSplit", holdingsAfterSplit);
    	
    	log.info("Exiting modelSplit()");
    	
    	return beforeAfterSplitMap;
    }*/
    
    /*public void saveSplit(Date transactionDate, Long investmentId, Float splitRatio) {
    	
    	List<Holding> holdingsBeforeSplit = this.holdingsService.getHoldingsByInvestmentId(investmentId);
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
    	
    	this.holdingsService.saveAll(holdingsAfterSplit);
    	this.transactionsDataService.saveAll(transactions);
    	
    }*/
    
    /*private List<Holding> applySplit(List<Holding> preSplitHoldings, Float splitRatio) {
    	
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
*/    
    public List<Transaction> getTotalDividendsForInvestments() {
    	
    	log.info("Entered getDividendsForInvestments()");
    	
    	List<Transaction> dividends = this.transactionsRepo.findTotalDividendsForInvestments();
    	
    	log.debug("Returning {} dividends", dividends != null ? dividends.size() : 0);
    	log.info("Exiting getDividendsForInvestments()");
    	
    	return dividends;
    }
    
    public Optional<Transaction> getTotalDividendsForInvestment(Long investmentId) {
    	
    	if (investmentId == null) {
    		throw new InvalidRequestException("investmentId must be non-null");
    	}
    	
    	log.info("Entered getDividendsForInvestment()");
    	
    	Optional<Transaction> dividend = this.transactionsRepo.findTotalDividendsForInvestment(investmentId);
    	
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
    
    
    public void deleteTransaction(Transaction transaction) {
    	this.transactionsRepo.delete(transaction);
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
    	
    	if (holding == null || account == null || holding.getHoldingId() == null || account.getAccountId() == null) {
    		throw new InvalidRequestException("required attributes are null");
    	}
    	
    	return this.transactionsRepo.findAllByAccountAndHolding(account, holding);
    }
    

    /**
     * Retrieves transactions for an account and holding.
     * 
     * @param accountId - unique account identifier
     * @param holdingId - unique holding identifier
     * @return List<Transaction>
     */
    public List<Transaction> getAllTransactionsForAccountAndInvestment(Account account, Investment investment) {
    	
    	if (account == null || investment == null || account.getAccountId() == null || investment.getInvestmentId() == null) {
    		throw new InvalidRequestException("required attributes are null");
    	}
    	
    	return this.transactionsRepo.findAllByAccountAndInvestment(account, investment);
    }
    
    
    public Optional<Transaction> getBuyTransactionsForHoldingId(Long holdingId) {
    	
    	if (holdingId == null) {
    		throw new InvalidRequestException("holdingId must be non-null");
    	}
    	
    	return this.transactionsRepo.findBuyTransactionforHoldingId(holdingId);
    }
    
    
    
    
    // Trade Methods
    
    /**
     * Persists the specified trade
     * 
     * @param transaction - transaction to save
     * @return {@link Transaction}
     */
    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
    	
    	if (transaction == null) {
    		throw new InvalidRequestException("transaction must be non-null");
    	}
        
        return this.transactionsRepo.save(transaction);
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
        
        return this.transactionsRepo.findAllByAccountAccountIdWithCash(accountId);
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
	    
	    Transaction currentTransaction = this.getTransaction(changedTrade.getTransactionId());
	    Holding holding = currentTransaction.getHolding();
	    if (! currentTransaction.getTransactionType().equals(newTradeTypeValue)) {
	        throw new InvalidRequestException("Updating the transaction type on a trade is not supported");
	    }
	    else {
	        if (currentTransaction.getHolding().getHoldingId() != null && holding != null) {
    	        holding.setAccount(changedTrade.getAccount());
    	        holding.setInvestment(changedTrade.getInvestment());
    	        holding.setQuantity(changedTrade.getTradeQuantity());
    	        holding.setPurchasePrice(changedTrade.getTradePrice());
		    }
	    }
	        
	   this.holdingsRepository.save(holding);
	   this.transactionsRepo.save(changedTrade);
    }
    
    /**
     * Retrieves the trades associated with an account
     * 
     * @param accountId - unique account identifier
     * @return {@link List}<Trade>
     */
    public List<Transaction> getTradesForAccount(Account account) {
    	
    	if (account == null || account.getAccountId() == null) {
    		throw new InvalidRequestException("account and accountId must be non-null");
    	}
        
        return this.transactionsRepo.findAllByAccountAccountIdOrderByTransactionDateDesc(account);
    }
    
    /**
     * Retrieve a specific trade
     * 
     * @param transactionId - unique transaction identifier
     * @return {@link Transactions}
     */
    public Transaction getTransaction(Long transactionId) {
    	
    	if (transactionId == null) {
    		throw new InvalidRequestException("transactionId must be non-null");
    	}
        
        Transaction trade = null;
        
        Optional<Transaction> optTransaction = this.transactionsRepo.findById(transactionId);
        
        if (optTransaction.isPresent()) {
        	trade = optTransaction.get();
        }
        return trade;
    }
    
    
    // End Trade Methods
    
    
    
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
    public void transferCash(Date transferDate, BigDecimal transferAmount, Long fromAccountId, Long toAccountId) {
    	
    	if (transferDate == null || transferAmount == null || fromAccountId == null || toAccountId == null) {
    		throw new InvalidRequestException("required attributes are null");
    	}
        
        Transaction debitTransaction = new Transaction();
        debitTransaction.setAccount(new Account(fromAccountId));
        debitTransaction.setTradePrice(BigDecimal.valueOf(-1));
        debitTransaction.setTradeQuantity(transferAmount);
        debitTransaction.setTransactionDate(transferDate);
        debitTransaction.setTransactionType(TransactionTypeEnum.Cash);
        
        Transaction creditTransaction = new Transaction();
        creditTransaction.setAccount(new Account(toAccountId));
        creditTransaction.setTradePrice(BigDecimal.valueOf(1));
        creditTransaction.setTradeQuantity(transferAmount);
        creditTransaction.setTransactionDate(transferDate);
        creditTransaction.setTransactionType(TransactionTypeEnum.Cash);
        
        this.transactionsRepo.save(debitTransaction);
        this.transactionsRepo.save(creditTransaction);
    }
    // End Cash Transaction Methods

	/**
	 * 
	 * @param holding 
	 */
	public Optional<Transaction> getPurchaseTransactionForHolding(Holding holding) {
		
		List<Transaction> transactions = this.transactionsRepo.findByHoldingHoldingId(holding.getHoldingId());
		Optional<Transaction> buyTransaction = transactions.stream().findFirst().filter(t -> t.getTransactionType().equals(TransactionTypeEnum.Buy));
		
		return buyTransaction;
		
	}
}
