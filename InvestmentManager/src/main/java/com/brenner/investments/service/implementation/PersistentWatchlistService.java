package com.brenner.investments.service.implementation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brenner.investments.data.WatchlistDataService;
import com.brenner.investments.entities.BatchHistoricalQuotes;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.entities.Quote;
import com.brenner.investments.entities.Watchlist;
import com.brenner.investments.model.InvestmentPerformance;
import com.brenner.investments.model.InvestmentPerformanceSet;
import com.brenner.investments.quote.service.QuoteRetrievalService;
import com.brenner.investments.service.QuotesService;
import com.brenner.investments.service.WatchlistService;
import com.brenner.investments.util.DataHelperUtil;

@Service
public class PersistentWatchlistService implements WatchlistService {
	
	private static final Logger log = LoggerFactory.getLogger(PersistentWatchlistService.class);
	
	@Autowired
	WatchlistDataService watchlistRepo;
	
	@Autowired
	QuoteRetrievalService quoteRetrievalService;
	
	@Autowired
	QuotesService quotesService;

	@Override
	public Watchlist addWatchlist(Watchlist watchlist) {
		
		log.info("Entered addWatchlist()");
		log.debug("Param: watchlist: {}", watchlist);
		
		Watchlist savedWatchlist = this.watchlistRepo.save(watchlist);
		
		log.info("Exiting addWatchList()");
		
		return savedWatchlist;
	}

	@Override
	public Watchlist updateWatchlist(Watchlist watchlist) {
		
		log.info("Entered updateWatchList()");
		log.debug("Param: watchlist: {}", watchlist);
		
		Watchlist savedWatchlist = this.watchlistRepo.save(watchlist);
		
		log.info("Exirting updateWatchlist()");
		
		return savedWatchlist;
	}

	@Override
	public void addInvestmentToWatchlist(Watchlist watchlist, Investment investment) {
		
		log.info("Entered addInvestmentToWatchlist()");
		log.debug("Param: watchlist: {}; investment: {}", watchlist, investment);
		
		List<Investment> investments = watchlist.getInvestmentsToWatch();
		
		if (investments == null) {
			investments = new ArrayList<>(1);
		}
		
		investments.add(investment);
		
		watchlist.setInvestmentsToWatch(investments);
		this.updateWatchlist(watchlist);
		
		log.info("Exiting addInvestmentToWatchlist()");

	}

	@Override
	public void addInvestmentsToWatchlist(Watchlist watchlist, List<Investment> investments) {
		
		log.info("Entered addInvestmentsToWatchlist()");
		log.debug("Params: watchlist: {}; investments: {}", watchlist, investments);
		
		List<Investment> existingInvestments = watchlist.getInvestmentsToWatch();
		
		if (existingInvestments == null) {
			existingInvestments = new ArrayList<>(investments.size());
		}
		
		existingInvestments.addAll(investments);
		watchlist.setInvestmentsToWatch(existingInvestments);
		this.updateWatchlist(watchlist);
		
		log.info("Exiting addInvestm entsToWatchlist()");

	}

	@Override
	public List<Watchlist> getAllWatchlists() {
		
		log.info("Entered getAllWatchlists()");
		
		Iterable<Watchlist> watchlistsIter = this.watchlistRepo.findAll();
		
		List<Watchlist> watchlists = DataHelperUtil.toList(watchlistsIter);
		
		log.info("Exiting getAllWatchlists()");
		
		return watchlists;
	}

	@Override
	public Watchlist getWatchlistById(Integer watchlistId) {
		
		log.info("Entered getWatchlistById()");
		log.debug("Param: watchlistId: {}", watchlistId);
		
		Watchlist watchlist = this.watchlistRepo.findById(watchlistId);
		
		log.debug("Found watchlist: {}", watchlist);
		log.info("Exiting getWatchlistById()");
		
		return watchlist;
	}

	@Override
	public void deleteWatchlistById(Integer watchlistId) {
		
		log.info("Entered deleteWatchlistById()");
		log.debug("Param: watchlistId: {}", watchlistId);
		
		this.watchlistRepo.deleteById(watchlistId);
		
		log.info("Exiting deleteWatchlistById()");
		
	}
	
	public List<String> getSymbolsForWatchlist(Integer watchlistId) throws IOException {
		
		log.info("Entered getChartsForWatchlist()");
		log.debug("Param: watchlistId: {}", watchlistId);
		
		Watchlist watchlist = this.watchlistRepo.findById(watchlistId);
		
		List<Investment> investments = watchlist.getInvestmentsToWatch();
		List<String> symbols = new ArrayList<>(investments.size());
		
		if (investments != null) {
			
			Iterator<Investment> iter = investments.iterator();
			while (iter.hasNext()) {
				Investment i = iter.next();
				symbols.add(i.getSymbol());
			}
		}
		
		log.debug("Returning {} stmbols", symbols != null ? symbols.size() : 0);
		log.info("Exiting getChartsForWatchlist()");
		
		return symbols;
		
	}
	
	public InvestmentPerformanceSet getChartsForSymbol(String symbol) throws IOException {
		
		log.info("Entered getChartsForSymbol()");
		log.debug("Param: symbol: {}", symbol);
		InvestmentPerformanceSet perfSet = new InvestmentPerformanceSet();
		perfSet.setSymbol(symbol);
		
		List<Quote> quotes = this.quotesService.getAllQuotesBySymbol(symbol);
		
		List<String> symbols = new ArrayList<>(1);
		symbols.add(symbol);
		
		List<InvestmentPerformance> perfList = new ArrayList<>();
		
		if (quotes == null || quotes.isEmpty()) {
			BatchHistoricalQuotes historicalQuotes = this.quoteRetrievalService.getSixMonthsHistoricalChartsForSymbols(symbols);
		
			Map<String, List<Quote>> quotesMap = historicalQuotes.getQuotesMap();
			quotes = quotesMap.get(symbol);
		}
		
		Collections.sort(quotes);
		Collections.reverse(quotes);
		Float startPoint = null;
		
		Iterator<Quote> quotesIterator = quotes.iterator();
		while (quotesIterator.hasNext()) {
			Quote quote = quotesIterator.next();
			InvestmentPerformance perf = new InvestmentPerformance();
			perf.setQuoteDate(quote.getDate());
			perf.setClose(quote.getClose());
			if (startPoint == null) {
				perf.setChange(0F);
			}
			else {
				perf.setChange(startPoint - quote.getClose());
			}
			startPoint = quote.getClose();
			perfList.add(perf);
		}
		perfSet.setInvestmentPerformanceList(perfList);
		
		log.debug("Returning {} quotes", quotes != null ? quotes.size() : 0);
		log.info("Exiting getChartsForWatchlist()");
		
		return perfSet;
		
	}
	
	

}
