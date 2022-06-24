/**
 * 
 */
package com.brenner.investments.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.investments.data.InvestmentsDataService;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.service.InvestmentsService;

/**
 * Data service specific to working with investments data
 * 
 * @author dbrenner
 *
 */
@Service
public class PersistentInvestmentsService implements InvestmentsService {
    
    @Autowired
    InvestmentsDataService investmentRepo;
    
    public List<Investment> getInvestmentsBySymbolAlpha(String alpha) {
    	
    	String searchString = alpha + '%';
    	return this.investmentRepo.findBySymbolLike(searchString);
    }
    
    /**
     * Injection of an InvestmentsRepository instance - used for testing
     * 
     * @param investmentRepo
     */
    public void setInvestmentRepo(InvestmentsDataService investmentRepo) {
		this.investmentRepo = investmentRepo;
	}

	/**
     * Deletes a specific investment;
     * 
     * @param investment
     */
    public void deleteInvestment(Investment investment) {
    	this.investmentRepo.delete(investment);
    }
    
    /**
     * Retrieves investments and quotes that are part of the portfolio.
     * List<Investment>
     * @return
     */
    public List<Investment> getInvestmentsAndQuotesAssociatedWithAHolding() {
    	
    	return this.investmentRepo.getInvestmentsForHoldingsWithQuotes();
    }
    
    /**
     * Retrieves a list of investments ordered by symbol
     * 
     * @return {@link List}<Investment>
     */
    public List<Investment> getInvestmentsOrderedBySymbolAsc() {
        
        return this.investmentRepo.findAllByOrderBySymbolAsc();
    }
    
    /**
     * Retrieves a specific investment based on symbol
     * 
     * @param symbol - investment identifier
     * @return {@link Investment}
     */
    public Investment getInvestmentBySymbol(String symbol) {
        
        return this.investmentRepo.findBySymbol(symbol);
    }
    
    /**
     * Persists the investment 
     * 
     * @param investment - investment to save
     * @return {@link Investment}
     */
    @Transactional
    public Investment saveInvestment(Investment investment) {
    	
    	if (investment != null && investment.getInvestmentId() == null) {
    		Investment i = this.getInvestmentBySymbol(investment.getSymbol());
    		if (i != null) {
    			investment.setInvestmentId(i.getInvestmentId());
    		}
    	}
        
        return this.investmentRepo.save(investment);
    }
    
    /**
     * Passes a list of investments to the  persistence tier for saving.
     * 
     * @param investments - list of investments
     */
    @Transactional
    public void saveInvestments(List<Investment> investments) {
        this.investmentRepo.saveAll(investments);
    }
    
    /**
     * Retrieves all investments
     * 
     * @return {@link Iterable}<Investment>
     */
    public Iterable<Investment> getAllInvestments() {
        
        return this.investmentRepo.findAllByOrderBySymbolAsc();
    }
    
    /**
     * Retrieves a specific investment
     * 
     * @param investmentId - investment unique identifier
     * @return {@link Investment}
     */
    public Investment getInvestmentByInvestmentId(Long investmentId) {
        
        return this.investmentRepo.findById(investmentId);
    }
}
