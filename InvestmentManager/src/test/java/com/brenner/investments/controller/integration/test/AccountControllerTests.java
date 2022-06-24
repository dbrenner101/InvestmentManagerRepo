package com.brenner.investments.controller.integration.test;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.brenner.investments.InvestmentsProperties;
import com.brenner.investments.entities.Account;
import com.brenner.investments.test.TestDataHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles(profiles= {"test"})
public class AccountControllerTests {
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	InvestmentsProperties props;
	
	@Autowired
    private MockMvc mockMvc;
	
	@Before
	public void setup() throws Exception {
		
		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}
	
	@Test
	public void testAddAccount() throws Exception {
		
		Account a = TestDataHelper.getAccount1();
		a.setAccountId(null);
		
		MockHttpServletRequestBuilder requestBuilder =
          MockMvcRequestBuilders.get("/addAccount")
            .param("accountName", a.getAccountName())
			.param("company", a.getCompany())
			.param("owner", a.getOwner())
			.param("accountNumber", a.getAccountNumber())
			.param("accountType", a.getAccountType());
		
		MvcResult result = this.mockMvc.perform(requestBuilder)
			.andExpect(status().is3xxRedirection())
			.andReturn();
		
		ModelAndView mandv = result.getModelAndView();
		Account account = (Account) mandv.getModel().get(props.getAccountAttributeKey());
		assertNotNull(account);
		assertNotNull(account.getAccountId());
		assertEquals(a.getAccountName(), account.getAccountName());
		assertEquals(a.getAccountNumber(), account.getAccountNumber());
		assertEquals(a.getAccountType(), account.getAccountType());
		assertEquals(a.getCompany(), account.getCompany());
		assertEquals(a.getOwner(), account.getOwner());
	}
	
	@Test
	public void testUpdateAccount() throws Exception {
		
		Account a = TestDataHelper.getAccount1();
		
		MockHttpServletRequestBuilder requestBuilder =
          MockMvcRequestBuilders.get("/updateAccount")
          	.param("accountId", a.getAccountId().toString())
            .param("accountName", a.getAccountName())
			.param("company", a.getCompany())
			.param("owner", a.getOwner())
			.param("accountNumber", a.getAccountNumber())
			.param("accountType", a.getAccountType());
		
		MvcResult result = this.mockMvc.perform(requestBuilder)
			.andExpect(status().is3xxRedirection())
			.andReturn();
		
		ModelAndView mandv = result.getModelAndView();
		Account account = (Account) mandv.getModel().get(props.getAccountAttributeKey());
		assertNotNull(account);
	}

}
