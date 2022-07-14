/**
 * 
 */
package com.brenner.portfoliomgmt.quotes;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.util.CommonUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Converts a JSON quote string to a Quote object
 * 
 * @author dbrenner
 *
 */
public class QuoteJsonDeserializer extends StdDeserializer<Quote> {
	
	private static final Logger log = LoggerFactory.getLogger(QuoteJsonDeserializer.class);

    protected QuoteJsonDeserializer(Class<?> vc) {
        super(vc);
    }
    
    public QuoteJsonDeserializer() {
        this(null);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    /*
     * 
     * {"quote":{
     * 	"symbol":"AET",
     * "companyName":"Aetna Inc.",
     * "primaryExchange":"New York Stock Exchange",
     * "sector":"Healthcare",
     * "calculationPrice":"tops","open":203.64,"openTime":1537795876154,"close":204.27,"closeTime":1537560090461,"high":204.65,"low":202.68,"latestPrice":203.43,"latestSource":"IEX real time price","latestTime":"2:41:12 PM","latestUpdate":1537814472795,"latestVolume":861988,"iexRealtimePrice":203.43,"iexRealtimeSize":100,"iexLastUpdated":1537814472795,"delayedPrice":203.44,"delayedPriceTime":1537813881581,"extendedPrice":203.43,"extendedChange":0,"extendedChangePercent":0,"extendedPriceTime":1537814472795,"previousClose":204.27,"change":-0.84,"changePercent":-0.00411,"iexMarketPercent":0.0689,"iexVolume":59391,"avgTotalVolume":1923980,"iexBidPrice":203.39,"iexBidSize":100,"iexAskPrice":203.46,"iexAskSize":100,"marketCap":66602982000,"peRatio":19.71,"week52High":206.66,"week52Low":149.69,"ytdChange":0.1357723790043343}}
     * 
     * {
     * 	"avgTotalVolume":4871002,
     * "calculationPrice":"tops",
     * "change":0.48,
     * "changePercent":0.0035,
     * "close":null,
     * "closeSource":"official",
     * "closeTime":null,
     * "companyName":"International Business Machines Corp.",
     * "currency":"USD",
     * "delayedPrice":null,
     * "delayedPriceTime":null,
     * "extendedChange":null,
     * "extendedChangePercent":null,
     * "extendedPrice":null,
     * "extendedPriceTime":null,
     * "high":null,
     * "highSource":"15 minute delayed price",
     * "highTime":1656004362391,
     * "iexAskPrice":137.55,
     * "iexAskSize":300,
     * "iexBidPrice":125.81,
     * "iexBidSize":100,
     * "iexClose":137.56,
     * "iexCloseTime":1656005259794,
     * "iexLastUpdated":1656005259794,
     * "iexMarketPercent":0.04746701698281995,
     * "iexOpen":137.1,
     * "iexOpenTime":1655991001011,
     * "iexRealtimePrice":137.56,
     * "iexRealtimeSize":2,
     * "iexVolume":80865,
     * "lastTradeTime":1656005259794,
     * "latestPrice":137.56,
     * "latestSource":"IEX real time price",
     * "latestTime":"1:27:39 PM",
     * "latestUpdate":1656005259794,
     * "latestVolume":null,"low":null,
     * "lowSource":"15 minute delayed price",
     * "lowTime":1656000300128,
     * "marketCap":123726323307,
     * "oddLotDelayedPrice":null,
     * "oddLotDelayedPriceTime":null,
     * "open":null,
     * "openTime":null,
     * "openSource":"official",
     * "peRatio":22.33,
     * "previousClose":137.08,
     * "previousVolume":3791635,
     * "primaryExchange":"NEW YORK STOCK EXCHANGE INC.",
     * "symbol":"IBM",
     * "volume":null,
     * "week52High":144.73,
     * "week52Low":111.84,
     * "ytdChange":0.054031741646441034,
     * "isUSMarketOpen":true}
     */
    public Quote deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    	log.info("Entering deserialize()");
        
        Quote q = new Quote();
        
        JsonNode quoteNode = jp.getCodec().readTree(jp);
        		
        String closeStr = quoteNode.get("iexClose") != null ? quoteNode.get("iexClose").asText() : null;
        log.debug("Close value: {}", closeStr);
        
        Float openStr = quoteNode.get("iexOpen").isFloat() ? quoteNode.get("iexOpen").floatValue() : null;
        log.debug("Open value: {}", openStr);
        
        String symbol = quoteNode.get("symbol") == null ? null : quoteNode.get("symbol").asText();
        log.debug("Symbol value: {}", symbol);
        
        Float highStr = quoteNode.get("high").isFloat() ? quoteNode.get("high").floatValue() : null;
        log.debug("High value: {}", highStr);
        
        Float lowStr = quoteNode.get("low").isFloat() ? quoteNode.get("low").floatValue() : null;
        log.debug("Low value: {}", lowStr);
        
        Integer volumeStr = quoteNode.get("avgTotalVolume").isInt() ? quoteNode.get("avgTotalVolume").intValue() : null;
        log.debug("Volume: {}", volumeStr);
        
        Float priceChangeStr = quoteNode.get("change").isFloat() ? quoteNode.get("change").floatValue() : null;
        log.debug("Price change value: {}", priceChangeStr);
        
        String dateStr = quoteNode.get("date") == null ? null : quoteNode.get("date").asText();
        log.debug("Date value: {}", dateStr);
        
        Float week52High = quoteNode.get("week52High").isFloat() ? null : quoteNode.get("week52High").floatValue();
        log.debug("52 week high: {}", week52High);
        
        Float week52Low = quoteNode.get("week52Low").isFloat() ? null : quoteNode.get("week52Low").floatValue();
        log.debug("52 week low: {}", week52Low);
        
        q.setClose(closeStr != null ? new BigDecimal(closeStr) : null);
        if (dateStr != null) {
	        try {
				q.setDate(CommonUtils.convertDatePickerDateFormatStringToDate(dateStr));
			} catch (ParseException e) {
				throw new IOException(e);
			}
        }
        
        q.setHigh(new BigDecimal(highStr));
        q.setLow(new BigDecimal(lowStr));
        q.setOpen(new BigDecimal(openStr));
        q.setPriceChange(new BigDecimal(priceChangeStr));
        q.setVolume(volumeStr);
        q.setWeek52High(new BigDecimal(week52High));
        q.setWeek52Low(new BigDecimal(week52Low));
        
        Investment i = new Investment();
        i.setCompanyName(quoteNode.get("companyName") != null ? quoteNode.get("companyName").asText() : null);
        i.setSymbol(quoteNode.get("symbol") != null ? quoteNode.get("symbol").asText() : null);
        i.setSector(quoteNode.get("sector") != null ? quoteNode.get("sector").asText() : null);
        i.setExchange(quoteNode.get("primaryExchange") != null ? quoteNode.get("primaryExchange").asText() : null);
        
        q.setInvestment(i);
        log.debug("Built quote: {}", q);
        
        log.info("Exiting deserialize()");
        return q;
    }

}
