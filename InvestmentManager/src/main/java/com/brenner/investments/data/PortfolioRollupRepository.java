package com.brenner.investments.data;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.brenner.investments.entities.PortfolioRollup;

/**
 * JPA interface for reporting data interactions
 * 
 * @author dbrenner
 *
 */
public interface PortfolioRollupRepository extends CrudRepository<PortfolioRollup, Long> {
    
    /**
     * Uses a native query to get the change in the protfolio by day
     * 
     * @return List<PortfolioRollup>
     */
    @Query(name="PortfolioRollup.getPortfolioRollup", nativeQuery=true)
    public List<PortfolioRollup> getChangeInPortfolioValue();
    
    /**
     * Uses a native query to get daily portfolio change by day for symbol
     * 
     * @param symbol - investment identifier
     * @param queryMinDate - length of time to go back for quotes
     * @return List<PortfolioRollup>
     */
    @Query(name="PortfolioRollup.getPortfolioRollupBySymbolAndMonths", nativeQuery=true)
    public List<PortfolioRollup> getChangeInPortfolioValueBySymbolAndDate(String symbol, String queryMinDate);
    
    /**
     * Uses a native query to get the total change in the portfolio by day starting from the supplied date
     * 
     * @param date - start date for retrieval
     * @return List<PortfolioRollup>
     */
    @Query(name="PortfolioRollup.getPortfolioRollupByMonths", nativeQuery=true)
    public List<PortfolioRollup> getChangeInPortfolioValueByMonths(String date);
    
    @Query(name="PortfolioRollup.getAllHoldingsOrderedByMarketValueDesc", nativeQuery=true)
    public List<PortfolioRollup> getHoldingsOrderedByMarketValueDesc();

}
