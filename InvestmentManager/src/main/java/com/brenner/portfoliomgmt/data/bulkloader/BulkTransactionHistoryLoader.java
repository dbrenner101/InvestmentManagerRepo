package com.brenner.portfoliomgmt.data.bulkloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.brenner.portfoliomgmt.accounts.Account;
import com.brenner.portfoliomgmt.accounts.AccountsService;
import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.investments.InvestmentsService;
import com.brenner.portfoliomgmt.transactions.Transaction;
import com.brenner.portfoliomgmt.transactions.TransactionTypeEnum;
import com.brenner.portfoliomgmt.transactions.TransactionsService;
import com.brenner.portfoliomgmt.util.CommonUtils;

/**
 * Class specific to parsing transaction history data
 * 
 * @author dbrenner
 *
 */
public class BulkTransactionHistoryLoader {
	
	private static final int ACCOUNT_NUM_COL = 0;
	private static final int DATE_COL = 2;
	private static final int SECURITY_COL = 4;
	private static final int DESC_COL = 6;
	private static final int QUANTITY_COL = 10;
	private static final int PRICE_COL = 11;
	
	/**
	 * Parses the fileinput stream into a list of transactions
	 * 
	 * @param fileInputStream - Input stream of data
	 * @param delimiter - field separator
	 * @return The list of transactions
	 * @throws IOException Errors reading from the stream
	 * @throws ParseException Errors parsing dates
	 * @throws BulkDataParseException General class level errors
	 */
	public List<Transaction> parseTransactionsData(InputStream fileInputStream, String delimiter) 
			throws IOException, ParseException, BulkDataParseException {

        List<Transaction> transactions = new ArrayList<>();
        
        BufferedReader reader = null;
        CSVParser parser = null;
        
        try {
            reader = new BufferedReader(new InputStreamReader(fileInputStream));
            
            parser = new CSVParser(reader, CSVFormat.DEFAULT);
            
            for (CSVRecord record : parser) {
            	
            	try {
            		record.get(BulkTransactionHistoryLoader.PRICE_COL);
            	}
            	catch (ArrayIndexOutOfBoundsException aie) {
            		throw new BulkDataParseException("Invalid record at line: " + record.getRecordNumber(), aie);
            	}
            	
            	boolean validRecord = true;

                String accountNumStr = record.get(BulkTransactionHistoryLoader.ACCOUNT_NUM_COL);
                String dateStr = record.get(BulkTransactionHistoryLoader.DATE_COL);
                String symbolStr = record.get(BulkTransactionHistoryLoader.SECURITY_COL);
                String descStr = record.get(BulkTransactionHistoryLoader.DESC_COL);
                String quantityStr = record.get(BulkTransactionHistoryLoader.QUANTITY_COL).replaceAll(",", "");
                String priceStr = record.get(BulkTransactionHistoryLoader.PRICE_COL).replaceAll(",", "");
                
                if (symbolStr == null || symbolStr.trim().length() == 0) {
                	validRecord = false;
                }
                if (! (descStr.contains("BUY ") || descStr.contains("SELL "))) {
                	validRecord = false;
                }
                
                if (validRecord) {
                	Transaction transaction = new Transaction();
                    Account account = new Account();
                    Investment investment = new Investment();
                    
                    transaction.setAccount(account);
                    transaction.setInvestment(investment);
                    
                    account.setAccountNumber(accountNumStr);
                    investment.setSymbol(symbolStr);
                    
                    transaction.setTradePrice(new BigDecimal(priceStr));
                    transaction.setTradeQuantity(new BigDecimal(quantityStr));
                    if (descStr.contains("BUY")) {
                    	transaction.setTransactionType(TransactionTypeEnum.Buy);
                    }
                    else if (descStr.contains("SELL")) {
                    	transaction.setTransactionType(TransactionTypeEnum.Sell);
                    }
                    transaction.setTransactionDate(CommonUtils.convertCommonDateFormatStringToDate(dateStr));
                    
                    transactions.add(transaction);
                }
            }
        }
        finally {
            if (reader != null) {
                reader.close();
            }
            if (parser != null) {
            	parser.close();
            }
        }
            
        return transactions;
        
    }
	
	/**
	 * Decorates the transaction data with Account and Investment details 
	 * 
	 * @param transactions - List of transactions
	 * @param accountsDataService Data service supporting {@link Account} data
	 * @param investmentsDataService Data service supporting {@link Investment} data
	 * @param transactionsDataService Data service supporting {@link Transaction} data
	 * @return The decorated list of transactions
	 * @throws BulkDataParseException Class level exception
	 */
	public List<Transaction> validateAndPopulateTransactionData(
			List<Transaction> transactions, 
			AccountsService accountsDataService, 
			InvestmentsService investmentsDataService, 
			TransactionsService transactionsDataService) 
					throws BulkDataParseException {
		
		if (transactions == null || transactions.isEmpty()) {
			throw new BulkDataParseException("Empty transactions list");
		}
		
		Iterator<Transaction> iter = transactions.iterator();
		while (iter.hasNext()) {
			Transaction transaction = iter.next();
			
			Account account = transaction.getAccount();
			Investment investment = transaction.getInvestment();
			
			String accountNumber = account.getAccountNumber();
			Optional<Account> optAccount = accountsDataService.getAccountByAccountNumber(accountNumber);
			account = optAccount.get();
			
			if (account == null) {
				throw new BulkDataParseException("Account cannot be validated with account number: " + accountNumber);
			}
			transaction.setAccount(account);
			
			String symbol = investment.getSymbol();
			investment = investmentsDataService.getInvestmentBySymbol(symbol).get();
			if (investment == null) {
				throw new BulkDataParseException("Investment cannot be validated with symbol: " + symbol);
			}
			transaction.setInvestment(investment);
			
			if (transaction.getTradePrice() == null || transaction.getTradeQuantity() == null || transaction.getTransactionDate() == null || transaction.getTransactionType() == null) {
				throw new BulkDataParseException("Transaction is not valid");
			}
		}
		
		
		return transactions;
	}
}
