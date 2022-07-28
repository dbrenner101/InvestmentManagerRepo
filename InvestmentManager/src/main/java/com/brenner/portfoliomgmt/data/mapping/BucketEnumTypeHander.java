/**
 * 
 */
package com.brenner.portfoliomgmt.data.mapping;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.brenner.portfoliomgmt.domain.BucketEnum;

/**
 *
 * @author dbrenner
 * 
 */
public class BucketEnumTypeHander extends EnumTypeHandler<BucketEnum> {

	/**
	 * @param type
	 */
	public BucketEnumTypeHander(Class<BucketEnum> type) {
		super(type);
	}

	@Override
	public void setParameter(PreparedStatement ps, int i, BucketEnum parameter, JdbcType jdbcType) throws SQLException {
		ps.setInt(i, parameter.ordinal());
		
	}

	@Override
	public BucketEnum getResult(ResultSet rs, String columnName) throws SQLException {
		
		Integer bucketId = rs.getInt(columnName);
		if (rs.wasNull()) {
			bucketId = 99;
		}
		
		return BucketEnum.getBucketEnumByOrdinalValue(bucketId);
	}

	@Override
	public BucketEnum getResult(ResultSet rs, int columnIndex) throws SQLException {
		Integer bucketId = rs.getInt(columnIndex);
		if (rs.wasNull()) {
			bucketId = 99;
		}
		
		return BucketEnum.getBucketEnumByOrdinalValue(bucketId);
	}

	@Override
	public BucketEnum getResult(CallableStatement cs, int columnIndex) throws SQLException {
		Integer bucketId = cs.getInt(columnIndex);
		if (cs.wasNull()) {
			bucketId = 99;
		}
		
		return BucketEnum.getBucketEnumByOrdinalValue(bucketId);
	}

}
