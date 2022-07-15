/**
 * 
 */
package com.brenner.portfoliomgmt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author dbrenner
 * 
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6004931991791288456L;

	/**
	 * @param message
	 */
	public NotFoundException(String message) {
		super(message);
	}

}
