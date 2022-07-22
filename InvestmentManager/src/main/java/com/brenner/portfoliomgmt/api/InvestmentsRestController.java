package com.brenner.portfoliomgmt.api;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brenner.portfoliomgmt.data.entities.InvestmentDTO;
import com.brenner.portfoliomgmt.domain.Investment;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.service.InvestmentsService;

/**
 * Spring REST controller for interacting with {@link InvestmentDTO}
 * @author dbrenner
 *
 */
@RestController
@RequestMapping("/api")
public class InvestmentsRestController {

	private static final Logger log = LoggerFactory.getLogger(InvestmentsRestController.class);
	
	@Autowired
	InvestmentsService investmentsService;
	
	/**
	 * GET to retrieve all investments
	 * @return a JSON list of investments
	 */
	@GetMapping(path="/investments")
	public List<Investment> getAllInvestments() {
		log.info("Entered getAllInvestments()");
		
		List<Investment> investments = this.investmentsService.findAllInvestments("symbol");
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
	@GetMapping("/investments/{investmentId}")
	public Investment getInvestment(@PathVariable Long investmentId) {
		log.info("Entered getInvestment()");
		log.debug("Param: investmentId: {}", investmentId);
		
		Optional<Investment> optInvestment = this.investmentsService.findInvestmentByInvestmentId(investmentId);
		if (! optInvestment.isPresent()) {
			throw new NotFoundException("Investment with id " + investmentId + " does not exist.");
		}
		
		Investment investment = optInvestment.get();
		
		log.debug("Returning investment: {}", investment);
		log.info("Exiting getInvestment()");
		
		return investment;
	}
	
	/**
	 * PUT to update an investment
	 * 
	 * @param investmentId - JSON representation of the investment to update
	 * @return JSON of the updated investment
	 * 
	 * @throws IOException - errors in the JSON parsing
	 */
	@PutMapping(path="/investments/{investmentId}")
	public Investment updateInvestment(@PathVariable Long investmentId, @RequestBody Investment inv) throws IOException {
		log.info("Entered updateInvestment()");
		log.debug("Param: investmentJson: {}", inv);
		
		Optional<Investment> optInvestment = this.investmentsService.findInvestmentByInvestmentId(investmentId);
		if (optInvestment.isEmpty()) {
			throw new NotFoundException("Investment with id " + investmentId + " was not found.");
		}
		
		Investment investment = this.investmentsService.saveInvestment(inv);
		log.debug("Updating investment: {}", investment);
		
		log.info("Exiting updateInvestment()");
		return investment;
	}
	
	/**
	 * POST to add an investment
	 * 
	 * @param inv JSON representation of the {@link InvestmentDTO} to add
	 * @return JSON of the saved investment
	 * @throws IOException JSON parsing errors
	 */
	@PostMapping(path="/investments")
	public Investment addInvestment(@RequestBody Investment inv) throws IOException {
		log.info("Entered addInvestment()");
		log.debug("Param: investmentJson: {}", inv);
		
		Investment investment =  this.investmentsService.saveInvestment(inv);
		log.debug("Saved investment: {}", investment);
		
		log.info("Exiting addInvestment()");
		return investment;
	}
	
	/**
	 * DELETE method to delete an investment
	 * 
	 * @param investmentId - unique investment identifier as a path variable
	 */
	@DeleteMapping(path="/investments/{investmentId}")
	public void deleteInvestment(@PathVariable Long investmentId) {
		log.info("Entered deleteInvestment()");
		log.debug("Param: investmentId: {}", investmentId);
		
		Optional<Investment> optionalInvestment = this.investmentsService.findInvestmentByInvestmentId(investmentId);
		if (optionalInvestment.isEmpty()) {
			throw new NotFoundException("Investment with id " + investmentId + " was not found.");
		}
		
		Investment investment = optionalInvestment.get();
		this.investmentsService.deleteInvestment(investment);
		
		log.info("Exiting deleteInvestment()");
	}
}
