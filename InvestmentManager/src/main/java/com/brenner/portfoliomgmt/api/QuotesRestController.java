/**
 * 
 */
package com.brenner.portfoliomgmt.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.service.QuotesService;

/**
 * API access to quotes functionality.
 * 
 * @author dbrenner
 * 
 */
@RestController
@RequestMapping("/api")
public class QuotesRestController {
    
    @Autowired QuotesService quotesService;

    /**
     * 
     * 
     * @param symbol
     * @return
     */
	@GetMapping(path="/quotes/symbol/{symbol}")
	public List<Quote> getQuotesBySymbol(@PathVariable String symbol) {
		
		List<Quote> quotes = this.quotesService.findAllQuotesBySymbol(symbol);
		return quotes;
	}
}
