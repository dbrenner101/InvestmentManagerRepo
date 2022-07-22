/**
 * 
 */
package com.brenner.portfoliomgmt.data.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brenner.portfoliomgmt.data.entities.AccountDTO;

/**
 *
 * @author dbrenner
 * 
 */
public interface AccountsRepository extends JpaRepository<AccountDTO, Long> {

	Optional<AccountDTO> findAccountByAccountNumber(String accountNumber);
	
	Optional<AccountDTO> findAccountByAccountName(String accountName);
}
