package com.brenner.investments.service;

import java.util.List;

import com.brenner.investments.entities.ValueChangeInstance;

public interface DashboardService {

	public ValueChangeInstance getTotalPortfoilioChange();
	
	public List<ValueChangeInstance> getPortfolioChangeBySector();
	
	public List<ValueChangeInstance> changesInPortfolioByHolding();
}
