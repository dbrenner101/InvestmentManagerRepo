/**
 * 
 */
package com.brenner.portfoliomgmt.domain.serialize;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.util.CommonUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 *
 * @author dbrenner
 * 
 */
public class QuoteSerializer extends StdSerializer<Quote> {

	private static final long serialVersionUID = 2244155871315914320L;
	
	private static final Logger log = LoggerFactory.getLogger(QuoteSerializer.class);

	protected QuoteSerializer(Class<Quote> t) {
		super(t);
	}
	
	public QuoteSerializer() {
		super(Quote.class);
	}

	@Override
	public void serialize(Quote quote, JsonGenerator gen, SerializerProvider provider) throws IOException {
		
		log.info("Entered serialize()");
		log.debug("Converting quote: {} to json", quote);

		
		gen.writeStartObject();
			gen.writeNumberField("quoteId", quote.getQuoteId());
			gen.writeStringField("date", CommonUtils.convertDateToMMDDYYYYString(quote.getDate()));
			if (quote.getOpen() == null) {
				gen.writeNullField("open");
			} else {
				gen.writeNumberField("open", quote.getOpen());
			}
			gen.writeNumberField("close", quote.getClose());
			
			if (quote.getHigh() == null) {
				gen.writeNullField("high");
			} else {
				gen.writeNumberField("high", quote.getHigh());
			}
			
			if (quote.getLow() == null) {
				gen.writeNullField("low");
			} else {
				gen.writeNumberField("low", quote.getLow());
			}
			
			if (quote.getVolume() == null) {
				gen.writeNullField("volume");
			} else {
				gen.writeNumberField("volume", quote.getVolume());
			}
			
			if (quote.getPriceChange() == null) {
				gen.writeNullField("priceChange");
			} else {
				gen.writeNumberField("priceChange", quote.getPriceChange());
			}
			
			if (quote.getWeek52High() == null) {
				gen.writeNullField("week52High");
			} else {
				gen.writeNumberField("week52High", quote.getWeek52High());
			}
			
			if (quote.getWeek52Low() == null) {
				gen.writeNullField("week52Low");
			} else {
				gen.writeNumberField("week52Low", quote.getWeek52Low());
			}
			
			provider.defaultSerializeField("Investment", quote.getInvestment(), gen);
			
		gen.writeEndObject();
		
		log.info("Exiting serialize() ");
	}
}
