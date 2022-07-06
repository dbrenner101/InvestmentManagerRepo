/**
 * 
 */
package com.brenner.portfoliomgmt.security;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
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

	/**
	 * GET mapping target for new authentications
	 * 
	 * @param user The Spring User Principal
	 * @return The Principal
	 */
	@RequestMapping("/auth")
	public void authenticate(@CurrentSecurityContext SecurityContext securityContext) {
		System.out.println("auth call");
		
		//securityContext.getAuthentication().getPrincipal();
	}
	
	@PostMapping("/register")
	public void register(
			@RequestParam(name="username", required=true) String username, 
			@RequestParam(name="password", required=true) String password) {
		
	}

}