package com.brenner.portfoliomgmt.api;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brenner.portfoliomgmt.data.entities.AccountDTO;
import com.brenner.portfoliomgmt.data.entities.TransactionDTO;
import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.domain.Transaction;
import com.brenner.portfoliomgmt.service.HoldingsService;

/**
 * Spring REST controller for {@link TransactionDTO}
 * @author dbrenner
 *
 */
@RestController
@RequestMapping("/api")
public class TransactionsRestController {
	
	private static final Logger log = LoggerFactory.getLogger(TransactionsRestController.class);
	
	@Autowired
	HoldingsService holdingsService;
	
	/**
	 * GET for retrieving transactions associated with an {@link AccountDTO}
	 * 
	 * @param accountId - account identifier
	 * @return JSON list of transactions
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@GetMapping(path="/transactions/account/{accountId}")
	public List<Transaction> getTransactionsForAccount(@PathVariable Long accountId) {
		log.info("Entered getTransactionsForAccount()");
		log.debug("Param: accountId: {}", accountId);
		
		Account account = new Account();
		account.setAccountId(accountId);
		
		List<Transaction> transactions = this.holdingsService.findTradesForAccount(account);
		
		log.debug("Retrieved {} transactions", transactions != null ? transactions.size() : 0);
		log.debug("Transactions: " + transactions);
		log.info("Exiting getTransactionsForAccount()");
		
		return transactions;
	}
	
	/**
	 * GET method to retrieve a specific transaction
	 * 
	 * @param transactionId transaction identifier identified as a path variable
	 * @return JSON representation of the {@link TransactionDTO}
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@GetMapping(path="/transactions/{transactionId}")
	public Transaction getTransaction(@PathVariable Long transactionId) {
		log.info("Entered getTransaction()");
		log.debug("Param: transactionId: {}", transactionId);
		
		Optional<Transaction> transaction = this.holdingsService.findTransaction(transactionId);
		log.debug("Retrieved transaction: {}", transaction);
		
		log.info("Exiting getTransaction()");
		return transaction.get();
	}
	
	/**
	 * POST to update a transaction
	 * 
	 * @param transactionStr JSON representation of the {@link TransactionDTO}
	 * @return JSON of the updated transaction
	 * @throws IOException - parsing errors
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@PostMapping(
			path="/transactions")
	public Transaction updateTransaction(@RequestBody Transaction t) throws IOException {
		log.info("Entered updateAccount()");
		log.debug("Param: transaction JSON: {}", t);
		
		Transaction transaction = this.holdingsService.saveTransaction(t);
		log.debug("Saved transaction: {}", transaction);
		
		log.info("Exiting updateAccount()");
		return transaction;
	}
	
	/**
	 * Converts Transaction JSON to Transaction object
	 * 
	 * @param transactionJson JSON representation of a Transaction
	 * @return Transaction object
	 * @throws IOException parsing errors
	 */
	/*private Transaction deserializeTransactionJson(String transactionJson) throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Transaction.class, new TransactionDeserializer());
		mapper.registerModule(module);
		
		Transaction transaction = mapper.readValue(transactionJson, Transaction.class);
		
		return transaction;
	}*/

}
