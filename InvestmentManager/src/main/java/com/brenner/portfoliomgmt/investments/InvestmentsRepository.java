/**
 * 
 */
package com.brenner.portfoliomgmt.investments;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author dbrenner
 * 
 */
@Repository
public interface InvestmentsRepository extends JpaRepository<Investment, Long> {
	
	public Optional<Investment> findBySymbol(String symbol);
	
	@Query(nativeQuery = true, value = "SELECT * FROM investments WHERE symbol = ?%")
	public List<Investment> findLikeSymbol(String symbol);
	
	@Query(nativeQuery = true, value = "SELECT DISTINCT(i.symbol), i.investment_id, i.company_name, i.exchange, i.sector, i.investment_type, MAX(q.quote_date) as quote_date\n"
			+ "			FROM investments i RIGHT JOIN holdings h ON i.investment_id = h.investment_investment_id \n"
			+ "			LEFT JOIN quotes q on q.investment_id = i.investment_id WHERE h.quantity > 0\n"
			+ "			GROUP BY i.symbol, i.investment_id, i.company_name, i.exchange, i.sector, i.investment_type\n"
			+ "			ORDER by i.symbol")
	List<Investment> findInvestmentsForHoldingsOrderedBySymbol();
	
	@Query(nativeQuery = true, value="SELECT * FROM investments WHERE investment_id IN (\n"
			+ "                SELECT investment_id FROM holdings WHERE quantity > 0) ORDER BY symbol")
	List<Investment> getInvestmentsAllForHoldingsWithQuotesOrderedBySymbol();
	
	@Query(nativeQuery = true, value="SELECT DISTINCT(i.symbol), i.investment_id \n"
			+ "            FROM investments i, holdings h \n"
			+ "            WHERE i.investment_id = h.investment_id ORDER BY i.symbol;")
	List<Investment> findUniqueInvestmentsForHoldingsOrderBySymbol();

}
