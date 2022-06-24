/**
 * 
 */
package com.brenner.investments.data;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Transaction;

/**
 *
 * @author dbrenner
 * 
 */
@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

	@Query(nativeQuery = true, value = 
			"SELECT investment_id, SUM(trade_price * trade_quantity) as dividend FROM transactions WHERE transaction_type='Dividend' GROUP BY investment_id;")
	List<Transaction> findTotalDividendsForInvestments();
	
	@Query(nativeQuery = true, value = 
			"SELECT investment_id, SUM(trade_price * trade_quantity) as dividend FROM transactions "
				+ "WHERE transaction_type='Dividend' and investment_id = ?1 GROUP BY investment_id;")
	Transaction findTotalDividendsForInvestment(Long investmentId);
	
	List<Transaction> findAllByAccountAccountIdAndInvestmentInvestmentId(Account account, Investment investment);
	
	@Query(nativeQuery = true, value = 
			"SELECT t.*, i.*, a.* FROM transactions t, investments i, accounts a "
			+ "WHERE t.holding_id = ? AND t.transaction_type = 'Buy' AND a.account_id = t.account_account_id AND t.investment_investment_id = i.investment_id;")
	Transaction findBuyTransactionforHoldingId(Long holdingId);
	
	List<Transaction> findAllByAccountAccountId(Account account, Sort sort);
	
	List<Transaction> findAllByAccountAccountIdAndHoldingHoldingId(Account account, Holding holding);
	
	
	@Query(nativeQuery = true, value = 
			"SELECT t.*, i.*, a.* FROM transactions t, investments i, accounts a "
			+ "		WHERE t.transaction_type in ('Cash', 'Dividend') "
			+ "			AND i.investment_id = t.investment_investment_id AND a.account_id = t.account_account_id AND t.account_account_id = ?;" )
	List<Transaction> findAllByAccountAccountIdWithCash(Long accountId);
}
