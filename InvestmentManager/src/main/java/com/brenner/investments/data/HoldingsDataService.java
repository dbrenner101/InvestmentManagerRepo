package com.brenner.investments.data;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.investments.entities.Holding;

/**
 * Data service for {@link Holding}
 * 
 * @author dbrenner
 *
 */
@Component
public class HoldingsDataService {
	
	private static final Logger log = LoggerFactory.getLogger(HoldingsDataService.class);

	@Autowired
	HoldingsRepository holdingsRepo;
	
	/**
	 * Retrieves the holdings associated with an investment
	 * 
	 * @param investmentId - investment unique identifier
	 * @return A {@link List} of holdings associated with the investment
	 */
	public List<Holding> getHoldingsByInvestmentId(Long investmentId) {
		log.info("Entered getHoldingsByInvestmentId()");
		
		List<Holding> holdings = this.holdingsRepo.findHoldingsByInvestmentInvestmentId(investmentId);
		
		log.info("Exiting getHoldingsByInvestmentId()");
		return holdings;
	}
	
	/**
	 * Rerieves all holdings
	 * 
	 * @return A {@link List} of {@link Holding}s
	 */
	public List<Holding> findAll() {
		log.info("Entered findAll()");
		
		List<Holding> holdings = this.holdingsRepo.findAll();
				
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findAll()");
		return holdings;
	}
	
	/**
	 * Retrieves a specific {@link Holding}
	 * 
	 * @param holdingId - {@link Holding} unique identifier
	 * @return The {@link Holding}
	 */
	public Holding findById(Long holdingId) {
		log.info("Entered findAll()");
		log.debug("Param: holdingId: {}", holdingId);
		
		if (holdingId == null) {
			throw new InvalidDataRequestException("holdingId must be non-null");
		}
		
		Optional<Holding> optHolding = this.holdingsRepo.findById(holdingId);
		
		Holding holding = null;
		
		if (optHolding.isPresent()) {
			holding = optHolding.get();
		}
		log.debug("Retrieved {} holding", holding);
		
		log.info("Exiting findAll()");
		return holding;
	}
	
	/**
	 * Retrieves all holdings ordered by most recent market value
	 * 
	 * @return A {@link List} of {@link Holding}s
	 */
	public List<Holding> findHoldingsByMarketValueOrderedDesc(String sortOrder) {
		log.info("Entered findHoldingsByMarketValueOrderedDesc()");
		log.debug("Param: sortOrder: {}", sortOrder);
		
		List<Holding> holdings = this.holdingsRepo.getAllHoldingsOrderedByMarketValue(sortOrder);
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findHoldingsByMarketValueOrderedDesc()");
		return holdings;
	}
	
	/**
	 * Retrieves holdings ordered by a change in value
	 * 
	 * @return A {@link List} of {@link Holding}s
	 */
	public List<Holding> findHoldingByChangeInValueOrderedDesc() {
		log.info("Entered findHoldingByChangeInValueOrderedDesc()");
		
		List<Holding> holdings = this.holdingsRepo.getAllHoldingsOrderedByChangeInValue();
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findHoldingByChangeInValueOrderedDesc()");
		return holdings;
	}
	
	/**
     * Attempts to locate a holding based on the account identifier and investment identifier
     * 
     * @param accountId - unique account id
     * @param investmentId - unique investment id
     * @return A {@link List} of {@link Holding}s
     */
	public List<Holding> findByAccountIdAndInvestmentId(Long accountId, Long investmentId) {
		log.info("Entered findByAccountIdAndInvestmentId()");
		log.debug("Params: accountId: {}; investmentId: {}", accountId, investmentId);
		
		List<Holding> holdings = this.holdingsRepo.findByAccountAccountIdAndInvestmentInvestmentId(accountId, investmentId);
		
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findByAccountIdAndInvestmentId()");
		return holdings;
	}
	
	/**
	 * Located a list of holdings associated with an account
	 * 
	 * @param accountId - unique account identifier
	 * @return The {@link List} of {@link Holding}s
	 */
	public List<Holding> findByAccountAccountId(Long accountId) {
		log.info("Entered findByAccountId()");
		log.debug("Param: accountId: {}", accountId);
		
		List<Holding> holdings = this.holdingsRepo.findByAccountAccountId(accountId);
		
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findByAccountId()");
		return holdings;
	}
	
	/**
     * Retrieves all holdings ordered by their associated investment symbol
     * 
     * @return The {@link List} of {@link Holding}s
     */
	public List<Holding> findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol(Long accountId) {
		log.info("Entered findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol)");
		log.debug("Param: accountId: {}", accountId);
		
		List<Holding> holdings = this.holdingsRepo.findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol(accountId);
		
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol()");
		return holdings;
	}
	
	/**
	 * Retrieves all holdings ordered by their associated investment symbol
	 * 
	 * @return The {@link List} of {@link Holding}s
	 */
	public List<Holding> findHoldingsOrderByInvestmentSymbol() {
		log.info("Entered findHoldingsOrderByInvestmentSymbol()");
		
		List<Holding> holdings = this.holdingsRepo.findAll(Sort.by(Sort.Direction.ASC, "investment.symbol"));
		
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findHoldingsOrderByInvestmentSymbol()");
		return holdings;
	}
	
	/**
	 * Deletes a specific {@link Holding}
	 * 
	 * @param holding - Holding to delete, must contain a holdingId
	 */
	@Transactional
	public void delete(Holding holding) {
		log.info("Entered delete()");
		log.debug("Param: holdingId: {}", holding);
		
		if (holding.getHoldingId() == null) {
			throw new InvalidDataRequestException("holdingId must be non-null");
		}
		
		this.holdingsRepo.delete(holding);
		
		log.info("Exiting delete()");
	}
	
	/**
	 * Entry point for saving a {@link List} of holding objects. Each is saved individually - not batch
	 * 
	 * @param holdings - the {@link List} of holdings to save. 
	 */
	@Transactional
	public void saveAll(List<Holding> holdings) {
		log.info("Entered saveAll()");
		
		Iterator<Holding> iter = holdings.iterator();
		while (iter.hasNext()) {
			this.save(iter.next());
		}
		
		log.info("Exiting saveAll()");
	}
	
	/**
	 * Entry point for new and updated holdings.
	 * 
	 * @param holding The holding object to save
	 * @return the new or updated holding object. includes the holdingId key
	 */
	@Transactional
	public Holding save(Holding holding) {
		log.info("Entered saveHolding()");
		log.debug("Param: holding: {}", holding);
		
		Holding newHolding = this.holdingsRepo.save(holding);
		
		log.debug("Saved holding: {}", holding);
		
		log.info("Exiting saveHolding()");
		return newHolding;
	}

}
