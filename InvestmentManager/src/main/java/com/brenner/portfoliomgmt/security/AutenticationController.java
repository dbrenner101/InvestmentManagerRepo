/**
 * 
 */
package com.brenner.portfoliomgmt.security;

import javax.servlet.http.HttpSession;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author dbrenner
 * 
 */
@Controller
@Secured("ROLE_USER")
public class AutenticationController {
	
	@PostMapping("/register")
	public void register(
			@RequestParam(name="username", required=true) String username, 
			@RequestParam(name="password", required=true) String password) {
		
	}
	
	@RequestMapping("/logout")
	public String logoutRedirection(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

}
