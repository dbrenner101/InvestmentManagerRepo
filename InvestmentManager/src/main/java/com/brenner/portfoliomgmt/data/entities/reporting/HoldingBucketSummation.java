/**
 * 
 */
package com.brenner.portfoliomgmt.data.entities.reporting;

import java.util.Date;

/**
 *
 * @author dbrenner
 * 
 */
public class HoldingBucketSummation {
	
	private Date summationDate;
	
	private Double bucket1Total;
	
	private Double bucket2Total;
	
	private Double bucket3Total;
	
	private Double noBucketTotal; 
	
	public HoldingBucketSummation() {}

	public Date getSummationDate() {
		return this.summationDate;
	}

	public void setSummationDate(Date summationDate) {
		this.summationDate = summationDate;
	}

	public Double getBucket1Total() {
		return this.bucket1Total;
	}

	public void setBucket1Total(Double bucket1Total) {
		this.bucket1Total = bucket1Total;
	}

	public Double getBucket2Total() {
		return this.bucket2Total;
	}

	public void setBucket2Total(Double bucket2Total) {
		this.bucket2Total = bucket2Total;
	}

	public Double getBucket3Total() {
		return this.bucket3Total;
	}

	public void setBucket3Total(Double bucket3Total) {
		this.bucket3Total = bucket3Total;
	}

	public Double getNoBucketTotal() {
		return this.noBucketTotal;
	}

	public void setNoBucketTotal(Double noBucketTotal) {
		this.noBucketTotal = noBucketTotal;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HoldingBucketSummation [summationDate=").append(this.summationDate).append(", bucket1Total=")
				.append(this.bucket1Total).append(", bucket2Total=").append(this.bucket2Total).append(", bucket3Total=")
				.append(this.bucket3Total).append(", noBucketTotal=").append(this.noBucketTotal).append("]");
		return builder.toString();
	}

}
