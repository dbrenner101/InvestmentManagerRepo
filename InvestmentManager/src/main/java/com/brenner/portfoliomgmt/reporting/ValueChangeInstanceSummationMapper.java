package com.brenner.portfoliomgmt.reporting;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ValueChangeInstanceSummationMapper implements RowMapper<ValueChangeInstance> {

	@Override
	public ValueChangeInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		ValueChangeInstance vc = new ValueChangeInstance();
		vc.setMarketValue(rs.getFloat("market_value"));
		vc.setPurchaseValue(rs.getFloat("purchase_value"));
		vc.setQuoteDate(rs.getDate("quote_date"));
		
		return vc;
	}

}
