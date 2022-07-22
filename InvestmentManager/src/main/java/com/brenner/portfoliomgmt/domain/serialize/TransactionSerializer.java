/**
 * 
 */
package com.brenner.portfoliomgmt.domain.serialize;

import java.io.IOException;

import com.brenner.portfoliomgmt.domain.Transaction;
import com.brenner.portfoliomgmt.util.CommonUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 *
 * @author dbrenner
 * 
 */
public class TransactionSerializer extends StdSerializer<Transaction> {
	
	private static final long serialVersionUID = -5536260627234946829L;

	protected TransactionSerializer(Class<Transaction> t) {
		super(t);
	}
	
	public TransactionSerializer() {
		super(Transaction.class);
	}

	@Override
	public void serialize(Transaction transaction, JsonGenerator gen, SerializerProvider provider) throws IOException {
		
		gen.writeStartObject();
			gen.writeNumberField("transactionId", transaction.getTransactionId());
			if (transaction.getAssociatedCashTransactionId() == null) {
				gen.writeNullField("associatedCashTransactionId");
			} else {
				gen.writeNumberField("associatedCashTransactionId", transaction.getAssociatedCashTransactionId());
			}
			
			String transactionDate = "";
			if (transaction.getTransactionDate() != null) {
				transactionDate = CommonUtils.convertDateToMMYYDDString(transaction.getTransactionDate());
			}
			gen.writeStringField("transactionDate", transactionDate);
			
			gen.writeNumberField("tradePrice", transaction.getTradePrice());
			gen.writeNumberField("tradeQuantity", transaction.getTradeQuantity());
			gen.writeStringField("transactionType", transaction.getTransactionType().getDescription());
			
			gen.writeFieldName("Investment");
			ObjectMapper mapper = (ObjectMapper) gen.getCodec();
			String investmentStr = mapper.writeValueAsString(transaction.getInvestment());
			gen.writeRawValue(investmentStr);
			
			gen.writeFieldName("Account");
			String accountStr = mapper.writeValueAsString(transaction.getAccount());
			gen.writeRawValue(accountStr);
		gen.writeEndObject();
	}

}
