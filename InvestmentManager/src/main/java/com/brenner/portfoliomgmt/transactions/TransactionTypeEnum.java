package com.brenner.portfoliomgmt.transactions;

public enum TransactionTypeEnum {
    Buy("Buy"),
    Cash("Cash"),
    Dividend("Dividend"),
    Revinvest_Dividend("Reinvest Dividend"),
    Sell("Sell"),
    Split("Split"),
    Transfer("Transfer");
	
	private String description;

	/**
	 * 
	 */
	private TransactionTypeEnum(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
}
