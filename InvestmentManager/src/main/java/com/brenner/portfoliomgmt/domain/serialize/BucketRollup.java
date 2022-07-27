/**
 * 
 */
package com.brenner.portfoliomgmt.domain.serialize;

import com.brenner.portfoliomgmt.domain.BucketEnum;

/**
 *
 * @author dbrenner
 * 
 */
public class BucketRollup {
	
	private BucketEnum bucketGroup;
	private Float valueAtPurchase;
	private Float currentValue;
	
	public BucketRollup() {}
	
	public BucketRollup(BucketEnum bucketGroup, Float valueAtPurchase, Float currentValue) {
		super();
		this.bucketGroup = bucketGroup;
		this.valueAtPurchase = valueAtPurchase;
		this.currentValue = currentValue;
	}

	public BucketEnum getBucketGroup() {
		return this.bucketGroup;
	}

	public void setBucketGroup(BucketEnum bucketGroup) {
		this.bucketGroup = bucketGroup;
	}

	public Float getValueAtPurchase() {
		return this.valueAtPurchase;
	}

	public void setValueAtPurchase(Float valueAtPurchase) {
		this.valueAtPurchase = valueAtPurchase;
	}

	public Float getCurrentValue() {
		return this.currentValue;
	}

	public void setCurrentValue(Float currentValue) {
		this.currentValue = currentValue;
	}
	

}
