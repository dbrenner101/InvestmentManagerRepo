package com.brenner.investments.data.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.brenner.investments.entities.Account;

/**
 * Maps a result set to an {@link Account} object
 * 
 * @author dbrenner
 *
 */
public class AccountRowMapper implements RowMapper<Account> {

	@Override
	public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
		Account account = new Account();
		account.setAccountId(rs.getLong("account_id"));
		account.setAccountName(rs.getString("account_name"));
		account.setAccountNumber(rs.getString("account_number"));
		account.setAccountType(rs.getString("account_type"));
		account.setCompany(rs.getString("company"));
		account.setOwner(rs.getString("account_owner"));
		return account;
	}

}
