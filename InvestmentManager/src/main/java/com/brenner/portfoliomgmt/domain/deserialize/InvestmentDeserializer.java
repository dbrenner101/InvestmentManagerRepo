package com.brenner.portfoliomgmt.domain.deserialize;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.InvestmentTypeEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Class specific to converting Investment JSON to {@link InvestmentDTO} object
 * @author dbrenner
 *
 */
public class InvestmentDeserializer extends StdDeserializer<Investment> {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(InvestmentDeserializer.class);
	
	public InvestmentDeserializer() {
		this(null);
	}
	
	public InvestmentDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Investment deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		log.info("Entered deserialize()");
		
		JsonNode json = jp.getCodec().readTree(jp);
		Investment i = this.deserialize(json);
		log.debug("Deserialized into investment: {}", i);
		
		log.info("Exiting deserialize()");
		return i;
	}
	
	public Investment deserialize(JsonNode json) {
		log.info("Entered deserialize()");
		
		Investment i = new Investment();
		i.setCompanyName(json.get("companyName") != null ? json.get("companyName").asText() : null);
		i.setExchange(json.get("exchange") != null ? json.get("exchange").asText() : null);
		i.setInvestmentId(json.get("investmentId") != null ? json.get("investmentId").asLong() : null);
		i.setInvestmentType(json.get("investmentType") != null ? InvestmentTypeEnum.valueOf(json.get("investmentType").asText()) : null);
		i.setSector(json.get("sector") != null ? json.get("sector").asText() : null);
		i.setSymbol(json.get("symbol") != null ? json.get("symbol").asText() : null);
		log.debug("Investment: {}", i);
		
		log.info("Exiting deserialize()");
		return i;
		
	}

}
