package com.brenner.investments.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.brenner.investments.data.mapping.InvestmentRowMapper;
import com.brenner.investments.data.props.WatchlistSqlProperties;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Watchlist;

@Component
public class WatchlistDataService {

    private static final Logger log = LoggerFactory.getLogger(WatchlistDataService.class);

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    WatchlistSqlProperties props;
    
    
    
    public Watchlist findById(Integer watchlistId) {
        log.info("Entered findById()");
        log.debug("Param: watchlistId: {}", watchlistId);
        
        final String SQL = this.props.getFindById();
        log.debug("SQL: {}", SQL);
        
        Watchlist w = new Watchlist();
        this.jdbcTemplate.query(SQL, new RowCallbackHandler() {
            
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                w.setWatchlistId(rs.getInt("watchlist_id"));
                w.setWatchlistName(rs.getString("watchlist_name"));
            }
        }, watchlistId);
        
        log.info("Exiting findById()");
        return w;
    }
    
    public List<Watchlist> findAll() {
        log.info("Entering findAll()");
        
        final String SQL = this.props.getFindAll();
        log.debug("SQL: {}", SQL);
        
        List<Watchlist> watchlistList = new ArrayList<Watchlist>();
        
        this.jdbcTemplate.query(SQL, new RowCallbackHandler() {
            
            @Override
            public void processRow(ResultSet rs) throws SQLException {
               Watchlist w = new Watchlist();
                w.setWatchlistId(rs.getInt("watchlist_id"));
                w.setWatchlistName(rs.getString("watchlist_name"));
                watchlistList.add(w);
            }
        });
        
        if (watchlistList != null && ! watchlistList.isEmpty()) {
            Iterator<Watchlist> iter = watchlistList.iterator();
            while (iter.hasNext()) {
                Watchlist w = iter.next();
                w.setInvestmentsToWatch(this.getInvestmentsToWatch(w.getWatchlistId()));
            }
        }
        
        log.info("Exiting findAll()");
        return watchlistList;
    }
    
    public List<Investment> getInvestmentsToWatch(Integer watchlistId) {
        log.info("Entered getInvestmentsToWatch()");
        
        final String SQL = this.props.getFindInvestmentsToWatchForWatchlist();
        log.debug("SQL: {}", SQL);
        
        List<Investment> investments = this.jdbcTemplate.query(SQL, new InvestmentRowMapper(), watchlistId);
        
        log.info("Exiting getInvestmentsToWatch()");
        
        return investments;
    }
    
    @Transactional
    public Watchlist save(Watchlist watchlist) {
        
        if (watchlist.getWatchlistId() == null) {
            watchlist = insertWatchlist(watchlist);
        }
        else {
            watchlist = updateWatchlist(watchlist);
        }

        if (watchlist.getInvestmentsToWatch() != null) {
            updateAllInvestmentsToWatch(watchlist);
        }
        
        return watchlist;
    }
    
    @Transactional
    private Watchlist updateWatchlist(Watchlist watchlist) {
        log.info("Entered updateWatchlist()");
        
        final String SQL = props.getUpdateWatchlist();
        log.debug("SQL: {}", SQL);
        
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(SQL);
                ps.setString(1, watchlist.getWatchlistName());
                ps.setInt(2, watchlist.getWatchlistId());
                return ps;
            }
        });
        
        log.info("Exiting updateWatchlist()");
        return watchlist;
    }
    
    @Transactional
    private Watchlist insertWatchlist(Watchlist watchlist) {
        log.info("Entered insertWatchlist()");
        
        final String SQL = this.props.getAddWatchlist();
        log.debug("SQL: {}", SQL);
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, watchlist.getWatchlistName());
                return ps;
            }
            
        }, keyHolder);
        watchlist.setWatchlistId(keyHolder.getKey().intValue());
        log.debug("Added watchlist: {}", watchlist);
        
        log.info("Exiting insertWatchlist()");
        return watchlist;
    }
    
    @Transactional
    public void deleteById(Integer watchlistId) {
        log.info("Entered deleteWatchlist()");
        log.debug("Param: watchlistId: {}", watchlistId);
        
        final String SQL = this.props.getDeleteById();
        log.debug("SQL: {}", SQL);
        
        this.jdbcTemplate.update(SQL, new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(SQL);
                ps.setInt(1, watchlistId);
                return ps;
            }
            
        }, watchlistId);
        
        this.deleteInvestmentsToWatch(watchlistId);
        
        log.info("Exiting deleteWatchlist()");
    }
    
    @Transactional
    private void deleteInvestmentsToWatch(Integer watchlistId) {
        log.info("Entered deleteInvestmentsToWatch()");
        
        final String SQL = this.props.getDeleteInvestmentsToWatch();
        log.debug("SQL: {}", SQL);
        
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(SQL);
                ps.setInt(1, watchlistId);
                return ps;
            }
        });
        
        log.info("Exiting deleteInvestmentsToWatch()");
    }
    
    @Transactional
    private void updateAllInvestmentsToWatch(Watchlist watchlist) {
        log.info("Entered updateInvestmentsToWatch()");
        
        deleteInvestmentsToWatch(watchlist.getWatchlistId());
        
        final String SQL = this.props.getAddInvestmentsToWatch();
        log.debug("SQL: {}", SQL);
        
        List<Investment> investments = watchlist.getInvestmentsToWatch();
        Iterator<Investment> iter = investments.iterator();
        while (iter.hasNext()) {
            updateInvestmentsToWatch(watchlist.getWatchlistId(), iter.next().getInvestmentId());
        }
        
        log.info("Exiting updateInvestmentsToWatch()");
    }
    
    @Transactional
    private void updateInvestmentsToWatch(Integer watchlistId, Long investmentId) {
        log.info("Entered updateInvestmentsToWatch()");
        
        final String SQL = this.props.getAddInvestmentsToWatch();
        log.debug("SQL: {}", SQL);
        
        this.jdbcTemplate.update(SQL, new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(SQL);
                ps.setInt(1, watchlistId);
                ps.setLong(2,  investmentId);
                return ps;
            }
            
        });
        
        log.info("Exiting updateInvestmentsToWatch()");
    }
    
    
}
