/**
 * 
 */
package com.brenner.portfoliomgmt.domain;

import java.util.Objects;

import com.brenner.portfoliomgmt.data.entities.InvestmentDTO;
import com.brenner.portfoliomgmt.domain.serialize.InvestmentJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 *
 * @author dbrenner
 * 
 */
@JsonSerialize(using = InvestmentJsonSerializer.class)
public class Investment {
	
	private Long investmentId;
	
	private String symbol;
	
	private String companyName;
	
	private String exchange;
	
	private String sector;
	
	private InvestmentTypeEnum investmentType;
	
	private Quote mostRecentQuote;
	
	public Investment() {}
	
	public Investment(Long investmentId) {
		this.investmentId = investmentId;
	}
	
	public Investment(Long investmentId, String symbol, String companyName, String exchange, String sector,
			InvestmentTypeEnum investmentType) {
		super();
		this.investmentId = investmentId;
		this.symbol = symbol;
		this.companyName = companyName;
		this.exchange = exchange;
		this.sector = sector;
		this.investmentType = investmentType;
	}

	public Investment(InvestmentDTO investment) {
		if (investment != null) {
			this.investmentId = investment.getInvestmentId();
			this.symbol = investment.getSymbol();
			this.companyName = investment.getCompanyName();
			this.exchange = investment.getExchange();
			this.sector = investment.getSector();
			this.investmentType = investment.getInvestmentType();
		}
	}

	public Long getInvestmentId() {
		return this.investmentId;
	}

	public void setInvestmentId(Long investmentId) {
		this.investmentId = investmentId;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getExchange() {
		return this.exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getSector() {
		return this.sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public InvestmentTypeEnum getInvestmentType() {
		return this.investmentType;
	}

	public void setInvestmentType(InvestmentTypeEnum investmentType) {
		this.investmentType = investmentType;
	}

	public Quote getMostRecentQuote() {
		return this.mostRecentQuote;
	}

	public void setMostRecentQuote(Quote mostRecentQuote) {
		this.mostRecentQuote = mostRecentQuote;
	}

	@Override
	public int hashCode() {
		return Objects.hash(companyName, exchange, investmentId, investmentType, mostRecentQuote, sector,
				symbol);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Investment other = (Investment) obj;
		return Objects.equals(this.companyName, other.companyName) && Objects.equals(this.exchange, other.exchange)
				&& Objects.equals(this.investmentId, other.investmentId) && this.investmentType == other.investmentType
				&& Objects.equals(this.mostRecentQuote, other.mostRecentQuote)
				&& Objects.equals(this.symbol, other.symbol);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Investment [investmentId=").append(this.investmentId).append(", symbol=").append(this.symbol)
				.append(", companyName=").append(this.companyName).append(", exchange=").append(this.exchange)
				.append(", sector=").append(this.sector).append(", investmentType=").append(this.investmentType)
				.append(", mostRecentQuote=").append(this.mostRecentQuote).append(", quotes=")
				.append("]");
		return builder.toString();
	}
	

}
