package com.brenner.investments.data.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.brenner.investments.entities.ValueChangeInstance;

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
