package com.brenner.investments.entities;

import java.util.List;
import java.util.Map;

import com.brenner.investments.data.deserialization.HistoricalQuotesBatchDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonDeserialize(using = HistoricalQuotesBatchDeserializer.class)
public class BatchHistoricalQuotes {
    
    private Map<String, List<Quote>> quotesMap;

    public BatchHistoricalQuotes () {}

    public Map<String, List<Quote>> getQuotesMap() {
        return quotesMap;
    }

    public void setQuotesMap(Map<String, List<Quote>> quotesMap) {
        this.quotesMap = quotesMap;
    }

}
