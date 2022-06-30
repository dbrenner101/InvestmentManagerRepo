package com.brenner.portfoliomgmt.reporting;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brenner.portfoliomgmt.quotes.QuotesService;

@Service
public class DashboardService {
	
	private static final Logger log = LoggerFactory.getLogger(DashboardService.class);
	
	@Autowired
	DashboardDataService dashboardDataService;
	
	@Autowired
	QuotesService quotesService;

	public ValueChangeInstance getTotalPortfoilioChange() {
		
		log.info("Entered getPortfoilioChangesForLast30Days()");
		
		DateTime date = DateTime.now();
		date = date.withTime(0, 0, 0, 0);
		date = date.minusDays(30);
		log.debug("Using date: {}", date.toDate());
		
		ValueChangeInstance valueChange = this.dashboardDataService.getChangesInPortfolioForHoldingsSinceMaxQuoteDate();
		log.debug("Retrieved {} valuechangeinstance", valueChange);
		
		log.info("Exiting getPortfoilioChangesForLast30Days()");
		
		return valueChange;
	}
	
	public List<ValueChangeInstance> changesInPortfolioByHolding() {
		
		log.info("Entered getTotalPortfolioChangeByInvestment()");
		
		String maxQuoteDate = this.quotesService.findMaxQuoteDate();
		
		List<ValueChangeInstance> instances = this.dashboardDataService.changesInPortfolioByHoldingSinceDate(maxQuoteDate);
		log.debug("Retrieved {} instances", instances != null ? instances.size() : 0);
		
		log.info("Exiting getTotalPortfolioChangeByInvestment()");
		
		return instances;
	}
	
	public List<ValueChangeInstance> getPortfolioChangeBySector() {
		
		log.info("Entered getPortfolioChangeBySector()");
		
		List<ValueChangeInstance> instances = this.dashboardDataService.getSectorSummation();
		Collections.sort(instances);
		
		log.info("Exiting getPortfolioChangeBySector()");
		
		return instances;
	}

}
