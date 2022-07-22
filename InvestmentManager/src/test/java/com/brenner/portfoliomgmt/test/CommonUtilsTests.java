package com.brenner.portfoliomgmt.test;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import com.brenner.portfoliomgmt.util.CommonUtils;

@Testable
public class CommonUtilsTests {
	
	@Test
	public void testConvertCurrencyStringToFloat() throws Exception {
		
		String testString = "$69.95";
		
		assertEquals(69.95F, CommonUtils.convertCurrencyStringToFloat(testString));
		
		testString="$5,955.04";
		assertEquals(5955.04F, CommonUtils.convertCurrencyStringToFloat(testString));
		
		testString="$1,095,955.04";
		assertEquals(1095955.04F, CommonUtils.convertCurrencyStringToFloat(testString));
	}

	@Test
	public void testWorkDaysSinceDateWeekDayFutureStart() throws Exception {
		DateTime startPoint = new DateTime(2018, 10, 8, 12, 1);
		
		DateTime pastPoint = new DateTime(2018, 10, 1, 12, 1);
		
		int numWorkDaysBetween = CommonUtils.workdayDiff(pastPoint.toDate(), startPoint.toDate());
		
		assertEquals(5, numWorkDaysBetween);;
	}

	@Test
	public void testWorkDaysSinceDateWeekDayFutureSaturdayStart() throws Exception {
		DateTime startPoint = new DateTime(2018, 10, 6, 12, 1);
		
		DateTime pastPoint = new DateTime(2018, 10, 1, 12, 1);
		
		int numWorkDaysBetween = CommonUtils.workdayDiff(pastPoint.toDate(), startPoint.toDate());
		
		assertEquals(5, numWorkDaysBetween);;
	}

	@Test
	public void testWorkDaysSinceDateWeekDayFutureSundayStart() throws Exception {
		DateTime startPoint = new DateTime(2018, 10, 7, 12, 1);
		
		DateTime pastPoint = new DateTime(2018, 10, 1, 12, 1);
		
		int numWorkDaysBetween = CommonUtils.workdayDiff(pastPoint.toDate(), startPoint.toDate());
		
		assertEquals(5, numWorkDaysBetween);;
	}

	@Test
	public void testWorkDaysSinceDateWeekDayFutureSaturdayEndAndEnd() throws Exception {
		DateTime startPoint = new DateTime(2018, 10, 6, 12, 1);
		
		DateTime pastPoint = new DateTime(2018, 9, 29, 12, 1);
		
		int numWorkDaysBetween = CommonUtils.workdayDiff(pastPoint.toDate(), startPoint.toDate());
		
		assertEquals(5, numWorkDaysBetween);;
	}

	@Test
	public void testWorkDaysSinceDateWeekDayFutureSundayStartAndEnd() throws Exception {
		DateTime startPoint = new DateTime(2018, 10, 7, 12, 1);
		
		DateTime pastPoint = new DateTime(2018, 9, 30, 12, 1);
		
		int numWorkDaysBetween = CommonUtils.workdayDiff(pastPoint.toDate(), startPoint.toDate());
		
		assertEquals(5, numWorkDaysBetween);;
	}
	
	
}
