package com.brenner.investments.model;

import java.util.List;

import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Watchlist;

public class WatchlistForm {

	private List<Investment> possibleInvestments;

	private Watchlist watchlist;

	public WatchlistForm(List<Investment> possibleInvestments, Watchlist watchlist) {
		super();
		this.possibleInvestments = possibleInvestments;
		this.watchlist = watchlist;
	}
	
	public List<Investment> getPossibleInvestments() {
		return possibleInvestments;
	}

	public void setPossibleInvestments(List<Investment> possibleInvestments) {
		this.possibleInvestments = possibleInvestments;
	}

	public Watchlist getWatchlist() {
		return watchlist;
	}

	public void setWatchlist(Watchlist watchlist) {
		this.watchlist = watchlist;
	}
}
