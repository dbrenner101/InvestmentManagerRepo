package com.brenner.portfoliomgmt.data.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.brenner.portfoliomgmt.domain.InvestmentTypeEnum;

@Entity
@Table(name="investments")
public class InvestmentDTO {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="investment_id")
	private Long investmentId;
	
	@Column(name="symbol", nullable=false, unique=true)
	private String symbol;
	
	@Column(name="company_name")
	private String companyName;
	
	@Column(name="exchange")
	private String exchange;
	
	@Column(name="sector")
	private String sector;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="investment_id")
	private List<QuoteDTO> quotes;
	
	@Column(name="investment_type")
	private InvestmentTypeEnum investmentType;
	
	public InvestmentDTO() {}
	
	public InvestmentDTO(Long investmentId, String symbol, String companyName, String exchange, String sector,InvestmentTypeEnum investmentType) {
		super();
		this.investmentId = investmentId;
		this.symbol = symbol;
		this.companyName = companyName;
		this.exchange = exchange;
		this.sector = sector;
		this.investmentType = investmentType;
	}

	public InvestmentDTO(Long investmentId) {
		this.investmentId = investmentId;
	}
	
	public InvestmentDTO(String symbol) {
	    this.symbol = symbol;
	}

	public Long getInvestmentId() {
		return investmentId;
	}

	public void setInvestmentId(Long investmentId) {
		this.investmentId = investmentId;
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

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public List<QuoteDTO> getQuotes() {
		return quotes;
	}

	public void setQuotes(List<QuoteDTO> quotes) {
		this.quotes = quotes;
	}
	
	public void addQuote(QuoteDTO quote) {
		if (this.quotes == null) {
			this.quotes = new ArrayList<QuoteDTO>();
		}
		this.quotes.add(quote);
	}

	public InvestmentTypeEnum getInvestmentType() {
		return investmentType;
	}

	public void setInvestmentType(InvestmentTypeEnum investmentType) {
		this.investmentType = investmentType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(companyName, exchange, investmentId, investmentType, quotes, sector, symbol);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InvestmentDTO other = (InvestmentDTO) obj;
		return Objects.equals(this.companyName, other.companyName) && Objects.equals(this.exchange, other.exchange)
				&& Objects.equals(this.investmentId, other.investmentId) && this.investmentType == other.investmentType
				&& Objects.equals(this.quotes, other.quotes) && Objects.equals(this.sector, other.sector)
				&& Objects.equals(this.symbol, other.symbol);
	}

	@Override
	public String toString() {
		
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
		builder.append("investmentId", investmentId)
			.append("symbol", symbol)
			.append("companyName", companyName)
			.append("exchange", exchange)
			.append("sector", sector)
			//.append("quotes", quotes)
			.append("investmentType", investmentType);
		
		return builder.toString();
	}

}
