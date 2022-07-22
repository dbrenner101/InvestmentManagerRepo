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
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException {

	private static final long serialVersionUID = -4995861645360472611L;

	/**
	 * @param message
	 */
	public InvalidRequestException(String message) {
		super(message);
	}

}
