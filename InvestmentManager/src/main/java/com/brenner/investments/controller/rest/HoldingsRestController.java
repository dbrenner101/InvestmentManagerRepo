package com.brenner.investments.controller.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.brenner.investments.entities.Account;
import com.brenner.investments.entities.Holding;
import com.brenner.investments.service.TransactionsService;

/**
 * Spring REST controller for interacting with Holdings
 * 
 * @author dbrenner
 *
 */
@RestController
public class HoldingsRestController {

	private static final Logger log = LoggerFactory.getLogger(HoldingsRestController.class);
	
	@Autowired
	TransactionsService transactionService;
	
	/**
	 * GET for retrieving all {@link Holding}
	 * @return a {@link List} of all holdings
	 */
	@GetMapping("/restful/allHoldings")
	public List<Holding> allHoldings() {
		log.info("Entered allHoldings()");
		
		List<Holding> holdings = this.transactionService.getAllHoldingsOrderedBySymbol();
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
	@GetMapping("/restful/allHoldingsForAccount/{accountId}")
	public List<Holding> allHoldingsForAccount(@PathVariable Long accountId) {
		log.info("Entered allHoldingsForAccount()");
		log.debug("Param: accountId: {}", accountId);
		
		List<Holding> holdings = this.transactionService.getHoldingsForAccount(accountId);
		log.debug("Returning {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting allHoldingsForAccount()");
		return holdings;
	}
}
