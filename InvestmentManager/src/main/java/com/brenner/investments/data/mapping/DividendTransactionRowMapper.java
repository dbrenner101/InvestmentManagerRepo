package com.brenner.investments.data.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Transaction;

/**
 * Maps result set to a Transaction object
 * 
 * @author dbrenner
 *
 */
public class DividendTransactionRowMapper implements RowMapper<Transaction> {

	@Override
	public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Transaction t = new Transaction();
		t.setDividend(rs.getFloat("dividend"));
		Investment i = new Investment(rs.getLong("investment_id"));
		t.setInvestment(i);
		
		return t;
	}

}
