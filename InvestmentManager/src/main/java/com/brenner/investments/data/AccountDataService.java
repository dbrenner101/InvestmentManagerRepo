package com.brenner.investments.data;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.investments.data.props.AccountsSqlProperties;
import com.brenner.investments.entities.Account;

/**
 * Persistence service for {@link Account}
 * 
 * @author dbrenner
 *
 */
@Component
public class AccountDataService {
	
	private static final Logger log = LoggerFactory.getLogger(AccountDataService.class);
	
	@Autowired
	AccountsSqlProperties sqlProps;
	
	@Autowired
	AccountsRepository accountsRepo;
	
	/**
	 * Retrieves all accounts - no order specified
	 * 
	 * @return a {@link List} of {@link Account} objects
	 */
	public List<Account> findAll() {
		log.info("Entered findAll()");
		
		List<Account> accounts = this.accountsRepo.findAll();
		
		log.debug("Returning {} accounts", accounts != null ? accounts.size() : 0);
		
		log.info("Exiting findAll()");
		return accounts;
	}
	
	/**
	 * Retrieve a specific account based on primary key
	 * 
	 * @param accountId - Account identifier
	 * @return {@link Account}
	 */
	public Account findById(Long accountId) {
		log.info("Entered findById()");
		log.debug("Params: accountId: {}", accountId);
		
		if (accountId == null) {
			throw new InvalidDataRequestException("accountId must not be null");
		}
		
		Optional<Account> optAccount = this.accountsRepo.findById(accountId);
		
		Account account = null;
		
		if (optAccount.isPresent()) {
			account = optAccount.get();
		}
		
		log.debug("Retrieved account: {}", account);
		
		log.info("Exiting findById()");
		return account;
	}
	
	/**
     * Retrieves all accounts ordered by account name ascending
     * 
     * @return The {@link List} of {@link Account} objects
     */
	public List<Account> findAllByOrderByAccountNameAsc() {
		log.info("Entered findAllByOrderByAccountNameAsc()");
		
		List<Account> accounts = this.accountsRepo.findAll(Sort.by(Sort.Direction.ASC, "accountName"));
		log.debug("Retrieved {} accounts", accounts != null ? accounts.size() : 0);
		
		log.info("Exiting findAllByOrderByAccountNameAsc()");
		return accounts;
	}
	
	/**
	 * Retrieves a account by its account number
	 * 
	 * @param accountNumber
	 * @return {@link Account}
	 */
	public Account findByAccountNumber(String accountNumber) {
		log.info("Entered findByAccountNumber()");
		log.debug("Param: accountNumber: {}", accountNumber);
		
		Optional<Account> optAccount = this.accountsRepo.findAccountByAccountNumber(accountNumber);
		
		Account account = null;
		
		if (optAccount.isPresent()) {
			account = optAccount.get();
		}
		log.debug("Retrieved account: {}", account);
		
		log.info("Exiting findByAccountNumber()");
		return account;
	}
	
	/**
	 * Deletes specified account
	 * 
	 * @param accountId - account to delete, must include unique identifier.
	 */
	@Transactional
	public void delete(Long accountId) {
		log.info("Entered delete()");
		log.debug("Param: accountId: {}", accountId);
		
		if (accountId == null) {
			throw new InvalidDataRequestException("accountId in Account object must not be null");
		}
		
		Optional<Account> optAccount = this.accountsRepo.findById(accountId);
		
		if (optAccount.isPresent()) {
			this.accountsRepo.delete(optAccount.get());
		}
		
		log.info("Exiting delete()");
	}
	
	/**
	 * Save the specified account. If accountId is specified treat as an update otherwise an insert
	 * 
	 * @param account - object to persist
	 * @return Account
	 */
	@Transactional
	public Account save(Account account) {
		log.info("Entered save()");
		log.debug("Param: account: {}", account);
		
		Account savedAccount = this.accountsRepo.save(account);
		
		log.debug("Saved account: {}", savedAccount);
		
		log.info("Exiting save()");
		return savedAccount;
	}
}
