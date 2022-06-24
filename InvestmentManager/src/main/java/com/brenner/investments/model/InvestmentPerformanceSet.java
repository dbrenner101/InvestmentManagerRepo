package com.brenner.investments.model;

import java.util.List;

public class InvestmentPerformanceSet {
	
	private String symbol;

	private List<InvestmentPerformance> investmentPerformanceList;
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public List<InvestmentPerformance> getInvestmentPerformanceList() {
		return investmentPerformanceList;
	}

	public void setInvestmentPerformanceList(List<InvestmentPerformance> investmentPerformanceList) {
		this.investmentPerformanceList = investmentPerformanceList;
	}
	
}
