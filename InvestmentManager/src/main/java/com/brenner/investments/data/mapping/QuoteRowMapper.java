package com.brenner.investments.data.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Quote;

public class QuoteRowMapper implements RowMapper<Quote> {
	
	InvestmentRowMapper invRowMapper = new InvestmentRowMapper();

	@Override
	public Quote mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Quote quote = new Quote();
		quote.setQuoteId(rs.getLong("quote_id"));
		quote.setClose(rs.getFloat("price_at_close"));
		quote.setDate(rs.getDate("quote_date"));
		quote.setHigh(rs.getFloat("high"));
		quote.setLow(rs.getFloat("low"));
		quote.setOpen(rs.getFloat("price_at_open"));
		quote.setPriceChange(rs.getFloat("price_change"));
		quote.setVolume(rs.getInt("volume"));
		
		Investment i = invRowMapper.mapRow(rs, rowNum);
		quote.setInvestment(i);
		
		return quote;
	}

}
