package com.brenner.investments.controller.integration.test;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.brenner.investments.entities.User;

@Profile("test")
@Primary
@Service
public class AuthenticationProviderMock implements AuthenticationProvider {

	@Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		User u = new User("dbrenner", "password");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(u, "", null);
        usernamePasswordAuthenticationToken.setDetails(u);
        return usernamePasswordAuthenticationToken;
    }

	@Override
	public boolean supports(Class<?> authentication) {
		
		return true;
	}

}
