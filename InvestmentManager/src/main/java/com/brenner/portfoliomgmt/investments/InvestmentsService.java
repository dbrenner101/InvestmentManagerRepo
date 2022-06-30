/**
 * 
 */
package com.brenner.portfoliomgmt.investments;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.exception.NotFoundException;

/**
 * Data service specific to working with investments data
 * 
 * @author dbrenner
 *
 */
@Service
public class InvestmentsService {

	private static final Logger log = LoggerFactory.getLogger(InvestmentsService.class);
    
    @Autowired
    InvestmentsRepository investmentsRepo;
    
    public List<Investment> getInvestmentsBySymbolAlpha(String alpha) {
		log.info("Entered findLikeSymbol()");
		log.debug("Param: symbol: {}", alpha);
		
		if (alpha == null) {
			throw new InvalidRequestException("Search string must be non-null.");
		}
		
		List<Investment> investments = this.investmentsRepo.findLikeSymbol(alpha);
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		log.info("Exiting findLikeSymbol()");
		
		return investments;
    }

	/**
     * Deletes a specific investment;
     * 
     * @param investment
     */
    public void deleteInvestment(Investment investment) {
		log.info("Entered delete()");
		log.debug("Param: investment: {}", investment);
		
		if (investment== null || investment.getInvestmentId() == null) {
			throw new InvalidRequestException("investment and investmentId must not be null");
		}
		
		Optional<Investment> optInv = this.investmentsRepo.findById(investment.getInvestmentId());
		if (! optInv.isPresent()) {
			throw new NotFoundException("Investment with id " + investment.getInvestmentId() + " does not exist.");
		}
		
		this.investmentsRepo.delete(investment);
		
		log.info("Exiting delete()");
	}
    
    /**
     * Retrieves investments and quotes that are part of the portfolio.
     * 
     * @return List<Investment>
     */
    public List<Investment> getInvestmentsAndQuotesAssociatedWithAHolding() {
		log.info("Entered getInvestmentsForHoldingsWithQuotes()");
		
		List<Investment> investments = this.investmentsRepo.getInvestmentsAllForHoldingsWithQuotesOrderedBySymbol();
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		log.info("Exiting getInvestmentsForHoldingsWithQuotes()");
		
		return investments;
    }
    
    /**
     * Retrieves a list of investments ordered by symbol
     * 
     * @return {@link List}<Investment>
     */
    public List<Investment> getInvestmentsOrderedBySymbolAsc() {
		log.info("Entered findAllByOrderBySymbolAsc()");
		
		List<Investment> investments = this.investmentsRepo.findAll(Sort.by(Sort.Direction.ASC, "symbol"));
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		log.info("Exiting findAllByOrderBySymbolAsc()");
		
		return investments;
    }
    
    /**
     * Retrieves a specific investment based on symbol
     * 
     * @param symbol - investment identifier
     * @return {@link Investment}
     */
    public Optional<Investment> getInvestmentBySymbol(String symbol) {
		log.info("Entered findBySymbol()");
		log.debug("Param: symbol: {}", symbol);
		
		if (symbol == null) {
			throw new InvalidRequestException("symbol must not be null.");
		}
		
		Optional<Investment> optInv = this.investmentsRepo.findBySymbol(symbol);
		
		log.debug("Retrieved investment: {}", optInv);
		log.info("Exiting findBySymbol()");
		
		return optInv;
    }
    
    /**
     * Persists the investment 
     * 
     * @param investment - investment to save
     * @return {@link Investment}
     */
    @Transactional
    public Investment saveInvestment(Investment investment) {
		log.info("Entered save()");
		log.debug("Param: investment: {}", investment);
		
		if (investment == null) {
			throw new InvalidRequestException("investment must not be null");
		}
		
		Investment inv = this.investmentsRepo.save(investment);
		
		log.info("Exiting save()");
		return inv;
    }
    
    /**
     * Passes a list of investments to the  persistence tier for saving.
     * 
     * @param investments - list of investments
     */
    @Transactional
    public void saveInvestments(List<Investment> investments) {
		log.info("Entered saveAll()");
		log.debug("Received {} investments to save.", investments != null ? investments.size() : 0);
		
		if (investments == null) {
			throw new InvalidRequestException("investments must not be null");
		}
		
		Iterator<Investment> iter = investments.iterator();
		while (iter.hasNext()) {
			saveInvestment(iter.next());
		}
		
		log.info("Exiting saveAll()");
    }
    
    /**
     * Retrieves all investments
     * 
     * @return {@link Iterable}<Investment>
     */
    public List<Investment> getAllInvestmentsSortedBySymbol() {
		log.info("Entered findAllByOrderBySymbolAsc()");
		
		List<Investment> investments = this.investmentsRepo.findAll(Sort.by(Sort.Direction.ASC, "symbol"));
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		log.info("Exiting findAllByOrderBySymbolAsc()");
		
		return investments;
    }
    
    /**
     * Retrieves a specific investment
     * 
     * @param investmentId - investment unique identifier
     * @return {@link Investment}
     */
    public Optional<Investment> getInvestmentByInvestmentId(Long investmentId) {
		log.info("Entered findById()");
		log.debug("Param: investmentId: {}", investmentId);
		
		if (investmentId == null) {
			throw new InvalidRequestException("investmentId must not be null");
		}
		
		Optional<Investment> optInv = this.investmentsRepo.findById(investmentId);
		
		log.debug("Retrieved investment: {}", optInv);
		log.info("Exiting findById()");
		
		return optInv;
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
}
