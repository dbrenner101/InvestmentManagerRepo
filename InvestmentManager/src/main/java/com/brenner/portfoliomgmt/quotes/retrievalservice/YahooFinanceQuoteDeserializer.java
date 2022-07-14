/**
 * 
 */
package com.brenner.portfoliomgmt.quotes.retrievalservice;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.quotes.Quote;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 *
 * @author dbrenner
 * 
 */
public class YahooFinanceQuoteDeserializer extends StdDeserializer<Quote> {

	private static final long serialVersionUID = -8724484626910957374L;
	
	private static final Logger log = LoggerFactory.getLogger(YahooFinanceQuoteDeserializer.class);

    protected YahooFinanceQuoteDeserializer(Class<?> vc) {
        super(vc);
    }
    
    public YahooFinanceQuoteDeserializer() {
        this(null);
    }

    /**
     * {
     * "language":"en-US",
     * "region":"US",
     * "quoteType":"EQUITY",
     * "typeDisp":"Equity",
     * "quoteSourceName":"Nasdaq Real Time Price",
     * "triggerable":true,"
     * customPriceAlertConfidence":"HIGH",
     * "quoteSummary":{
     * 		"earnings":{
     * 			"maxAge":86400,"earningsChart":{"quarterly":[{"date":"2Q2021","actual":2.33,"estimate":2.29},{"date":"3Q2021","actual":2.52,"estimate":2.5},{"date":"4Q2021","actual":3.35,"estimate":3.3},{"date":"1Q2022","actual":1.4,"estimate":1.38}],"currentQuarterEstimate":2.29,"currentQuarterEstimateDate":"2Q","currentQuarterEstimateYear":2022,"earningsDate":[1658174400]},"financialsChart":{"yearly":[{"date":2018,"revenue":79591000000,"earnings":8728000000},{"date":2019,"revenue":57714000000,"earnings":9431000000},{"date":2020,"revenue":55179000000,"earnings":5590000000},{"date":2021,"revenue":57351000000,"earnings":5743000000}],"quarterly":[{"date":"2Q2021","revenue":18745000000,"earnings":1325000000},{"date":"3Q2021","revenue":17618000000,"earnings":1130000000},{"date":"4Q2021","revenue":3258000000,"earnings":2333000000},{"date":"1Q2022","revenue":14197000000,"earnings":733000000}]},"financialCurrency":"USD"}},"exDividendDate":1652054400,"earningsTimestamp":1658174400,"earningsTimestampStart":1658174400,"earningsTimestampEnd":1658174400,"trailingAnnualDividendRate":6.57,"trailingPE":22.581703,"pegRatio":1.07,"dividendsPerShare":6.56,"dividendRate":6.6,"trailingAnnualDividendYield":0.047928218,"dividendYield":4.81,"revenue":5.8361E10,"priceToSales":2.1117527,"marketState":"REGULAR","epsTrailingTwelveMonths":6.089,"epsForward":10.43,"epsCurrentYear":9.69,"epsNextQuarter":2.35,"priceEpsCurrentYear":14.189887,"priceEpsNextQuarter":58.51064,"sharesOutstanding":896320000,"bookValue":21.18,"fiftyDayAverage":134.9242,"fiftyDayAverageChange":2.5758057,"fiftyDayAverageChangePercent":0.019090762,"twoHundredDayAverage":129.9519,"twoHundredDayAverageChange":7.5480957,"twoHundredDayAverageChangePercent":0.058083765,"marketCap":123244003328,"forwardPE":13.1831255,"priceToBook":6.4919734,"currency":"USD","firstTradeDateMilliseconds":-252322200000,"priceHint":2,"components":["^NQDMXJPLMAUD","^NQUSBLM","^NQSHYL","^NQUSB9533LMAUD","^NQGMOIN","^NQNALMEURN","^NQUSB9000LMJPYN","^NQUSB9530LM","^NQUSBLMCAD","^NQUSB9000LMEURN","^NQDMXKRJPYN","^NQDMXKRLMAUDN","^NQNA9000LMGBPN","^NQDMXJPLMAUDN","^NQDMXKRLMAUD","^NQFFUSHYN","^NQUSBLMJPY","^NQUSHEIN","^NQNA9000LMEUR","^NQNA9000LMCAD","^NQGXGBLMN","^NQUS500LCVN","^NQUSB9533LMEURN","^NQDMXKRLCJPY","^NQUSB9000LMEUR","^NQDXUSLCEUR","^NQUSB9533LMGBP","^CPQ","^CPQNTR","^NQUSB9000LMCAD","^NQUSB9533LMJPYN","^NQDM9000LMJPY","^NQG9000LMCADN","^NQDMXKRAUD","^NQDMXKRLMGBPN","^NQDXUSMLTCVN","^NQGXGBLMGBPN","^NQDM9000LMEURN","^NQGXJPLMJPYN","^NQNALMCADN","^NQUSBLMEUR","^NQG9000LMGBP","^NQDM9000LMJPYN","^NQNALMJPY","^NQUSHEI","^DAA","^MSH","^NQGXGBLMJPY","^QCRD","^NQGXJPLMJPY","^NQUSB9530LMCAD","^NQUSB9530LMGBPN","^NQDMXKR","^NQDMXGBLMCADN","^NQUSB9530LMEUR","^NQUSHEIEURN","^NQUSB9000LMJPY","^NQDMXKRLCN","^NQGXGBLMEURN","^NQGXJPLMCAD","^NQUSB9530LMN","^NQGXJPLMEUR","^NQUSB9530LMGBP","^NQDMXJPLMGBPN","^NQUSLV","^NQDMXJPLMN","^NQGIHEIGBP","^NQDM9000LMAUD","^NQDMXKRLCJPYN","^NQDMXKRLMCADN","^NQDMXKRLCEUR","^NQUSB9000LMN","^NQDXUSLC","^NQGXGBLMJPYN","^IBMSY","^NQG9000LMJPYN","^NQGIHEIGBPN","^NQUSBLMEURN","^NQDMXKRJPY","^NQDMXKRLCCAD","^NQDMXKRLCEURN","^NQG9000LMAUD","^NQUSHEIGBPN","^NQGXJPLMGBPN","^NQUSB9000LMCADN","^NQNALM","^NQUS500LC","^NQDMXGBLMJPY","^NQUSMLTCV","^NQNALMAUD","^NQNA9000LMN","^NQVMVUS","^NQDXUSLCN","^NQGXGBLMAUD","^NQUSB9530LMJPYN","^NQDXUSLCV","^NQUSBLMN","^NQDMXJPLMGBP","^NQDMXKRLMEUR","^NQNALMAUDN","^NQG9000LMEURN","^NQDM9000LMCADN","^NQFFUSHY","^NQDMXKRLMCAD","^NQDMXGBLM","^NQSHYLN","^NQNA9000LMGBP","^NQDMXGBLMJPYN","^NQG9000LMN","^NQDMXKRLMJPY","^NQUSBLMJPYN","^NQDM9000LMN","^NQDMXGBLMCAD","^XMI","^NQDMXGBLMEURN","^NQDMXKRGBPN","^NQDMXGBLMEUR","^NQDMXKRAUDN","^NQDMXKRLMJPYN","^NQUSBLMCADN","^NQDMXGBLMN","^NQUSB9533LMGBPN","^NQNALMGBPN","^NQGXGBLMCADN","^NQDMXKRLCCADN","^NQGXJPLMCADN","^BGDNTR","^NQUSHEIGBP","^NQGXJPLM","^NQNA9000LMAUD","^NQUSBLMGBP","^NQNALMN","^NQUSB9000LMAUD","^NQNA9000LM","^NQFFUSLV","^NQGXJPLMAUDN","^NQUSLVN","^NQGIHEIN","^NQCAPST","^NQUSB9530LMEURN","^NQ96DIVUS","^NQUSB9530LMAUD","^DJI","^NQDMXKRLCGBP","^NQDMXKRLM","^NQUSB9533LMJPY","^NQDM9000LMGBP","^NQDMXKRLC","^NQNALMGBP","^NQDMXJPLMJPYN","^NQG9000LMEUR","^NQGXGBLM","^NQG9000LMCAD","^NQGMOI","^NQDMXKRN","^NQUSMLTCVN","^NQCRDN","^NQUSB9533LM","^NQDMXJPLMEURN","^NQG9000LMJPY","^NQFFUSLVN","^NQNA9000LMJPYN","^NQDMXJPLM","^NQDMXKREUR","^NQUSB9533LMAUDN","^NQGXJPLMGBP","^NQDM9000LMAUDN","^NQDMXKRLMEURN","^NQUSBLMAUD","^NQGXGBLMGBP","^NQDMXKRCAD","^NQNA9000LMEURN","^DJA","^DAAXMLPREIT","^NQDXUSMLTCV","^NQUSB9533LMCAD","^NQUSB9000LMAUDN","^NQUSB9000LMGBP","^NQDXUSLCEURN","^NQUSB9533LMEUR","^NQNA9000LMAUDN","^NQG9000LMAUDN","^NQDMXKRLCAUD","^NQDXUSMEGAN","^NQNALMCAD","^NQUSB9530LMJPY","^XCI","^NQGXJPLMEURN","^NQDM9000LM","^NQDXUSLCGBP","^NQGXJPLMN","^NQNALMEUR","^NQGXGBLMEUR","^NQDMXKRGBP","^NQDM9000LMEUR","^NQDM9000LMCAD","^NQDMXGBLMGBPN","^BGD","^NQCRD","^NQDMXKRCADN","^NQGXGBLMCAD","^NQDMXGBLMGBP","^NQDMXKRLCAUDN","^NQDM9000LMGBPN","^NQDXUSLCGBPN","^NQG9000LM","^NQUSBLMGBPN","^NQDMXKRLMN","^NQUSB9530LMCADN","^NQUSB9533LMCADN","^NQUSB9533LMN","^NQGXJPLMAUD","^NQDMXGBLMAUDN","^NQUSHEIEUR","^NQUSB9000LM","^NQDMXJPLMJPY","^NQDMXKRLCGBPN","^NQVMVUSN","^NQGIHEIEURN","^NQDMXKREURN","^NQNALMJPYN","^NQUSB9000LMGBPN","^NQG9000LMGBPN","^NQDMXJPLMCADN","^NQGIHEI","^NQGIHEIEUR","^NQDMXGBLMAUD","^NQUSB9530LMAUDN","^NQUSBLMAUDN","^NQNA9000LMJPY","^NQDMXJPLMCAD","^NQDMXJPLMEUR","^NQDXUSMEGA","^NQDMXKRLMGBP","^NQDXUSLCVN","^NQGXGBLMAUDN","^NQUS500LCN","^NQNA9000LMCADN","^AXI","^NQUS500LCV"],"targetPriceHigh":166.0,"targetPriceLow":115.0,"targetPriceMean":143.63,"targetPriceMedian":140.0,"preMarketPrice":137.0,"heldPercentInsiders":0.088,"heldPercentInstitutions":56.96,"regularMarketChange":0.41999817,"regularMarketChangePercent":0.3063891,"regularMarketTime":1656009457,"regularMarketPrice":137.5,"regularMarketDayHigh":138.62,"regularMarketDayRange":"136.5 - 138.62","sharesShort":19584317,"sourceInterval":15,"exchangeDataDelayedBy":0,"exchangeTimezoneName":"America/New_York","exchangeTimezoneShortName":"EDT","pageViews":{"midTermTrend":"NEUTRAL","longTermTrend":"UP","shortTermTrend":"UP"},"gmtOffSetMilliseconds":-14400000,"esgPopulated":false,"tradeable":false,"totalCash":1.04839997E10,"floatShares":897843324,"ebitda":12120000512,"shortRatio":3.63,"preMarketChange":-0.0800018,"preMarketChangePercent":-0.0583614,"preMarketTime":1655990998,"regularMarketDayLow":136.5,"regularMarketVolume":2037313,"sharesShortPrevMonth":21646384,"shortPercentFloat":2.18,"regularMarketPreviousClose":137.08,"bid":137.33,"ask":137.34,"bidSize":8,"askSize":11,"exchange":"NYQ","market":"us_market","messageBoardId":"finmb_112350","fullExchangeName":"NYSE","shortName":"International Business Machines","longName":"International Business Machines Corporation","regularMarketOpen":137.14,"averageDailyVolume3Month":5085562,"averageDailyVolume10Day":5319140,"beta":1.004083,"fiftyTwoWeekLowChange":22.940002,"fiftyTwoWeekLowChangePercent":0.20024444,"fiftyTwoWeekRange":"114.56 - 144.73","fiftyTwoWeekHighChange":-7.2299957,"fiftyTwoWeekHighChangePercent":-0.049955063,"fiftyTwoWeekLow":114.56,"fiftyTwoWeekHigh":144.73,"dividendDate":1654819200,"symbol":"IBM"}],"error":null}}
     */
	@Override
	public Quote deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
		
		log.info("Entering deserialize()");
        
        Quote q = new Quote();
        
        JsonNode quoteParent = jp.getCodec().readTree(jp);
        JsonNode quoteNode = quoteParent.get(0);
        
        log.debug("quote parent: " + quoteParent.toPrettyString());
        log.debug("quote: " + quoteNode.toPrettyString());
        		
        String closeStr = quoteNode.get("regularMarketPreviousClose") != null ? quoteNode.get("regularMarketPreviousClose").asText() : null;
        log.debug("Close value: {}", closeStr);
        
        Float openStr = quoteNode.get("regularMarketOpen") != null ? quoteNode.get("regularMarketOpen").floatValue() : null;
        log.debug("Open value: {}", openStr);
        
        String symbol = quoteNode.get("symbol") == null ? null : quoteNode.get("symbol").asText();
        log.debug("Symbol value: {}", symbol);
        
        Float highStr = quoteNode.get("targetPriceHigh") != null ? quoteNode.get("targetPriceHigh").floatValue() : null;
        log.debug("High value: {}", highStr);
        
        Float lowStr = quoteNode.get("targetPriceLow")!= null ? quoteNode.get("targetPriceLow").floatValue() : null;
        log.debug("Low value: {}", lowStr);
        
        Integer volumeStr = quoteNode.get("regularMarketVolume") != null ? quoteNode.get("regularMarketVolume").intValue() : null;
        log.debug("Volume: {}", volumeStr);
        
        Float priceChangeStr = quoteNode.get("regularMarketChange") != null ? quoteNode.get("regularMarketChange").floatValue() : null;
        log.debug("Price change value: {}", priceChangeStr);
        
        Float week52High = quoteNode.get("fiftyTwoWeekHigh") != null ? quoteNode.get("fiftyTwoWeekHigh").floatValue(): null;
        log.debug("52 week high: {}", week52High);
        
        Float week52Low = quoteNode.get("fiftyTwoWeekLow") != null ? quoteNode.get("fiftyTwoWeekLow").floatValue() : null;
        log.debug("52 week low: {}", week52Low);
        
        q.setClose(closeStr != null ? new BigDecimal(closeStr) : null);
        q.setDate(new Date());
        
        q.setHigh(new BigDecimal(highStr));
        q.setLow(new BigDecimal(lowStr));
        q.setOpen(new BigDecimal(openStr));
        q.setPriceChange(new BigDecimal(priceChangeStr));
        q.setVolume(volumeStr);
        q.setWeek52High(new BigDecimal(week52High));
        q.setWeek52Low(new BigDecimal(week52Low));
        
        Investment i = new Investment();
        i.setCompanyName(quoteNode.get("longName") != null ? quoteNode.get("longName").asText() : null);
        i.setSymbol(quoteNode.get("symbol") != null ? quoteNode.get("symbol").asText() : null);
        //i.setSector(quoteNode.get("sector") != null ? quoteNode.get("sector").asText() : null);
        i.setExchange(quoteNode.get("fullExchangeName") != null ? quoteNode.get("fullExchangeName").asText() : null);
        
        q.setInvestment(i);
        log.debug("Built quote: {}", q);
        
        log.info("Exiting deserialize()");
		return q;
	}

}
