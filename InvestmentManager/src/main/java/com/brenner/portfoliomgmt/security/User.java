package com.brenner.portfoliomgmt.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity(name="user")
@Table(name="users")
public class User implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 381627710813598952L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_id")
	private Integer userId;
	
	@Column(name="username", nullable=false, unique=true)
	private String username;
	
	@Column(name="password", nullable=false)
	private String password;
	
	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", 
    	joinColumns = { 
    			@JoinColumn(name = "user_id", unique = false, nullable = false) 
    		}, inverseJoinColumns = { @JoinColumn(name = "role_id", unique = false, nullable = false) })
	private List<Role> roles;
	
	public User() {}
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public User(String username, String password, List<Role> roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;
	}
	
	public void addRole(Role role) {
		
		if (this.roles == null) {
			this.roles = new ArrayList<>();
		}
		
		this.roles.add(role);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		if (this.roles == null) {
			return null;
		}
		
		List<GrantedAuthority> auths = new ArrayList<>(this.roles.size());
		
		Iterator<Role> iter = this.roles.iterator();
		while(iter.hasNext()) {
			Role role = iter.next();
			SimpleGrantedAuthority auth = new SimpleGrantedAuthority(role.getRoleName());
			auths.add(auth);
		}
		
		return auths;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
		builder.append("userId", userId)
			.append("username", username)
			.append("password", password)
			.append("roles", roles);
		
		return builder.toString();
	}
}
