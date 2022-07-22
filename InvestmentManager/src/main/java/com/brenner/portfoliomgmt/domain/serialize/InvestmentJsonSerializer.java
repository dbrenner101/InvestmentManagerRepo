package com.brenner.portfoliomgmt.domain.serialize;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.portfoliomgmt.domain.Investment;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class InvestmentJsonSerializer extends StdSerializer<Investment> {
	
	private static final Logger log = LoggerFactory.getLogger(InvestmentJsonSerializer.class);
	
	/***/
	private static final long serialVersionUID = -7760264517395179830L;

	protected InvestmentJsonSerializer(Class<Investment> t) {
		super(t);
	}
	
	public InvestmentJsonSerializer() {
		super(Investment.class);
	}

	@Override
	public void serialize(
			Investment investment, 
			JsonGenerator gen, 
			SerializerProvider provider) throws IOException {
		
		log.info("Entered serialize");
		log.debug("Param: investment: {}", investment);
		
		gen.writeStartObject();
		gen.writeStringField("companyName", investment.getCompanyName());
		gen.writeStringField("exchange", investment.getExchange());
		gen.writeStringField("sector", investment.getSector());
		gen.writeStringField("symbol", investment.getSymbol());
		gen.writeEndObject();
		
		log.info("Exiting serialize()");
		
	}

}
