/**
 * 
 */
package com.brenner.portfoliomgmt.accounts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.transactions.Transaction;
import com.brenner.portfoliomgmt.transactions.TransactionsService;
import com.brenner.portfoliomgmt.util.DataHelperUtil;

/**
 * Data service specific to working with account data
 * 
 * @author dbrenner
 *
 */
@Service
public class AccountsService {
	
	private static final Logger log = LoggerFactory.getLogger(AccountsService.class);
	
	@Autowired
	AccountsRepository accountsRepo;
    
    @Autowired
    TransactionsService transactionsService;
    
    public AccountsService() {}

	/**
     * Retrieves an account by its assigned account number.
     * 
     * @param accountNumber - account number to search for.
     * 
     * @return {@link Optional<Account>}
     */
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
    	
    	log.info("Entered getAccountByAccountNumber()");
    	log.debug("Param: accountNumber: {}", accountNumber);
    	
    	if (accountNumber == null || accountNumber.length() == 0) {
    		throw new InvalidRequestException("account number must be non-null");
    	}
    	
    	Optional<Account> optAccount = this.accountsRepo.findAccountByAccountNumber(accountNumber);
    	
    	log.debug("Retrieved account: {}", optAccount);
    	log.info("Exiting getAccountByAccountNumber()");
    	
    	return optAccount;
    }
    
    /**
     * Deletes the specified account
     * 
     * @param account - account object to delete
     */
    public void deleteAccount(Account account) {
    	
    	log.info("Entered deleteAccount()");
    	log.debug("Param: account: {}", account);
    	
    	if (account == null) {
    		throw new InvalidRequestException("account must be non-null");
    	}
		
		Optional<Account> optAccount = this.accountsRepo.findById(account.getAccountId());
		
		if (! optAccount.isPresent()) {
			throw new NotFoundException("Account with accountId " + account.getAccountId() + " does not exist.");
		}
    	
    	this.accountsRepo.delete(account);
    	
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
    	
    	if (account == null) {
    		throw new InvalidRequestException("Account must be non-null.");
    	}
        
        Account a = this.accountsRepo.save(account);
        
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
    	
    	if (account == null) {
    		throw new InvalidRequestException("Account must be non-null.");
    	}
		
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
    
    
    /**
     * Retrieve all accounts
     * 
     * @return {@link List}<Account>
     */
    public List<Account> getAllAccounts() {
    	
    	log.info("Entered getAllAccounts()");
        
        List<Account> accounts = this.accountsRepo.findAll();
        
        log.info("Exiting getAllAccounts()");
        
        return accounts;
    }
    
    /**
     * Retrieve a specific account
     * 
     * @param accountId - account unique identifier
     * @return {@link Account}
     */
    public Optional<Account> getAccountByAccountId(Long accountId) {
    	
    	log.info("Entered getAccountByAccountId()");
    	log.debug("Param: accountId: {}", accountId);
    	
    	if (accountId == null) {
    		throw new InvalidRequestException("account id must be non-null.");
    	}
        
        Optional<Account> optAccount = this.accountsRepo.findById(accountId);
        
        log.debug("Retrieved account: {}", optAccount);
        log.info("Exiting getAccountByAccountId()");
        
        return optAccount;
    }
    
    /**
     * Retrieve a list of accounts and their corresponding cash balance
     * 
     * @return {@link List}<Account>
     */
    public  List<Account> getAccountsAndCash() {
    	log.info("Entered getAccountsAndCash()");
    	
        List<Account> accounts = new ArrayList<>();
        Iterable<Account> allAccounts = this.accountsRepo.findAll();
        log.debug("Retrieved all accounts: {}", allAccounts);
        
        accounts.forEach((account) -> {
        	List<Transaction> accountCashList = this.transactionsService.findAllCashTransactionsForAccount(account.getAccountId());
        	
        	log.debug("Retrieved {} cashTransactions for account {}", accountCashList != null ? accountCashList.size() : 0, account);
            
            DataHelperUtil.loadCashTransactionsToAccount(account, accountCashList);
            accounts.add(account);
        });
        
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
    public Optional<Account> getAccountAndCash(Long accountId) {
    	
    	log .info("Entered getAccountAndCash()");
    	log.debug("Param: accountId: {}", accountId);
    	
    	if (accountId == null) {
    		throw new InvalidRequestException("accountId must be non-null.");
    	}
        
        Optional<Account> optAccount = this.accountsRepo.findById(accountId);
        
        log.debug("Found account: {}", optAccount);
        
        if (optAccount.isPresent()) {
            
            List<Transaction> accountCashList = this.transactionsService.findAllCashTransactionsForAccount(optAccount.get().getAccountId());
            
            log.debug("Cash transactions {} for account", accountCashList != null ? accountCashList.size() : 0);
            
            DataHelperUtil.loadCashTransactionsToAccount(optAccount.get(), accountCashList);
        }
        
        log.info("Exiting getAccountAndCash()");
        
        return optAccount;
        
    }
    
    /**
     * Retrieve all account and order by account name ascending
     * 
     * @return {@link List}<Account>
     */
    public List<Account> getAllAccountsOrderByAccountNameAsc() {
    	
    	log.info("Entered getAllAccountsOrderByAccountNameAsc()");
        
    	List<Account> accounts = this.accountsRepo.findAll(Sort.by(Sort.Direction.ASC, "accountName"));
    	
    	log.debug("Retrieved {} accounts", accounts != null ? accounts.size() : 0);
    	log.info("Exiting getAllAccountsOrderByAccountNameAsc()");
    	
    	return accounts;
    }
}
