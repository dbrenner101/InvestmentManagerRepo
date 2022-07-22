/**
 * 
 */
package com.brenner.portfoliomgmt.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;

import com.brenner.portfoliomgmt.data.entities.QuoteDTO;
import com.brenner.portfoliomgmt.domain.serialize.QuoteSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 *
 * @author dbrenner
 * 
 */
@JsonSerialize(using = QuoteSerializer.class)
public class Quote {
	
	private Long quoteId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
	
	private BigDecimal open;
	
	private BigDecimal close;
	
	private BigDecimal high;
	
	private BigDecimal low;
	
	private Integer volume;
	
	private BigDecimal priceChange; 
	
	private BigDecimal week52High;
	
	private BigDecimal week52Low;
	
	private Investment investment;
	
	public Quote() {}
	
	public Quote(Long quoteId, Date date, BigDecimal open, BigDecimal close, BigDecimal high, BigDecimal low,
			Integer volume, BigDecimal priceChange, BigDecimal week52High, BigDecimal week52Low,
			Investment investment) {
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

	public Quote(QuoteDTO quote) {
		if (quote != null) {
			this.quoteId = quote.getQuoteId();
			this.date = quote.getDate();
			this.open = quote.getOpen();
			this.close = quote.getClose();
			this.high = quote.getHigh();
			this.low = quote.getLow();
			this.volume = quote.getVolume();
			this.priceChange = quote.getPriceChange();
			this.week52High = quote.getWeek52High();
			this.week52Low = quote.getWeek52Low();
			this.investment = new Investment(quote.getInvestment());
		}
	}

	public Long getQuoteId() {
		return this.quoteId;
	}

	public void setQuoteId(Long quoteId) {
		this.quoteId = quoteId;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getOpen() {
		return this.open;
	}

	public void setOpen(BigDecimal open) {
		this.open = open;
	}

	public BigDecimal getClose() {
		return this.close;
	}

	public void setClose(BigDecimal close) {
		this.close = close;
	}

	public BigDecimal getHigh() {
		return this.high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getLow() {
		return this.low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public Integer getVolume() {
		return this.volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public BigDecimal getPriceChange() {
		return this.priceChange;
	}

	public void setPriceChange(BigDecimal priceChange) {
		this.priceChange = priceChange;
	}

	public BigDecimal getWeek52High() {
		return this.week52High;
	}

	public void setWeek52High(BigDecimal week52High) {
		this.week52High = week52High;
	}

	public BigDecimal getWeek52Low() {
		return this.week52Low;
	}

	public void setWeek52Low(BigDecimal week52Low) {
		this.week52Low = week52Low;
	}

	public Investment getInvestment() {
		return this.investment;
	}

	public void setInvestment(Investment investment) {
		this.investment = investment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(close, date, high, investment, low, open, priceChange, quoteId, volume, week52High,
				week52Low);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Quote other = (Quote) obj;
		return Objects.equals(this.close, other.close) && Objects.equals(this.date, other.date)
				&& Objects.equals(this.high, other.high) && Objects.equals(this.investment, other.investment)
				&& Objects.equals(this.low, other.low) && Objects.equals(this.open, other.open)
				&& Objects.equals(this.priceChange, other.priceChange) && Objects.equals(this.quoteId, other.quoteId)
				&& Objects.equals(this.volume, other.volume) && Objects.equals(this.week52High, other.week52High)
				&& Objects.equals(this.week52Low, other.week52Low);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QuoteValue [quoteId=").append(this.quoteId).append(", date=").append(this.date)
				.append(", open=").append(this.open).append(", close=").append(this.close).append(", high=")
				.append(this.high).append(", low=").append(this.low).append(", volume=").append(this.volume)
				.append(", priceChange=").append(this.priceChange).append(", week52High=").append(this.week52High)
				.append(", week52Low=").append(this.week52Low).append(", investment=").append(this.investment.getSymbol())
				.append("]");
		return builder.toString();
	}

}
