package com.brenner.investments.data.deserialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.investments.entities.BatchQuotes;
import com.brenner.investments.entities.Quote;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Custom deserializer for BatchQuotes. 
 * 
 * @author dbrenner
 *
 */
public class BatchQuotesDeserializer extends StdDeserializer<BatchQuotes> {
	
	private static final Logger log = LoggerFactory.getLogger(BatchQuotesDeserializer.class);

    /**
     * 
     */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default constructor
	 */
	public BatchQuotesDeserializer() {
		this(null);
	}

	/**
	 * Required by parent class
	 * 
	 * @param vc
	 */
	@SuppressWarnings("unchecked")
	public BatchQuotesDeserializer(Class<?> vc) {
		super((Class<Quote[]>) vc);
	}

	/**
	 * Converts the JSON data to a BatchQuotes object
	 * 
	 * @param jp - the JSON parser with the JOSN data
	 * @param ctxt - the deserialization context
	 * @return {@link BatchQuotes}
	 */
	@Override
	public BatchQuotes deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		log.info("Entering deserialize()");
		
		JsonNode quoteNodes = jp.getCodec().readTree(jp);
		List<JsonNode> quotesList = quoteNodes.findValues("quote");
		log.debug("Count of quoteNodes: {}", quotesList != null ? quotesList.size() : 0);
		
		int numQuotes = quotesList.size();
		
		Map<String, Quote> quotes = new HashMap<>();
		
		for (int i=0; i<numQuotes; i++) {
			ObjectMapper objectMapper = new ObjectMapper();
			Quote q = objectMapper.readValue(quotesList.get(i).toString(), Quote.class);
			log.debug("Deserialized quote: {}", q);
			quotes.put(q.getInvestment().getSymbol(), q);
		}
		
		log.info("Exiting deserialize()");
		return new BatchQuotes(quotes);
	}
}
