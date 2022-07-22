/**
 * 
 */
package com.brenner.portfoliomgmt.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brenner.portfoliomgmt.data.entities.Watchlist;

/**
 *
 * @author dbrenner
 * 
 */
@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Integer> {

}
