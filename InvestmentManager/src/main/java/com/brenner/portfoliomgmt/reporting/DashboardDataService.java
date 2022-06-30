package com.brenner.portfoliomgmt.reporting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

/**
 * Persistence methods for the dashboard
 * 
 * @author dbrenner
 *
 */
@Component
public class DashboardDataService {

	private static final Logger log = LoggerFactory.getLogger(DashboardDataService.class);

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	DashboardSqlProperties sqlProps;
	
	/**
	 * Retrieves the changes in portfolio since the last quote date
	 * @return A {@link ValueChangeInstance} for the changes
	 */
	public ValueChangeInstance getChangesInPortfolioForHoldingsSinceMaxQuoteDate() {
		log.info("Entered getChangesInPortfolioForHoldingsSinceDate()");
		
		final String SQL = this.sqlProps.getQuotesForHoldingsSinceMaxQuoteDate();
		log.debug("SQL: {}", SQL);
		
		ValueChangeInstance valueChange = this.jdbcTemplate.queryForObject(SQL, new ValueChangeInstanceSummationMapper());
		log.debug("Retrieved value change {}", valueChange);
		
		log.info("Exiting getChangesInPortfolioForHoldingsSinceDate()");
		return valueChange;
	}
	
	/**
	 * Retrieves a summation of the changes in the portfolio by investment sector
	 * @return A {@link List} of the changes by sector
	 */
	public List<ValueChangeInstance> getSectorSummation() {
		log.info("Entered getSectorSummation()");
		
		final String SQL = this.sqlProps.getSectorSummation();
		log.debug("SQL: {}", SQL);
		
		List<ValueChangeInstance> results = this.jdbcTemplate.query(SQL, new ValueChangeInstanceMapper());
		log.debug("Retrieved {} rows from database", results != null ? results.size() : 0);
		log.debug("Returning {} objects", results.size());
		
		log.info("Exiting getSectorSummation()");
		return results;
	}
	
	/**
	 * Total changes in the portfolio since the supplied date
	 * 
	 * @param maxQuoteDate - most recent quote date
	 * @return A {@link List} of the changes 
	 */
	public List<ValueChangeInstance> changesInPortfolioByHoldingSinceDate(String maxQuoteDate) {
		log.info("Entered getTotalPortfolioChangeByInvestment()");
		
		final String SQL = this.sqlProps.getTotalPortfolioChangeByInvestment();
		log.debug("SQL: {}", SQL);
		
		List<ValueChangeInstance> results = new ArrayList<ValueChangeInstance>();
		this.jdbcTemplate.query(SQL, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				ValueChangeInstance instance = new ValueChangeInstance();
				instance.setMarketValue(rs.getFloat("market_value"));
				instance.setPurchaseValue(rs.getFloat("purchase_value"));
				instance.setSymbol(rs.getString("symbol"));
				instance.setCompanyName(rs.getString("company_name"));
				
				results.add(instance);
			}
			
		}, maxQuoteDate);
		log.debug("Retrieved {} rows", results != null ? results.size() : 0);
		
		log.info("Exiting getTotalPortfolioChangeByInvestment()");
		return results;
	}
}
