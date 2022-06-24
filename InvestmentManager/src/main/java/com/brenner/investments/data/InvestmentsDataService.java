package com.brenner.investments.data;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.brenner.investments.entities.Investment;

/**
 * Data service for {@link Investment}
 * @author dbrenner
 *
 */
@Component
public class InvestmentsDataService {
	
	private static final Logger log = LoggerFactory.getLogger(InvestmentsDataService.class);
	
	@Autowired
	InvestmentsRepository investmentsRepo;
	
	/**
	 * Retrieve a specific {@link Investment}
	 * 
	 * @param investmentId investment identifier
	 * @return The investment 
	 */
	public Investment findById(Long investmentId) {
		log.info("Entered findById()");
		log.debug("Param: investmentId: {}", investmentId);
		
		Investment inv = this.investmentsRepo.findById(investmentId).get();
		
		log.debug("Retrieved investment: {}", inv);
		
		log.info("Exiting findById()");
		return inv;
	}
	
	/**
	 * Retrieve a specific investment
	 * 
	 * @param symbol - alternate unique identifier
	 * @return The {@link Investment}
	 */
	public Investment findBySymbol(String symbol) {
		log.info("Entered findBySymbol()");
		log.debug("Param: symbol: {}", symbol);
		
		Investment inv = null;
		try {
			Optional<Investment> optInv = this.investmentsRepo.findBySymbol(symbol);
			if (optInv.isPresent()) {
				inv = optInv.get();
			}
		}
		catch (EmptyResultDataAccessException e) {
			// object not found - eat the exception
		}
		log.debug("Retrieved investment: {}", inv);
		
		log.info("Exiting findBySymbol()");
		return inv;
	}
	
	/**
	 * Retrieves a list of investments whose symbol starts with the supplied letter.
	 * 
	 * @param symbol - Letter to base query on
	 * @return A {@link List} of {@link Investment}s
	 */
	public List<Investment> findBySymbolLike(String symbol) {
		log.info("Entered findLikeSymbol()");
		log.debug("Param: symbol: {}", symbol);
		
		List<Investment> investments = this.investmentsRepo.findLikeSymbol(symbol);
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		
		log.info("Exiting findLikeSymbol()");
		return investments;
	}
	
	/**
	 * Retrieves all investments and orders them by Company Name
	 * 
	 * @return A {@link List} of {@link Investment}s
	 */
	public List<Investment> findAllByOrderByCompanyNameAsc() {
		log.info("Entered findAllByOrderByCompanyNameAsc()");
		
		List<Investment> investments = this.investmentsRepo.findAll(Sort.by(Sort.Direction.ASC, "companyName"));
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		
		log.info("Exiting findAllByOrderByCompanyNameAsc()");
		return investments;
	}
	
	/**
	 * Retrieves a list of investments ordered by symbol.
	 * 
	 * @return A {@link List} of {@link Investment}s
	 */
	public List<Investment> findAllByOrderBySymbolAsc() {
		log.info("Entered findAllByOrderBySymbolAsc()");
		
		List<Investment> investments = this.investmentsRepo.findAll(Sort.by(Sort.Direction.ASC, "symbol"));
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		
		log.info("Exiting findAllByOrderBySymbolAsc()");
		return investments;
	}
	
	/**
	 * Retrieves all investments associated with a holding
	 * 
	 * @return A {@link List} of {@link Investment}s
	 */
	public List<Investment> findInvestmentsForHoldingsOrderedBySymbol() {
		log.info("Entered findIvestmentsForHoldingsOrderedBySymbol()");
		
		List<Investment> investments = this.investmentsRepo.findInvestmentsForHoldingsOrderedBySymbol();
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		
		log.info("Exiting findIvestmentsForHoldingsOrderedBySymbol()");
		return investments;
	}
	
	/**
	 * Retrieves all investments with their associated quotes
	 * 
	 * @return A {@link List} of {@link Investment}s
	 */
	public List<Investment> getInvestmentsForHoldingsWithQuotes() {
		log.info("Entered getInvestmentsForHoldingsWithQuotes()");
		
		List<Investment> investments = this.investmentsRepo.getInvestmentsAllForHoldingsWithQuotesOrderedBySymbol();
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		
		log.info("Exiting getInvestmentsForHoldingsWithQuotes()");
		return investments;
	}
	
	/**
	 * Retrieves a unique list of investments associated with a holding
	 * 
	 * @return A {@link List} of {@link Investment}s
	 */
	public List<Investment> findUniqueInvestmentsForHoldingsOrderBySymbol() {
		log.info("Entered findUniqueInvestmentsForHoldingsOrderBySymbol()");
		
		List<Investment> investments = this.investmentsRepo.findUniqueInvestmentsForHoldingsOrderBySymbol();
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		
		log.info("Exiting findUniqueInvestmentsForHoldingsOrderBySymbol()");
		return investments;
	}
	
	/**
	 * Delete a specific investment
	 * 
	 * @param investment - {@link Investment} to delete. Must include investmentId
	 */
	@Transactional
	public void delete(Investment investment) {
		log.info("Entered delete()");
		log.debug("Param: investment: {}", investment);
		
		if (investment.getInvestmentId() == null) {
			throw new InvalidDataRequestException("investmentId must not be null");
		}
		
		this.investmentsRepo.delete(investment);
		
		log.info("Exiting delete()");
	}
	
	/**
	 * Entry point for saving a list of investment. Each investment is saved individually - not batch
	 * 
	 * @param investments List of investments to save
	 */
	@Transactional
	public void saveAll(List<Investment> investments) {
		log.info("Entered saveAll()");
		log.debug("Received {} investments to save.", investments != null ? investments.size() : 0);
		
		Iterator<Investment> iter = investments.iterator();
		while (iter.hasNext()) {
			save(iter.next());
		}
		
		log.info("Exiting saveAll()");
	}
	
	/**
	 * Entry point to save or update an investment
	 * 
	 * @param investment - investment to persist
	 * @return the updated or added investment
	 */
	@Transactional
	public Investment save(Investment investment) {
		log.info("Entered save()");
		log.debug("Param: investment: {}", investment);
		
		Investment inv = this.investmentsRepo.save(investment);
		
		log.info("Exiting save()");
		return inv;
	}

}
