package com.brenner.portfoliomgmt.domain.serialize;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.portfoliomgmt.domain.Account;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class AccountSerializer extends StdSerializer<Account> {
	
	private static final long serialVersionUID = -5911708456939187261L;
	
	private static final Logger log = LoggerFactory.getLogger(AccountSerializer.class);

	protected AccountSerializer(Class<Account> t) {
		super(t);
	}
	
	public AccountSerializer() {
		super(Account.class);
	}


	@Override
	public void serialize(Account account, JsonGenerator gen, SerializerProvider provider) throws IOException {
		
		log.info("Entered serialize()");
		log.debug("Converting account: {} to json", account);
		
		gen.writeStartObject();
			gen.writeNumberField("accountId", account.getAccountId());
			gen.writeStringField("accountName", account.getAccountName());
			gen.writeStringField("accountNumber", account.getAccountNumber());
			gen.writeStringField("accountType", account.getAccountType());
			gen.writeStringField("company", account.getCompany());
			gen.writeStringField("owner", account.getOwner());
			gen.writeNumberField("cashOnAccount", account.getCashOnAccount() != null ? account.getCashOnAccount().floatValue() : 0);
		gen.writeEndObject();
		
		log.info("Exiting serialize()");
		
	}

}
