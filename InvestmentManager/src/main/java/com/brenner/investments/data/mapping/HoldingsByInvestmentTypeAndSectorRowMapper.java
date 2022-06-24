package com.brenner.investments.data.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.brenner.investments.entities.constants.InvestmentTypeEnum;
import com.brenner.investments.pojo.HoldingsReport;

/**
 * Maps 
 * @author dbrenner
 *
 */
public class HoldingsByInvestmentTypeAndSectorRowMapper implements RowMapper<HoldingsReport> {

	@Override
	public HoldingsReport mapRow(ResultSet rs, int rowNum) throws SQLException {
		HoldingsReport report = new HoldingsReport();
		report.setValueAtPurchase(rs.getFloat("value_at_purchase" ));
		report.setInvestmentType(InvestmentTypeEnum.valueOf(rs.getInt("investment_type")));
		report.setSector(rs.getString("sector"));
		return report;
	}

}
