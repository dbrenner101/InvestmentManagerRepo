package com.brenner.investments.entities;

import java.util.Map;

import com.brenner.investments.data.deserialization.BatchQuotesDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
