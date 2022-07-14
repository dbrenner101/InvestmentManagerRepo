package com.brenner.portfoliomgmt.quotes;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.investments.InvestmentJsonSerializer;
import com.brenner.portfoliomgmt.util.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonIgnoreProperties(ignoreUnknown=true)
@Entity(name="quotes")
@Table(name="quotes")
public class Quote implements Comparable<Quote> {
	
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name="quote_id")
    private Long quoteId;
    
    @Column(name="quote_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
	
	@Column(name="price_at_open")
    private BigDecimal open;
	
	@Column(name="price_at_close")
    private BigDecimal close;
	
	@Column(name="high")
    private BigDecimal high;
	
	@Column(name="low")
    private BigDecimal low;
	
	@Column(name="volume")
    private Integer volume;
	
	@Column(name="price_change")
    private BigDecimal priceChange; 
	
	private BigDecimal week52High;
	
	private BigDecimal week52Low;
	
	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinColumn(name="investment_id", nullable = false)
	@JsonSerialize(using=InvestmentJsonSerializer.class)
	private Investment investment;
	
	@JsonProperty("quote")
	protected void unpackNested(Map<String, Object> quote) {
	    
	    if (quote != null) {
    		Investment investment = new Investment((String) quote.get("symbol"));
    		investment.setCompanyName((String) quote.get("companyName"));
    		investment.setSector((String) quote.get("sector"));
    		investment.setExchange((String) quote.get("primaryExchange"));
    		this.setInvestment(investment);
    		
    	    //this.symbol = quote.get("symbol") != null ? (String) quote.get("symbol") : null;
    		//this.companyName = quote.get("companyName") != null ? (String) quote.get("companyName") : null;
    		//this.sector = quote.get("sector") != null ? (String) quote.get("sector") : null;
    		//this.exchange = quote.get("primaryExchange") != null ? (String) quote.get("primaryExchange") : null;
    		this.open = quote.get("open") != null ? new BigDecimal(quote.get("open").toString()) : null;
    		this.close = quote.get("close") != null ? new BigDecimal(quote.get("close").toString()) : null;
    		this.high = quote.get("high") != null ? new BigDecimal(quote.get("high").toString()) : null;
    		this.low = quote.get("low") != null ? new BigDecimal(quote.get("low").toString()) : null;
    		this.volume = quote.get("volume") != null ? Integer.valueOf(quote.get("volume").toString()) : null;
    		this.priceChange = quote.get("change") != null ? new BigDecimal(quote.get("change").toString()) : null;
    		try {
    		    this.date = quote.get("date") != null ? this.date = CommonUtils.convertDatePickerDateFormatStringToDate(quote.get("date").toString()) : null;
    		}
    		catch (ParseException pe) {
    		    pe.printStackTrace();
    		}
	    }
	}
	
	public Quote() {}

    public Long getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(Long quoteId) {
		this.quoteId = quoteId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getOpen() {
		return open;
	}

	public void setOpen(BigDecimal open) {
		this.open = open;
	}

	public BigDecimal getClose() {
		return close;
	}

	public void setClose(BigDecimal close) {
		this.close = close;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public BigDecimal getPriceChange() {
		return priceChange;
	}

	public void setPriceChange(BigDecimal priceChange) {
		this.priceChange = priceChange;
	}

	public BigDecimal getWeek52High() {
		return week52High;
	}

	public void setWeek52High(BigDecimal week52High) {
		this.week52High = week52High;
	}

	public BigDecimal getWeek52Low() {
		return week52Low;
	}

	public void setWeek52Low(BigDecimal week52Low) {
		this.week52Low = week52Low;
	}

	public Investment getInvestment() {
		return investment;
	}

	public void setInvestment(Investment investment) {
		this.investment = investment;
	}

	@Override
    public int compareTo(Quote o) {
        
        int compare = o.getDate().compareTo(this.getDate());
        if (this.getInvestment() != null && o.getInvestment() != null) {
            compare += this.getInvestment().getSymbol().compareTo(o.getInvestment().getSymbol());
        }
        
        return compare;
    }

	@Override
	public String toString() {
		
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
		builder.append("quoteId", quoteId)
			.append("date", date)
			.append("open", open)
			.append("close", close)
			.append("high", high)
			.append("low", low)
			.append("volume", volume)
			.append("priceChange", priceChange)
			.append("week52High", week52High)
			.append("week52Low", week52Low)
			.append("investment",  investment);
		
		return builder.toString();
	}

}
