package com.brenner.portfoliomgmt.domain;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
//@JsonDeserialize(using = BatchQuotesDeserializer.class)
public class BatchQuotes {

	private Map<String, Quote> quotes;
	
	public BatchQuotes() {}
	
	public BatchQuotes(Map<String, Quote> quotes) {
		this.quotes = quotes;
	}

	public Map<String, Quote> getQuotes() {
		return quotes;
	}

	public void setQuotes(Map<String, Quote> quotes) {
		this.quotes = quotes;
	}
	
}
