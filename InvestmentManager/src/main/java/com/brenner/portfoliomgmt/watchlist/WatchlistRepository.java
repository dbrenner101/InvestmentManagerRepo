/**
 * 
 */
package com.brenner.portfoliomgmt.watchlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author dbrenner
 * 
 */
@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Integer> {

}
