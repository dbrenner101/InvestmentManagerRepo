/**
 * 
 */
package com.brenner.investments.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brenner.investments.entities.User;

/**
 *
 * @author dbrenner
 * 
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
