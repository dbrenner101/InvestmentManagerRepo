package com.brenner.investments.data.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.brenner.investments.entities.User;

public class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		User u = new User();
		u.setUserId(rs.getInt("user_id"));
		u.setUsername(rs.getString("username"));
		u.setPassword(rs.getString("password"));
		
		return u;
	}

}
