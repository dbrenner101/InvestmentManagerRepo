package com.brenner.portfoliomgmt.holdings.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brenner.portfoliomgmt.accounts.Account;
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
	
	/**
	 * GET for retrieving all {@link Holding}
	 * @return a {@link List} of all holdings
	 */
	@GetMapping("/allHoldings")
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
	@GetMapping("/allHoldingsForAccount/{accountId}")
	public List<Holding> allHoldingsForAccount(@PathVariable Long accountId) {
		log.info("Entered allHoldingsForAccount()");
		log.debug("Param: accountId: {}", accountId);
		
		List<Holding> holdings = this.holdingsService.getHoldingsForAccount(accountId);
		log.debug("Returning {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting allHoldingsForAccount()");
		return holdings;
	}
}