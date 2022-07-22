/**
 * 
 */
package com.brenner.portfoliomgmt.domain.deserialize;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Quote;
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
