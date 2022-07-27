package com.brenner.portfoliomgmt.view.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.brenner.portfoliomgmt.InvestmentsProperties;
import com.brenner.portfoliomgmt.data.entities.Watchlist;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.reporting.InvestmentPerformanceSet;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.service.InvestmentsService;
import com.brenner.portfoliomgmt.service.WatchlistService;
import com.brenner.portfoliomgmt.util.CommonUtils;

/**
 * Controller for managing watchlists
 * @author dbrenner
 *
 */
@Controller
public class WatchlistsController implements WebMvcConfigurer {
	
	private static final Logger log = LoggerFactory.getLogger(WatchlistsController.class);
	
	@Autowired
	WatchlistService watchlistService;
	
	@Autowired
	InvestmentsService investmentsService;
	
	@Autowired
	InvestmentsProperties props;
	
	/**
	 * Step 1 in the add watchlist workflow: create the list
	 * 
	 * @param model - object container
	 * @return watchlist/addWatchlist
	 */
	@RequestMapping("/prepWatchlistForm")
	public String prepWatchlistForm(Model model) {
		
		log.info("Entered prepWatchlistForm()");
		
		Watchlist list = new Watchlist();
		model.addAttribute("watchlist", list);
		
		List<Investment> investments = this.investmentsService.findInvestments("symbol");
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		
		model.addAttribute(props.getInvestmentsListAttributeKey(), investments);
		
		log.info("Exiting prepWatchlistForm()");
		return "watchlist/addWatchlist";
	}
	
	/**
	 * Step 2 in the add watchlist workflow: save the list
	 * 
	 * @param watchlist - {@link Watchlist} details
	 * @param bindingResult - Spring MVC binding
	 * @param model - object container
	 * @return redirect:prepWatchlistForm
	 */
	@RequestMapping("/addWatchlist")
	public String addWatchlist(
			@ModelAttribute("watchlist") Watchlist watchlist,
			BindingResult bindingResult,
			Model model) {
		log.info("Entered addWatchlist()");
		log.debug("Param: watchlist: {}", watchlist);
		
		this.watchlistService.addWatchlist(watchlist);
		
		log.info("Exiting addWatchlist()");
		return "redirect:prepWatchlistForm";
	}
	
	/**
	 * Retrieves the saved watchlists
	 * 
	 * @param model - object container
	 * @return watchlist/listWatchlists
	 */
	@RequestMapping("/getWatchlists")
	public String getWatchlists(Model model) {
		log.info("Entered getWatchlists()");
		
		List<Watchlist> watchlists = this.watchlistService.getAllWatchlists();
		model.addAttribute("watchlists", watchlists);
		
		log.info("Exiting getWatchlists()");
		return "watchlist/listWatchlists";
		
	}
	
	/**
	 * Step 1 in the edit watchlist workflow
	 * 
	 * @param watchlistId - watchlist unique identifier
	 * @param model - object contaier
	 * @return watchlist/editWatchlist
	 */
	@RequestMapping("/editWatchlist")
	public String editWatchlist(@RequestParam(name="watchlistId", required=true) String watchlistId, 
			Model model) {
		log.info("Entered editWatchlist()");
		log.debug("Param: watchlistId: {}", watchlistId);
		
		Optional<Watchlist> optWatchlist = this.watchlistService.getWatchlistById(Integer.valueOf(watchlistId));
		
		if (! optWatchlist.isPresent()) {
			throw new NotFoundException("Watchist with id " + watchlistId + " does not exist");
		}
		
		Watchlist watchlist = optWatchlist.get();		
		model.addAttribute("watchlist", watchlist);
		
		log.debug("Retrieved watchlist: {}", watchlist);
		
		List<Investment> investments = this.investmentsService.findInvestments("symbol");
		
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		
		model.addAttribute(props.getInvestmentsListAttributeKey(), investments);
		
		log.info("Exiting editWatchlist()");
		
		return "watchlist/editWatchlist";
	}
	
	/**
	 * Delete a watchlist
	 * 
	 * @param watchlistId - watchlist unique identifier
	 * @return redirect:getWatchlists
	 */
	@RequestMapping("/deleteWatchlist")
	public String deleteWatchlist(@RequestParam(name="watchlistId", required=true) String watchlistId) {
		log.info("Entered deleteWatchlist()");
		log.debug("Param: watchlistId: {}", watchlistId);
		
		this.watchlistService.deleteWatchlistById(Integer.valueOf(watchlistId));
		
		log.info("Exiting deleteWatchlist()");
		return "redirect:getWatchlists";
	}
	
	/**
	 * Step 2 in the edit watchlist workflow
	 * 
	 * @param watchlist - {@link Watchlist} to update
	 * @param model - object container
	 * @return redirect:getWatchlists
	 */
	@RequestMapping("/updateWatchlist")
	public String updateWatchlist(@ModelAttribute(name="watchlist") Watchlist watchlist, Model model) {
		log.info("Entered updateWatchlist()");
		log.debug("Param: watchlist: {}", watchlist);
		
		watchlist = this.watchlistService.updateWatchlist(watchlist);
		model.addAttribute("watchlist", watchlist);
		
		log.info("Exiting updateWatchlist()");
		return "redirect:getWatchlists";
	}
	
	/**
	 * Retrieves quotes for the watchlist investments
	 * 
	 * @param watchlistId - {@link Watchlist} identifier
	 * @param model - object container 
	 * @return watchlist/watchlistPerformance
	 * @throws IOException - passed on from the service layer
	 */
	@RequestMapping("/getChartsForWatchlist")
	public String getChartsForWatchlist(
			@RequestParam(name="watchlistId", required=true) String watchlistId, Model model) throws IOException {
		log.info("Entered getChartsForWatchlist()");
		
		List<String> symbols = this.watchlistService.getSymbolsForWatchlist(Integer.valueOf(watchlistId));
		model.addAttribute("symbols", symbols);
		
		log.info("Exiting getChartsForWatchlist()");
		return "watchlist/watchlistPerformance";
	}
	
	/**
	 * Async to retrieves quotes for the watchlist investments
	 * 
	 * @param symbol - Investment identifier
	 * @param response - Http response object
	 * @throws IOException - Passed on from the service layer
	 */
	@RequestMapping("/getChartsForWatchlistAjax")
	public void getChartsForWatchlistAjax(
			@RequestParam(name="symbol", required=true) String symbol, HttpServletResponse response) throws IOException {
		log.info("Entered getChartsForWatchlist()");
		
		InvestmentPerformanceSet perfSet = this.watchlistService.getChartsForSymbol(symbol);
		
		log.info("Exiting getChartsForWatchlist()");
		CommonUtils.serializeObjectToJson(response.getOutputStream(), perfSet);
	}

}
