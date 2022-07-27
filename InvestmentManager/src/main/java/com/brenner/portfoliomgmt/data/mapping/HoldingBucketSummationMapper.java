/**
 * 
 */
package com.brenner.portfoliomgmt.data.mapping;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.brenner.portfoliomgmt.data.entities.reporting.HoldingBucketSummation;

/**
 *
 * @author dbrenner
 * 
 */
@Mapper
public interface HoldingBucketSummationMapper {

	List<HoldingBucketSummation> historicalBucketSummation();
}
