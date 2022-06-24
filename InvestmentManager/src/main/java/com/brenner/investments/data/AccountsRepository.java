/**
 * 
 */
package com.brenner.investments.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brenner.investments.entities.Account;

/**
 *
 * @author dbrenner
 * 
 */
public interface AccountsRepository extends JpaRepository<Account, Long> {

	Optional<Account> findAccountByAccountNumber(String accountNumber);
}
