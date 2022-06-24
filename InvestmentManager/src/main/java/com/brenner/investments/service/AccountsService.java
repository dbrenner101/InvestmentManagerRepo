package com.brenner.investments.service;

import java.util.List;

import com.brenner.investments.entities.Account;

public interface AccountsService {
	
	public Account save(Account account);
    
    /**
     * Retrieves an account by its assigned account number.
     * 
     * @param accountNumber - account number to search for.
     * 
     * @return {@link Account}
     */
    public Account getAccountByAccountNumber(String accountNumber);
    
    /**
     * Deletes the specified account
     * 
     * @param account - account object to delete
     */
    public void deleteAccount(Account account);
    
    /**
     * Builds an account from the supplied information. Sums cash transactions to the account object and returns
     * 
     * @param accountName - name of the account
     * @param accountNumber - account number
     * @param company - company the account is with
     * @param owner - owner name(s)
     * @param accountType - type of account
     * @return {@link Account}
     */
    public Account addNewAccount(Account account);
    
    
    /**
     * Retrieve all accounts
     * 
     * @return {@link Iterable}<Account>
     */
    public Iterable<Account> getAllAccounts();
    
    /**
     * Retrieve a specific account
     * 
     * @param accountId - account unique identifier
     * @return {@link Account}
     */
    public Account getAccountByAccountId(Long accountId);
    
    /**
     * Retrieve a list of accounts and their corresponding cash balance
     * 
     * @return {@link List}<Account>
     */
    public  List<Account> getAccountsAndCash();
    
    /**
     * Get a specific account and its associated cash balance
     * 
     * @param accountId - unique account identifier
     * @return {@link Account}
     */
    public Account getAccountAndCash(Long accountId);
    
    /**
     * Retrieve all account and order by account name ascending
     * 
     * @return {@link List}<Account>
     */
    public List<Account> getAllAccountsOrderByAccountNameAsc();
    
    /**
     * Updates an account with the supplied details.
     * 
     * @param account - the account to update
     * @return {@link Account}
     */
    public Account updateAccount(Account account);

}
