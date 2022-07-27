package com.brenner.portfoliomgmt.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.brenner.portfoliomgmt.data.entities.reporting.HoldingBucketSummation;
import com.brenner.portfoliomgmt.data.mapping.HoldingBucketSummationMapper;
import com.brenner.portfoliomgmt.data.mapping.HoldingsByBucketMapping;
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
	
	private static final String HOLDINGS_BY_BUCKET_SQL = 
			"SELECT SUM(h.purchase_price * h.quantity) AS purchase_value, "
			+ "SUM(q.price_at_close * h.quantity) AS current_value, h.bucket_enum AS bucket "
			+ "FROM holdings h "
			+ "LEFT JOIN investments i ON h.investment_investment_id = i.investment_id "
			+ "LEFT JOIN quotes q ON q.investment_id = i.investment_id "
			+ "GROUP BY h.bucket_enum ORDER BY bucket ASC;";
	
	public List<HoldingBucket> getHoldingsByBucket() {
		
		log.info("Entered getHoldingsByBucket()");
		log.debug("SQL: {}", HOLDINGS_BY_BUCKET_SQL);
		
		List<HoldingBucket> holdings = this.jdbcTemplate.query(HOLDINGS_BY_BUCKET_SQL, new HoldingsByBucketMapping());
		
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
		
		java.sql.Date summaryReportDate = new java.sql.Date(new Date().getTime());
		
		Map<BucketEnum, HoldingBucket> summaryReport = getHoldingsListAsMapByBucket();
		
		if (summaryReport.isEmpty()) {
			return;
		}
		HoldingBucket bucket1 = summaryReport.get(BucketEnum.BUCKET_1);
		HoldingBucket bucket2 = summaryReport.get(BucketEnum.BUCKET_2);
		HoldingBucket bucket3 = summaryReport.get(BucketEnum.BUCKET_3);
		HoldingBucket noBucket = summaryReport.get(BucketEnum.BUCKET_NA);
		
		final String GET_SUMMATION_FOR_DATE_SQL = "SELECT * FROM bucket_summation_snapshots WHERE snap_shot_date = '" + summaryReportDate + "';";
		List<Map<String, Object>> instances = this.jdbcTemplate.queryForList(GET_SUMMATION_FOR_DATE_SQL);
		
		if (instances == null || instances.size() == 0) {
			final String INSERT_BUCKET_SUMMATION = "INSERT INTO BUCKET_SUMMATION_SNAPSHOTS(SNAP_SHOT_DATE, BUCKET_1_TOTAL, BUCKET_2_TOTAL, BUCKET_3_TOTAL, NO_BUCKET_TOTAL) VALUES(?, ?, ?, ?, ?);";
			this.jdbcTemplate.execute(INSERT_BUCKET_SUMMATION, new PreparedStatementCallback<Boolean>() {

				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setDate(1, summaryReportDate);
					ps.setDouble(2, bucket1.getAmount());
					ps.setDouble(3, bucket2.getAmount());
					ps.setDouble(4, bucket3.getAmount());
					ps.setDouble(5, noBucket.getAmount());
					return ps.execute();
				}
			});
		}
		else {
			final String UPDATE_BUCKET_SUMMATION = "UPDATE BUCKET_SUMMATION_SNAPSHOTS SET BUCKET_1_TOTAL = ?, BUCKET_2_TOTAL = ?, BUCKET_3_TOTAL = ?, NO_BUCKET_TOTAL = ? WHERE SNAP_SHOT_DATE = ?;";
			this.jdbcTemplate.execute(UPDATE_BUCKET_SUMMATION, new PreparedStatementCallback<Boolean>() {

				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setDouble(1, bucket1.getAmount());
					ps.setDouble(2, bucket2.getAmount());
					ps.setDouble(3, bucket3.getAmount());
					ps.setDouble(4, noBucket.getAmount());
					ps.setDate(5, summaryReportDate);
					return ps.execute();
				}
			});
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
