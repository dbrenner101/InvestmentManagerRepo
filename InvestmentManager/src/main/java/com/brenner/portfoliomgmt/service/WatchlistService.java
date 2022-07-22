package com.brenner.portfoliomgmt.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brenner.portfoliomgmt.data.entities.Watchlist;
import com.brenner.portfoliomgmt.data.repo.WatchlistRepository;
import com.brenner.portfoliomgmt.domain.BatchHistoricalQuotes;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.quotes.retrievalservice.QuoteRetrievalService;
import com.brenner.portfoliomgmt.reporting.InvestmentPerformance;
import com.brenner.portfoliomgmt.reporting.InvestmentPerformanceSet;
import com.brenner.portfoliomgmt.util.DataHelperUtil;

@Service
public class WatchlistService {
	
	private static final Logger log = LoggerFactory.getLogger(WatchlistService.class);
	
	@Autowired
	WatchlistRepository watchlistRepo;
	
	@Autowired
	QuoteRetrievalService quoteRetrievalService;
	
	@Autowired
	QuotesService quotesService;

	public Watchlist addWatchlist(Watchlist watchlist) {
		
		log.info("Entered addWatchlist()");
		log.debug("Param: watchlist: {}", watchlist);
		
		Watchlist savedWatchlist = this.watchlistRepo.save(watchlist);
		
		log.info("Exiting addWatchList()");
		
		return savedWatchlist;
	}

	public Watchlist updateWatchlist(Watchlist watchlist) {
		
		log.info("Entered updateWatchList()");
		log.debug("Param: watchlist: {}", watchlist);
		
		Watchlist savedWatchlist = this.watchlistRepo.save(watchlist);
		
		log.info("Exirting updateWatchlist()");
		
		return savedWatchlist;
	}

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

	public List<Watchlist> getAllWatchlists() {
		
		log.info("Entered getAllWatchlists()");
		
		Iterable<Watchlist> watchlistsIter = this.watchlistRepo.findAll();
		
		List<Watchlist> watchlists = DataHelperUtil.toList(watchlistsIter);
		
		log.info("Exiting getAllWatchlists()");
		
		return watchlists;
	}

	public Optional<Watchlist> getWatchlistById(Integer watchlistId) {
		
		log.info("Entered getWatchlistById()");
		log.debug("Param: watchlistId: {}", watchlistId);
		
		Optional<Watchlist> watchlist = this.watchlistRepo.findById(watchlistId);
		
		log.debug("Found watchlist: {}", watchlist);
		log.info("Exiting getWatchlistById()");
		
		return watchlist;
	}

	public void deleteWatchlistById(Integer watchlistId) {
		
		log.info("Entered deleteWatchlistById()");
		log.debug("Param: watchlistId: {}", watchlistId);
		
		this.watchlistRepo.deleteById(watchlistId);
		
		log.info("Exiting deleteWatchlistById()");
		
	}
	
	public List<String> getSymbolsForWatchlist(Integer watchlistId) throws IOException {
		
		log.info("Entered getChartsForWatchlist()");
		log.debug("Param: watchlistId: {}", watchlistId);
		
		Optional<Watchlist> watchlist = this.watchlistRepo.findById(watchlistId);
		
		if (watchlist.isPresent()) {
			List<Investment> investments = watchlist.get().getInvestmentsToWatch();
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
		return null;	
	}
	
	public InvestmentPerformanceSet getChartsForSymbol(String symbol) throws IOException {
		
		log.info("Entered getChartsForSymbol()");
		log.debug("Param: symbol: {}", symbol);
		InvestmentPerformanceSet perfSet = new InvestmentPerformanceSet();
		perfSet.setSymbol(symbol);
		
		List<Quote> quotes = this.quotesService.findAllQuotesBySymbol(symbol);
		
		List<String> symbols = new ArrayList<>(1);
		symbols.add(symbol);
		
		List<InvestmentPerformance> perfList = new ArrayList<>();
		
		if (quotes == null || quotes.isEmpty()) {
			BatchHistoricalQuotes historicalQuotes = this.quoteRetrievalService.getSixMonthsHistoricalChartsForSymbols(symbols);
		
			Map<String, List<Quote>> quotesMap = historicalQuotes.getQuotesMap();
			quotes = quotesMap.get(symbol);
		}
		
		//Collections.sort(quotes);
		Collections.reverse(quotes);
		BigDecimal startPoint = null;
		
		Iterator<Quote> quotesIterator = quotes.iterator();
		while (quotesIterator.hasNext()) {
			Quote quote = quotesIterator.next();
			InvestmentPerformance perf = new InvestmentPerformance();
			perf.setQuoteDate(quote.getDate());
			perf.setClose(quote.getClose());
			if (startPoint == null) {
				perf.setChange(BigDecimal.ZERO);
			}
			else {
				perf.setChange(startPoint.subtract(quote.getClose()));
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
