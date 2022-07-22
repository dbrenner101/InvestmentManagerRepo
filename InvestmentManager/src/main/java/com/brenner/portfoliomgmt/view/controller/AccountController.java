package com.brenner.portfoliomgmt.view.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.brenner.portfoliomgmt.domain.Account;
import com.brenner.portfoliomgmt.exception.NotFoundException;
import com.brenner.portfoliomgmt.service.AccountsService;

/**
 * Controller for the Account-centric requests.
 * 
 * @author dbrenner
 *
 */
@Controller
@Secured("ROLE_USER")
public class AccountController implements WebMvcConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
	
	@Autowired
	AccountsService accountsService;
	

	/**
	 * MVC request method to start the add new account form. It also retrieves all existing accounts and their cash balance.
	 * 
	 * @param model adds the account attribute with cash balance for each account.
	 * 
	 * @return the path to the add account form (/accounts/addAccountForm)
	 */
	@RequestMapping("/getNewAccountForm")
	public String getNewAccountForm(@ModelAttribute(name="account") Account account, Model model) {
		logger.info("Entering getNewAccountForm()");
		
		List<Account> accounts = this.accountsService.findAllAccounts("accountName");
		
		logger.debug("Retrieved {} accounts", accounts != null ? accounts.size() : 0);
		
		model.addAttribute("accountsandcash", accounts);
		
		logger.info("Forwarding to /accounts/addAccountForm");
		return "accounts/addAccount";
	}
	
	/**
	 * Request mapping to add a new account instance.
	 * 
	 * @param account - The account instance
	 * @param model - add the newly added account into the model.
	 * @return redirect:/getNewAccountForm
	 */
	@RequestMapping("/addAccount")
	public ModelAndView addAccount(@ModelAttribute(name="account") Account account, Model model) {
		logger.info("Entering addAccount()");
		logger.debug("Request parameters: account: {}", account);
		
		// save the account
		account = this.accountsService.save(account);
		
		logger.debug("Added new Account: {}", account);
		
		// put the account object in the model
		model.addAttribute("account", account);
		
		logger.info("Redirecting to: getNewAccountForm");
		
		return new ModelAndView("redirect:getNewAccountForm", model.asMap());
	}
	
	/**
	 * Prepare the data for the edit account form
	 * 
	 * @param model - add the accounts list to the model
	 * @return /accounts/chooseAccountToEdit
	 */
	@RequestMapping("/editAccountPrep")
	public String editAccountPrep(Model model) {
		logger.info("Entering editAccountPrep()");
		
		List<Account> accounts = this.accountsService.findAllAccounts();
		
		model.addAttribute("accounts", accounts);
		
		logger.info("Forwarding to accounts/chooseAccountToEdit");
		
		return ("accounts/chooseAccountToEdit");
	}
    
	/**
	 * After an account is chosen this methods retrieves the details.
	 * 
	 * @param accountId - unique account identifier
	 * @param model put the selected account into the model
	 * @return /accounts/editAccountForm
	 */
    @RequestMapping("/getAccountDetailsForEdit")
    public String getAccountDetailsForEdit( @RequestParam(name="accountId") String accountId, Model model) {
    	logger.info("Entering getAccountDetailsForEdit()");
    	logger.debug("Request parameter: accountId: {}", accountId);
    	
    	Optional<Account> optAccount = this.accountsService.findAccountByAccountId(Long.valueOf(accountId));
    	
    	if (! optAccount.isPresent()) {
    		throw new NotFoundException("Account with accountId " + accountId + " does not exist.");
    	}
    	
    	Account account = optAccount.get();
    	
    	logger.debug("Retrieved account: {}", account);
        
        model.addAttribute("account", account);
        
        logger.info("Forwarding to accounts/editAccountForm");
        
        return "accounts/editAccountForm";
    }
	
    /**
     * Update account details MVC method
     * 
     * @param account - model attribute
     * @param model - request object container
     * @return redirect:editAccountPrep
     */
	@RequestMapping("/updateAccount")
	public ModelAndView updateAccount(@ModelAttribute(name="account") Account account, Model model) {
		logger.info("Entering updateAccount()");
		logger.debug("Request parameters: account: {}", account);
		
		account = this.accountsService.save(account);
	    
	    logger.debug("Updated account: {}", account);
	    
	    model.addAttribute("account", account);
	    
	    logger.info("Redirecting to editAccountPrep");
	    
	    return new ModelAndView("redirect:editAccountPrep", model.asMap());
	}
}
