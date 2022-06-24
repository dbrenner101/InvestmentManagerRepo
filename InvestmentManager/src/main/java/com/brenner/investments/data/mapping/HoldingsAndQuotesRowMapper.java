package com.brenner.investments.data.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.entities.Investment;

/**
 * Maps a result set to a Holdings and child Quote, Account and Investment objects
 * 
 * @author dbrenner
 *
 */
public class HoldingsAndQuotesRowMapper implements RowMapper<Holding> {
	
	private AccountRowMapper accountMapper = new AccountRowMapper();
	private InvestmentRowMapper invMapper = new InvestmentRowMapper();
	private QuoteRowMapper quoteMapper = new QuoteRowMapper();

	@Override
	public Holding mapRow(ResultSet rs, int rowNum) throws SQLException {
		Holding holding = new Holding();
		holding.setHoldingId(rs.getLong("holding_id"));
		holding.setPurchasePrice(rs.getFloat("purchase_price"));
		holding.setQuantity(rs.getFloat("quantity"));
		
		Account account = accountMapper.mapRow(rs, rowNum);
		holding.setAccount(account);
		
		Investment investment = invMapper.mapRow(rs, rowNum);
		holding.setInvestment(investment);
		
		holding.addQuote(quoteMapper.mapRow(rs, rowNum));
		
		return holding;
	}

}
