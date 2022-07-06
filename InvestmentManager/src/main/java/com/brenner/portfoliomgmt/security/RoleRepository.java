/**
 * 
 */
package com.brenner.portfoliomgmt.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author dbrenner
 * 
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

}
