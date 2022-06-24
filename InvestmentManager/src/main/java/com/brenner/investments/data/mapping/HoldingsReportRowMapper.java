package com.brenner.investments.data.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.brenner.investments.pojo.HoldingsReport;

public class HoldingsReportRowMapper implements RowMapper<HoldingsReport> {

	@Override
	public HoldingsReport mapRow(ResultSet rs, int rowNum) throws SQLException {
		HoldingsReport report = new HoldingsReport();
		report.setChangeInValue(rs.getFloat("change_in_value"));
		report.setCompanyName(rs.getString("company_name"));
		report.setMarketValue(rs.getFloat("market_value"));
		report.setPriceAtClose(rs.getFloat("price_at_close"));
		report.setSymbol(rs.getString("symbol"));
		report.setValueAtPurchase(rs.getFloat("value_at_purchase"));
		report.setInvestmentId(rs.getLong("investment_id"));
		
		return report;
	}

}
