package com.brenner.portfoliomgmt.deprecated;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.portfoliomgmt.data.entities.HoldingDTO;
import com.brenner.portfoliomgmt.exception.InvalidDataRequestException;
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
	HoldingsSqlProperties sqlProps;
	
	/**
	 * Retrieves a summation of holdings by investment type and sector
	 *  
	 * @return a list of holdings to report on, grouped by sector and investment type
	 */
	public List<HoldingsReport> getHoldingsByInvestmentTypeAndSector() {
		log.info("Entered getHoldingsByInvestmentTypeAndSector()");
		
		final String SQL = this.sqlProps.getAllHoldingsByTypeAndSector();
		log.debug("SQL: {}", SQL);
		
		List<HoldingsReport> report = null;//this.jdbcTemplate.query(SQL, new HoldingsByInvestmentTypeAndSectorRowMapper());
		log.debug("Retrieved {} objects", report != null ? report.size() : 0);
		
		log.info("Exiting getHoldingsByInvestmentTypeAndSector()");
		return report;
	}
	
	/**
	 * 
	 * @return A list of all holdings
	 */
	public List<HoldingDTO> findAll() {
		log.info("Entered findAll()");
		
		final String SQL = this.sqlProps.getAllHoldings();
		log.debug("SQL: {}", SQL);
		
		List<HoldingDTO> holdings = null;//this.jdbcTemplate.query(SQL, new HoldingsRowMapper());
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findAll()");
		return holdings;
	}
	
	/**
	 * Retrieves a specific holding
	 * 
	 * @param holdingId = Holding unique identifier
	 * @return the {@link HoldingDTO} identified by the holdingId. May be null.
	 */
	public HoldingDTO findById(Long holdingId) {
		log.info("Entered findAll()");
		log.debug("Param: holdingId: {}", holdingId);
		
		if (holdingId == null) {
			throw new InvalidDataRequestException("holdingId must be non-null");
		}
		
		final String SQL = this.sqlProps.getHoldingById();
		log.debug("SQL: {}", SQL);
		
		HoldingDTO holding = null;//this.jdbcTemplate.queryForObject(SQL, new HoldingsRowMapper(), holdingId);
		log.debug("Retrieved {} holding", holding);
		
		log.info("Exiting findAll()");
		return holding;
	}
	
	/**
	 * Retrieves all holdings ordered by most recent market value
	 * 
	 * @return A sorted list of holdings
	 */
	public List<HoldingsReport> findHoldingsByMarketValueOrderedDesc(String sortOrder) {
		log.info("Entered findHoldingsByMarketValueOrderedDesc()");
		log.debug("Param: sortOrder: {}", sortOrder);
		
		String SQL = this.sqlProps.getAllHoldingsByMarketValue();
		
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
		
		String SQL = this.sqlProps.getAllHoldingsByChangeInValue();
		if (sortOrder == null) {
			sortOrder = "DESC";
		}
		
		
		log.debug("SQL: {}", SQL);
		
		List<HoldingsReport> holdings = null;//this.jdbcTemplate.query(SQL, new HoldingsReportRowMapper());
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findHoldingByChangeInValueOrderedDesc()");
		return holdings;
	}
	
	/**
     * Attempts to locate a holding based on the account identifier and investment identifier
     * 
     * @param accountId - unique account id
     * @param investmentId - unique investment id
     * @return A list of holdings
     */
	public List<HoldingDTO> findByAccountIdAndInvestmentId(Long accountId, Long investmentId) {
		log.info("Entered findByAccountIdAndInvestmentId()");
		log.debug("Params: accountId: {}; investmentId: {}", accountId, investmentId);
		
		final String SQL = this.sqlProps.getHoldingsByAccountIdAndInvestmentId();
		log.debug("SQL: {}", SQL);
		
		List<HoldingDTO> holdings = null;//this.jdbcTemplate.query(SQL, null;//new HoldingsRowMapper(), accountId, investmentId);
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findByAccountIdAndInvestmentId()");
		return holdings;
	}
	
	/**
	 * Located a list of holdings associated with an account
	 * 
	 * @param accountId - unique account identifier
	 * @return A list of holdings
	 */
	public List<HoldingDTO> findByAccountAccountId(Long accountId) {
		log.info("Entered findByAccountId()");
		log.debug("Param: accountId: {}", accountId);
		
		final String SQL = this.sqlProps.getHoldingsByAccountId();
		log.debug("SQL: {}", SQL);
		
		List<HoldingDTO> holdings = null;//this.jdbcTemplate.query(SQL, new HoldingsRowMapper(), accountId);
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findByAccountId()");
		return holdings;
	}
	
	/**
     * Retrieves all holdings ordered by their associated investment symbol
     * 
     * @param accountId - accountIdentifier
     * 
     * @return A list of holdings associated with the account and with a quantity greater than zero.
     */
	public List<HoldingDTO> findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol(Long accountId) {
		log.info("Entered findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol)");
		log.debug("Param: accountId: {}", accountId);
		
		final String SQL = this.sqlProps.getHoldingsByAccountIdWithQuantityGreater0OrderSymbol();
		log.debug("SQL: {}", SQL);
		
		List<HoldingDTO> holdings = null;//this.jdbcTemplate.query(SQL, new HoldingsRowMapper(), accountId);
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		
		log.info("Exiting findHoldingsByAccountIdAndQuantityGreaterThan0OrderBySymbol()");
		return holdings;
	}
	
	/**
	 * Retrieves all holdings ordered by their associated investment symbol
	 * 
	 * @return {@link List} of Holding objects
	 */
	public List<HoldingDTO> findHoldingsOrderByInvestmentSymbol() {
		
		log.info("Entered findHoldingsOrderByInvestmentSymbol()");
		
		final String SQL = this.sqlProps.getAllHoldingsOrderedBySymbol();
		
		log.debug("SQL: {}", SQL);
		
		List<HoldingDTO> holdings = null;//this.jdbcTemplate.query(SQL, new HoldingsRowMapper());
		
		log.debug("Retrieved {} holdings", holdings != null ? holdings.size() : 0);
		log.info("Exiting findHoldingsOrderByInvestmentSymbol()");
		
		return holdings;
	}
	
	/**
	 * Deletes the holding identified by the unique identifier.
	 * 
	 * @param holdingId - {@link HoldingDTO} unique identifier
	 */
	@Transactional
	public void delete(Long holdingId) {
		log.info("Entered delete()");
		log.debug("Param: holdingId: {}", holdingId);
		
		if (holdingId == null) {
			throw new InvalidDataRequestException("holdingId must be non-null");
		}
		
		final String SQL = this.sqlProps.getDeleteHolding();
		log.debug("SQL: {}", SQL);
		
		this.jdbcTemplate.update(SQL, holdingId);
		
		log.info("Exiting delete()");
	}
	
	/**
	 * Entry point for saving a new or updated {@link HoldingDTO}.
	 * 
	 * @param holding - The object to persist
	 * @return The updated {@link HoldingDTO} instance
	 */
	@Transactional
	public HoldingDTO save(HoldingDTO holding) {
		log.info("Entered saveHolding()");
		log.debug("Param: holding: {}", holding);
		
		HoldingDTO newHolding = null;
		
		if (holding.getHoldingId() == null) {
			newHolding = insert(holding);
		}
		else {
			newHolding = update(holding);
		}
		
		log.debug("Saved holding: {}", holding);
		log.info("Exiting saveHolding()");
		
		return newHolding;
	}
	
	/**
	 * Persists a specific holding
	 * 
	 * @param holding - object to persist
	 * @return The new {@link HoldingDTO} object with key
	 */
	@Transactional
	private HoldingDTO insert(HoldingDTO holding) {
		log.info("Entered insert()");
		log.debug("Param: holding: {}", holding);
		
		final String SQL = this.sqlProps.getHoldingsInsert();
		log.debug("SQL: {}", SQL);
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setFloat(1, holding.getPurchasePrice().floatValue());
				ps.setFloat(2, holding.getQuantity().floatValue());
				ps.setLong(3, holding.getAccount().getAccountId());
				ps.setLong(4, holding.getInvestment().getInvestmentId());
				return ps;
			}
		}, keyHolder);
		
		holding.setHoldingId(keyHolder.getKey().longValue());
		log.debug("Holding: {}", holding);
		
		log.info("Exiting insert()");
		return holding;
	}
	
	/**
	 * Updates the holding data
	 * 
	 * @param holding container for data to update
	 * @return The updated {@link HoldingDTO}
	 */
	@Transactional
	private HoldingDTO update(HoldingDTO holding) {
		log.info("Entered update()");
		log.debug("Param: holding: {}", holding);
		
		final String SQL = this.sqlProps.getHoldingUpdate();
		log.debug("SQL: {}", SQL);
		
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(SQL);
				ps.setFloat(1, holding.getPurchasePrice().floatValue());
				ps.setFloat(2, holding.getQuantity().floatValue());
				ps.setLong(3, holding.getAccount().getAccountId());
				ps.setLong(4, holding.getInvestment().getInvestmentId());
				ps.setLong(5, holding.getHoldingId());
				return ps;
			}
		});
		
		log.info("Exiting update()");
		return holding;
	}

}
