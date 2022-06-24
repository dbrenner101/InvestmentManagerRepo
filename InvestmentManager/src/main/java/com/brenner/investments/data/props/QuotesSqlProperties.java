package com.brenner.investments.data.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sql/quotes.sql.xml")
public class QuotesSqlProperties {
	
	@Value("${allQuotes}")
	private String allQuotes;
	
	@Value("${quotesByQuoteId}")
	private String quotesByQuoteId;
	
	@Value("${quoteInsert}")
	private String quoteInsert;
	
	@Value("${quoteUpdate}")
	private String quoteUpdate;
	
	@Value("${quoteDelete}")
	private String quoteDelete;
	
	@Value("${quotesByDateAndSymbol}")
	private String quotesByDateAndSymbol;
	
	@Value("${maxQuoteDateForInvestmentSymbol}")
	private String maxQuoteDateForInvestmentSymbol;
	
	@Value("${maxQuoteDate}")
	private String maxQuoteDate;
	
	@Value("${quotesBySymbol}")
	private String quotesBySymbol;
	
	@Value("${quotesByInvestmentId}")
	private String quotesByInvestmentId;
	
	

	public String getQuotesByInvestmentId() {
		return quotesByInvestmentId;
	}

	public void setQuotesByInvestmentId(String quotesByInvestmentId) {
		this.quotesByInvestmentId = quotesByInvestmentId;
	}

	public String getQuotesBySymbol() {
		return quotesBySymbol;
	}

	public void setQuotesBySymbol(String quotesBySymbol) {
		this.quotesBySymbol = quotesBySymbol;
	}

	public String getQuotesByDateAndSymbol() {
		return quotesByDateAndSymbol;
	}

	public void setQuotesByDateAndSymbol(String quotesByDateAndSymbol) {
		this.quotesByDateAndSymbol = quotesByDateAndSymbol;
	}

	public String getQuoteDelete() {
		return quoteDelete;
	}

	public void setQuoteDelete(String quoteDelete) {
		this.quoteDelete = quoteDelete;
	}

	public String getQuoteUpdate() {
		return quoteUpdate;
	}

	public void setQuoteUpdate(String quoteUpdate) {
		this.quoteUpdate = quoteUpdate;
	}

	public String getQuoteInsert() {
		return quoteInsert;
	}

	public void setQuoteInsert(String quoteInsert) {
		this.quoteInsert = quoteInsert;
	}

	public String getQuotesByQuoteId() {
		return quotesByQuoteId;
	}

	public void setQuotesByQuoteId(String quotesByQuoteId) {
		this.quotesByQuoteId = quotesByQuoteId;
	}

	public String getAllQuotes() {
		return allQuotes;
	}

	public void setAllQuotes(String allQuotes) {
		this.allQuotes = allQuotes;
	}

	public String getMaxQuoteDateForInvestmentSymbol() {
		return maxQuoteDateForInvestmentSymbol;
	}

	public void setMaxQuoteDateForInvestmentSymbol(String maxQuoteDateForInvestmentSymbol) {
		this.maxQuoteDateForInvestmentSymbol = maxQuoteDateForInvestmentSymbol;
	}

	public String getMaxQuoteDate() {
		return maxQuoteDate;
	}

	public void setMaxQuoteDate(String maxQuoteDate) {
		this.maxQuoteDate = maxQuoteDate;
	}
	
	

}
