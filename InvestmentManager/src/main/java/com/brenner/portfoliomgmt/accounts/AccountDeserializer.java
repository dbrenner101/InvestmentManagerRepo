package com.brenner.portfoliomgmt.accounts;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Class to convert a JSON stream to an {@link Account} object
 * 
 * @author dbrenner
 *
 */
public class AccountDeserializer extends StdDeserializer<Account> {

	private static final Logger log = LoggerFactory.getLogger(AccountDeserializer.class);
	
	public AccountDeserializer() {
		this(null);
	} 
 
    public AccountDeserializer(Class<?> vc) { 
        super(vc); 
    }

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Account deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		log.info("Entered deserialize()");
		
		JsonNode json = jp.getCodec().readTree(jp);
		Account a = this.deserialize(json);
		log.debug("Deserialized to account: {}", a);
		
		log.info("Exiting deserialize()");
		return a;
	}
	
	/**
	 * Reads the {@link JsonNode} into an {@link Account} object
	 * 
	 * @param json JSON representation of an Account object
	 * @return The Account object
	 */
	public Account deserialize(JsonNode json) {
		log.info("Entered deserialize()");
		
		Account a = new Account();a.setAccountId(json.get("accountId") != null ? json.get("accountId").asLong() : null);
		a.setAccountName(json.get("accountName") != null ? json.get("accountName").asText() : null);
		a.setAccountNumber(json.get("accountNumber") != null ? json.get("accountNumber").asText() : null);
		a.setAccountType(json.get("accountType") != null ? json.get("accountType").asText() : null);
		a.setCompany(json.get("company") != null ? json.get("company").asText() : null);
		a.setOwner(json.get("owner") != null ? json.get("owner").asText() : null);
		log.debug("Account: {}", a);
		
		log.info("Exiting deserialize()");
		return a;
	}

}
