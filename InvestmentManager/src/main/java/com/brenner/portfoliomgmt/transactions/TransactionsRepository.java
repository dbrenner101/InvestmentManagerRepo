/**
 * 
 */
package com.brenner.portfoliomgmt.transactions;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.brenner.portfoliomgmt.accounts.Account;
import com.brenner.portfoliomgmt.holdings.Holding;
import com.brenner.portfoliomgmt.investments.Investment;

/**
 *
 * @author dbrenner
 * 
 */
@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {
	@Modifying
	@Query(nativeQuery = true, value="DELETE FROM transactions WHERE holding_holding_id=?;")
	void deleteTransactionForHolding(Long holdingId);

	@Query(nativeQuery = true, value = 
			"SELECT investment_investment_id, SUM(trade_price * trade_quantity) as dividend FROM transactions WHERE transaction_type='Dividend' GROUP BY investment_investment_id;")
	List<Transaction> findTotalDividendsForInvestments();
	
	@Query(nativeQuery = true, value = 
			"SELECT investment_id, SUM(trade_price * trade_quantity) as dividend FROM transactions "
				+ "WHERE transaction_type='Dividend' and investment_id = ?1 GROUP BY investment_id;")
	Optional<Transaction> findTotalDividendsForInvestment(Long investmentId);
	
	List<Transaction> findAllByAccountAndInvestment(Account account, Investment investment);
	
	
//	"SELECT t.*, i.*, a.* FROM transactions t, investments i, accounts a "
//	+ "WHERE t.holding_holding_id = ? AND t.transaction_type = 'Buy' AND a.account_id = t.account_account_id AND t.investment_investment_id = i.investment_id;")
	@Query(nativeQuery = true, value = 
			"SELECT t.* FROM transactions t WHERE t.holding_holding_id = ? AND t.transaction_type = 'Buy';")
	Optional<Transaction> findBuyTransactionforHoldingId(Long holdingId);
	
	@Query(nativeQuery = true, value = "SELECT t.transaction_date FROM transactions t WHERE t.holding_holding_id = ? AND t.transaction_type IN ('Buy', 'Revinvest_Dividend');")
	Optional<Date> findBuyDateForHolding(Long holdingId);
	
	List<Transaction> findAllByAccountAccountId(Account account);
	
	List<Transaction> findAllByAccountAndHolding(Account account, Holding holding);
	
	@Query(nativeQuery = true, value = "SELECT t.*, i.*, a.* FROM transactions t, investments i, accounts a "
			+ "WHERE t.account_account_id = ? AND t.investment_investment_id = i.investment_id AND a.account_id = t.account_account_id "
			+ "ORDER BY t.transaction_date DESC;")
	List<Transaction> findAllByAccountAccountIdOrderByTransactionDateDesc(Account account);
	
	@Query(nativeQuery = true, value = "SELECT t.*, i.* FROM transactions t JOIN investments i on i.investment_id = t.investment_investment_id "
			+ "ORDER BY i.symbol;")
	List<Transaction> findAllByAccountAccountIdOrderByInvestmentSymbol(Account account);
	
	@Query(nativeQuery = true, value = 
			"SELECT t.*, i.*, a.* FROM transactions t, investments i, accounts a "
			+ "		WHERE t.transaction_type in ('Cash', 'Dividend') "
			+ "			AND i.investment_id = t.investment_investment_id AND a.account_id = t.account_account_id AND t.account_account_id = ?;" )
	List<Transaction> findAllByAccountAccountIdWithCash(Long accountId);
	
	List<Transaction> findByHoldingHoldingId(Long holdingId);
}
