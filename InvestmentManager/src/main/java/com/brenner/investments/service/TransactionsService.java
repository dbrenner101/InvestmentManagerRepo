package com.brenner.investments.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Transaction;
import com.brenner.investments.entities.constants.TransactionTypeEnum;

public interface TransactionsService {

	public void deleteHolding(Holding holding);
    
    
    public void deleteTransaction(Transaction transaction);
    
    //public void saveBulkTransaction(List<Transaction> transactions);
    
    
    public List<Transaction> getTotalDividendsForInvestments();
    
    
    public Transaction getTotalDividendsForInvestment(Long investmentId);
    
    public Map<Long, Transaction> getTotalDidivendsForAllInvestments();
    
    public Map<String, List<Holding>> modelSplit(Date transactionDate, Long investmentId, Float splitRatio);
    
    public void saveSplit(Date transactionDate, Long investmentId, Float splitRatio);
    

    /**
     * Retrieves transactions for an account and holding.
     * 
     * @param accountId - unique account identifier
     * @param holdingId - unique holding identifier
     * @return List<Transaction>
     */
    public List<Transaction> getAllTransactionsForAccountAndHolding(Account account, Holding holding);
    

    /**
     * Retrieves transactions for an account and holding.
     * 
     * @param accountId - unique account identifier
     * @param holdingId - unique holding identifier
     * @return List<Transaction>
     */
    public List<Transaction> getAllTransactionsForAccountAndInvestment(Account account, Investment investment);
    
    public Transaction getBuyTransactionsForHoldingId(Long holdingId);
    
    /**
     * Persists a holding
     * 
     * @param holding - object to persist
     * @return {@link Holding}
     */
    @Transactional
    public Holding saveHolding(Holding holding);
    
    /**
     * Retrieves a list of Holdings for a given account
     * 
     * @param accountId - unique account identifier
     * @return List<Holding>
     */
    public List<Holding> getHoldingsForAccount(Long accountId);
    
    /**
     * Retrieves the holdings associated with an account where the quantity > 0
     * 
     * @param accountId - account unique identifier
     * @return List<Holding>
     * @throws ParseException 
     */
    public List<Holding> getHoldingsForAccountAndQuantityGreaterThanOrderedBySymbol(Long accountId) throws ParseException;
    
    /**
     * Executes the logic to handle the sale of a holding. This includes modifying the Holding, persisting a trade and updating cash position for the account
     * 
     * @param holding - the holding that represents investment being sold
     * @param saleTransaction - the transaction that wraps the sale
     * @param cashTransaction - the cash result of the trade
     * @param realized - the actual gain or loss from the transaction
     */
    @Transactional
    public void sellHolding(Long transactionId, Date saleDate, Float tradeQuantity, Float tradePrice);
    
    /**
     * Saves a new holding instance
     * 
     * @param investment - the investment for the holding
     * @param trade - trade associated with the investment
     */
    @Transactional
    public Holding addHolding(Transaction trade);
    
    /**
     * Retrieves a specific holding for an account and investment
     * 
     * @param accountId - unique account identifier
     * @param investmentId - unique investment identifier
     * @return List<Holding>
     */
    public List<Holding> getHoldingByAccountAccountIdAndInvestmentInvestmentId(Long accountId, Long investmentId);
    
    /**
     * Retrieve a specific holding based on a holding identifier
     * 
     * @param holdingId - unique holding identifier
     * @return {@link Holding}
     */
    public Holding getHoldingByHoldingId(Long holdingId);
    
    public Holding getHoldingByHoldingIdWithBuyTransactions(Long holdingId);
    
    /**
     * Updates a holding and the associated trade
     * 
     * @param holding - holding to update
     * @param trade - trade associated with the holding
     */
    @Transactional
    public void updateHoldingAndTrade(Holding holding, Transaction trade);
    
    /**
     * Retrieves all holdings
     * 
     * @return {@link Iterable}<Holding>
     */
    public Iterable<Holding> getAllHoldings();
    
    /**
     * Retrieves all holdings ordered by their associated investment symbol
     * 
     * @return {@link List}<Holding>
     */
    public List<Holding> getAllHoldingsOrderedBySymbol();
    
    /**
     * Retrieve a list of investments that correspond to holdings and order by their symbol.
     * 
     * @return {@link List}<Investment>
     */
    public List<Investment> getAllInvestmentsCurrentlyHeldOrderedBySymbol();
    
    
    // End Holdings Methods
    
    
    
    
    // Trade Methods
    
    /**
     * Persists the specified trade
     * 
     * @param transaction - transaction to save
     * @return {@link Transaction}
     */
    @Transactional
    public Transaction saveTransaction(Transaction transaction);
    
    
    /**
     * Retrieves all the transactions of type cash for a particular account.
     * 
     * @param accountId - unique account identifier
     * @return List<Transaction>
     */
    public List<Transaction> findAllCashTransactionsForAccount(Long accountId);
    
    /**
     * Updates the holding and associated trade
     * 
     * @param trade - trade to update
     * @param holding - holding to update
     */
    @Transactional
    public void updateTrade(Transaction changedTrade, TransactionTypeEnum previousTradeType);
    
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
			Long accountId);
    
    /**
     * Retrieves the trades associated with an account
     * 
     * @param accountId - unique account identifier
     * @return {@link List}<Trade>
     */
    public List<Transaction> getTradesForAccount(Account account);
    
    /**
     * Retrieve a specific trade
     * 
     * @param transactionId - unique transaction identifier
     * @return {@link Transactions}
     */
    public Transaction getTransaction(Long transactionId);
    
    
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
    public void transferCash(Date transferDate, Float transferAmount, Long fromAccountId, Long toAccountId);
}
