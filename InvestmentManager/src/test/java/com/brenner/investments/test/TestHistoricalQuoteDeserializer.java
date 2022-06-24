package com.brenner.investments.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.brenner.investments.data.deserialization.HistoricalQuotesDeserializer;
import com.brenner.investments.entities.HistoricalQuotes;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.util.CommonUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class TestHistoricalQuoteDeserializer {

    private ObjectMapper mapper;
    private HistoricalQuotesDeserializer deserializer;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        deserializer = new HistoricalQuotesDeserializer();
    }
    
    @Test
    public void testDeserializeHistoricalQuoteJson() throws Exception {
        
        JsonParser parser = mapper.getFactory().createParser(getJsonResponse());
        DeserializationContext context = mapper.getDeserializationContext();
        
        HistoricalQuotes historicalQuotes = deserializer.deserialize(parser, context);
        assertNotNull(historicalQuotes); 
        
        Map<Date, Quote> quotes = historicalQuotes.getQuotes();
        assertNotNull(quotes.get(CommonUtils.convertDatePickerDateFormatStringToDate("2018-07-30")));
        Quote quote = quotes.get(CommonUtils.convertDatePickerDateFormatStringToDate("2018-07-30"));
        assertNotNull(quote);
        assertTrue(quote.getOpen().toString().equals("191.2293"));
        
    }
    
    
    private String getJsonResponse() throws Exception {
        
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("[{\"date\":\"2018-07-30\",\"open\":191.2293,\"high\":191.5283,\"low\":188.4092,\"close\":189.2463,\"volume\":21029535,\"unadjustedVolume\":21029535,\"change\":-1.0663,\"changePercent\":-0.56,\"vwap\":189.3205,\"label\":\"Jul 30\",\"changeOverTime\":0},{\"date\":\"2018-07-31\",\"open\":189.6349,\"high\":191.4685,\"low\":188.6783,\"close\":189.6249,\"volume\":39373038,\"unadjustedVolume\":39373038,\"change\":0.378671,\"changePercent\":0.2,\"vwap\":190.1102,\"label\":\"Jul 31\",\"changeOverTime\":0.0020005675143979344},{\"date\":\"2018-08-01\",\"open\":198.434,\"high\":201.0548,\"low\":196.6204,\"close\":200.7958,\"volume\":67935716,\"unadjustedVolume\":67935716,\"change\":11.1708,\"changePercent\":5.891,\"vwap\":199.2232,\"label\":\"Aug 1\",\"changeOverTime\":0.06102893425129064},{\"date\":\"2018-08-02\",\"open\":199.879,\"high\":207.6517,\"low\":199.6498,\"close\":206.6652,\"volume\":62404012,\"unadjustedVolume\":62404012,\"change\":5.8694,\"changePercent\":2.923,\"vwap\":205.1286,\"label\":\"Aug 2\",\"changeOverTime\":0.09204354325553529},{\"date\":\"2018-08-03\",\"open\":206.3064,\"high\":208.0105,\"low\":204.7621,\"close\":207.2631,\"volume\":33447396,\"unadjustedVolume\":33447396,\"change\":0.597903,\"changePercent\":0.289,\"vwap\":206.5696,\"label\":\"Aug 3\",\"changeOverTime\":0.09520291810196563},{\"date\":\"2018-08-06\",\"open\":207.273,\"high\":208.5187,\"low\":206.3463,\"close\":208.3393,\"volume\":25425387,\"unadjustedVolume\":25425387,\"change\":1.0762,\"changePercent\":0.519,\"vwap\":207.7645,\"label\":\"Aug 6\",\"changeOverTime\":0.10088968714315692},{\"date\":\"2018-08-07\",\"open\":208.5884,\"high\":208.7678,\"low\":206.0374,\"close\":206.3862,\"volume\":25587387,\"unadjustedVolume\":25587387,\"change\":-1.9531,\"changePercent\":-0.937,\"vwap\":206.8425,\"label\":\"Aug 7\",\"changeOverTime\":0.0905692740095844},{\"date\":\"2018-08-08\",\"open\":205.3299,\"high\":207.0837,\"low\":203.8052,\"close\":206.5257,\"volume\":22525487,\"unadjustedVolume\":22525487,\"change\":0.13951,\"changePercent\":0.068,\"vwap\":206.0459,\"label\":\"Aug 8\",\"changeOverTime\":0.09130640863255984},{\"date\":\"2018-08-09\",\"open\":206.5556,\"high\":209.0468,\"low\":206.4758,\"close\":208.15,\"volume\":23492626,\"unadjustedVolume\":23492626,\"change\":1.6243,\"changePercent\":0.786,\"vwap\":208.3263,\"label\":\"Aug 9\",\"changeOverTime\":0.09988940338595796},{\"date\":\"2018-08-10\",\"open\":207.36,\"high\":209.1,\"low\":206.67,\"close\":207.53,\"volume\":24611202,\"unadjustedVolume\":24611202,\"change\":-0.619964,\"changePercent\":-0.298,\"vwap\":207.8576,\"label\":\"Aug 10\",\"changeOverTime\":0.09661324950606702},{\"date\":\"2018-08-13\",\"open\":207.7,\"high\":210.952,\"low\":207.7,\"close\":208.87,\"volume\":25890880,\"unadjustedVolume\":25890880,\"change\":1.34,\"changePercent\":0.646,\"vwap\":209.5754,\"label\":\"Aug 13\",\"changeOverTime\":0.1036939691819603},{\"date\":\"2018-08-14\",\"open\":210.155,\"high\":210.56,\"low\":208.26,\"close\":209.75,\"volume\":20748010,\"unadjustedVolume\":20748010,\"change\":0.88,\"changePercent\":0.421,\"vwap\":209.4477,\"label\":\"Aug 14\",\"changeOverTime\":0.10834399404374094},{\"date\":\"2018-08-15\",\"open\":209.22,\"high\":210.74,\"low\":208.33,\"close\":210.24,\"volume\":28807564,\"unadjustedVolume\":28807564,\"change\":0.49,\"changePercent\":0.234,\"vwap\":209.4035,\"label\":\"Aug 15\",\"changeOverTime\":0.11093321243268703},{\"date\":\"2018-08-16\",\"open\":211.75,\"high\":213.8121,\"low\":211.47,\"close\":213.32,\"volume\":28500367,\"unadjustedVolume\":28500367,\"change\":3.08,\"changePercent\":1.465,\"vwap\":212.9938,\"label\":\"Aug 16\",\"changeOverTime\":0.12720829944891923},{\"date\":\"2018-08-17\",\"open\":213.44,\"high\":217.95,\"low\":213.16,\"close\":217.58,\"volume\":35426997,\"unadjustedVolume\":35426997,\"change\":4.26,\"changePercent\":1.997,\"vwap\":216.0085,\"label\":\"Aug 17\",\"changeOverTime\":0.14971864707526658},{\"date\":\"2018-08-20\",\"open\":218.1,\"high\":219.18,\"low\":215.11,\"close\":215.46,\"volume\":30287695,\"unadjustedVolume\":30287695,\"change\":-2.12,\"changePercent\":-0.974,\"vwap\":216.4783,\"label\":\"Aug 20\",\"changeOverTime\":0.13851631445370408},{\"date\":\"2018-08-21\",\"open\":216.8,\"high\":217.19,\"low\":214.025,\"close\":215.04,\"volume\":26159755,\"unadjustedVolume\":26159755,\"change\":-0.42,\"changePercent\":-0.195,\"vwap\":215.6262,\"label\":\"Aug 21\",\"changeOverTime\":0.13629698440603596},{\"date\":\"2018-08-22\",\"open\":214.1,\"high\":216.36,\"low\":213.84,\"close\":215.05,\"volume\":19018131,\"unadjustedVolume\":19018131,\"change\":0.01,\"changePercent\":0.005,\"vwap\":215.1777,\"label\":\"Aug 22\",\"changeOverTime\":0.1363498255976472},{\"date\":\"2018-08-23\",\"open\":214.65,\"high\":217.05,\"low\":214.6,\"close\":215.49,\"volume\":18883224,\"unadjustedVolume\":18883224,\"change\":0.44,\"changePercent\":0.205,\"vwap\":215.9624,\"label\":\"Aug 23\",\"changeOverTime\":0.13867483802853753},{\"date\":\"2018-08-24\",\"open\":216.6,\"high\":216.9,\"low\":215.11,\"close\":216.16,\"volume\":18476356,\"unadjustedVolume\":18476356,\"change\":0.67,\"changePercent\":0.311,\"vwap\":216.0586,\"label\":\"Aug 24\",\"changeOverTime\":0.1422151978664841},{\"date\":\"2018-08-27\",\"open\":217.15,\"high\":218.74,\"low\":216.33,\"close\":217.94,\"volume\":20525117,\"unadjustedVolume\":20525117,\"change\":1.78,\"changePercent\":0.823,\"vwap\":217.7725,\"label\":\"Aug 27\",\"changeOverTime\":0.15162092997326768}]");
        
        return buffer.toString();
    }
    
}
