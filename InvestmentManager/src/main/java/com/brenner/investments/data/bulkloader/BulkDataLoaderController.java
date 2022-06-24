package com.brenner.investments.data.bulkloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.entities.Transaction;
import com.brenner.investments.entities.constants.TransactionTypeEnum;
import com.brenner.investments.service.AccountsService;
import com.brenner.investments.service.InvestmentsService;
import com.brenner.investments.service.QuotesService;
import com.brenner.investments.service.TransactionsService;
import com.brenner.investments.util.CommonUtils;

/**
 * Controller for handling bulkd data requests
 * 
 * @author dbrenner
 *
 */
@Controller
public class BulkDataLoaderController {
    
    @Autowired
    InvestmentsService investmentsDataService;
    
    @Autowired
    AccountsService accountsDataService;
    
    @Autowired
    TransactionsService transactionsDataService;
    
    @Autowired
    QuotesService quotesService;
    
    @Autowired
    InvestmentsService investmentService;
    
   /**
    * Entry point for uploading bulk data.
    *  
    * @param dataType - The type of bulk data being uploaded.
    * @param delimiter - character that denotes unique fields
    * @param symbol - Investment identifier
    * @param file - File containing the bulk data
    * @param model - Object container
    * @return /loadBulkData
    * @throws BulkDataParseException - Common exception for errors
    * @throws IOException - File stream errors
    * @throws ParseException - Errors parsing the date
    */
    @RequestMapping("/parseBulkData")
    public String loadBulkInvestmentData(
            @RequestParam(name="dataType", required=true) String dataType, 
            @RequestParam(name="delimiter", required=true) String delimiter, 
            @RequestParam(name="symbol", required=false) String symbol,
            @RequestParam(name="bulkData") MultipartFile file,
            Model model) throws BulkDataParseException, IOException, ParseException {
        
        if (dataType.trim().equals("investment")) {
            List<Investment> investments = parseInvestmentsData(file.getInputStream(), delimiter);
            validateInvestmentsData(investments);
            this.investmentsDataService.saveInvestments(investments);
        }
        else if (dataType.trim().equals("transaction")) {
        	BulkTransactionHistoryLoader loader = new BulkTransactionHistoryLoader();
        	List<Transaction> transactions = loader.parseTransactionsData(file.getInputStream(), delimiter);
        	transactions = loader.validateAndPopulateTransactionData(transactions, this.accountsDataService, this.investmentsDataService, this.transactionsDataService);
        	
        	Iterator<Transaction> iter = transactions.iterator();
        	while (iter.hasNext()) {
        		Transaction t = iter.next();
        		
        		if (t.getTransactionType().equals(TransactionTypeEnum.Buy)) {
	        		this.transactionsDataService.persistBuy(
	        				t.getTransactionDate(), 
	        				t.getTradePrice(), 
	        				t.getTradeQuantity(), 
	        				t.getInvestment().getInvestmentId(), 
	        				t.getAccount().getAccountId());
        		}
        		/*else if (t.getTransactionType().equals(TransactionTypeEnum.Sell)) {
        			DataHelperUtil.sellHolding(
        					t.getTransactionId(), 
        					t.getTransactionDate(), 
        					t.getTradeQuantity(), 
        					t.getTradePrice(), 
        					this.transactionsDataService);
        		}*/
        	}
        }
        else if (dataType.trim().equals("mutualFundQuotes")) {
        	
        	if (symbol == null || symbol.trim().length() == 0) {
        		throw new BulkDataParseException("Symbol cannot be null for Mutual Fund Quotes.");
        	}
        	
        	// validate symbol
        	Investment investment = this.investmentsDataService.getInvestmentBySymbol(symbol.toUpperCase());
        	
        	if (investment == null) {
        		throw new BulkDataParseException("Unable to locate investment for symbol: " + symbol);
        	}
        	
        	List<Quote> quotes = parseQuoteData(file.getInputStream(), delimiter, investment);
        	
        	this.quotesService.addQuotes(quotes);
        }
        
        return "/loadBulkData";
    }
    
    @RequestMapping("/loadBulkDataForm")
    public String loadBulkDataForm() {
        
        return "/loadBulkData";
    }
    
    /**
     * Method based on Yahoo Finance data download file format
     * 
     * @param fileInputStream Data input stream
     * @param delimiter - the record separator
     * @param investment - The investment the quote data is for
     * @return The {@link List} of quote objects created from the data
     * @throws IOException File stream errors
     * @throws ParseException - Date parsing errors
     */
    private List<Quote> parseQuoteData(InputStream fileInputStream, String delimiter, Investment investment) throws IOException, ParseException {
    	
    	List<Quote> quotes = new ArrayList<>();
    	
    	BufferedReader reader = null;
        
        try {
            reader = new BufferedReader(new InputStreamReader(fileInputStream));
        
        
            String line = null;
            Scanner scanner = null;
            int index = 0;
            
            // skip the first line which contains headers
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                
                Quote quote = new Quote();
                quote.setInvestment(investment);
                scanner = new Scanner(line);
                scanner.useDelimiter(delimiter);
                
                while (scanner.hasNext()) {
                    String data = scanner.next();
                    
                    switch (index) {
                    case 0:
                        quote.setDate(CommonUtils.convertDatePickerDateFormatStringToDate(data));
                        break;
                    case 1:
                        quote.setOpen(Float.valueOf(data));
                        break;
                    case 2:
                        quote.setHigh(Float.valueOf(data));
                        break;
                    case 3:
                        quote.setLow(Float.valueOf(data));
                        break;
                    case 4:
                    	quote.setClose(Float.valueOf(data));
                    	break;
                    }
                    
                    index++;
                }
            index = 0;
            quotes.add(quote);
            }
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
        
        return quotes;
    }
    
    /**
     * Validates that the list of investments has a symbol value
     * 
     * @param investments The list of investments to validate
     * @throws BulkDataParseException Thrown when an invalid investment is identified
     */
    private void validateInvestmentsData(List<Investment> investments) throws BulkDataParseException {
        
        for (Investment investment : investments) {
            if (investment.getSymbol() == null || investment.getSymbol().trim().length() == 0) {
                throw new BulkDataParseException("Invalid investment - no symbol");
            }
        }
    }

    /**
     * Parses the input stream into a List of Investments
     * 
     * @param fileInputStream - Input stream for the file
     * @param delimiter - Field separator
     * @return The list of investments
     * @throws IOException Errors with the input stream
     */
    private List<Investment> parseInvestmentsData(InputStream fileInputStream, String delimiter) throws IOException {

        List<Investment> investments = new ArrayList<>();
        
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader(new InputStreamReader(fileInputStream));
        
        
            String line = null;
            Scanner scanner = null;
            int index = 0;
            
            while ((line = reader.readLine()) != null) {
                
                Investment investment = new Investment();
                scanner = new Scanner(line);
                scanner.useDelimiter(delimiter);
                
                while (scanner.hasNext()) {
                    String data = scanner.next();
                    data = data.replaceAll("\"", "");
                    
                    switch (index) {
                    case 0:
                        investment.setCompanyName(data);
                        break;
                    case 1:
                        investment.setSymbol(data);
                        break;
                    case 2:
                        investment.setExchange(data);
                        break;
                    case 3:
                        investment.setSector(data);
                        break;
                    }
                    
                    index++;
                }
            index = 0;
            investments.add(investment);
            }
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
            
        return investments;
        
    }

}
