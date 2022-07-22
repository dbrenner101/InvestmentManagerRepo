package com.brenner.portfoliomgmt.batch.holdings;

import java.util.List;
import java.util.Optional;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.domain.Holding;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Transaction;
import com.brenner.portfoliomgmt.domain.TransactionTypeEnum;
import com.brenner.portfoliomgmt.service.AccountsService;
import com.brenner.portfoliomgmt.service.HoldingsService;
import com.brenner.portfoliomgmt.service.InvestmentsService;
import com.brenner.portfoliomgmt.service.QuotesService;

@Service
public class HoldingsUploadRowProcessor implements ItemProcessor<NewHoldingsUploadRowInstance, NewHoldingsUploadRowInstance> {
	
	@Autowired AccountsService accountsService;
	@Autowired HoldingsService holdingsService;
	@Autowired InvestmentsService investmentsService;
	@Autowired QuotesService quotesService;
	
    @Override
    public NewHoldingsUploadRowInstance process(final NewHoldingsUploadRowInstance rowInstance) throws Exception {
    	
    	Account account = new Account();
    	account.setAccountName(rowInstance.getAccountName());
    	
    	Optional<Account> optAccount = this.accountsService.findAccountByAccountName(account.getAccountName());
    	if (! optAccount.isPresent()) {
    		throw new Exception("Unable to locate account with name: " + rowInstance.getAccountName());
    	}
    	account = optAccount.get();
    	
    	Investment investment = new Investment();
    	investment.setSymbol(rowInstance.getInvestmentSymbol());
    	Optional<Investment> optInvestment = this.investmentsService.findInvestmentBySymbol(investment.getSymbol());
    	if (! optInvestment.isPresent()) {
    		investment = this.investmentsService.saveInvestment(investment);
    	}
    	else {
    		investment = optInvestment.get();
    	}
    	
    	final Holding holding = new Holding();
    	holding.setAccount(account);
    	holding.setInvestment(investment);
    	holding.setPurchaseDate(rowInstance.getAcquiredDate());
    	holding.setPurchasePrice(rowInstance.getSharePrice());
    	holding.setQuantity(rowInstance.getQuantity());
    	
    	
    	List<Holding> holdings = this.holdingsService.findHoldingByAccountAccountIdAndInvestmentInvestmentId(account.getAccountId(), investment.getInvestmentId());
    	Optional<Holding> optHolding = holdings.stream()
    			.filter(h -> h.getPurchaseDate().equals(holding.getPurchaseDate()))
    			.filter(h -> h.getQuantity().equals(holding.getQuantity()))
    			.filter(h -> h.getPurchasePrice().equals(holding.getPurchasePrice()))
    			.findFirst();
    	if (optHolding.isPresent()) {
    		throw new Exception("Holding appears to be a duplicate. New holding: " + holding + "; found holding: " + optHolding.get());
    	}
    	
    	Transaction transaction = new Transaction();
    	transaction.setHolding(holding);
    	transaction.setAccount(account);
    	transaction.setTradePrice(rowInstance.getSharePrice());
    	transaction.setTransactionDate(rowInstance.getAcquiredDate());
    	transaction.setTradeQuantity(rowInstance.getQuantity());
    	
    	if (rowInstance.getSharePrice().signum() == 0) {
    		transaction.setTransactionType(TransactionTypeEnum.Revinvest_Dividend);
    	}
    	else {
    		transaction.setTransactionType(TransactionTypeEnum.Buy);
    	}
    	holding.addTransaction(transaction);
    	this.holdingsService.saveHolding(holding);
    	
        return rowInstance;
    }
}
