package com.brenner.portfoliomgmt.holdings.api;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brenner.portfoliomgmt.accounts.Account;
import com.brenner.portfoliomgmt.accounts.AccountsService;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.holdings.Holding;
import com.brenner.portfoliomgmt.holdings.HoldingsService;

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
	 * GET for retrieving all {@link Holding}
	 * @return a {@link List} of all holdings
	 */
	@GetMapping("/holdings")
	public List<Holding> allHoldings() {
		log.info("Entered allHoldings()");
		
		List<Holding> holdings = this.holdingsService.getAllHoldingsOrderedBySymbol();
		log.debug("Returning {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting allHoldings()");
		return holdings;
	}
	
	/**
	 * Retrieves {@link Holding} for a specific account
	 * 
	 * @param accountId - {@link Account} unique identifier
	 * @return a {@link List} of Holdings for the account
	 */
	@GetMapping("/holdings/account/{accountId}")
	public List<Holding> allHoldingsForAccount(@PathVariable Long accountId) {
		log.info("Entered allHoldingsForAccount()");
		log.debug("Param: accountId: {}", accountId);
		
		List<Holding> holdings = this.holdingsService.getHoldingsForAccount(Long.valueOf(accountId));
        
        if (holdings != null) {
        	Collections.sort(holdings);
        }
        
        log.debug("Retrieved {} accounts", holdings != null ? holdings.size() : 0);
        
        Optional<Account> optAccount = this.accountsService.getAccountAndCash(Long.valueOf(accountId));
        
        if (! optAccount.isPresent()) {
        	throw new NotFoundException("Account with id " + accountId + " does not exist.");
        }
        
        Account account = optAccount.get();
        BigDecimal totalValueChange = BigDecimal.ZERO;
        BigDecimal totalStockValue = BigDecimal.ZERO;
        
        if (holdings != null && ! holdings.isEmpty()) {
            holdings.get(0).setAccount(account);
            
            for(Holding holding : holdings) {
            	log.debug("Calc changes in holding: {}", holding);
                totalValueChange = holding.getChangeInValue() == null ? BigDecimal.ZERO : totalValueChange.add(holding.getChangeInValue());
                totalStockValue = holding.getCurrentValue() == null ? BigDecimal.ZERO : totalStockValue.add(holding.getCurrentValue());
            }
        }
        
        log.debug("Total value change: {}", totalValueChange);
        log.debug("Total stock value: {}", totalStockValue);
        log.info("Serializing holdings to JSON");
        
		return holdings;
	}
}
