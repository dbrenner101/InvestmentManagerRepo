package com.brenner.portfoliomgmt.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Controler for interacting with {@link User} objects
 * 
 * @author dbrenner
 *
 */
@Controller
//@Secured("ROLE_ADMIN")
public class UsersController implements WebMvcConfigurer {
	
	private static final Logger log = LoggerFactory.getLogger(UsersController.class);
	
	@Autowired
	InvestmentUserDetailsService userDetailsService;
	
	/**
	 * Entry point for adding a {@link User} workflow
	 * 
	 * @param model - object container
	 * @param request - Http request object
	 * @returnusers/addUserForm
	 */
	@RequestMapping("/prepAddUserForm")
	public String prepAddUserForm(Model model, HttpServletRequest request) {
		log.info("Entered prepAddUserForm()");
		
		model.addAttribute("roles", this.userDetailsService.getAllRoles());
		model.addAttribute("users", this.userDetailsService.getAllUsers());
		
		log.info("Exiting prepAddUserForm()");
		return "users/addUserForm";
	}
	
	/**
	 * Entry point for the edit {@link User} workflow.
	 * 
	 * @param userIdStr - unique identifier for a {@link User} object
	 * @param model - object container
	 * @return users/editUserForm
	 */
	@RequestMapping("/editUserFormPrep")
	public String editUserFormPrep(
			@RequestParam(name="userId", required=true) String userIdStr, 
			Model model) {
		log.info("Entered editUserFormPrep");

		model.addAttribute("roles", this.userDetailsService.getAllRoles());
		model.addAttribute("users", this.userDetailsService.getAllUsers());
		
		User user = this.userDetailsService.findUserById(Integer.parseInt(userIdStr));
		model.addAttribute("user", user);
		
		log.info("Exiting editUserFormPrep()");
		return "users/editUserForm";
	}
	
	/**
	 * Step 2 in the add or update user workflow
	 * 
	 * @param username - user's username
	 * @param password - user's password
	 * @param userIdStr - unique user identifier (will be null for new user)
	 * @param roleIdStr - list of roles assigned to the user
	 * @return redirect:prepAddUserForm
	 */
	@RequestMapping("/addUserSubmit")
	public String addUserSubmit(
			@RequestParam(name="username", required=true) String username, 
			@RequestParam(name="password", required=true) String password, 
			@RequestParam(name="userId", required=false) String userIdStr,
			@RequestParam(name="selectedRoleId", required=true) String roleIdStr) {
		log.info("Entered addUser()");
		log.debug("Params: username: {}", username);
		
		Scanner scanner = new Scanner(roleIdStr);
		scanner.useDelimiter(",");
		List<Role> roles = new ArrayList<>();
		while (scanner.hasNext()) {
			Role role = new Role();
			role.setRoleId(Integer.parseInt(scanner.next()));
			roles.add(role);
		}
		
		scanner.close();
		
		User user = new User();
		user.setUserId(userIdStr != null ? Integer.parseInt(userIdStr) : null);
		user.setUsername(username);
		user.setPassword(password);
		user.setRoles(roles);
		
		this.userDetailsService.saveNewUser(user);
		
		log.info("Exiting to redirect:prepAddUserForm");
		return "redirect:prepAddUserForm";
	}
	
	/**
	 * Delete a user
	 * 
	 * @param userIdStr - unique user identifier
	 * @return redirect:prepAddUserForm
	 */
	@RequestMapping("/deleteUser")
	public String deleteUser(@RequestParam(name="userId", required=true) String userIdStr) {
		log.info("Entered deleteUser()");
		log.debug("Param: userId: {}", userIdStr);
		
		User user = new User();
		user.setUserId(Integer.parseInt(userIdStr));
		
		this.userDetailsService.deleteUser(user);
		
		log.info("Exiting deleteUser()");
		return "redirect:prepAddUserForm";
	}

}
