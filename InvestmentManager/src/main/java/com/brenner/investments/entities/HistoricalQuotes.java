package com.brenner.investments.entities;

import java.util.Date;
import java.util.Map;

import com.brenner.investments.data.deserialization.HistoricalQuotesDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * 
 * @author dbrenner
 *  {
        "date":"2018-07-30",
        "open":191.2293,
        "high":191.5283,
        "low":188.4092,
        "close":189.2463,
        "volume":21029535,
        "unadjustedVolume":21029535,
        "change":-1.0663,
        "changePercent":-0.56,
        "vwap":189.3205,
        "label":"Jul 30",
        "changeOverTime":0
    },
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonDeserialize(using = HistoricalQuotesDeserializer.class)
public class HistoricalQuotes {

    private Map<Date, Quote> quotes;
    
    public HistoricalQuotes() {}
    
    public HistoricalQuotes(Map<Date, Quote> quotes) {
        this.quotes = quotes;
    }


    public Map<Date, Quote> getQuotes() {
        return quotes;
    }



    public void setQuotes(Map<Date, Quote> quotes) {
        this.quotes = quotes;
    }
    
}
