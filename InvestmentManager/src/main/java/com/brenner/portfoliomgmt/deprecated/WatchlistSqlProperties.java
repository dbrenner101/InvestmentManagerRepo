package com.brenner.portfoliomgmt.deprecated;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sql/watchlist.sql.xml")
public class WatchlistSqlProperties {
    
    @Value("${addWatchlist}")
    private String addWatchlist;
    
    @Value("${updateWatchlist}")
    private String updateWatchlist;
    
    @Value("${addInvestmentsToWatch}")
    private String addInvestmentsToWatch;
    
    @Value("${deleteInvestmentsToWatch")
    private String deleteInvestmentsToWatch;
    
    @Value("${findAll}")
    private String findAll;
    
    @Value("${findInvestmentsToWatchForWatchlist}")
    private String findInvestmentsToWatchForWatchlist;
    
    @Value("${findById}")
    private String findById;
    
    @Value("${deleteById}")
    private String deleteById;
    

    public String getAddWatchlist() {
        return addWatchlist;
    }

    public void setAddWatchlist(String addWatchlist) {
        this.addWatchlist = addWatchlist;
    }

    public String getUpdateWatchlist() {
        return updateWatchlist;
    }

    public void setUpdateWatchlist(String updateWatchlist) {
        this.updateWatchlist = updateWatchlist;
    }

    public String getAddInvestmentsToWatch() {
        return addInvestmentsToWatch;
    }

    public void setAddInvestmentsToWatch(String addInvestmentsToWatch) {
        this.addInvestmentsToWatch = addInvestmentsToWatch;
    }

    public String getDeleteInvestmentsToWatch() {
        return deleteInvestmentsToWatch;
    }

    public void setDeleteInvestmentsToWatch(String deleteInvestmentsToWatch) {
        this.deleteInvestmentsToWatch = deleteInvestmentsToWatch;
    }

    public String getFindAll() {
        return findAll;
    }

    public void setFindAll(String findAll) {
        this.findAll = findAll;
    }

    public String getFindInvestmentsToWatchForWatchlist() {
        return findInvestmentsToWatchForWatchlist;
    }

    public void setFindInvestmentsToWatchForWatchlist(String findInvestmentsToWatchForWatchlist) {
        this.findInvestmentsToWatchForWatchlist = findInvestmentsToWatchForWatchlist;
    }

    public String getFindById() {
        return findById;
    }

    public void setFindById(String findById) {
        this.findById = findById;
    }

    public String getDeleteById() {
        return deleteById;
    }

    public void setDeleteById(String deleteById) {
        this.deleteById = deleteById;
    }
    

}
