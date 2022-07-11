package com.brenner.portfoliomgmt.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Load properties from an external file
 */
@Configuration
@PropertySource(value = "file:/home/dbrenner/dev/secrets/secrets.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:/Users/dbrenner/dev/secrets/secrets.properties", ignoreResourceNotFound = true)
public class PropertyLoader {
    
    @Value("${investment.datasource.password}")
    private String springDataSourcePassword;
    
    public String getSpringDataSourcePassword() {
        return springDataSourcePassword;
    }
    
    public void setSpringDataSourcePassword(String springDataSourcePassword) {
        this.springDataSourcePassword = springDataSourcePassword;
    }
}
