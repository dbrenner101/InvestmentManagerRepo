package com.brenner.portfoliomgmt.accounts.api;

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
import org.springframework.web.bind.annotation.RestController;

import com.brenner.portfoliomgmt.accounts.Account;
import com.brenner.portfoliomgmt.accounts.AccountDeserializer;
import com.brenner.portfoliomgmt.accounts.AccountsService;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Spring REST controller for {@link Account}
 * 
 * @author dbrenner
 *
 */
@RestController
public class AccountsRestController {

	private static final Logger log = LoggerFactory.getLogger(AccountsRestController.class);
	
	@Autowired
	AccountsService accountsService;
	
	/**
	 * GET access to all accounts
	 * @return a list of all {@link Account} objects
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@GetMapping(path="/restful/manageAccounts", produces={"application/JSON"})
	public List<Account> getAllAccounts() {
		log.info("Entered getAllAccounts()");
		
		List<Account> accounts = this.accountsService.getAccountsAndCash();
		log.debug("Returning {} accounts", accounts != null ? accounts.size() : 0);
		
		log.info("Exiting getAllAccounts()");
		return accounts;
	}
	
	/**
	 * GET access to a specific {@link Account}
	 * 
	 * @param accountId - the unique identifier picked up as a path variable
	 * @return the {@link Account} associated with the identifier.
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@GetMapping("/restful/manageAccounts/{accountId}")
	public Account getAccount(@PathVariable Long accountId) {
		log.info("Entered getAccount()");
		log.debug("Param: accountId: {}", accountId);
		
		Optional<Account> optAccount = this.accountsService.getAccountAndCash(accountId);
		
		if (!optAccount.isPresent()) {
			throw new NotFoundException("Account with id " + accountId + " does not exist.");
		}
		
		Account account = optAccount.get();
		
		log.debug("Returning account: {}", account);
		
		log.info("Exiting getAccount()");
		return account;
	}
	
	/**
	 * PUT for updating an {@link Account} instance
	 * 
	 * @param accountStr - JSON representation of the {@link Account} object
	 * @return the updated {@link Account} 
	 * @throws JsonParseException - error deserializing the account JSON
	 * @throws JsonMappingException - error deserializing the account JSON
	 * @throws IOException - general parsing error
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@PutMapping(
			path="/restful/manageAccounts", 
			consumes= {MediaType.APPLICATION_JSON_VALUE}, 
			produces= {MediaType.APPLICATION_JSON_VALUE})
	public Account updateAccount(@RequestBody String accountStr) throws JsonParseException, JsonMappingException, IOException {
		log.info("Entered updateAccount()");
		log.debug("Param: account JSON: {}", accountStr);
		
		Account account = this.deserializeAccountJson(accountStr);
		account = this.accountsService.save(account);
		log.debug("Saved account: {}", account);
		
		log.info("Exiting updateAccount()");
		return account;
	}
	
	/**
	 * POST method for adding a new {@link Account}
	 * 
	 * @param accountStr - JSON representation of an account
	 * @return the updated account
	 * @throws IOException - error in deserialization
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@PostMapping(
			path="/restful/manageAccounts", 
			consumes= {MediaType.APPLICATION_JSON_VALUE}, 
			produces= {MediaType.APPLICATION_JSON_VALUE})
	public Account addAccount(@RequestBody String accountStr) throws IOException {
		log.info("Entered addAccount()");
		log.debug("Param: account JSON: {}", accountStr);
		
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Account.class, new AccountDeserializer());
		mapper.registerModule(module);
		
		Account account = this.deserializeAccountJson(accountStr);
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
			path="/restful/manageAccounts/{accountId}", 
			consumes= {MediaType.APPLICATION_JSON_VALUE}, 
			produces= {MediaType.APPLICATION_JSON_VALUE})
	public void deleteAccount(@PathVariable Long accountId) {
		log.info("Entered deleteAccount()");
		log.debug("Param: account JSON: {}", accountId);
		
		Account account = new Account(accountId);
		this.accountsService.deleteAccount(new Account(accountId));
		log.debug("Deleted account: {}", account);
		
		log.info("Exiting deleteAccount()");
	}
	
	/**
	 * Deserializes account JSON to an {@link Account}
	 *  
	 * @param accountJson - JSON representing the Account instance
	 * @return the {@link Account} 
	 * @throws IOException - error in deserialization
	 */
	private Account deserializeAccountJson(String accountJson) throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Account.class, new AccountDeserializer());
		mapper.registerModule(module);
		
		Account account = mapper.readValue(accountJson, Account.class);
		
		return account;
	}
}
