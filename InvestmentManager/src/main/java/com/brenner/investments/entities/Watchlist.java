package com.brenner.investments.entities;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Watchlist {

	private Integer watchlistId;
	
	private String watchlistName;
	
	private List<Investment> investmentsToWatch;

	public Integer getWatchlistId() {
		return watchlistId;
	}

	public void setWatchlistId(Integer watchlistId) {
		this.watchlistId = watchlistId;
	}

	public String getWatchlistName() {
		return watchlistName;
	}

	public void setWatchlistName(String watchlistName) {
		this.watchlistName = watchlistName;
	}

	public List<Investment> getInvestmentsToWatch() {
		return investmentsToWatch;
	}

	public void setInvestmentsToWatch(List<Investment> investmentsToWatch) {
		this.investmentsToWatch = investmentsToWatch;
	}
	
	public void addInvestmentToWatch(Investment investment) {
	    if (this.investmentsToWatch == null) {
	        this.investmentsToWatch = new ArrayList<Investment>();
	    }
	    
	    this.investmentsToWatch.add(investment);
	}

	@Override
	public String toString() {
		
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
		builder.append("watchlistId", watchlistId)
			.append("watchlistName", watchlistName)
			.append("investmentsToWatch", investmentsToWatch);
		
		return builder.toString();
	}
	
	
}
