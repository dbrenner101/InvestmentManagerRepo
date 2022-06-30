package com.brenner.portfoliomgmt.news;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.brenner.portfoliomgmt.InvestmentsProperties;
import com.brenner.portfoliomgmt.investments.Investment;
import com.brenner.portfoliomgmt.investments.InvestmentsService;
import com.brenner.portfoliomgmt.quotes.retrievalservice.QuoteRetrievalService;
import com.brenner.portfoliomgmt.util.CommonUtils;

/**
 * Controller for accessing News functions
 * 
 * @author dbrenner
 *
 */
@Controller
public class NewsController {
    
    private static final Logger log = LoggerFactory.getLogger(NewsController.class);
    
    @Autowired
    QuoteRetrievalService quoteService;
    
    @Autowired
    InvestmentsService investmentsService;
    
    @Autowired
    InvestmentsProperties props;

    /**
     * Retrieves today's news from the service layer. Also retrieves a list of stored investments to request
     * company specific news.
     * 
     * @param model Container for passing objects to presentation layer
     * @return The target resource (news/displayNews)
     */
    @RequestMapping("todaysNews")
    public String todaysNews(Model model) {
        log.info("Entered todaysNews()");
        
        List<News> news = this.quoteService.getCurrentNews();
        model.addAttribute("news", news);
        log.debug("Retrieved {} articles", news != null ? news.size() : 0);
        
        List<Investment> investments = this.investmentsService.getInvestmentsOrderedBySymbolAsc();
        model.addAttribute(props.getInvestmentsListAttributeKey(), investments);
        
        log.info("Exiting todaysNews");
        
        return "news/displayNews";
    }
    
    @RequestMapping("companyNews")
    public void companyNews(
            @RequestParam(name="symbol", required=true) String symbol, 
            HttpServletResponse response, 
            Model model) throws IOException {
        log.info("Entered companyNews()");
        
        List<News> news = this.quoteService.getCompanyNews(symbol);
        log.debug("Retrieved {} articles", news != null ? news.size() : 0);
        
        log.info("Exiting companyNews()");
        CommonUtils.serializeObjectToJson(response.getOutputStream(), news);
    }
}
