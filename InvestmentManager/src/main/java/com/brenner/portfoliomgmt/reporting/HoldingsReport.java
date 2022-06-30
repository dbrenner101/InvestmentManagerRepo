package com.brenner.portfoliomgmt.reporting;

import com.brenner.portfoliomgmt.investments.InvestmentTypeEnum;

public class HoldingsReport {

	private Float valueAtPurchase;
	private String symbol;
	private Long investmentId;
	private InvestmentTypeEnum investmentType;
	private String sector;
	private String companyName;
	private Float priceAtClose;
	private Float marketValue;
	private Float changeInValue;
	private Float totalDividends;
	private Float totalValue;
	
	
	public Float getValueAtPurchase() {
		return valueAtPurchase;
	}
	public void setValueAtPurchase(Float valueAtPurchase) {
		this.valueAtPurchase = valueAtPurchase;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public Float getPriceAtClose() {
		return priceAtClose;
	}
	public void setPriceAtClose(Float priceAtClose) {
		this.priceAtClose = priceAtClose;
	}
	public Float getMarketValue() {
		return marketValue;
	}
	public void setMarketValue(Float marketValue) {
		this.marketValue = marketValue;
	}
	public Float getChangeInValue() {
		return changeInValue;
	}
	public void setChangeInValue(Float changeInValue) {
		this.changeInValue = changeInValue;
	}
	public Float getTotalDividends() {
		return totalDividends;
	}
	public void setTotalDividends(Float totalDividends) {
		this.totalDividends = totalDividends;
	}
	public Float getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(Float totalValue) {
		this.totalValue = totalValue;
	}
	public Long getInvestmentId() {
		return investmentId;
	}
	public void setInvestmentId(Long investmentId) {
		this.investmentId = investmentId;
	}
	public InvestmentTypeEnum getInvestmentType() {
		return investmentType;
	}
	public void setInvestmentType(InvestmentTypeEnum investmentType) {
		this.investmentType = investmentType;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	@Override
	public String toString() {
		return "HoldingsReport [valueAtPurchase=" + valueAtPurchase + ", symbol=" + symbol + ", investmentId="
				+ investmentId + ", investmentType=" + investmentType + ", sector=" + sector + ", companyName="
				+ companyName + ", priceAtClose=" + priceAtClose + ", marketValue=" + marketValue + ", changeInValue="
				+ changeInValue + ", totalDividends=" + totalDividends + ", totalValue=" + totalValue + "]";
	}
	
	
}
