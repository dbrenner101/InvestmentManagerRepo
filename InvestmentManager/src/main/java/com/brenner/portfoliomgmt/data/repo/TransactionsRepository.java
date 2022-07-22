/**
 * 
 */
package com.brenner.portfoliomgmt.data.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.brenner.portfoliomgmt.data.entities.AccountDTO;
import com.brenner.portfoliomgmt.data.entities.HoldingDTO;
import com.brenner.portfoliomgmt.data.entities.TransactionDTO;

/**
 *
 * @author dbrenner
 * 
 */
@Repository
public interface TransactionsRepository extends JpaRepository<TransactionDTO, Long> {
	@Modifying
	@Query(nativeQuery = true, value="DELETE FROM transactions WHERE holding_holding_id=?;")
	void deleteTransactionForHolding(Long holdingId);

	@Query(nativeQuery = true, value = 
			"SELECT holding_holding_id, SUM(trade_price * trade_quantity) as dividend FROM transactions WHERE transaction_type='Dividend' GROUP BY holding_holding_id;")
	List<TransactionDTO> findTotalDividendsForHoldings();
	
	@Query(nativeQuery = true, value = 
			"SELECT holding_holding_id, SUM(trade_price * trade_quantity) as dividend FROM transactions "
				+ "WHERE transaction_type='Dividend' and holding_holding_id = ?1 GROUP BY holding_holding_id;")
	Optional<TransactionDTO> findTotalDividendsForHolding(Long holdingId);
	
	
//	"SELECT t.*, i.*, a.* FROM transactions t, investments i, accounts a "
//	+ "WHERE t.holding_holding_id = ? AND t.transaction_type = 'Buy' AND a.account_id = t.account_account_id AND t.investment_investment_id = i.investment_id;")
	@Query(nativeQuery = true, value = 
			"SELECT t.* FROM transactions t WHERE t.holding_holding_id = ? AND t.transaction_type = 'Buy';")
	Optional<TransactionDTO> findBuyTransactionforHoldingId(Long holdingId);
	
	@Query(nativeQuery = true, value = "SELECT t.transaction_date FROM transactions t WHERE t.holding_holding_id = ? AND t.transaction_type IN ('Buy', 'Revinvest_Dividend');")
	Optional<Date> findBuyDateForHolding(Long holdingId);
	
	List<TransactionDTO> findAllByAccountAccountId(AccountDTO account);
	
	List<TransactionDTO> findAllByAccountAndHolding(AccountDTO account, HoldingDTO holding);
	
	@Query(nativeQuery = true, value = "SELECT t.*, a.* FROM transactions t, accounts a "
			+ "WHERE t.account_account_id = ? AND a.account_id = t.account_account_id "
			+ "ORDER BY t.transaction_date DESC;")
	List<TransactionDTO> findAllByAccountAccountIdOrderByTransactionDateDesc(AccountDTO account);
	
	@Query(nativeQuery = true, value = "SELECT t.*, i.* FROM transactions t JOIN investments i on i.investment_id = t.investment_investment_id "
			+ "ORDER BY i.symbol;")
	List<TransactionDTO> findAllByAccountAccountIdOrderByInvestmentSymbol(AccountDTO account);
	
	@Query(nativeQuery = true, value = 
			"SELECT t.*, a.* FROM transactions t, accounts a "
			+ "		WHERE t.transaction_type in ('Cash', 'Dividend') "
			+ "			AND a.account_id = t.account_account_id AND t.account_account_id = ?;" )
	List<TransactionDTO> findAllByAccountAccountIdWithCash(Long accountId);
	
	List<TransactionDTO> findByHoldingHoldingId(Long holdingId);
}
