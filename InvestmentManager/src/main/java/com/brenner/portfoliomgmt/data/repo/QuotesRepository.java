/**
 * 
 */
package com.brenner.portfoliomgmt.data.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.brenner.portfoliomgmt.data.entities.QuoteDTO;

/**
 *
 * @author dbrenner
 * 
 */
public interface QuotesRepository extends JpaRepository<QuoteDTO, Long> {
	
	public Optional<QuoteDTO> findByInvestmentSymbolAndDate(String symbol, Date date);
	
	@Query(nativeQuery = true, value = "SELECT MAX(quote_date) as quote_date FROM quotes, investments "
			+ " WHERE quotes.investment_id = investments.investment_id and investments.symbol=?;")
	public Optional<String> getMaxQuoteDateForInvestmentSymbol(String symbol);
	
	@Query(nativeQuery = true, value = "SELECT MAX(quote_date) as quote_date FROM quotes;")
	public String getMaxQuoteDate();
	
	public List<QuoteDTO> findAllByInvestmentSymbol(String symbol);
	
	public List<QuoteDTO> findAllByInvestmentInvestmentId(Long investmentId);

	@Query(nativeQuery = true, value = "select * from quotes q where investment_id = ? and q.quote_date = (select max(quote_date) from quotes where investment_id = ?);")
	public List<QuoteDTO> findMostRecentQuotesForInvestmentId(Long investmentId, Long investmentId2);
}
