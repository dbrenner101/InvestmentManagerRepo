package com.brenner.portfoliomgmt.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brenner.portfoliomgmt.data.entities.AccountDTO;
import com.brenner.portfoliomgmt.data.entities.HoldingDTO;
import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.domain.BucketEnum;
import com.brenner.portfoliomgmt.domain.Holding;
import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.service.AccountsService;
import com.brenner.portfoliomgmt.service.HoldingsService;

/**
 * Spring REST controller for interacting with Holdings
 * 
 * @author dbrenner
 *
 */
@RestController
@RequestMapping("/api")
public class HoldingsRestController {

	private static final Logger log = LoggerFactory.getLogger(HoldingsRestController.class);
	
	@Autowired
	HoldingsService holdingsService;
	
	@Autowired
	AccountsService accountsService;
	
	/**
	 * GET for retrieving all {@link HoldingDTO}
	 * @return a {@link List} of all holdings
	 */
	@GetMapping("/holdings")
	public List<Holding> allHoldings() {
		log.info("Entered allHoldings()");
		
		List<Holding> holdings = this.holdingsService.findAllHoldingsOrderedBySymbol();
		for (Holding holding : holdings) {
			holding.setValueAtPurchase(holding.getPurchasePrice().multiply(holding.getQuantity()));
		}
		log.debug("Returning {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting allHoldings()");
		return holdings;
	}
	
	@GetMapping("/holdings/bucket/{bucketEnum}")
	public List<Holding> allHoldingsByBucketEnum(@PathVariable String bucketEnum) {
		
		BucketEnum bucket = BucketEnum.valueOf(bucketEnum);
		
		List<Holding> holdings = this.holdingsService.findHoldingsByBucket(bucket);
		for (Holding holding : holdings) {
			holding.setValueAtPurchase(holding.getPurchasePrice().multiply(holding.getQuantity()));
		}
		
		return holdings;
	}
	
	/**
	 * Retrieves {@link HoldingDTO} for a specific account
	 * 
	 * @param accountId - {@link AccountDTO} unique identifier
	 * @return a {@link List} of Holdings for the account
	 */
	@GetMapping("/holdings/account/{accountId}")
	public List<Holding> allHoldingsForAccount(@PathVariable Long accountId) {
		log.info("Entered allHoldingsForAccount()");
		log.debug("Param: accountId: {}", accountId);
        
        Optional<Account> optAccount = this.accountsService.findAccountByAccountId(accountId);
        
        if (! optAccount.isPresent()) {
        	throw new NotFoundException("Account with id " + accountId + " does not exist.");
        }
		
		List<Holding> holdings = this.holdingsService.findHoldingsForAccount(Long.valueOf(accountId));
        
        for (Holding holding : holdings) {
        	BigDecimal valueAtPurchase = holding.getPurchasePrice().multiply(holding.getQuantity());
			holding.setValueAtPurchase(valueAtPurchase);
			
			Quote mostRecentQuote = holding.getMostRecentQuote();
			if (mostRecentQuote != null) {
				BigDecimal close = mostRecentQuote.getClose();
				holding.setCurrentValue(holding.getQuantity().multiply(close));
				holding.setChangeInValue(holding.getCurrentValue().subtract(valueAtPurchase));
			}
        }
        
		return holdings;
	}
}
