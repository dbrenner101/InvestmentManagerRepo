package com.brenner.investments.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Transaction;

public class DataHelperUtil {

	private static final Logger logger = LoggerFactory.getLogger(DataHelperUtil.class);
	
	/**
	 * Loops through the cash transactions and sums them to set the cash on account value on the supplied account object
	 * 
	 * @param account - target account object
	 * @param cashTransactions - list of transactions that may have cash
	 */
	public static void loadCashTransactionsToAccount(Account account, List<Transaction> cashTransactions) {
		
		logger.info("Entering loadCashTransactionsToAccount()");
		logger.debug("Parameters: account: {}; cashTransactions.size: {}"
				, account, cashTransactions != null ? cashTransactions.size() : 0);
		
		if (cashTransactions != null && ! cashTransactions.isEmpty()) {
			Float currentCash = 0F;
			for (Transaction cashTrans : cashTransactions) {
				if (cashTrans.getAccount().getAccountId().equals(account.getAccountId())) {
					currentCash += cashTrans.getTradeQuantity() * cashTrans.getTradePrice();
				}
			}
			
			logger.debug("Setting {} cash on account", currentCash);
			
			account.setCashOnAccount(currentCash);
		}
		
		logger.info("Exiting loadCashTransactionsToAccount()");
	}
	
	public static <E> List<E> toList(Iterable<E> iterable) {
	    if(iterable instanceof List) {
	      return (List<E>) iterable;
	    }
	    ArrayList<E> list = new ArrayList<E>();
	    if(iterable != null) {
	      for(E e: iterable) {
	        list.add(e);
	      }
	    }
	    return list;
	  }
	
}
