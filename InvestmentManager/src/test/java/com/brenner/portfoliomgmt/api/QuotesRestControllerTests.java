package com.brenner.portfoliomgmt.api;

import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.domain.Quote;
import com.brenner.portfoliomgmt.service.QuotesService;
import com.brenner.portfoliomgmt.test.DomainTestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        QuotesRestController.class,
        QuotesService.class
})
@AutoConfigureMockMvc
@EnableWebMvc
public class QuotesRestControllerTests {
    
    @Autowired MockMvc mockMvc;
    
    @MockBean QuotesService quotesService;
    
    ObjectMapper mapper = new ObjectMapper();
    
    @Test @WithMockUser
    public void testGetQuotesBySymbol_Success() throws Exception {
        Investment inv = DomainTestData.generateInvestment(1);
        List<Quote> quotes = DomainTestData.generateQuotesList(4, inv);
    
        Mockito.when(this.quotesService.findAllQuotesBySymbol(inv.getSymbol())).thenReturn(quotes);
        
        this.mockMvc.perform(MockMvcRequestBuilders
                .get("/api/quotes/symbol/" + inv.getSymbol())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", hasSize(quotes.size())))
                .andExpect(jsonPath("$[1].volume", is(quotes.get(1).getVolume())));
    }
}
