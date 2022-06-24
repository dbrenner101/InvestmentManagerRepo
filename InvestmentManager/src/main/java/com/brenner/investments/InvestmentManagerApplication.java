package com.brenner.investments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Spring Boot Base Class
 * 
 * @author dbrenner
 *
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class InvestmentManagerApplication extends SpringBootServletInitializer {
	
	@Override
	  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	    return application.sources(InvestmentManagerApplication.class);
	  }

	public static void main(String[] args) {
		SpringApplication.run(InvestmentManagerApplication.class);
	}

}
