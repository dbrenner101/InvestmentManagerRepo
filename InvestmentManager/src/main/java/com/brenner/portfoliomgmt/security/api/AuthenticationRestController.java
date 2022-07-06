/**
 * 
 */
package com.brenner.portfoliomgmt.security.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author dbrenner
 * 
 */
@RestController
@RequestMapping("/api")
public class AuthenticationRestController {

	@PostMapping(path = "/auth")
	public String authenticateUser() {
		
		return "Authenticated";
	}

}
