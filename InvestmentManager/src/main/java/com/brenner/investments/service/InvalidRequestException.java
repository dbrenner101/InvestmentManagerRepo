/**
 * 
 */
package com.brenner.investments.service;

/**
 *
 * @author dbrenner
 * 
 */
public class InvalidRequestException extends RuntimeException {

	private static final long serialVersionUID = -4995861645360472611L;

	/**
	 * 
	 */
	public InvalidRequestException() {}

	/**
	 * @param message
	 */
	public InvalidRequestException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidRequestException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InvalidRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
