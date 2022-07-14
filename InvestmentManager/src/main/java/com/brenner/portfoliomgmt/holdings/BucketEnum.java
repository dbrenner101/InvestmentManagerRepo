/**
 * 
 */
package com.brenner.portfoliomgmt.holdings;

/**
 * Enumerated value to support dividing holdings into buckets (i.e. The Bucket Strategy)
 * 
 * @author dbrenner
 * 
 */
public enum BucketEnum {
	
	BUCKET_1("0 - 3 years"),BUCKET_2("3 - 10 years"), BUCKET_3("Beyond 10 years"), BUCKET_0("excluded");
	
	private String description;
	
	private BucketEnum(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}

}
