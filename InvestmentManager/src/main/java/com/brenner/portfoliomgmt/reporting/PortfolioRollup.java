package com.brenner.portfoliomgmt.reporting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;


@JsonIgnoreProperties(ignoreUnknown=true)
@JsonRootName(value="portfolio_rollup")
@SqlResultSetMappings({
	@SqlResultSetMapping(
	    name="portfolioRollupMapping",
	    classes= {
	        @ConstructorResult(
	            targetClass=PortfolioRollup.class, 
	            columns= { 
	                    @ColumnResult(name="quote_date", type=Date.class), 
	                    @ColumnResult(name="market_value", type=Float.class)
	            }), 
	    }
	    
	),
	@SqlResultSetMapping(
			name="HoldingsOrderedByMarketValue", 
			classes= {
				@ConstructorResult(
		            targetClass=PortfolioRollup.class, 
		            columns= { 
		            		@ColumnResult(name="value_at_purchase", type=Float.class),
		            		@ColumnResult(name="symbol", type=String.class),
		                    @ColumnResult(name="market_value", type=Float.class)
		            }), 	
			})
})
@NamedNativeQueries({
    @NamedNativeQuery(
            name="PortfolioRollup.getPortfolioRollup", 
            query="select sum(t.trade_quantity * q.price_at_close) as market_value, q.quote_date \n" + 
            		"from holdings h, transactions t, investments i, quotes q  \n" + 
            		"where h.holding_id = t.holding_id and h.investment_investment_id = i.investment_id and q.investment_id = i.investment_id group by q.quote_date \n" + 
            		"order by q.quote_date;;", 
            resultSetMapping="portfolioRollupMapping",
            resultClass=PortfolioRollup.class
    ),
    @NamedNativeQuery(
            name="PortfolioRollup.getPortfolioRollupBySymbolAndMonths", 
            query="select sum(t.trade_quantity * q.price_at_close) as market_value, q.quote_date from holdings h, transactions t, investments i, quotes q \n" + 
            		"where h.holding_id = t.holding_id and h.investment_investment_id = i.investment_id and q.investment_id = i.investment_id and i.symbol=?1 and q.quote_date >= ?2 \n" + 
            		"group by q.quote_date \n" + 
            		"order by q.quote_date;", 
            resultSetMapping="portfolioRollupMapping",
            resultClass=PortfolioRollup.class
    ),
    @NamedNativeQuery(
            name="PortfolioRollup.getPortfolioRollupByMonths", 
            query="select sum(t.trade_quantity * q.price_at_close) as market_value, q.quote_date from holdings h, transactions t, investments i, quotes q \n" + 
            		"where h.holding_id = t.holding_id and h.investment_investment_id = i.investment_id and q.investment_id = i.investment_id and q.quote_date >= ? \n" + 
            		"group by q.quote_date \n" + 
            		"order by q.quote_date;", 
            resultSetMapping="portfolioRollupMapping",
            resultClass=PortfolioRollup.class
    ),
    @NamedNativeQuery(
    		name="PortfolioRollup.getAllHoldingsOrderedByMarketValueDesc", 
    		query="", 
    		resultSetMapping="HoldingsOrderedByMarketValue", 
    		resultClass=PortfolioRollup.class)
})

@Entity
public class PortfolioRollup {
    
    @Id
    Long id;
    
    @Column(name="symbol")
    String symbol;
    
    @Column(name="quote_date")
    Date quoteDate;
    
    @Column(name="orig_value")
    Float valueAtPurchase;
    
    @Column(name="market_value")
    Float marketValue;
    
    @Column(name="change_in_value")
    Float changeInValue;
    
    public PortfolioRollup(String symbol, Float valueAtPurchase, Date quoteDate, Float marketValue, Float changeInValue) {
        this.symbol = symbol;
        this.valueAtPurchase = valueAtPurchase;
        this.quoteDate = quoteDate;
        this.marketValue = marketValue;
        this.changeInValue = changeInValue;
    }
    
    public PortfolioRollup(Float valueAtPurchase, Date quoteDate, Float marketValue, Float changeInValue) {
        this.valueAtPurchase = valueAtPurchase;
        this.quoteDate = quoteDate;
        this.marketValue = marketValue;
        this.changeInValue = changeInValue;
    }
    
    public PortfolioRollup(Date quoteDate, Float marketValue) {
        this.quoteDate = quoteDate;
        this.marketValue = marketValue;
    }
    
    public PortfolioRollup(Float valueAtPurchase, String symbol, Float marketValue) {
    	this.valueAtPurchase = valueAtPurchase;
    	this.symbol = symbol;
    	this.marketValue = marketValue;
    }
    
    public PortfolioRollup() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Date getQuoteDate() {
		return quoteDate;
	}

	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}

	public Float getValueAtPurchase() {
		return valueAtPurchase;
	}

	public void setValueAtPurchase(Float valueAtPurchase) {
		this.valueAtPurchase = valueAtPurchase;
	}

	public Float getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(Float marketValue) {
		this.marketValue = marketValue;
	}

	public Float getChangeInValue() {
		return changeInValue;
	}

	public void setChangeInValue(Float changeInValue) {
		this.changeInValue = changeInValue;
	}

	@Override
	public String toString() {
		
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
		builder.append("id", id)
			.append("symbol", symbol)
			.append("quoteDate", quoteDate)
			.append("valueAtPurchase", valueAtPurchase)
			.append("marketValue", marketValue)
			.append("changeInValue", changeInValue);
		
		return builder.toString();
	}
    
    
}
