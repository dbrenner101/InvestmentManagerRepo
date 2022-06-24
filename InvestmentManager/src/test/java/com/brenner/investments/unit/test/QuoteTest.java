package com.brenner.investments.unit.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.brenner.investments.entities.Quote;
import com.brenner.investments.test.TestDataHelper;

public class QuoteTest {

	@Test
	public void testQuoteComparison() throws Exception {
		
		DateTime past = new DateTime();
		past = past.minusYears(5);
		
		DateTime future = new DateTime();
		future = future.plusYears(5);
		
		Quote q1 = TestDataHelper.getQuoteAAPL();
		q1.setDate(future.toDate());
		
		Quote q2 = TestDataHelper.getQuoteFB();
		q2.setDate(past.toDate());
		
		List<Quote> quotes = new ArrayList<Quote>(2);
		quotes.add(q2);
		quotes.add(q1);
		
		assertEquals(quotes.get(0), q2);
		assertEquals(quotes.get(1), q1);
		
		Collections.sort(quotes);
		
		assertEquals(quotes.get(0), q1);
		assertEquals(quotes.get(1), q2);
	}
}
