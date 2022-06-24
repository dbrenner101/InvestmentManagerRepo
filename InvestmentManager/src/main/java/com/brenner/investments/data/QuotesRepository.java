/**
 * 
 */
package com.brenner.investments.data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.brenner.investments.entities.Quote;

/**
 *
 * @author dbrenner
 * 
 */
public interface QuotesRepository extends JpaRepository<Quote, Long> {
	
	public Optional<Quote> findByInvestmentSymbolAndDate(String symbol, Date date);
	
	@Query(nativeQuery = true, value = "SELECT MAX(quote_date) as quote_date FROM quotes, investments "
			+ " WHERE quotes.investment_id = investments.investment_id and investments.symbol=?;")
	public String getMaxQuoteDateForInvestmentSymbol(String symbol);
	
	@Query(nativeQuery = true, value = "SELECT MAX(quote_date) as quote_date FROM quotes;")
	public String getMaxQuoteDate();
	
	public List<Quote> findAllByInvestmentSymbol(String symbol);
	
	public List<Quote> findAllByInvestmentInvestmentId(Long investmentId);

}
