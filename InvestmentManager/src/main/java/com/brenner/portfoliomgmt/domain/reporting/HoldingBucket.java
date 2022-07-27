/**
 * 
 */
package com.brenner.portfoliomgmt.domain.reporting;

import com.brenner.portfoliomgmt.domain.BucketEnum;

/**
 *
 * @author dbrenner
 * 
 */
public class HoldingBucket {
	
	private BucketEnum bucket;
	
	private Double amount;
	
	private Double amountAtPurchase;
	
	public HoldingBucket() {}

	public BucketEnum getBucket() {
		return this.bucket;
	}

	public void setBucket(BucketEnum bucket) {
		this.bucket = bucket;
	}

	public Double getAmount() {
		return this.amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getAmountAtPurchase() {
		return this.amountAtPurchase;
	}

	public void setAmountAtPurchase(Double amountAtPurchase) {
		this.amountAtPurchase = amountAtPurchase;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HoldingBucket [bucket=").append(this.bucket).append(", amount=").append(this.amount)
				.append(", amountAtPurchase=").append(this.amountAtPurchase).append("]");
		return builder.toString();
	}
	

}
