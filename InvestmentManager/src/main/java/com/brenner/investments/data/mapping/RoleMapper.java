package com.brenner.investments.data.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.brenner.investments.entities.Role;

public class RoleMapper implements RowMapper<Role> {

	@Override
	public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Role role = new Role();
		role.setRoleId(rs.getInt("role_id"));
		role.setRoleName(rs.getString("role_name"));
		
		return role;
	}

}
