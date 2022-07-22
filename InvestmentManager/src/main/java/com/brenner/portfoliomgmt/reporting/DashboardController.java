package com.brenner.portfoliomgmt.reporting;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brenner.portfoliomgmt.data.entities.HoldingDTO;

/**
 * Spring REST controller for the Dashboard service
 * 
 * @author dbrenner
 *
 */
@RestController
@RequestMapping("/api")
public class DashboardController {

	private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
	
	@Autowired
	DashboardService dashboardService;
	
	/**
	 * GET for last 30 days of performance changes in the portfolio
	 * 
	 * @return {@link ValueChangeInstance} encapsulating the portfolio changes
	 */
	@GetMapping(path="/changesInPortfolioForLastMonth", produces={"application/JSON"})
	public ValueChangeInstance changesInPortfolioForLastMonth() {
		log.info("Entered changesInPortfolioForLastMonth()");
		
		ValueChangeInstance portfolioChange = this.dashboardService.getTotalPortfoilioChange();
		log.debug("Postfolio change: {}", portfolioChange);
		
		log.info("Exiting changesInPortfolioForLastMonth()");
		return portfolioChange;
	}
	
	/**
	 * Retrieves the changes in the portfoilio summarized by investment sector
	 * 
	 * @return {@link ValueChangeInstance} encapsulating the summary
	 */
	@GetMapping(path="/changesInPortfolioBySector", produces={"application/JSON"})
	public List<ValueChangeInstance> changesInPortfolioBySector() {
		log.info("Entered changesInPortfolioForLastMonth()");
		
		List<ValueChangeInstance> portfolioChange = this.dashboardService.getPortfolioChangeBySector();
		log.debug("Portfolio change: {}", portfolioChange);
		
		log.info("Exiting changesInPortfolioForLastMonth()");
		return portfolioChange;
	}
	
	/**
	 * Summary of current position by {@link HoldingDTO}
	 * @return A {@link List} of changes by Holding
	 */
	@GetMapping(path="/changesInPortfolioByHolding", produces={"application/JSON"})
	public List<ValueChangeInstance> changesInPortfolioByHolding() {
		log.info("Entered changesInPortfolioForLastMonth()");
		
		List<ValueChangeInstance> portfolioChange = this.dashboardService.changesInPortfolioByHolding();
		log.debug("Retrived {} change instances", portfolioChange != null ? portfolioChange.size() : 0);
		
		log.info("Exiting changesInPortfolioForLastMonth()");
		return portfolioChange;
	}
}
