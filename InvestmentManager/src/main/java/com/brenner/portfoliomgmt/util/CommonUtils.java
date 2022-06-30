package com.brenner.portfoliomgmt.util;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.portfoliomgmt.InvestmentsProperties;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Common utility methods
 * 
 * @author dbrenner
 *
 */
public class CommonUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);
	
	public static final int DAYS_PER_WEEKEND = 2;
	public static final int WEEK_START = DateTimeConstants.MONDAY;
	public static final int WEEK_END = DateTimeConstants.FRIDAY;
    
    
    private CommonUtils () {}
    
    /**
     * MM/dd/yyyy date format
     */
    private final SimpleDateFormat commonDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    
    /**
     * yyyy-MM-dd date format
     */
    private final SimpleDateFormat datePickerDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Converts a date string to a date object. Uses the commonDateFormat formatter
     * 
     * @param dateString - string to convert
     * @return new date object
     * @throws ParseException is thrown when the string can't be converted to a date
     */
	public static Date convertCommonDateFormatStringToDate(String dateString) throws ParseException {
		
		logger.debug("Converting string {} to date object", dateString);
		
		return new CommonUtils().getCommonDateFormat().parse(dateString);
	}
	
	public static String convertDateToMMDDYYYYString(Date date) {
		
		logger.debug("Converting date {} to MMDDYYYY string", date);
		
		return new CommonUtils().getCommonDateFormat().format(date);
	}
	
	/**
     * Converts a date string to a date object. Uses the iexJsonDateFormat formatter
     * 
     * @param dateString - string to convert
     * @return new date object
     * @throws ParseException is thrown when the string can't be converted to a date
     */
	public static Date convertDatePickerDateFormatStringToDate(String dateString) throws ParseException {
		
		logger.debug("Converting string {} to date", dateString);
	    
	    return new CommonUtils().getIexJasonDateFormat().parse(dateString);
	}
	
	/**
	 * Determines the number of weekdays between two dates.
	 * 
	 * @param begin - the start period
	 * @param end - the end period
	 * @return the count of workdays in the period
	 */
	public static int workDaysBetweenDates(DateTime begin, DateTime end) {
	    
	    DateTime startCycle = begin;
	    
	    int weekDaysCount = 0;
	    while (startCycle.isBefore(end)) {
	        if (startCycle.getDayOfWeek() != 0 && startCycle.getDayOfWeek() != 6) {
	            ++weekDaysCount;
	        }
	        
	        startCycle = startCycle.plusDays(1);
	    }
	    
	    return weekDaysCount;
	}

	  public static int workdayDiff(Date d1, Date d2) {
		  
		  LocalDate start = LocalDate.fromDateFields(d1);
		  LocalDate end = LocalDate.fromDateFields(d2);

		  start = toWorkday(start);
		  end = toWorkday(end);

		  int daysBetween = Days.daysBetween(start, end).getDays();
		  int weekendsBetween = Weeks.weeksBetween(start.withDayOfWeek(WEEK_START), end.withDayOfWeek(WEEK_START)).getWeeks();

		  return daysBetween - (weekendsBetween * DAYS_PER_WEEKEND);
	  } 

	  public static LocalDate toWorkday(LocalDate d) {
		  if (d.getDayOfWeek() > WEEK_END) {
			  return d.plusDays(DateTimeConstants.DAYS_PER_WEEK - d.getDayOfWeek() + 1);
		  }
		  return d;
	  }
	
	/**
	 * Converts an object to JSON and writes the result to the output stream.
	 * 
	 * @param out - output stream
	 * @param obj - object to convert
	 * @throws IOException is thrown if there are issues writing to the stream
	 */
	public static void serializeObjectToJson(OutputStream out, Object obj) throws IOException {
		
		logger.info("Entered serializeObjectToJson()");
		logger.debug("Serializing {} to JSON", obj);
	    
	    ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(Include.ALWAYS);
        mapper.setDateFormat(new CommonUtils().commonDateFormat);
        
        logger.info("Exiting serializeObjectToJson()");
        
        mapper.writeValue(out, obj);
	}
	
	/**
	 * Builds a list of DateTime weekday objects for the last six months
	 * 
	 * @return the list of weekdays
	 */
	public static List<DateTime> getLastSixMonthsOfDates() {
	    
	    DateTime today = new DateTime();
	    DateTime startPeriod = today.minusMonths(6);
	    
	    List<DateTime> daysBetween = new ArrayList<>();
	    
	    while (startPeriod.isBefore(today.getMillis())) {
	        if (startPeriod.getDayOfWeek() > 0 && startPeriod.getDayOfWeek() < 6) {
	            daysBetween.add(startPeriod);
	        }
	        startPeriod = startPeriod.plusDays(1);
	    }
	    
	    
	    return daysBetween;
	}
	
	/**
	 * Determines a date that is the defined number of months in the past
	 * 
	 * @param numMonthsBack - number of months to regress
	 * @return the past Date
	 */
	public static Date getDateSomeNumMonthsAgo(Integer numMonthsBack) {
	    
	    DateTime now = new DateTime();
	    now = now.minusMonths(numMonthsBack);
	    
	    return now.toDate(); 
	}
	
	/**
	 * Builds a map with two dates. One for two days ago and one for 6 months ago
	 * 
	 * @param props - the common application properties to get shared keys
	 * @return the map
	 */
	public static Map<String, DateTime> getWeekDay2DaysAgoAndWeekDaySixMonthsAgo(InvestmentsProperties props) {
	    
	    DateTime nearestWeekDay = new DateTime();
	    nearestWeekDay = nearestWeekDay.minusDays(2);
	    nearestWeekDay = nearestWeekDay.withHourOfDay(0);
	    nearestWeekDay = nearestWeekDay.withMinuteOfHour(0);
	    nearestWeekDay = nearestWeekDay.withSecondOfMinute(0);
	    nearestWeekDay = nearestWeekDay.withMillisOfSecond(0);
	     
	    
	    while (nearestWeekDay.getDayOfWeek() > 5) {
	        nearestWeekDay = nearestWeekDay.minusDays(1);
	    }
	    
	    DateTime sixMonthsAgo = nearestWeekDay.minusMonths(6);
	    
	    while (sixMonthsAgo.getDayOfWeek() > 5) {
	        sixMonthsAgo = sixMonthsAgo.plusDays(1);
	    };
	    
	    Map<String, DateTime> twoDatesMap = new HashMap<>();
	    twoDatesMap.put(props.getSixMonthsAgoWeekdayKey(), sixMonthsAgo);
	    twoDatesMap.put(props.getNearestWeekdayKey(), nearestWeekDay);
	    
	    return twoDatesMap;
	    
	}
	
	/**
	 * Converts a date to a string representation. Uses the iexJsonDateFormat date formatter
	 * 
	 * @param date - date to convert
	 * @return the string representation of the date
	 */
	public static String convertDateToMMYYDDString(Date date) {
		
		logger.debug("Converting date {} to MMYYDD string", date);
	    
	    return new CommonUtils().datePickerDateFormat.format(date);
	}

	/**
	 * Access to the common date formatter
	 * 
	 * @return the date formatter
	 */
    public SimpleDateFormat getCommonDateFormat() {
        return commonDateFormat;
    }

    /**
     * Access to the JSON date formatter
     * 
     * @return the date formatter
     */
    public SimpleDateFormat getIexJasonDateFormat() {
        return datePickerDateFormat;
    }
}
