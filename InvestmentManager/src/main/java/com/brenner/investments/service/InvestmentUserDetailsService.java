package com.brenner.investments.service;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.brenner.investments.data.RoleDataService;
import com.brenner.investments.data.UserDataService;
import com.brenner.investments.entities.Role;

@Configuration
public class InvestmentUserDetailsService implements AuthenticationProvider {
	
	private static final Logger log = LoggerFactory.getLogger(InvestmentUserDetailsService.class);
	
	@Autowired
	UserDataService userDataService;
	
	@Autowired
	RoleDataService rolesDataService;
	
	@Lazy
	@Autowired
	PasswordEncoder encoder;

	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		
		log.info("Entered loadUserByUsername()");
		log.debug("Param: username: {}", username);
		
		com.brenner.investments.entities.User user = this.userDataService.findUserByUsername(username);
		
		if (user == null) {
			throw new UsernameNotFoundException(username + " not valid for login.");
		}
		
		return createUser(user);
	}
	
	/**
	 * Helper method to create a Spring User object.
	 * 
	 * @param u User account information
	 * @return Spring User 
	 */
	public User createUser(com.brenner.investments.entities.User u) {
		
        return new User(u.getUsername(), u.getPassword(), createAuthorities(u));
    }
	
	
	private Collection<GrantedAuthority> createAuthorities(com.brenner.investments.entities.User u) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return  authorities;
    }
	
	public Iterable<Role> getAllRoles() {
		
		log.info("Entered getAllRoles()");
		
		Iterable<Role> roles = this.rolesDataService.findAll();
		
		log.info("Exiting getAllRoles()");
		
		return roles;
	}
	
	public com.brenner.investments.entities.User saveNewUser(com.brenner.investments.entities.User user) {
		
		log.info("Entered saveNewUser()");
		log.debug("Params: user: {}", user);
		
		user.setPassword(encoder.encode(user.getPassword()));
		
		com.brenner.investments.entities.User u = this.userDataService.saveUser(user);
		
		log.info("Exiting saveNewUser()");
		
		return u;
		
		
	}
	
	public com.brenner.investments.entities.User findUserById(Integer userId) {
		
		return this.userDataService.findById(userId);
	}
	
	public Iterable<com.brenner.investments.entities.User> getAllUsers() {
		
		log.info("Entered getAllUsers()");
		
		Iterable<com.brenner.investments.entities.User> users = this.userDataService.findAll();
		
		log.info("Exiting getAllUsers()");
		
		return users;
	}
	
	public void deleteUser(com.brenner.investments.entities.User user) {
		
		log.info("Entered deleteUser()");
		
		this.userDataService.deleteUser(user);
		
		log.info("Exiting deleteUser()");
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		User u = loadUserByUsername(authentication.getName());
		
		if (u != null) {
			return new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword(), u.getAuthorities());
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
