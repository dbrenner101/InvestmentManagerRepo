package com.brenner.investments.controller.rest;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.brenner.investments.data.deserialization.InvestmentDeserializer;
import com.brenner.investments.entities.Investment;
import com.brenner.investments.service.InvestmentsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Spring REST controller for interacting with {@link Investment}
 * @author dbrenner
 *
 */
@RestController
public class InvestmentsRestController {

	private static final Logger log = LoggerFactory.getLogger(InvestmentsRestController.class);
	
	@Autowired
	InvestmentsService investmentsService;
	
	/**
	 * GET to retrieve all investments
	 * @return a JSON list of investments
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@GetMapping(path="/restful/manageInvestments", produces= {"application/JSON"})
	public List<Investment> getAllInvestments() {
		log.info("Entered getAllInvestments()");
		
		List<Investment> investments = this.investmentsService.getInvestmentsOrderedBySymbolAsc();
		log.debug("Retrieved {} investments", investments != null ? investments.size() : 0);
		
		log.info("Exiting getAllInvestments()");
		return investments;
	}
	
	/**
	 * GET to retrive a specific investment
	 * 
	 * @param investmentId - investment identifier retrieved as a path variable {investmentId}
	 * @return the JSON representation of the investment
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@GetMapping("/restful/manageInvestments/{investmentId}")
	public Investment getInvestment(@PathVariable Long investmentId) {
		log.info("Entered getInvestment()");
		log.debug("Param: investmentId: {}", investmentId);
		
		Investment investment = this.investmentsService.getInvestmentByInvestmentId(investmentId);
		log.debug("Returning investment: {}", investment);
		
		log.info("Exiting getInvestment()");
		return investment;
	}
	
	/**
	 * GET to retrieve investments by an alpha character
	 * 
	 * @param alpha - character to base search on. Identified as a patch variable
	 * @return a JSON list of investments that start with the alpa character
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@GetMapping("/restful/getInvestmentsByAlpha/{alpha}")
	public List<Investment> getInvestmentByAlpha(@PathVariable String alpha) {
		log.info("Entered getInvestmentByAlpha()");
		log.debug("Param: alpha: {}", alpha);
		
		List<Investment> investments = this.investmentsService.getInvestmentsBySymbolAlpha(alpha);
		log.debug("Returning {} investments", investments != null ? investments.size() : 0);
		
		log.info("Exiting getInvestmentByAlpha()");
		return investments;
	}
	
	/**
	 * PUT to update an investment
	 * 
	 * @param investmentStr - JSON representation of the investment to update
	 * @return JSON of the updated investment
	 * 
	 * @throws IOException - errors in the JSON parsing
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@PutMapping(
			path="/restful/manageInvestments", 
			consumes= {MediaType.APPLICATION_JSON_VALUE}, 
			produces= {MediaType.APPLICATION_JSON_VALUE})
	public Investment updateInvestment(@RequestBody String investmentStr) throws IOException {
		log.info("Entered updateInvestment()");
		log.debug("Param: investmentJson: {}", investmentStr);
		
		Investment investment = this.deserializeInvestmentJson(investmentStr);
		this.investmentsService.saveInvestment(investment);
		log.debug("Updating investment: {}", investment);
		
		log.info("Exiting updateInvestment()");
		return investment;
	}
	
	/**
	 * POST to add an investment
	 * 
	 * @param investmentStr JSON representation of the {@link Investment} to add
	 * @return JSON of the saved investment
	 * @throws IOException JSON parsing errors
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@PostMapping(
			path="/restful/manageInvestments", 
			consumes= {MediaType.APPLICATION_JSON_VALUE}, 
			produces= {MediaType.APPLICATION_JSON_VALUE})
	public Investment addInvestment(@RequestBody String investmentStr) throws IOException {
		log.info("Entered addInvestment()");
		log.debug("Param: investmentJson: {}", investmentStr);
		
		Investment investment = this.deserializeInvestmentJson(investmentStr);
		this.investmentsService.saveInvestment(investment);
		log.debug("Saved investment: {}", investment);
		
		log.info("Exiting addInvestment()");
		return investment;
	}
	
	/**
	 * DELETE method to delete an investment
	 * 
	 * @param investmentId - unique investment identifier as a path variable
	 */
	@CrossOrigin(origins = {"http://localhost:4200", "http://invmgr.localhost"})
	@DeleteMapping(
			path="/restful/manageInvestments/{investmentId}", 
			consumes= {MediaType.APPLICATION_JSON_VALUE}, 
			produces= {MediaType.APPLICATION_JSON_VALUE})
	public void deleteInvestment(@PathVariable Long investmentId) {
		log.info("Entered deleteInvestment()");
		log.debug("Param: investmentId: {}", investmentId);
		
		this.investmentsService.deleteInvestment(new Investment(investmentId));
		
		log.info("Exiting deleteInvestment()");
	}
	
	/**
	 * JSON deserializer to convert JSON representation of {@link Investment} to an object
	 * 
	 * @param investmentJson {@link Investment} JSON
	 * @return the object
	 * @throws IOException - parsing errors
	 */
	private Investment deserializeInvestmentJson(String investmentJson) throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Investment.class, new InvestmentDeserializer());
		mapper.registerModule(module);
		
		Investment investment = mapper.readValue(investmentJson, Investment.class);
		
		return investment;
		
	}
}
