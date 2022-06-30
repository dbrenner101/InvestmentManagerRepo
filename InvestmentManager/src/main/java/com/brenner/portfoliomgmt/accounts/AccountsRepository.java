/**
 * 
 */
package com.brenner.portfoliomgmt.accounts;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author dbrenner
 * 
 */
public interface AccountsRepository extends JpaRepository<Account, Long> {

	Optional<Account> findAccountByAccountNumber(String accountNumber);
}
