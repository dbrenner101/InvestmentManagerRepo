package com.brenner.portfoliomgmt.quotes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.portfoliomgmt.investments.Investment;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Custom deserializer for batch historical quotes
 * @author dbrenner
 *
 */
public class HistoricalQuotesBatchDeserializer extends StdDeserializer<BatchHistoricalQuotes> {
	
	private static final Logger log = LoggerFactory.getLogger(HistoricalQuotesBatchDeserializer.class);

    private static final long serialVersionUID = 1L;
    
    public HistoricalQuotesBatchDeserializer() {
        this(null);
    }

    @SuppressWarnings("unchecked")
    public HistoricalQuotesBatchDeserializer(Class<?> vc) {
        super((Class<Quote[]>) vc);
    }

    /**
     * Converts JSON text to BatchHistoricalQuotes object
     * 
     * @param jp - the JSON parser with JSON
     * @param ctxt - the deserialization context
     * @return {@link BatchHistoricalQuotes}
     */
    @Override
    public BatchHistoricalQuotes deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    	log.info("Entering deserialize()");
        
        JsonNode symbolNodes = jp.getCodec().readTree(jp);
        log.debug("Count of symbol nodes: {}", symbolNodes.size());
        
        Iterator<String> fieldNamesIter = symbolNodes.fieldNames();
        
        Map<String, List<Quote>> quotesMap = new HashMap<>();
        
        while (fieldNamesIter.hasNext()) {
            String symbol = fieldNamesIter.next();
            log.debug("Extracting details for symbol: {}", symbol);
            
            JsonNode chartNode = symbolNodes.get(symbol);
            Iterator<JsonNode> chartNodeIter = chartNode.elements();
            
            while (chartNodeIter.hasNext()) {
                JsonNode quoteNodes = chartNodeIter.next();
                Iterator<JsonNode> quoteNodeIter = quoteNodes.elements();
                List<Quote> quotesList = new ArrayList<>();
                
                while (quoteNodeIter.hasNext()) {
                    JsonNode quoteNode = quoteNodeIter.next();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Quote quote = objectMapper.readValue(quoteNode.toString(), Quote.class);
                    log.debug("Built quote: {}", quote);
                    quote.setInvestment(new Investment(symbol));
                    quotesList.add(quote);
                }
                quotesMap.put(symbol, quotesList);
            }
        }
        
        BatchHistoricalQuotes histQuotes = new BatchHistoricalQuotes();
        histQuotes.setQuotesMap(quotesMap);
        
        log.info("Exiting deserialize()");
        return histQuotes;
    }

}
