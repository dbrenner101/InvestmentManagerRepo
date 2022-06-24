package com.brenner.investments.data.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.brenner.investments.entities.Transaction;
import com.brenner.investments.entities.constants.TransactionTypeEnum;

public class TransactionRowMapper implements RowMapper<Transaction> {
	
	InvestmentRowMapper invMapper = new InvestmentRowMapper();
	AccountRowMapper accountMapper = new AccountRowMapper();

	@Override
	public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
		Transaction trans = new Transaction();
		trans.setTransactionId(rs.getLong("transaction_id"));
		trans.setAssociatedCashTransactionId(rs.getLong("cash_transaction_id"));
		if (rs.wasNull()) {
			trans.setAssociatedCashTransactionId(null);
		}
		trans.setHoldingId(rs.getLong("holding_id"));
		if (rs.wasNull()) {
			trans.setHoldingId(null);
		}
		trans.setTradePrice(rs.getFloat("trade_price"));
		trans.setTradeQuantity(rs.getFloat("trade_quantity"));
		trans.setTransactionDate(rs.getDate("transaction_date"));
		trans.setTransactionType(TransactionTypeEnum.valueOf(rs.getString("transaction_type")));
		
		trans.setInvestment(invMapper.mapRow(rs, rowNum));
		
		trans.setAccount(accountMapper.mapRow(rs, rowNum));
		
		return trans;
	}

}
