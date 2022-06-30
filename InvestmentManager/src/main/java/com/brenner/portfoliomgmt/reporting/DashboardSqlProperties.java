package com.brenner.portfoliomgmt.reporting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sql/dashboard.sql.xml")
public class DashboardSqlProperties {

	@Value("${quotesForHoldingsSinceMaxQuoteDate}")
	private String quotesForHoldingsSinceMaxQuoteDate;
	
	@Value("${sectorSummation}")
	private String sectorSummation;
	
	@Value("${totalPortfolioChangeByInvestment}")
	private String totalPortfolioChangeByInvestment;
	

	public String getQuotesForHoldingsSinceMaxQuoteDate() {
		return quotesForHoldingsSinceMaxQuoteDate;
	}

	public void setQuotesForHoldingsSinceMaxQuoteDate(String quotesForHoldingsSinceMaxQuoteDate) {
		this.quotesForHoldingsSinceMaxQuoteDate = quotesForHoldingsSinceMaxQuoteDate;
	}

	public String getSectorSummation() {
		return sectorSummation;
	}

	public void setSectorSummation(String sectorSummation) {
		this.sectorSummation = sectorSummation;
	}

	public String getTotalPortfolioChangeByInvestment() {
		return totalPortfolioChangeByInvestment;
	}

	public void setTotalPortfolioChangeByInvestment(String totalPortfolioChangeByInvestment) {
		this.totalPortfolioChangeByInvestment = totalPortfolioChangeByInvestment;
	}
}
