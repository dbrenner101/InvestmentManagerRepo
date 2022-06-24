package com.brenner.investments.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.brenner.investments.entities.Investment;

public interface InvestmentsService {
	
	public void deleteInvestment(Investment investment);
	
	public List<Investment> getInvestmentsBySymbolAlpha(String alpha);

	/**
	 * Retrieves investments and quotes that are part of the portfolio.
	 * List<Investment>
	 * @return
	 */
	public List<Investment> getInvestmentsAndQuotesAssociatedWithAHolding();
	
	/**
	 * Retrieves a list of investments ordered by symbol
	 * 
	 * @return {@link List}<Investment>
	 */
	public List<Investment> getInvestmentsOrderedBySymbolAsc();
	
	/**
	 * Retrieves a specific investment based on symbol
	 * 
	 * @param symbol - investment identifier
	 * @return {@link Investment}
	 */
	public Investment getInvestmentBySymbol(String symbol);
	
	/**
	 * Persists the investment 
	 * 
	 * @param investment - investment to save
	 * @return {@link Investment}
	 */
	@Transactional
	public Investment saveInvestment(Investment investment);
	
	/**
	 * Passes a list of investments to the  persistence tier for saving.
	 * 
	 * @param investments - list of investments
	 */
	@Transactional
	public void saveInvestments(List<Investment> investments);
	
	/**
	 * Retrieves all investments
	 * 
	 * @return {@link Iterable}<Investment>
	 */
	public Iterable<Investment> getAllInvestments();
	
	/**
	 * Retrieves a specific investment
	 * 
	 * @param investmentId - investment unique identifier
	 * @return {@link Investment}
	 */
	public Investment getInvestmentByInvestmentId(Long investmentId);

}
