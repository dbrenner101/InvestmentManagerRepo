/**
 * 
 */
package com.brenner.portfoliomgmt.data.mapping;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;

import com.brenner.portfoliomgmt.data.entities.reporting.HoldingBucketSummation;
import com.brenner.portfoliomgmt.domain.BucketEnum;
import com.brenner.portfoliomgmt.domain.reporting.HoldingBucket;

/**
 *
 * @author dbrenner
 * 
 */
@Mapper
public interface HoldingBucketSummationMapper {

	List<HoldingBucketSummation> historicalBucketSummation();
	
	@Result(property = "bucket", column = "bucket", typeHandler=BucketEnumTypeHander.class, javaType = BucketEnum.class)
	List<HoldingBucket> holdingsByBucket();
	
	Date findbucketSummaryReport(Date summaryReportDate);
	
	void insertBucketSummation(HoldingBucketSummation summation);
	
	void updateBucketSummation(HoldingBucketSummation summation);
}
