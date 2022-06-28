package com.brenner.investments.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.investments.data.mapping.RoleMapper;
import com.brenner.investments.data.mapping.UserRowMapper;
import com.brenner.investments.data.props.UsersSqlProperties;
import com.brenner.investments.entities.Role;
import com.brenner.investments.entities.User;

/**
 * Data service for working with User data
 * @author dbrenner
 *
 */
@Component
public class UserDataService {

	private static final Logger log = LoggerFactory.getLogger(UserDataService.class);

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	UsersSqlProperties sqlProps;
	
	@Autowired
	UserRepository userRepo;
	
	/**
	 * Delete a specific user
	 * 
	 * @param user {@link User} to delete - must include userId
	 */
	@Transactional
	public void deleteUser(User user) {
		log.info("Entered deleteUser()");
		
		final String SQL = this.sqlProps.getDeleteUser();
		
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(SQL);
				ps.setInt(1, user.getUserId());
				return ps;
			}
		});
		
		this.removeUserRoles(user);
		log.info("Exiting deleteUser()");
	}
	
	/**
	 * Retrieve a user by primary key
	 * 
	 * @param userId {@link User} unique identifier
	 * @return The {@link User}
	 */
	public User findById(Integer userId) {
		
		final String SQL = this.sqlProps.getUserByUserId();
		
		User user = this.jdbcTemplate.queryForObject(SQL, new UserRowMapper(), userId);
		
		if (user != null) {
			user.setRoles(this.getRolesForUser(user));
		}
		
		return user;
	}
	
	/**
	 * Retrieve all Users
	 * 
	 * @return The {@link List} of {@link User}s
	 */
	public List<User> findAll() {
		
		final String SQL = this.sqlProps.getGetAllUsers();
		
		List<User> users = this.jdbcTemplate.query(SQL, new UserRowMapper());
		
		if (users != null) {
			Iterator<User> iter = users.iterator();
			while (iter.hasNext()) {
				User u = iter.next();
				u.setRoles(this.getRolesForUser(u));
			}
		}
		
		return users;
	}
	
	/**
	 * Retrieve a user by their username
	 * 
	 * @param username - Unique username
	 * @return The {@link User}
	 */
	public User findUserByUsername(String username) {
		
		final String SQL = this.sqlProps.getUserByUsername();
		
		User user = this.jdbcTemplate.queryForObject(SQL, new UserRowMapper(), username);
		
		if (user != null) {
			user.setRoles(getRolesForUser(user));
		}
		
		return user;
		
	}
	
	/**
	 * Retrieves the {@link Role} associated with a user
	 * 
	 * @param user The user 
	 * @return The List of roles
	 */
	public List<Role> getRolesForUser(User user) {
		
		final String SQL = this.sqlProps.getUserRoles();
		
		List<Role> roles = this.jdbcTemplate.query(SQL, new RoleMapper(), user.getUserId());
		
		return roles;
	}
	
	
	public User findUserByUsernameAndPassword(String username, String password) {
		
		final String SQL = this.sqlProps.getUserByUsernameAndPassword();
		
		User user = this.jdbcTemplate.queryForObject(SQL, new UserRowMapper(), username, password);
		
		if (user != null) {
			user.setRoles(getRolesForUser(user));
		}
		
		return user;
		
	}
	
	public User saveUser(User user) {
		
		return userRepo.save(user);
	}
	
	@Transactional
	public void assignRolesToUser(List<Role> roles, User user) {
		
		if (roles != null && ! roles.isEmpty()) {
			this.removeUserRoles(user);
			
			Iterator<Role> rolesIter = roles.iterator();
			while (rolesIter.hasNext()) {
				Role role = rolesIter.next();
				this.associateRoleToUser(role, user);
			}
		}
		
	}
	
	@Transactional
	private void associateRoleToUser(Role role, User user) {
		
		final String SQL = this.sqlProps.getAssociateRolesToUser();
		
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(SQL);
				ps.setInt(1, user.getUserId());
				ps.setInt(2, role.getRoleId());
				return ps;
			}
		});
	}
	
	@Transactional
	private void removeUserRoles(User user) {
		
		String SQL = this.sqlProps.getRemoveUserRoles();
		
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(SQL);
				ps.setInt(1, user.getUserId());
				return ps;
			}
		});
	}
	
}
