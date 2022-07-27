/**
 * 
 */
package com.brenner.portfoliomgmt.data.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.brenner.portfoliomgmt.domain.BucketEnum;
import com.brenner.portfoliomgmt.domain.reporting.HoldingBucket;

/**
 *
 * @author dbrenner
 * 
 */
public class HoldingsByBucketMapping implements RowMapper<HoldingBucket> {

	@Override
	public HoldingBucket mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		HoldingBucket holding = new HoldingBucket();
		holding.setAmountAtPurchase(rs.getDouble("purchase_value"));
		holding.setAmount(rs.getDouble("current_value"));
		Integer bucketId = rs.getInt("bucket");
		if (rs.wasNull()) {
			bucketId = 99;
		}
		holding.setBucket(BucketEnum.getBucketEnumByOrdinalValue(bucketId));
		return holding;
	}

}
