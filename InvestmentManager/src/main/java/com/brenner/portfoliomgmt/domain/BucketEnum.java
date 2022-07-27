/**
 * 
 */
package com.brenner.portfoliomgmt.domain;

/**
 * Enumerated value to support dividing holdings into buckets (i.e. The Bucket Strategy)
 * 
 * @author dbrenner
 * 
 */
public enum BucketEnum {
	
	BUCKET_1("0 - 3 years"),BUCKET_2("3 - 10 years"), BUCKET_3("Beyond 10 years"), BUCKET_NA("Excluded");
	
	private String description;
	
	private BucketEnum(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public static BucketEnum getBucketEnumByOrdinalValue(int ordinalValue) {
		switch (ordinalValue) {
			case 0:
				return BucketEnum.BUCKET_1;
			case 1:
				return BucketEnum.BUCKET_2;
			case 2: 
				return BucketEnum.BUCKET_3;
			case 3:
				return BucketEnum.BUCKET_NA;
			default:
				return null;
		}
	}

}
