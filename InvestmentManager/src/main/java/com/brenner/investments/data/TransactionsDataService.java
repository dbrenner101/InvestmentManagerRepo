package com.brenner.investments.data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.investments.data.mapping.DividendTransactionRowMapper;
import com.brenner.investments.data.mapping.TransactionRowMapper;
import com.brenner.investments.data.props.TransactionsSqlProperties;
import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Transaction;

/**
 * Data service for working with {@link Transaction}s
 * 
 * @author dbrenner
 *
 */
@Component
public class TransactionsDataService {
	
	private static final Logger log = LoggerFactory.getLogger(TransactionsDataService.class);

	@Autowired
	TransactionsRepository transactionRepo;
	
	
	/**
	 * Retrieves a summation of dividends for each investment
	 *  
	 * @return The {@link List} of summarized dividend {@link Transaction}s
	 */
	public List<Transaction> findTotalDividendsForInvestments() {
		log.info("Entered findTotalDividendsForInvestments()");
		
		List<Transaction> transactions = this.transactionRepo.findTotalDividendsForInvestments();
		
		log.debug("Returning {} transactions", transactions != null ? transactions.size() : 0);
		
		log.info("Exiting findTotalDividendsForInvestments()");
		return transactions;
	}
	
	/**
	 * Summarizes the total dividends for a specific investment
	 * 
	 * @param investmentId - Investment unique identifier
	 * @return The {@link Transaction} with summarized dividend value for the specified investment
	 */
	public Transaction findTotalDividendsForInvestment(Long investmentId) {
		log.info("Entered findTotalDividendsForInvestment()");
		log.debug("Param: investmentId: {}", investmentId);
		
		Transaction t = this.transactionRepo.findTotalDividendsForInvestment(investmentId);
		
		log.debug("Retrieved transaction: {}", t);
		
		log.info("Exiting findTotalDividendsForInvestment()");
		return t;
	}
	
	/**
	 * Retrieves a specific Transaction
	 * 
	 * @param transactionId Transaction unique identifier
	 * @return The {@link Transaction} defined by the key
	 */
	public Transaction findById(Long transactionId) {
		log.info("Entered findById()");
		log.debug("Param: transactionId: {}", transactionId);
		
		Optional<Transaction> optTrans = this.transactionRepo.findById(transactionId);
		
		Transaction t = null;
		
		if (optTrans.isPresent()) {
			t = optTrans.get();
		}
		
		log.debug("Retrieved transaction: {}", t);
		
		log.info("Exiting findById()");
		return t;
	}
	
	/**
	 * Retrieves all Transactions for the account and investment combination
	 * 
	 * @param accountId {@link Account} unique identifier
	 * @param investmentId {@link Investment} unique identifier
	 * @return All the {@link Transaction}s associated with the investment and account
	 */
	public List<Transaction> findAllByAccountAccountIdAndInvestmentInvestmentId(Account account, Investment investment) {
		log.info("Entered findAllByAccountAccountIdAndInvestmentInvestmentId()");
		log.debug("Params: accountId: {}; investmentId: {}", account, investment);
		
		List<Transaction> transactions = this.transactionRepo.findAllByAccountAccountIdAndInvestmentInvestmentId(account, investment);
		
		log.debug("Returning {} transactions", transactions != null ? transactions.size() : 0);
		
		log.info("Exiting findAllByAccountAccountIdAndInvestmentInvestmentId()");
		return transactions;
	}
	
	/**
	 * Retrieves the Buy {@link Transaction} associated with the specified {@link Holding} 
	 * 
	 * @param holdingId {@link Holding} unique identifier
	 * @return The Buy Transaction associated with the Holding
	 */
	public Transaction findBuyTransactionforHoldingId(Long holdingId) {
		log.info("Entered findBuyTransactionforHoldingId()");
		log.debug("Params: holdingId: {}", holdingId);
		
		Transaction transaction = this.transactionRepo.findBuyTransactionforHoldingId(holdingId);
		
		log.debug("Returning transaction: {}", transaction);
		
		log.info("Exiting findBuyTransactionforHoldingId()");
		return transaction;
	}
	
	/**
	 * Retrieves all Transactions associated with the supplied account and ordered by Transaction Date
	 * 
	 * @param accountId {@link Account} unique identifier
	 * @return The {@link List} of Transactions associated with the Account
	 */
	public List<Transaction> findAllByAccountAccountIdOrderByTransactionDateDesc(Account account) {
		log.info("Entered findAllByAccountAccountIdOrderByTransactionDateDesc()");
		log.debug("Param: accountId: {}", account);
		
		List<Transaction> transactions = this.transactionRepo.findAllByAccountAccountId(account, Sort.by(Sort.Direction.DESC, "transactionDate"));
		
		log.debug("Returning {} transactions", transactions != null ? transactions.size() : 0);
		
		log.info("Exiting findAllByAccountAccountIdOrderByTransactionDateDesc()");
		return transactions;
	}
	
	/**
	 * Retrieves all Transactions associated with an {@link Account} and orders them by the Investment symbol
	 * 
	 * @param accountId {@link Account} unique identifier
	 * @return The List of Transactions
	 */
	public List<Transaction> findAllByAccountAccountIdOrderByInvestmentSymbol(Account account) {
		log.info("Entered findAllByAccountAccountIdOrderByInvestmentSymbol()");
		log.debug("Param: accountId: {}", account);
		
		List<Transaction> transactions = this.transactionRepo.findAllByAccountAccountId(account, Sort.by(Sort.Direction.DESC, "investment.symbol"));
		
		log.debug("Returning {} transactions", transactions != null ? transactions.size() : 0);
		
		log.info("Exiting findAllByAccountAccountIdOrderByInvestmentSymbol()");
		return transactions;
	}
	
	/**
	 * Retrieve all transactions for the {@link Account}/{@link Holding} combination
	 * 
	 * @param accountId {@link Account} unique identifer
	 * @param holdingId {@link Holding} unique identifier
	 * @return The {@link List} of Transactions
	 */
	public List<Transaction> findAllByAccountAccountIdAndHoldingHoldingId(Account account, Holding holding) {
		log.info("Entered findAllByAccountAccountIdAndHoldingId()");
		log.debug("Params: accountId: {}); holdingId: {}", account, holding);
		
		List<Transaction> transactions = this.transactionRepo.findAllByAccountAccountIdAndHoldingHoldingId(account, holding);
		
		log.debug("Returning {} transactions", transactions != null ? transactions.size() : 0);
		
		log.info("Exiting findAllByAccountAccountIdAndHoldingId()");
		return transactions;
	}
	
	/**
	 * Retrieve all Transactions for the {@link Account} along with the current cash value
	 * 
	 * @param accountId {@link Account} unique identifier
	 * @return The {@link List} of transactions including a cash summation
	 */
	public List<Transaction> findAllByAccountAccountIdWithCash(Long accountId) {
		log.info("Entered findAllByAccountAccountIdWithCash()");
		log.debug("Param: accountId: {}", accountId);
		
		List<Transaction> transactions = this.transactionRepo.findAllByAccountAccountIdWithCash(accountId);
		
		log.debug("Returning {} transactions", transactions != null ? transactions.size() : 0);
		
		log.info("Exiting findAllByAccountAccountIdWithCash()");
		return transactions;
	}
	
	/**
	 * Entry point to save a list of transactions. Each is saved individually rather than batch.
	 * 
	 * @param transactions {@link List} of transactions to save
	 */
	@Transactional
	public void saveAll(List<Transaction> transactions) {
		log.info("Entered saveAll()");
		
		Iterator<Transaction> iter = transactions.iterator();
		while (iter.hasNext()) {
			this.save(iter.next());
		}
		
		log.info("Exiting saveAll()");
	}
	
	/**
	 * Entry point to add or update a transaction. A {@link Transaction} object with a null transactionId 
	 * will be treated as a new {@link Transaction}
	 * 
	 * @param transaction {@link Transaction} object to persist
	 * @return The updated transaction along with key
	 */
	@Transactional
	public Transaction save(Transaction transaction) {
		log.info("Entered saveTransaction()");
		log.debug("Param: transaction: {}", transaction);
		
		Transaction t = this.transactionRepo.save(transaction);
		
		log.debug("Saved transaction: {}", t);
		
		log.info("Exiting saveTransaction()");
		return t;
	}
	
	/**
	 * Delete a transaction
	 * 
	 * @param t Transaction data to delete - must include transactionId
	 */
	@Transactional
	public void delete(Transaction t) {
		
		log.info("Entered deleteTransaction()");
		log.debug("Param: transaction: {}", t);
		
		this.transactionRepo.delete(t);
		
		log.info("Exiting deleteTransaction()");
	}

}
