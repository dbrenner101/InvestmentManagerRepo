package com.brenner.portfoliomgmt.test;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
@Profile(value="test")
public class TestConfig {
	
	@Bean
	public DataSource h2DataSource() {
		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
	            .addScript("/static/sql/createAccountTable.sql")
	            .addScript("/static/sql/createInvestmentsTable.sql")
	            .addScript("/static/sql/createHoldingsTable.sql")
	            .build();
	}
}
