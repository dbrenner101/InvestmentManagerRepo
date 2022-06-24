package com.brenner.investments.data.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sql/roles.sql.xml")
public class RolesSqlProperties {
	
	@Value("${allRoles}")
	private String allRoles;
	
	@Value("${roleById}")
	private String roleById;

	public String getAllRoles() {
		return allRoles;
	}

	public void setAllRoles(String allRoles) {
		this.allRoles = allRoles;
	}

	public String getRoleById() {
		return roleById;
	}

	public void setRoleById(String roleById) {
		this.roleById = roleById;
	}
	

}
