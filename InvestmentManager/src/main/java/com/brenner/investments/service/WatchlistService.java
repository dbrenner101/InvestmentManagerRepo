package com.brenner.investments.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Watchlist;
import com.brenner.investments.model.InvestmentPerformanceSet;

@Service
public interface WatchlistService {
	
	public List<Watchlist> getAllWatchlists();
	
	public Watchlist getWatchlistById(Integer watchlistId);

	public Watchlist addWatchlist(Watchlist watchlist);
	
	public Watchlist updateWatchlist(Watchlist watchlist);
	
	public void addInvestmentToWatchlist(Watchlist watchlist, Investment investment);
	
	public void addInvestmentsToWatchlist(Watchlist watchlist, List<Investment> investments);
	
	public void deleteWatchlistById(Integer watchlistId);
	
	public List<String> getSymbolsForWatchlist(Integer watchlistId) throws IOException;
	
	public InvestmentPerformanceSet getChartsForSymbol(String symbol) throws IOException;
}
