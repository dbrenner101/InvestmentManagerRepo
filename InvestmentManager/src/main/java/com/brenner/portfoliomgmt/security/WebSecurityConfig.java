package com.brenner.portfoliomgmt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity(debug = false)
@Profile("prod")
public class WebSecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
		        .authorizeRequests()
		    		.antMatchers("/prepAddUserForm", "users/addUserForm").permitAll()
		    	.and()
		    	.authorizeRequests(authorize -> authorize
                        .anyRequest().authenticated())
                .formLogin()
                .loginProcessingUrl("/auth")
                .loginPage("/login")
                .defaultSuccessUrl("/index", true)
                .permitAll()
                .and()
                .cors().and().csrf().disable();
        return http.build();
    }
}
