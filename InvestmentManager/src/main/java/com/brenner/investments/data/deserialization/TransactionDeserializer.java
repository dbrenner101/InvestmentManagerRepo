package com.brenner.investments.data.deserialization;

import java.io.IOException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Transaction;
import com.brenner.investments.entities.constants.TransactionTypeEnum;
import com.brenner.investments.util.CommonUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Converts Transaction JSON to {@link Transaction} object
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
		
		Transaction t = new Transaction();
		
		JsonNode json = jp.getCodec().readTree(jp);
		
		t.setTransactionId(json.get("transactionId") != null ? json.get("transactionId").asLong() : null);
		
		Account account = new AccountDeserializer().deserialize(json);
		t.setAccount(account);
		t.setAssociatedCashTransactionId(json.get("associatedCashTransactionId") != null ? 
				Long.valueOf(json.get("associatedCashTransactionId").asText()) : null);
		t.setDividend(json.get("dividend") != null ? json.get("dividend").floatValue() : null);
		t.setHoldingId(json.get("holdingId") != null ? json.get("holdingId").asLong() : null);
		
		Investment investment = new InvestmentDeserializer().deserialize(json);
		t.setInvestment(investment);
		t.setTradePrice(json.get("tradePrice") != null ? Float.valueOf(json.get("tradePrice").asText()) : null);
		t.setTradeQuantity(json.get("tradeQuantity") != null ? Float.valueOf(json.get("tradeQuantity").asText()) : null);
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
