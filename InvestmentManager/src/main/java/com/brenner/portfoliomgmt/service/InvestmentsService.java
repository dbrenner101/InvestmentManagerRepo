/**
 * 
 */
package com.brenner.portfoliomgmt.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.portfoliomgmt.data.entities.InvestmentDTO;
import com.brenner.portfoliomgmt.data.repo.InvestmentsRepository;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.exception.InvalidRequestException;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.util.ObjectMappingUtil;

/**
 * Data service specific to working with investments data
 * 
 * @author dbrenner
 *
 */
@Service
public class InvestmentsService {

	private static final Logger log = LoggerFactory.getLogger(InvestmentsService.class);
    
    @Autowired InvestmentsRepository investmentsRepo;

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
		
		Optional<InvestmentDTO> optInv = this.investmentsRepo.findById(investment.getInvestmentId());
		
		if (optInv.isEmpty()) {
			throw new NotFoundException("Investment with id " + investment.getInvestmentId() + " does not exist.");
		}
		
		this.investmentsRepo.deleteById(investment.getInvestmentId());
		
		log.info("Exiting delete()");
	}
    
    /**
     * Retrieves investments and quotes that are part of the portfolio.
     * 
     * @return List<Investment>
     */
    public List<Investment> findInvestmentsAndQuotesAssociatedWithAHolding() {
		log.info("Entered getInvestmentsForHoldingsWithQuotes()");
		
		List<InvestmentDTO> investments = this.investmentsRepo.getInvestmentsAllForHoldingsWithQuotesOrderedBySymbol();
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		log.info("Exiting getInvestmentsForHoldingsWithQuotes()");
		
		return ObjectMappingUtil.mapInvestmentDtoList(investments);
    }
    
    /**
     * Retrieves a list of investments ordered by symbol
     * 
     * @return {@link List}<Investment>
     */
    public List<Investment> findInvestments(String orderByProp) {
		log.info("Entered findAllByOrderBySymbolAsc()");
		
		List<InvestmentDTO> investments = this.investmentsRepo.findAll(Sort.by(Sort.Direction.ASC, orderByProp));
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		log.info("Exiting findAllByOrderBySymbolAsc()");
		
		return ObjectMappingUtil.mapInvestmentDtoList(investments);
    }
    
    /**
     * Retrieves a specific investment based on symbol
     * 
     * @param symbol - investment identifier
     * @return {@link InvestmentDTO}
     */
    public Optional<Investment> findInvestmentBySymbol(String symbol) {
		log.info("Entered findBySymbol()");
		log.debug("Param: symbol: {}", symbol);
		
		if (symbol == null) {
			throw new InvalidRequestException("symbol must not be null.");
		}
		
		Optional<InvestmentDTO> optInv = this.investmentsRepo.findBySymbol(symbol);
		
		log.debug("Retrieved investment: {}", optInv);
		log.info("Exiting findBySymbol()");
		
		return optInv.isEmpty() ? 
				Optional.empty() : 
					Optional.of(ObjectMappingUtil.mapInvestmentDtoToInvestment(optInv.get()));
    }
    
    /**
     * Persists the investment 
     * 
     * @param investment - investment to save
     * @return {@link InvestmentDTO}
     */
    @Transactional
    public Investment saveInvestment(Investment investment) {
		log.info("Entered save()");
		log.debug("Param: investment: {}", investment);
		
		if (investment == null) {
			throw new InvalidRequestException("investment must not be null");
		}
		
		InvestmentDTO inv = ObjectMappingUtil.mapInvestmentToInvestmentDTO(investment);
		Optional<InvestmentDTO> optInv = this.investmentsRepo.findById(investment.getInvestmentId());
		if (optInv.isPresent()) {
			inv.setQuotes(optInv.get().getQuotes());
		}
		inv = this.investmentsRepo.save(inv);
		
		log.info("Exiting save()");
		return ObjectMappingUtil.mapInvestmentDtoToInvestment(inv);
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
		
		for (Investment inv : investments) {
			saveInvestment(inv);
		}
		
		log.info("Exiting saveAll()");
    }
    
    /**
     * Retrieves all investments
     * 
     * @return {@link Iterable}<Investment>
     */
    public List<Investment> findAllInvestments(String sortByProperty) {
		log.info("Entered findAllByOrderBySymbolAsc()");
		
		List<InvestmentDTO> investments = this.investmentsRepo.findAll(Sort.by(Sort.Direction.ASC, sortByProperty));
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		log.info("Exiting findAllByOrderBySymbolAsc()");
		
		return ObjectMappingUtil.mapInvestmentDtoList(investments);
    }
    
    /**
     * Retrieves a specific investment
     * 
     * @param investmentId - investment unique identifier
     * @return {@link InvestmentDTO}
     */
    public Optional<Investment> findInvestmentByInvestmentId(Long investmentId) {
		log.info("Entered findById()");
		log.debug("Param: investmentId: {}", investmentId);
		
		if (investmentId == null) {
			throw new InvalidRequestException("investmentId must not be null");
		}
		
		Optional<InvestmentDTO> optInv = this.investmentsRepo.findById(investmentId);
		
		log.debug("Retrieved investment: {}", optInv);
		log.info("Exiting findById()");
		
		return optInv.isEmpty() ? 
				Optional.empty() : 
					Optional.of(ObjectMappingUtil.mapInvestmentDtoToInvestment(optInv.get()));
    }
	
	/**
	 * Retrieves all investments with their associated quotes
	 * 
	 * @return A {@link List} of {@link InvestmentDTO}s
	 */
	public List<Investment> findInvestmentsForHoldingsWithQuotes() {
		log.info("Entered getInvestmentsForHoldingsWithQuotes()");
		
		List<InvestmentDTO> investments = this.investmentsRepo.getInvestmentsAllForHoldingsWithQuotesOrderedBySymbol();
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		
		log.info("Exiting getInvestmentsForHoldingsWithQuotes()");
		
		return ObjectMappingUtil.mapInvestmentDtoList(investments);
	}
	
	/**
	 * Retrieves a unique list of investments associated with a holding
	 * 
	 * @return A {@link List} of {@link InvestmentDTO}s
	 */
	public List<Investment> findUniqueInvestmentsForHoldingsOrderBySymbol() {
		log.info("Entered findUniqueInvestmentsForHoldingsOrderBySymbol()");
		
		List<InvestmentDTO> investments = this.investmentsRepo.findUniqueInvestmentsForHoldingsOrderBySymbol();
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		log.info("Exiting findUniqueInvestmentsForHoldingsOrderBySymbol()");
		
		return ObjectMappingUtil.mapInvestmentDtoList(investments);
	}
	
	/**
	 * Retrieves all investments associated with a holding
	 * 
	 * @return A {@link List} of {@link InvestmentDTO}s
	 */
	public List<Investment> findInvestmentsForHoldingsOrderedBySymbol() {
		log.info("Entered findIvestmentsForHoldingsOrderedBySymbol()");
		
		List<InvestmentDTO> investments = this.investmentsRepo.findInvestmentsForHoldingsOrderedBySymbol();
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		log.info("Exiting findIvestmentsForHoldingsOrderedBySymbol()");
		
		return ObjectMappingUtil.mapInvestmentDtoList(investments);
	}
}
