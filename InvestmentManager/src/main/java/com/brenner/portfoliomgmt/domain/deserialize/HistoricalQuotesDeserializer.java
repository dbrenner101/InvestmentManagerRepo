package com.brenner.portfoliomgmt.domain.deserialize;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.domain.reporting.HistoricalQuotes;
import com.brenner.portfoliomgmt.util.CommonUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Custom deserializer for historical quotes
 * 
 * @author dbrenner
 *
 */
public class HistoricalQuotesDeserializer extends StdDeserializer<HistoricalQuotes> {
	
	private static final Logger log = LoggerFactory.getLogger(HistoricalQuotesDeserializer.class);

    private static final long serialVersionUID = 1L;
    
    public HistoricalQuotesDeserializer() {
        this(null);
    }

    @SuppressWarnings("unchecked")
    public HistoricalQuotesDeserializer(Class<?> vc) {
        super((Class<Quote[]>) vc);
    }

    /**
     * Converts JSON text to HistoricalQuotes object
     * 
     * @param jp - the JSON parser with JSON
     * @param ctxt - the deserialization context
     * @return {@link HistoricalQuotes}
     */
    @Override
    public HistoricalQuotes deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    	log.info("Entering deserialize()");
        
        JsonNode quoteNodes = jp.getCodec().readTree(jp);
        List<String> nodes = quoteNodes.findValuesAsText("date");
        log.debug("Quote nodes: {}", nodes != null ? nodes.size() : 0);
        
        int numQuotes = nodes.size();
        Iterator<JsonNode> iterator = quoteNodes.elements();

        Map<Date, Quote> quotes = new HashMap<>(numQuotes);
        int i = 0;
        
        while (iterator.hasNext()) {
            JsonNode node = iterator.next();
            String dateStr = node.get("date").asText();
            log.debug("Node date value: {}", dateStr);
            
            Date date;
            try {
                date = CommonUtils.convertDatePickerDateFormatStringToDate(dateStr);
            } catch (ParseException e) {
                throw new IOException(e);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Quote q = objectMapper.readValue(quoteNodes.get(i).toString(), Quote.class);
            log.debug("Extracted quote: {}", q);
            
            quotes.put(date, q);
            i++;
        }
        
        log.info("Exiting deserialize()");
        return new HistoricalQuotes(quotes);
    }

}
