package com.brenner.portfoliomgmt.deprecated;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sql/holdings.sql.xml")
public class HoldingsSqlProperties {
	
	@Value("${allHoldingsByMarketValue}")
	private String allHoldingsByMarketValue;
	
	@Value("${allHoldingsByChangeInValue}")
	private String allHoldingsByChangeInValue;
	
	@Value("${holdingsByAccountIdAndInvestmentId}")
	private String holdingsByAccountIdAndInvestmentId;
	
	@Value("${holdingsByAccountId}")
	private String holdingsByAccountId;
	
	@Value("${holdingsByAccountIdWithQuantityGreater0OrderSymbol}")
	private String holdingsByAccountIdWithQuantityGreater0OrderSymbol;
	
	@Value("${allHoldingsOrderedBySymbol}")
	private String allHoldingsOrderedBySymbol;
	
	@Value("${holdingsInsert}")
	private String holdingsInsert;
	
	@Value("${allHoldings}")
	private String allHoldings;
	
	@Value("${holdingById}")
	private String holdingById;
	
	@Value("${deleteHolding}")
	private String deleteHolding;
	
	@Value("${allHoldingsByTypeAndSector}")
	private String allHoldingsByTypeAndSector;

	@Value("${holdingUpdate}")
	private String holdingUpdate;
	
	@Value("${holdingsAccountInvestmentByInvestmentId}")
	private String holdingsAccountInvestmentByInvestmentId;
	
	
	
	public String getAllHoldingsByTypeAndSector() {
		return allHoldingsByTypeAndSector;
	}

	public void setAllHoldingsByTypeAndSector(String allHoldingsByTypeAndSector) {
		this.allHoldingsByTypeAndSector = allHoldingsByTypeAndSector;
	}

	public String getDeleteHolding() {
		return deleteHolding;
	}

	public void setDeleteHolding(String deleteHolding) {
		this.deleteHolding = deleteHolding;
	}

	public String getHoldingById() {
		return holdingById;
	}

	public void setHoldingById(String holdingById) {
		this.holdingById = holdingById;
	}

	public String getAllHoldings() {
		return allHoldings;
	}

	public void setAllHoldings(String allHoldings) {
		this.allHoldings = allHoldings;
	}

	public String getHoldingsInsert() {
		return holdingsInsert;
	}

	public void setHoldingsInsert(String holdingsInsert) {
		this.holdingsInsert = holdingsInsert;
	}

	public String getHoldingUpdate() {
		return holdingUpdate;
	}

	public void setHoldingUpdate(String holdingUpdate) {
		this.holdingUpdate = holdingUpdate;
	}
	

	public String getAllHoldingsOrderedBySymbol() {
		return allHoldingsOrderedBySymbol;
	}

	public void setAllHoldingsOrderedBySymbol(String allHoldingsOrderedBySymbol) {
		this.allHoldingsOrderedBySymbol = allHoldingsOrderedBySymbol;
	}

	public String getHoldingsByAccountIdWithQuantityGreater0OrderSymbol() {
		return holdingsByAccountIdWithQuantityGreater0OrderSymbol;
	}

	public void setHoldingsByAccountIdWithQuantityGreater0OrderSymbol(
	        String holdingsByAccountIdWithQuantityGreater0OrderSymbol) {
		this.holdingsByAccountIdWithQuantityGreater0OrderSymbol = holdingsByAccountIdWithQuantityGreater0OrderSymbol;
	}

	public String getHoldingsByAccountId() {
		return holdingsByAccountId;
	}

	public void setHoldingsByAccountId(String holdingsByAccountId) {
		this.holdingsByAccountId = holdingsByAccountId;
	}

	public String getHoldingsByAccountIdAndInvestmentId() {
		return holdingsByAccountIdAndInvestmentId;
	}

	public void setHoldingsByAccountIdAndInvestmentId(String holdingsByAccountIdAndInvestmentId) {
		this.holdingsByAccountIdAndInvestmentId = holdingsByAccountIdAndInvestmentId;
	}

	public String getAllHoldingsByChangeInValue() {
		return allHoldingsByChangeInValue;
	}

	public void setAllHoldingsByChangeInValue(String allHoldingsByChangeInValue) {
		this.allHoldingsByChangeInValue = allHoldingsByChangeInValue;
	}

	public String getAllHoldingsByMarketValue() {
		return allHoldingsByMarketValue;
	}

	public void setAllHoldingsByMarketValue(String allHoldingsByMarketValue) {
		this.allHoldingsByMarketValue = allHoldingsByMarketValue;
	}

	public String getHoldingsAccountInvestmentByInvestmentId() {
		return holdingsAccountInvestmentByInvestmentId;
	}

	public void setHoldingsAccountInvestmentByInvestmentId(String holdingsAccountInvestmentByInvestmentId) {
		this.holdingsAccountInvestmentByInvestmentId = holdingsAccountInvestmentByInvestmentId;
	}
	
	

}
