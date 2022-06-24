/**
 * 
 */
package com.brenner.investments.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.brenner.investments.entities.Holding;

/**
 *
 * @author dbrenner
 * 
 */
@Repository
public interface HoldingsRepository extends JpaRepository<Holding, Long> {

	List<Holding> findHoldingsByInvestmentInvestmentId(Long investmentId);
	
	@Query(nativeQuery = true, value="SELECT SUM(h.purchase_price * h.quantity) as value_at_purchase, i.symbol, i.investment_id, \n"
			+ "                i.company_name, q.price_at_close, SUM(h.quantity * q.price_at_close) as market_value, \n"
			+ "                SUM((h.quantity * q.price_at_close) - (h.purchase_price * h.quantity)) as change_in_value\n"
			+ "            FROM holdings h\n"
			+ "            LEFT JOIN investments i on i.investment_id = h.investment_investment_id\n"
			+ "            LEFT OUTER JOIN quotes q on q.investment_id = i.investment_id and q.quote_date = "
			+ "					(SELECT MAX(quote_date) FROM quotes)\n"
			+ "            WHERE h.quantity > 0\n"
			+ "            GROUP BY h.purchase_price, h.quantity, i.symbol, i.company_name, q.price_at_close, i.investment_id \n"
			+ "            ORDER BY market_value ?;")
	List<Holding> getAllHoldingsOrderedByMarketValue(String sortOrder);
	
	@Query(nativeQuery = true, value = "SELECT SUM(h.purchase_price * h.quantity) as value_at_purchase, i.symbol, i.investment_id, \n"
			+ " i.company_name, q.price_at_close, SUM(h.quantity * q.price_at_close) as market_value, \n"
			+ " SUM((h.quantity * q.price_at_close) - (h.purchase_price * h.quantity)) as change_in_value \n"
			+ " FROM holdings h \n"
			+ " LEFT JOIN investments i on i.investment_id = h.investment_investment_id \n"
			+ " LEFT OUTER JOIN quotes q on q.investment_id = i.investment_id and q.quote_date = "
			+ "(SELECT MAX(quote_date) FROM quotes)\n"
			+ " WHERE h.quantity > 0 \n"
			+ " GROUP BY h.purchase_price, h.quantity, i.symbol, i.company_name, q.price_at_close, i.investment_id \n"
			+ " ORDER BY change_in_value DESC;")
	List<Holding> getAllHoldingsOrderedByChangeInValue();
	
	List<Holding> findByAccountAccountIdAndInvestmentInvestmentId(Long accountId, Long investmentId);
	
	List<Holding> findByAccountAccountId(Long accountId);
	
	@Query(nativeQuery = true, value = "SELECT h.*, a.*, t.*, i.*, q.* "
			+ " FROM holdings h, accounts a, transactions t, investments i, quotes q "
			+ " WHERE "
			+ "  h.account_account_id = a.account_id "
			+ "  AND t.holding_holding_id = h.holding_id "
			+ "  AND i.investment_id = h.investment_investment_id "
			+ "  AND q.investment_id = i.investment_id "
			+ "  AND q.quote_date = (SELECT MAX(quote_date) FROM quotes) "
			+ "  AND t.transaction_type in ('Buy','Transfer') "
			+ "  AND h.account_account_id = ? "
			+ "  AND h.quantity > 0;")
	List<Holding> findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol(Long accountId);
}
