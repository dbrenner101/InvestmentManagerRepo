/**
 * 
 */
package com.brenner.portfoliomgmt.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

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

import com.brenner.portfoliomgmt.data.repo.InvestmentsRepository;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.service.InvestmentsService;
import com.brenner.portfoliomgmt.test.DomainTestData;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author dbrenner
 * 
 */
@SpringBootTest(classes = {
		InvestmentsRestController.class,
		InvestmentsService.class,
		InvestmentsRepository.class
})
@AutoConfigureMockMvc
@EnableWebMvc
public class InvestmentsRestControllerTests {

	@Autowired MockMvc mockMvc;
	
	@MockBean InvestmentsService investmentsService;
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Test @WithMockUser
	public void testGetAllInvestments_Success() throws Exception {
		
		List<Investment> invs = DomainTestData.generateInvestmentList(4);
		Mockito.when(this.investmentsService.findAllInvestments(Mockito.anyString())).thenReturn(invs);
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/api/investments")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", notNullValue()))
			.andExpect(jsonPath("$", hasSize(invs.size())))
			.andExpect(jsonPath("$[0].symbol", is(invs.get(0).getSymbol())))
			.andExpect(jsonPath("$[1].symbol", is(invs.get(1).getSymbol())))
			.andExpect(jsonPath("$[2].symbol", is(invs.get(2).getSymbol())))
			.andExpect(jsonPath("$[3].symbol", is(invs.get(3).getSymbol())));
	}
	
	@Test @WithMockUser
	public void testGetInvestment_Success() throws Exception {
		
		Investment inv = DomainTestData.generateInvestment(1);
		Mockito.when(this.investmentsService.findInvestmentByInvestmentId(inv.getInvestmentId())).thenReturn(Optional.of(inv));
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/api/investments/" + inv.getInvestmentId())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", notNullValue()))
			.andExpect(jsonPath("$.symbol", is(inv.getSymbol())));
	}
	
	@Test @WithMockUser
	public void testGetInvestmentNotFound_Fail() throws Exception {
		
		Mockito.when(this.investmentsService.findInvestmentByInvestmentId(1L)).thenReturn(Optional.empty());
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/api/investments/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}
	
	@Test @WithMockUser
	public void testUpdateInvestment_Success() throws Exception {
		
		Investment investment = DomainTestData.generateInvestment(1);
		Mockito.when(this.investmentsService.findInvestmentByInvestmentId(investment.getInvestmentId())).thenReturn(Optional.of(investment));
		Mockito.when(this.investmentsService.saveInvestment(Mockito.any(Investment.class))).thenReturn(investment);
		
		this.mockMvc.perform(MockMvcRequestBuilders
						.put("/api/investments/" + investment.getInvestmentId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.mapper.writeValueAsString(investment))
						.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", notNullValue()))
				.andExpect(jsonPath("$.companyName", is(investment.getCompanyName())));
	}
	
	@Test @WithMockUser
	public void testUpdateInvestmentNotFound_Fail() throws Exception {
		
		Investment investment = DomainTestData.generateInvestment(1);
		Mockito.when(this.investmentsService.findInvestmentByInvestmentId(Mockito.anyLong())).thenReturn(Optional.empty());
		
		this.mockMvc.perform(MockMvcRequestBuilders
						.put("/api/investments/" + investment.getInvestmentId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(this.mapper.writeValueAsString(investment))
						.with(csrf()))
				.andExpect(status().isNotFound());
	}
	
	@Test @WithMockUser
	public void testAddInvestment_Success() throws Exception {
		
		Investment investment = DomainTestData.generateInvestment(1);
		Mockito.when(this.investmentsService.saveInvestment(Mockito.any(Investment.class))).thenReturn(investment);
		
		this.mockMvc.perform(MockMvcRequestBuilders
					.post("/api/investments")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(mapper.writeValueAsString(investment))
					.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", notNullValue()))
				.andExpect(jsonPath("$.companyName", is(investment.getCompanyName())));
	}
	
	@Test @WithMockUser
	public void testDeleteInvestment_Success() throws Exception {
		
		Investment investment = DomainTestData.generateInvestment(2);
		Mockito.when(this.investmentsService.findInvestmentByInvestmentId(investment.getInvestmentId())).thenReturn(Optional.of(investment));
		Mockito.when(this.investmentsService.saveInvestment(Mockito.any(Investment.class))).thenReturn(investment);
		
		this.mockMvc.perform(MockMvcRequestBuilders
					.delete("/api/investments/" + investment.getInvestmentId())
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.with(csrf()))
				.andExpect(status().isOk());
	}
	
	@Test @WithMockUser
	public void testDeleteInvestmentNotFound_Fail() throws Exception {
		
		Mockito.when(this.investmentsService.findInvestmentByInvestmentId(1L)).thenReturn(Optional.empty());
		
		this.mockMvc.perform(MockMvcRequestBuilders
						.delete("/api/investments/1")
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.with(csrf()))
				.andExpect(status().isNotFound());
	}
	
}
