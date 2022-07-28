/**
 * 
 */
package com.brenner.portfoliomgmt.util;

import java.util.Comparator;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.brenner.portfoliomgmt.data.entities.AccountDTO;
import com.brenner.portfoliomgmt.data.entities.HoldingDTO;
import com.brenner.portfoliomgmt.data.entities.InvestmentDTO;
import com.brenner.portfoliomgmt.data.entities.QuoteDTO;
import com.brenner.portfoliomgmt.data.entities.TransactionDTO;
import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.domain.Holding;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.domain.Transaction;

/**
 *
 * @author dbrenner
 * 
 */
public class ObjectMappingUtil {
	
	static ModelMapper modelMapper = new ModelMapper();
	
	public static List<Account> mapAccountDtoList(List<AccountDTO> accounts) {
		return accounts.stream().map(a -> mapAccountDtoToAccount(a)).toList();
	}
	
	public static List<Investment> mapInvestmentDtoList(List<InvestmentDTO> investments) {
		return investments.stream().map(i -> mapInvestmentDtoToInvestment(i)).toList();
	}
	
	public static List<Holding> mapHoldingDtoList(List<HoldingDTO> holdings) {
		return holdings.stream().map(h -> mapHoldingDtoToHolding(h)).toList();
	}
	
	public static List<Transaction> mapTransactionDtoList(List<TransactionDTO> transactions) {
		return transactions.stream().map(t -> mapTransactionDtoToTransaction(t)).toList();
	}
	
	public static List<Quote> mapQuoteDtoList(List<QuoteDTO> quotes) {
		return quotes.stream().map(q -> mapQuoteDtoToQuote(q)).toList();
	}
	
	public static Account mapAccountDtoToAccount(AccountDTO accountData) {
		Account account = modelMapper.map(accountData, Account.class);
		return account;
	}
	
	public static AccountDTO mapAccountToAccountDTO(Account account) {
		AccountDTO accountData = modelMapper.map(account, AccountDTO.class);
		return accountData;
	}
	
	public static Investment mapInvestmentDtoToInvestment(InvestmentDTO investmentData) {
		Investment investment = modelMapper.map(investmentData, Investment.class);
		return investment;
	}
	
	public static InvestmentDTO mapInvestmentToInvestmentDTO(Investment investment) {
		InvestmentDTO investmentData = modelMapper.map(investment, InvestmentDTO.class);
		return investmentData;
	}
	
	public static Holding mapHoldingDtoToHolding(HoldingDTO holdingData) {
		Holding holding = modelMapper.map(holdingData, Holding.class);
		if (holdingData.getInvestment() != null && holdingData.getInvestment().getQuotes() != null) {
			List<QuoteDTO> quotes = holdingData.getInvestment().getQuotes();
			if (quotes.size() > 1) {
				Comparator<QuoteDTO> quoteDTOComparator = Comparator.comparing(QuoteDTO::getDate);
				QuoteDTO mostRecentQuote = quotes.stream().max(quoteDTOComparator).get();
				holding.setMostRecentQuote(mapQuoteDtoToQuote(mostRecentQuote));
			}
			else {
				Quote mostRecentQuote = mapQuoteDtoToQuote(quotes.get(0));
				holding.setMostRecentQuote(mostRecentQuote);
			}
		}
		return holding;
	}
	
	public static HoldingDTO mapHoldingToHoldingDto(Holding holding) {
		HoldingDTO holdingData = modelMapper.map(holding, HoldingDTO.class);
		return holdingData;
	}
	
	public static Transaction mapTransactionDtoToTransaction(TransactionDTO transactionData) {
		Transaction transaction = modelMapper.map(transactionData, Transaction.class);
		return transaction;
	}
	
	public static TransactionDTO mapTransactionToTransactionDTO(Transaction transaction) {
		TransactionDTO transactionData = modelMapper.map(transaction, TransactionDTO.class);
		return transactionData;
	}
	
	public static Quote mapQuoteDtoToQuote(QuoteDTO quoteData) {
		Quote quote = modelMapper.map(quoteData, Quote.class);
		return quote;
	}
	
	public static QuoteDTO mapQuoteToQuoteDTO(Quote quote) {
		QuoteDTO quoteData = modelMapper.map(quote, QuoteDTO.class);
		return quoteData;
	}

}
