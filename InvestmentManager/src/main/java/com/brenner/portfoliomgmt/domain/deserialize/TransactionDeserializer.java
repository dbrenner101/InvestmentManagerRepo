package com.brenner.portfoliomgmt.domain.deserialize;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.domain.Transaction;
import com.brenner.portfoliomgmt.domain.TransactionTypeEnum;
import com.brenner.portfoliomgmt.util.CommonUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Converts Transaction JSON to {@link TransactionDTO} object
 * @author dbrenner
 *
 */
public class TransactionDeserializer extends StdDeserializer<Transaction> {
	
	private static final Logger log = LoggerFactory.getLogger(TransactionDeserializer.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TransactionDeserializer() {
		this(null);
	}
	
	public TransactionDeserializer(Class<?> vc) { 
        super(vc); 
    }
	
	@Override
	public Transaction deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		log.info("Entered deserialize()");
		
		JsonNode json = jp.getCodec().readTree(jp);
		
		Account account = new AccountDeserializer().deserialize(json);
		Transaction t = new Transaction(account);
		
		t.setTransactionId(json.get("transactionId") != null ? json.get("transactionId").asLong() : null);
		
		t.setAssociatedCashTransactionId(json.get("associatedCashTransactionId") != null ? 
				Long.valueOf(json.get("associatedCashTransactionId").asText()) : null);
		t.setDividend(json.get("dividend") != null ? BigDecimal.valueOf(json.get("dividend").floatValue()) : null);
		//t.setHolding(json.get("holdingId") != null ? json.get("holdingId").asLong() : null);
		
		t.setTradePrice(json.get("tradePrice") != null ? new BigDecimal(json.get("tradePrice").asText()) : null);
		t.setTradeQuantity(json.get("tradeQuantity") != null ? new BigDecimal(json.get("tradeQuantity").asText()) : null);
		try {
			t.setTransactionDate(json.get("transactionDate") != null ? CommonUtils.convertCommonDateFormatStringToDate(json.get("transactionDate").asText()) : null);
		} catch (ParseException e) {
			log.error("Unable to parse date: {}", json.get("transactionDate").asText());
			throw new IOException("Unable to parse date: " + json.get("transactionDate").asText());
		} 
		t.setTransactionType(json.get("transactionType") != null ? TransactionTypeEnum.valueOf(json.get("transactionType").asText()) : null);
		log.debug("Deserialized to transaction: {}", t);
		
		log.info("Exiting deserialize()");
		return t;
	}

}
