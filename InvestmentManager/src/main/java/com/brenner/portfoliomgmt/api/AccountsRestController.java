package com.brenner.portfoliomgmt.api;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.service.AccountsService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Spring REST controller for {@link AccountDTO}
 * 
 * @author dbrenner
 *
 */
@RestController
@RequestMapping("/api")
public class AccountsRestController {

	private static final Logger log = LoggerFactory.getLogger(AccountsRestController.class);
	
	@Autowired
	AccountsService accountsService;
	
	/**
	 * GET access to all accounts
	 * @return a list of all {@link AccountDTO} objects
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@GetMapping(path="/account", produces={"application/JSON"})
	public List<Account> getAllAccounts() {
		log.info("Entered getAllAccounts()");
		
		List<Account> accounts = this.accountsService.findAllAccounts("accountName");
		
		log.debug("Returning {} accounts", accounts != null ? accounts.size() : 0);
		log.info("Exiting getAllAccounts()");
		
		return accounts;
	}
	
	/**
	 * GET access to a specific {@link AccountDTO}
	 * 
	 * @param accountId - the unique identifier picked up as a path variable
	 * @return the {@link AccountDTO} associated with the identifier.
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@GetMapping("/account/{accountId}")
	public Account getAccount(@PathVariable Long accountId) {
		log.info("Entered getAccount()");
		log.debug("Param: accountId: {}", accountId);
		
		Optional<Account> optAccount = this.accountsService.findAccountByAccountId(accountId);
		
		if (!optAccount.isPresent()) {
			throw new NotFoundException("Account with id " + accountId + " does not exist.");
		}
		
		Account account = optAccount.get();
		
		log.debug("Returning account: {}", account);
		log.info("Exiting getAccount()");
		
		return account;
	}
	
	/**
	 * PUT for updating an {@link AccountDTO} instance
	 * 
	 * @param accountStr - JSON representation of the {@link AccountDTO} object
	 * @return the updated {@link AccountDTO} 
	 * @throws JsonParseException - error deserializing the account JSON
	 * @throws JsonMappingException - error deserializing the account JSON
	 * @throws IOException - general parsing error
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@PutMapping(
			path="/account/{accountId}", 
			consumes= {MediaType.APPLICATION_JSON_VALUE}, 
			produces= {MediaType.APPLICATION_JSON_VALUE})
	public Account updateAccount(@PathVariable Long accountId, @RequestBody Account account) throws JsonParseException, JsonMappingException, IOException {
		log.info("Entered updateAccount()");
		log.debug("Param: account JSON: {}", accountId);
		
		account = this.accountsService.save(account);
		
		log.debug("Saved account: {}", account);
		log.info("Exiting updateAccount()");
		
		return account;
	}
	
	/**
	 * POST method for adding a new {@link AccountDTO}
	 * 
	 * @param accountStr - JSON representation of an account
	 * @return the updated account
	 * @throws IOException - error in deserialization
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@PostMapping(
			path="/account", 
			consumes= {MediaType.APPLICATION_JSON_VALUE}, 
			produces= {MediaType.APPLICATION_JSON_VALUE})
	public Account addAccount(@RequestBody Account account) throws IOException {
		log.info("Entered addAccount()");
		log.debug("Param: account JSON: {}", account);
		
		account = this.accountsService.save(account);
		
		log.debug("Saved account: {}", account);
		log.info("Exiting addAccount()");
		
		return account;
	}
	
	/**
	 * DELETE for deleting an account
	 * 
	 * @param accountId - account identifier for the account to delete
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@DeleteMapping(
			path="/account/{accountId}", 
			consumes= {MediaType.APPLICATION_JSON_VALUE}, 
			produces= {MediaType.APPLICATION_JSON_VALUE})
	public void deleteAccount(@PathVariable Long accountId) {
		log.info("Entered deleteAccount()");
		log.debug("Param: account JSON: {}", accountId);
		
		Account account = new Account();
		account.setAccountId(accountId);
		this.accountsService.deleteAccount(account);
		
		log.debug("Deleted account: {}", account);
		log.info("Exiting deleteAccount()");
	}
}
