package com.brenner.portfoliomgmt.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.brenner.portfoliomgmt.data.entities.reporting.HoldingBucketSummation;
import com.brenner.portfoliomgmt.data.mapping.HoldingBucketSummationMapper;
import com.brenner.portfoliomgmt.domain.BucketEnum;
import com.brenner.portfoliomgmt.domain.reporting.HoldingBucket;
import com.brenner.portfoliomgmt.reporting.HoldingsReport;

/**
 * Class to handle reporting on holdings
 * 
 * @author dbrenner
 *
 */
@Component
public class HoldingsReportingDataService {
	
	private static final Logger log = LoggerFactory.getLogger(HoldingsReportingDataService.class);

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	HoldingBucketSummationMapper bucketSummationMapper;
	
	private static final String HOLDINGS_BY_TYPE_SECTOR_SQL = 
			"SELECT SUM(h.purchase_price * h.quantity) as value_at_purchase, i.investment_type, i.sector "
			+ "FROM holdings h, investments i "
			+ "WHERE h.quantity > 0 and h.investment_id = i.investment_id "
			+ "GROUP BY i.investment_type, i.sector "
			+ "ORDER BY i.investment_type;";
	
	private static final String HOLDINGS_BY_MARKET_VALUE_SQL = 
			"SELECT SUM(h.purchase_price * h.quantity) as value_at_purchase, i.symbol, i.investment_id, "
			+ "i.company_name, q.price_at_close, SUM(h.quantity * q.price_at_close) as market_value, "
			+ "SUM((h.quantity * q.price_at_close) - (h.purchase_price * h.quantity)) as change_in_value "
			+ "FROM holdings h "
			+ "LEFT JOIN investments i on i.investment_id = h.investment_investment_id "
			+ "LEFT OUTER JOIN quotes q on q.investment_id = i.investment_id and q.quote_date = "
			+ "(SELECT MAX(quote_date) FROM quotes) "
			+ "WHERE h.quantity > 0 "
			+ "GROUP BY h.purchase_price, h.quantity, i.symbol, i.company_name, q.price_at_close, i.investment_id "
			+ "ORDER BY market_value ?;";
	
	private static final String HOLDINGS_BY_CHANGE_IN_VALUE_SQL = 
			"SELECT SUM(h.purchase_price * h.quantity) as value_at_purchase, i.symbol, i.investment_id, "
			+ "i.company_name, q.price_at_close, SUM(h.quantity * q.price_at_close) as market_value, "
			+ "SUM((h.quantity * q.price_at_close) - (h.purchase_price * h.quantity)) as change_in_value "
			+ "FROM holdings h "
			+ "LEFT JOIN investments i on i.investment_id = h.investment_id "
			+ "LEFT OUTER JOIN quotes q on q.investment_id = i.investment_id and q.quote_date = "
			+ "(SELECT MAX(quote_date) FROM quotes) "
			+ "WHERE h.quantity > 0 "
			+ "GROUP BY h.purchase_price, h.quantity, i.symbol, i.company_name, q.price_at_close, i.investment_id "
			+ "ORDER BY change_in_value ?;";
	
	public List<HoldingBucket> getHoldingsByBucket() {
		
		log.info("Entered getHoldingsByBucket()");
		
		List<HoldingBucket> holdings = this.bucketSummationMapper.holdingsByBucket();
		
		log.debug("Retrieved {} objects", holdings != null ? holdings.size() : 0);
		
		log.debug("Exiting getHoldingsByBucket()");
		
		return holdings;
	}
	
	public Map<String, HoldingBucket> getHoldingsListAsMapByBucketDescription() {
		
		List<HoldingBucket> holdings = this.getHoldingsByBucket();
		
		Map<String, HoldingBucket> holdingsMap = holdings.stream().collect(
				Collectors.toMap(h -> h.getBucket() != null ? h.getBucket().getDescription() : "", h -> h));
		
		return holdingsMap;
	}
	
	public Map<BucketEnum, HoldingBucket> getHoldingsListAsMapByBucket() {
		
		List<HoldingBucket> holdings = this.getHoldingsByBucket();
		
		Map<BucketEnum, HoldingBucket> holdingsMap = holdings.stream().collect(
				Collectors.toMap(h -> h.getBucket(), h -> h));
		
		log.debug("Exiting getHoldingsByBucket()");
		
		return holdingsMap;
		
	}
	
	public void saveBucketSummarySnapShot() {
		
		Date summaryReportDate = new Date();
		
		Map<BucketEnum, HoldingBucket> summaryReport = getHoldingsListAsMapByBucket();
		
		if (summaryReport.isEmpty()) {
			return;
		}
		HoldingBucket bucket1 = summaryReport.get(BucketEnum.BUCKET_1);
		HoldingBucket bucket2 = summaryReport.get(BucketEnum.BUCKET_2);
		HoldingBucket bucket3 = summaryReport.get(BucketEnum.BUCKET_3);
		HoldingBucket noBucket = summaryReport.get(BucketEnum.BUCKET_NA);
		
		Date snapShotDate = this.bucketSummationMapper.findbucketSummaryReport(summaryReportDate);
		
		HoldingBucketSummation summation = new HoldingBucketSummation();
		summation.setSummationDate(snapShotDate);
		summation.setBucket1Total(bucket1.getAmount());
		summation.setBucket2Total(bucket2.getAmount());
		summation.setBucket3Total(bucket3.getAmount());
		summation.setExcludedBucketTotal(noBucket.getAmount());
		
		if (snapShotDate == null) {
			this.bucketSummationMapper.insertBucketSummation(summation);
		}
		else {
			this.bucketSummationMapper.updateBucketSummation(summation);
		}
	}
	
	public List<HoldingBucketSummation> getBucketSummarySnapshots() {
		
		List<HoldingBucketSummation> summations = this.bucketSummationMapper.historicalBucketSummation();
		
		return summations;
		
	}
	
	/**
	 * Retrieves a summation of holdings by investment type and sector
	 *  
	 * @return a list of holdings to report on, grouped by sector and investment type
	 */
	public List<HoldingsReport> getHoldingsByInvestmentTypeAndSector() {
		log.info("Entered getHoldingsByInvestmentTypeAndSector()");
		
		final String SQL = HOLDINGS_BY_TYPE_SECTOR_SQL;
		log.debug("SQL: {}", SQL);
		
		List<HoldingsReport> report = null;//this.jdbcTemplate.query(SQL, new HoldingsByInvestmentTypeAndSectorRowMapper());
		log.debug("Retrieved {} objects", report != null ? report.size() : 0);
		
		log.info("Exiting getHoldingsByInvestmentTypeAndSector()");
		return report;
	}
	
	/**
	 * Retrieves all holdings ordered by most recent market value
	 * 
	 * @return A sorted list of holdings
	 */
	public List<HoldingsReport> findHoldingsByMarketValueOrderedDesc(String sortOrder) {
		log.info("Entered findHoldingsByMarketValueOrderedDesc()");
		log.debug("Param: sortOrder: {}", sortOrder);
		
		String SQL = HOLDINGS_BY_MARKET_VALUE_SQL;
		
		if (sortOrder == null) {
			sortOrder = "DESC";
		}
		
		SQL = SQL.replace("?", sortOrder);
		log.debug("SQL: {}", SQL);
		
		List<HoldingsReport> holdings = this.jdbcTemplate.query(SQL, new RowMapper<HoldingsReport>() {
			
			@Override
			public HoldingsReport mapRow(ResultSet rs, int rowNum) throws SQLException {
				HoldingsReport report = new HoldingsReport();
				report.setChangeInValue(rs.getFloat("change_in_value"));
				report.setCompanyName(rs.getString("company_name"));
				report.setMarketValue(rs.getFloat("market_value"));
				report.setPriceAtClose(rs.getFloat("price_at_close"));
				report.setValueAtPurchase(rs.getFloat("value_at_purchase"));
				report.setSymbol(rs.getString("symbol"));
				report.setInvestmentId(rs.getLong("investment_id"));
				return report;
			}
			
		});
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findHoldingsByMarketValueOrderedDesc()");
		return holdings;
	}
	
	/**
	 * Retrieves holding ordered by their change in value
	 * 
	 * @return A list of HoldingsReport objects
	 */
	public List<HoldingsReport> findHoldingByChangeInValueOrderedDesc(String sortOrder) {
		log.info("Entered findHoldingByChangeInValueOrderedDesc()");
		
		String SQL = HOLDINGS_BY_CHANGE_IN_VALUE_SQL;
		if (sortOrder == null) {
			sortOrder = "DESC";
		}
		
		
		log.debug("SQL: {}", SQL);
		
		List<HoldingsReport> holdings = null;//this.jdbcTemplate.query(SQL, new HoldingsReportRowMapper());
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findHoldingByChangeInValueOrderedDesc()");
		return holdings;
	}

}
