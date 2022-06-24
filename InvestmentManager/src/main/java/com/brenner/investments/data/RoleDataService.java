package com.brenner.investments.data;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.brenner.investments.data.mapping.RoleMapper;
import com.brenner.investments.data.props.RolesSqlProperties;
import com.brenner.investments.entities.Role;

/**
 * Data service to manage {@link Role}s
 * @author dbrenner
 *
 */
@Component
public class RoleDataService {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	RolesSqlProperties sqlProps;
	
	/**
	 * Retrieves all {@link Role}s
	 * 
	 * @return The {@link List} of {@link Role}s 
	 */
	public List<Role> findAll() {
		
		final String SQL = this.sqlProps.getAllRoles();
		
		List<Role> roles = this.jdbcTemplate.query(SQL,  new RoleMapper());
		
		return roles;
	}
	
	/**
	 * Retrieves a specific {@link Role}
	 * 
	 * @param roleId - Role unique identifier
	 * @return The Role requested or null if it can't be found
	 */
	public Role findById(Integer roleId) {
		
		final String SQL = this.sqlProps.getRoleById();
		
		Role role = this.jdbcTemplate.queryForObject(SQL, new RoleMapper(), roleId);
		
		return role;
	}
}
