/**
 * 
 */
package com.brenner.investments.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.investments.data.AccountDataService;
import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Transaction;
import com.brenner.investments.service.AccountsService;
import com.brenner.investments.service.TransactionsService;
import com.brenner.investments.util.DataHelperUtil;

/**
 * Data service specific to working with account data
 * 
 * @author dbrenner
 *
 */
@Service
public class PersistentAccountsService implements AccountsService {
	
	private static final Logger log = LoggerFactory.getLogger(PersistentAccountsService.class);
    
    @Autowired
    AccountDataService accountDataService;
    
    @Autowired
    TransactionsService transactionsService;
    
    public PersistentAccountsService() {}
    
    /**
     * Injection of an AccountRepository implementation
     * 
     * @param accountRepo The AccountRepository instance
     */
    public void setAccountRepo(AccountDataService accountRepo) {
		this.accountDataService = accountRepo;
	}
    
    /**
     * Injection of a TransactionService instance. Used for testing.
     * 
     * @param transactionsService
     */
    public void setTransactionsService(TransactionsService transactionsService) {
    	this.transactionsService = transactionsService;
    }

	/**
     * Retrieves an account by its assigned account number.
     * 
     * @param accountNumber - account number to search for.
     * 
     * @return {@link Account}
     */
    public Account getAccountByAccountNumber(String accountNumber) {
    	
    	log.info("Entered getAccountByAccountNumber()");
    	log.debug("Param: accountNumber: {}", accountNumber);
    	
    	Account account = this.accountDataService.findByAccountNumber(accountNumber);
    	
    	log.debug("Retrieved account: {}", account);
    	log.info("Exiting getAccountByAccountNumber()");
    	
    	return account;
    }
    
    /**
     * Deletes the specified account
     * 
     * @param account - account object to delete
     */
    public void deleteAccount(Account account) {
    	
    	log.info("Entered deleteAccount()");
    	log.debug("Param: account: {}", account);
    	
    	this.accountDataService.delete(account.getAccountId());
    	
    	log.info("Exiting deleteAccount()");
    }
    
    /**
     * Persist the given account object
     * 
     * @param account - object to persist
     * @return {@link Account}
     */
    @Transactional
    public Account save(Account account) {
    	
    	log.info("Entered saveAccount()");
    	log.debug("Param: account: {}", account);
        
        Account a = this.accountDataService.save(account);
        
        log.debug("Saved account: {}", a);
        log.info("Exiting saveAccount()");
        
        return a;
    }
    
    /**
     * Builds an account from the supplied information. Sums cash transactions to the account object and returns
     * 
     * @param account - the account object to save
     * @return {@link Account}
     */
    public Account addNewAccount(Account account) {
    	
    	log.info("Entered addNewAccount()");
    	log.debug("Params: account: {}", account);
		
		account = this.save(account);
		
		log.debug("Saved account: {}", account);
		
		// get the cash transactions for the account
		List<Transaction> cashTransactions = transactionsService.findAllCashTransactionsForAccount(account.getAccountId());
		
		log.debug("Cash transactions for account: {}", cashTransactions != null ? cashTransactions.size() : 0);
				
		// if there are cash transactions for the account retrieve and make part of the account object
		DataHelperUtil.loadCashTransactionsToAccount(account, cashTransactions);
		
		log.debug("Final account returned: {}", account);
		log.info("Exiting addNewAccount()");
		
		return account;
    }
    
    public Account updateAccount(Account account) {
    	
    	log.info("Entered updateAccount()");
    	log.debug("Params: account: {}", account);
    	
    	account = this.save(account);
	    
	    log.debug("Saved account: {}", account);
	    log.info("Exiting updateAccount()");
    	
    	return account;
    }
    
    
    /**
     * Retrieve all accounts
     * 
     * @return {@link Iterable}<Account>
     */
    public Iterable<Account> getAllAccounts() {
    	
    	log.info("Entered getAllAccounts()");
        
        Iterable<Account> iter = this.accountDataService.findAll();
        
        log.info("Exiting getAllAccounts()");
        
        return iter;
    }
    
    /**
     * Retrieve a specific account
     * 
     * @param accountId - account unique identifier
     * @return {@link Account}
     */
    public Account getAccountByAccountId(Long accountId) {
    	
    	log.info("Entered getAccountByAccountId()");
    	log.debug("Param: accountId: {}", accountId);
        
        Account account = this.accountDataService.findById(accountId);
        
        log.debug("Retrieved account: {}", account);
        log.info("Exiting getAccountByAccountId()");
        
        return account;
    }
    
    /**
     * Retrieve a list of accounts and their corresponding cash balance
     * 
     * @return {@link List}<Account>
     */
    public  List<Account> getAccountsAndCash() {
    	log.info("Entered getAccountsAndCash()");
    	
        List<Account> accounts = new ArrayList<>();
        Iterable<Account> allAccounts = this.accountDataService.findAll();
        log.debug("Retrieved all accounts: {}", allAccounts);
                
        for (Account account : allAccounts) {
            List<Transaction> accountCashList = this.transactionsService.findAllCashTransactionsForAccount(account.getAccountId());
            log.debug("Retrieved {} cashTransactions for account {}", 
            		accountCashList != null ? accountCashList.size() : 0, account);
            DataHelperUtil.loadCashTransactionsToAccount(account, accountCashList);
            accounts.add(account);
        }
        log.debug("Returning {} accounts", accounts.size());
        
        log.info("Exiting getAccountsAndCash()");
        return accounts;
        
    }
    
    /**
     * Get a specific account and its associated cash balance
     * 
     * @param accountId - unique account identifier
     * @return {@link Account}
     */
    public Account getAccountAndCash(Long accountId) {
    	
    	log .info("Entered getAccountAndCash()");
    	log.debug("Param: accountId: {}", accountId);
        
        Account account = this.accountDataService.findById(accountId);
        
        log.debug("Found account: {}", account);
        
        if (account != null) {
            
            List<Transaction> accountCashList = this.transactionsService.findAllCashTransactionsForAccount(account.getAccountId());
            
            log.debug("Cash transactions {} for account", accountCashList != null ? accountCashList.size() : 0);
            DataHelperUtil.loadCashTransactionsToAccount(account, accountCashList);
        }
        
        log.info("Exiting getAccountAndCash()");
        
        return account;
        
    }
    
    /**
     * Retrieve all account and order by account name ascending
     * 
     * @return {@link List}<Account>
     */
    public List<Account> getAllAccountsOrderByAccountNameAsc() {
    	
    	log.info("Entered getAllAccountsOrderByAccountNameAsc()");
        
    	List<Account> accounts =  this.accountDataService.findAllByOrderByAccountNameAsc();
    	
    	log.debug("Retrieved {} accounts", accounts != null ? accounts.size() : 0);
    	log.info("Exiting getAllAccountsOrderByAccountNameAsc()");
    	
    	return accounts;
    }
}
