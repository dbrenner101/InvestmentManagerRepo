package com.brenner.portfoliomgmt;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Spring Boot Base Class
 * 
 * @author dbrenner
 *
 */
@SpringBootApplication
@MapperScan(basePackages = { "com.brenner.portfoliomgmt.data.mapping" }, annotationClass = Mapper.class)
public class InvestmentManagerApplication extends SpringBootServletInitializer {
	
	@Override
	  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	    return application.sources(InvestmentManagerApplication.class);
	  }

	public static void main(String[] args) {
		SpringApplication.run(InvestmentManagerApplication.class);
	}

}
