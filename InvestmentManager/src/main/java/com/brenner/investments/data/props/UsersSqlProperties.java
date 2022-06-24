package com.brenner.investments.data.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sql/users.sql.xml")
public class UsersSqlProperties {
	
	@Value("${userByUsernameAndPassword}")
	private String userByUsernameAndPassword;
	
	@Value("${insertUser}")
	private String insertUser;
	
	@Value("${updateUser}")
	private String updateUser;
	
	@Value("${userRoles}")
	private String userRoles;
	
	@Value("${userByUsername}")
	private String userByUsername;
	
	@Value("${userByUserId}")
	private String userByUserId;
	
	@Value("${getAllUsers}")
	private String getAllUsers;
	
	@Value("${associateRolesToUser}")
	private String associateRolesToUser;
	
	@Value("${removeUserRoles}")
	private String removeUserRoles;
	
	@Value("${deleteUser}")
	private String deleteUser;
	

	public String getUserByUsernameAndPassword() {
		return userByUsernameAndPassword;
	}

	public void setUserByUsernameAndPassword(String userByUsernameAndPassword) {
		this.userByUsernameAndPassword = userByUsernameAndPassword;
	}

	public String getInsertUser() {
		return insertUser;
	}

	public void setInsertUser(String insertUser) {
		this.insertUser = insertUser;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(String userRoles) {
		this.userRoles = userRoles;
	}

	public String getUserByUsername() {
		return userByUsername;
	}

	public void setUserByUsername(String userByUsername) {
		this.userByUsername = userByUsername;
	}

	public String getUserByUserId() {
		return userByUserId;
	}

	public void setUserByUserId(String userByUserId) {
		this.userByUserId = userByUserId;
	}

	public String getGetAllUsers() {
		return getAllUsers;
	}

	public void setGetAllUsers(String getAllUsers) {
		this.getAllUsers = getAllUsers;
	}

	public String getAssociateRolesToUser() {
		return associateRolesToUser;
	}

	public void setAssociateRolesToUser(String associateRolesToUser) {
		this.associateRolesToUser = associateRolesToUser;
	}

	public String getRemoveUserRoles() {
		return removeUserRoles;
	}

	public void setRemoveUserRoles(String removeUserRoles) {
		this.removeUserRoles = removeUserRoles;
	}

	public String getDeleteUser() {
		return deleteUser;
	}

	public void setDeleteUser(String deleteUser) {
		this.deleteUser = deleteUser;
	}
	
}
