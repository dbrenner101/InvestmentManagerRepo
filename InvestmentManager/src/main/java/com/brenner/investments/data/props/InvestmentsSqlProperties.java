package com.brenner.investments.data.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sql/investments.sql.xml")
public class InvestmentsSqlProperties {

	@Value("${investmentsFindBySymbol}")
	private String investmentsFindBySymbol;
	
	@Value("${investmentsAllOrderedByCompanyName}")
	private String investmentsAllOrderedByCompanyName;
	
	@Value("${investmentsAllOrderedBySymbol}")
	private String investmentsAllOrderedBySymbol;
	
	@Value("${investmentsAllForHoldingsOrderedBySymbol}")
	private String investmentsAllForHoldingsOrderedBySymbol;
	
	@Value("${investmentsAllForHoldingsWithQuotesOrderedBySymbol}")
	private String investmentsAllForHoldingsWithQuotesOrderedBySymbol;
	
	@Value("${investmentsUniqueForHoldingsOrderBySymbol}")
	private String investmentsUniqueForHoldingsOrderBySymbol;
	
	@Value("${investmentDelete}")
	private String investmentDelete;
	
	@Value("${investmentInsert}")
	private String investmentInsert;
	
	@Value("${investmentUpdate}")
	private String investmentUpdate;
	
	@Value("${investmentFindLikeSymbol}")
	private String investmentFindLikeSymbol;
	
	@Value("${investmentById}")
	private String investmentById;
	
	

	public String getInvestmentUpdate() {
		return investmentUpdate;
	}

	public void setInvestmentUpdate(String investmentUpdate) {
		this.investmentUpdate = investmentUpdate;
	}

	public String getInvestmentInsert() {
		return investmentInsert;
	}

	public void setInvestmentInsert(String investmentInsert) {
		this.investmentInsert = investmentInsert;
	}

	public String getInvestmentDelete() {
		return investmentDelete;
	}

	public void setInvestmentDelete(String investmentDelete) {
		this.investmentDelete = investmentDelete;
	}

	public String getInvestmentsUniqueForHoldingsOrderBySymbol() {
		return investmentsUniqueForHoldingsOrderBySymbol;
	}

	public void setInvestmentsUniqueForHoldingsOrderBySymbol(String investmentsUniqueForHoldingsOrderBySymbol) {
		this.investmentsUniqueForHoldingsOrderBySymbol = investmentsUniqueForHoldingsOrderBySymbol;
	}

	public String getInvestmentsAllForHoldingsWithQuotesOrderedBySymbol() {
		return investmentsAllForHoldingsWithQuotesOrderedBySymbol;
	}

	public void setInvestmentsAllForHoldingsWithQuotesOrderedBySymbol(
	        String investmentsAllForHoldingsWithQuotesOrderedBySymbol) {
		this.investmentsAllForHoldingsWithQuotesOrderedBySymbol = investmentsAllForHoldingsWithQuotesOrderedBySymbol;
	}

	public String getInvestmentsAllForHoldingsOrderedBySymbol() {
		return investmentsAllForHoldingsOrderedBySymbol;
	}

	public void setInvestmentsAllForHoldingsOrderedBySymbol(String investmentsAllForHoldingsOrderedBySymbol) {
		this.investmentsAllForHoldingsOrderedBySymbol = investmentsAllForHoldingsOrderedBySymbol;
	}

	public String getInvestmentsAllOrderedBySymbol() {
		return investmentsAllOrderedBySymbol;
	}

	public void setInvestmentsAllOrderedBySymbol(String investmentsAllOrderedBySymbol) {
		this.investmentsAllOrderedBySymbol = investmentsAllOrderedBySymbol;
	}

	public String getInvestmentsAllOrderedByCompanyName() {
		return investmentsAllOrderedByCompanyName;
	}

	public void setInvestmentsAllOrderedByCompanyName(String investmentsAllOrderedByCompanyName) {
		this.investmentsAllOrderedByCompanyName = investmentsAllOrderedByCompanyName;
	}

	public String getInvestmentsFindBySymbol() {
		return investmentsFindBySymbol;
	}

	public void setInvestmentsFindBySymbol(String investmentsFindBySymbol) {
		this.investmentsFindBySymbol = investmentsFindBySymbol;
	}

	public String getInvestmentFindLikeSymbol() {
		return investmentFindLikeSymbol;
	}

	public void setInvestmentFindLikeSymbol(String investmentFindLikeSymbol) {
		this.investmentFindLikeSymbol = investmentFindLikeSymbol;
	}

	public String getInvestmentById() {
		return investmentById;
	}

	public void setInvestmentById(String investmentById) {
		this.investmentById = investmentById;
	}
	
	
}
