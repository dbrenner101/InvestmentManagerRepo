package com.brenner.investments.test;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

import com.brenner.investments.util.CommonUtils;

public class CommonUtilsTests {

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
