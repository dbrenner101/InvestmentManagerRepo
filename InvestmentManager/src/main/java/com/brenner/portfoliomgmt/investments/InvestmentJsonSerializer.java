package com.brenner.portfoliomgmt.investments;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class InvestmentJsonSerializer extends StdSerializer<Investment> {
	
	private static final Logger log = LoggerFactory.getLogger(InvestmentJsonSerializer.class);
	
	private static final String COMPANY_NAME = "companyName";
	private static final String EXCHANGE = "exchange";
	private static final String SECTOR = "sector";
	private static final String SYMBOL = "symbol";
	
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
		gen.writeStringField(InvestmentJsonSerializer.COMPANY_NAME, investment.getCompanyName());
		gen.writeStringField(InvestmentJsonSerializer.EXCHANGE, investment.getExchange());
		gen.writeStringField(InvestmentJsonSerializer.SECTOR, investment.getSector());
		gen.writeStringField(InvestmentJsonSerializer.SYMBOL, investment.getSymbol());
		gen.writeEndObject();
		
		log.info("Exiting serialize()");
		
	}

}
