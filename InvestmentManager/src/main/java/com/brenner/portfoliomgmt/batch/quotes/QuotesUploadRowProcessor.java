/**
 * 
 */
package com.brenner.portfoliomgmt.batch.quotes;

import java.util.Optional;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.service.InvestmentsService;
import com.brenner.portfoliomgmt.service.QuotesService;

/**
 *
 * @author dbrenner
 * 
 */
public class QuotesUploadRowProcessor implements ItemProcessor<QuotesUploadRowInstance, QuotesUploadRowInstance> {
	

	@Autowired InvestmentsService investmentsService;
	@Autowired QuotesService quotesService;
	
	@Override
	public QuotesUploadRowInstance process(QuotesUploadRowInstance item) throws Exception {
		
		
		String symbol = item.getSymbol();
		Optional<Investment> optInv = this.investmentsService.findInvestmentBySymbol(symbol);
		
		if (optInv.isPresent()) {
			Quote quote = new Quote();
			quote.setDate(item.getQuoteDate());
			quote.setClose(item.getClose());
			quote.setInvestment(optInv.get());
			
			this.quotesService.saveQuote(quote);
		}
		
		return item;
	}

}
