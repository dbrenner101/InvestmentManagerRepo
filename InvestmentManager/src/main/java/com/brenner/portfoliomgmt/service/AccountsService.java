/**
 * 
 */
package com.brenner.portfoliomgmt.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.portfoliomgmt.data.entities.AccountDTO;
import com.brenner.portfoliomgmt.data.repo.AccountsRepository;
import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.util.ObjectMappingUtil;

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
    
    public AccountsService() {}
    
    
    public Optional<Account> findAccountByAccountName(String accountName) {
    	log.info("Entered findAccountByAccountName()");
    	log.debug("Param: accountName: {}", accountName);
    	
    	Optional<AccountDTO> accountData = this.accountsRepo.findAccountByAccountName(accountName);
    	
    	log.debug("Retrieved account: {}", accountData);
    	log.info("Exiting findAccountByAccountName()");
    	
    	return accountData.isEmpty() ? 
    			Optional.empty() : 
    				Optional.of(ObjectMappingUtil.mapAccountDtoToAccount(accountData.get()));
    }

	/**
     * Retrieves an account by its assigned account number.
     * 
     * @param accountNumber - account number to search for.
     * 
     * @return {@link Optional<Account>}
     */
    public Optional<Account> findAccountByAccountNumber(String accountNumber) {
 
    	log.info("Entered findAccountByAccountNumber()");
    	log.debug("Param: accountNumber: {}", accountNumber);
    	
    	if (accountNumber == null || accountNumber.length() == 0) {
    		throw new InvalidRequestException("Account number must be non-null");
    	}
    	
    	Optional<AccountDTO> optAccount = this.accountsRepo.findAccountByAccountNumber(accountNumber);
    	
    	log.debug("Retrieved account: {}", optAccount);
    	log.info("Exiting findAccountByAccountNumber()");
    	
    	return optAccount.isEmpty() ? 
    			Optional.empty() : 
    				Optional.of(ObjectMappingUtil.mapAccountDtoToAccount(optAccount.get()));
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
    		throw new InvalidRequestException("Account must be non-null");
    	}
		
		Optional<AccountDTO> optAccount = this.accountsRepo.findById(account.getAccountId());
		if (! optAccount.isPresent()) {
			throw new NotFoundException("Account with accountId " + account.getAccountId() + " does not exist.");
		}
    	
    	this.accountsRepo.deleteById(account.getAccountId());
    	
    	log.info("Exiting deleteAccount()");
    }
    
    /**
     * Persist the given account object
     * 
     * @param account - object to persist
     * @return {@link AccountDTO}
     */
    @Transactional
    public Account save(Account account) {
    	
    	log.info("Entered saveAccount()");
    	log.debug("Param: account: {}", account);
    	
    	if (account == null) {
    		throw new InvalidRequestException("Account must be non-null.");
    	}
        
        AccountDTO accountData = ObjectMappingUtil.mapAccountToAccountDTO(account);
        
        accountData = this.accountsRepo.save(accountData);
        
        log.debug("Saved account: {}", accountData);
        log.info("Exiting saveAccount()");
        
        account = ObjectMappingUtil.mapAccountDtoToAccount(accountData);
        return account;
    }
    
    
    /**
     * Retrieve all accounts
     * 
     * @return {@link List}<Account>
     */
    public List<Account> findAllAccounts() {
    	
    	log.info("Entered getAllAccounts()");
        
        List<AccountDTO> accounts = this.accountsRepo.findAll();
        
        log.info("Exiting getAllAccounts()");
        
        return ObjectMappingUtil.mapAccountDtoList(accounts);
    }
    
    /**
     * Retrieve a specific account
     * 
     * @param accountId - account unique identifier
     * @return {@link AccountDTO}
     */
    public Optional<Account> findAccountByAccountId(Long accountId) {
    	
    	log.info("Entered getAccountByAccountId()");
    	log.debug("Param: accountId: {}", accountId);
    	
    	if (accountId == null) {
    		throw new InvalidRequestException("Account id must be non-null.");
    	}
        
        Optional<AccountDTO> optAccount = this.accountsRepo.findById(accountId);
        
        log.debug("Retrieved account: {}", optAccount);
        log.info("Exiting getAccountByAccountId()");
        
        return optAccount.isEmpty() ? 
        		Optional.empty() : 
        			Optional.of(ObjectMappingUtil.mapAccountDtoToAccount(optAccount.get()));
    }
    
    /**
     * Retrieve all account and order by account name ascending
     * @param sortParam TODO
     * 
     * @return {@link List}<Account>
     */
    public List<Account> findAllAccounts(String sortParam) {
    	
    	log.info("Entered findAllAccounts()");
		
		if (sortParam == null || sortParam.trim().length() == 0) {
			sortParam = "accountName";
		}
        
    	List<AccountDTO> accounts = this.accountsRepo.findAll(Sort.by(Sort.Direction.ASC, sortParam));
    	
    	log.debug("Retrieved {} accounts", accounts != null ? accounts.size() : 0);
    	log.info("Exiting findAllAccounts()");
    	
    	return ObjectMappingUtil.mapAccountDtoList(accounts);
    }
}
