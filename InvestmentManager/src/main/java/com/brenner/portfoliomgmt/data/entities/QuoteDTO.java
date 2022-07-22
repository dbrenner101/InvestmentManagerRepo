package com.brenner.portfoliomgmt.data.entities;

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
import org.springframework.format.annotation.DateTimeFormat;

import com.brenner.portfoliomgmt.util.CommonUtils;


@Entity(name="quotes")
@Table(name="quotes")
public class QuoteDTO implements Comparable<QuoteDTO> {
	
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="quote_id")
    private Long quoteId;
    
    @Column(name="quote_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
	
	@Column(name="price_at_open")
    private BigDecimal open;
	
	@Column(name="price_at_close", nullable=false)
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
	
	@OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name="investment_id", nullable = false)
	private InvestmentDTO investment;
	
	public QuoteDTO() {}
	
	public QuoteDTO(Long quoteId, Date date, BigDecimal open, BigDecimal close, BigDecimal high, BigDecimal low,
			Integer volume, BigDecimal priceChange, BigDecimal week52High, BigDecimal week52Low,
			InvestmentDTO investment) {
		super();
		this.quoteId = quoteId;
		this.date = date;
		this.open = open;
		this.close = close;
		this.high = high;
		this.low = low;
		this.volume = volume;
		this.priceChange = priceChange;
		this.week52High = week52High;
		this.week52Low = week52Low;
		this.investment = investment;
	}

	protected void unpackNested(Map<String, Object> quote) {
	    
	    if (quote != null) {
    		InvestmentDTO investment = new InvestmentDTO((String) quote.get("symbol"));
    		investment.setCompanyName((String) quote.get("companyName"));
    		investment.setSector((String) quote.get("sector"));
    		investment.setExchange((String) quote.get("primaryExchange"));
    		this.setInvestment(investment);
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

	public InvestmentDTO getInvestment() {
		return investment;
	}

	public void setInvestment(InvestmentDTO investment) {
		this.investment = investment;
	}

	@Override
    public int compareTo(QuoteDTO o) {
        
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
