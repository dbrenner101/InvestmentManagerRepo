/**
 * 
 */
package com.brenner.portfoliomgmt.batch.investments;

import java.util.Optional;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.service.InvestmentsService;
import com.brenner.portfoliomgmt.service.QuotesService;

/**
 *
 * @author dbrenner
 * 
 */
@Service
public class InvestmentsUploadRowProcessor implements ItemProcessor<InvestmentsUploadRowInstance, InvestmentsUploadRowInstance> {
	

	@Autowired InvestmentsService investmentsService;
	@Autowired QuotesService quotesService;
	
	@Override
	public InvestmentsUploadRowInstance process(final InvestmentsUploadRowInstance item) throws Exception {
		
		Investment investment = new Investment();
		investment.setSymbol(item.getSymbol());
		investment.setCompanyName(item.getName());
		
		Optional<Investment> optInv = this.investmentsService.findInvestmentBySymbol(item.getSymbol());
		if (optInv.isEmpty()) {
			investment = this.investmentsService.saveInvestment(investment);
		}
		else {
			investment = optInv.get();
		}
		
		Quote quote = new Quote();
		quote.setDate(item.getDate());
		quote.setClose(item.getLastPrice());
		quote.setInvestment(investment);
		this.quotesService.saveQuote(quote);
		
		return item;
	}

}
