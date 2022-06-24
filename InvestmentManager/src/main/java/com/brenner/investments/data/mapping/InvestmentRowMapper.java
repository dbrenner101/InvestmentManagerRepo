package com.brenner.investments.data.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.constants.InvestmentTypeEnum;

public class InvestmentRowMapper implements RowMapper<Investment> {

	@Override
	public Investment mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Investment inv = new Investment(rs.getLong("investment_id"));
		inv.setCompanyName(rs.getString("company_name"));
		inv.setExchange(rs.getString("exchange"));
		inv.setSector(rs.getString("sector"));
		inv.setSymbol(rs.getString("symbol"));
		inv.setInvestmentType(InvestmentTypeEnum.values()[rs.getInt("investment_type")]);
		
		return inv;
	}

}
