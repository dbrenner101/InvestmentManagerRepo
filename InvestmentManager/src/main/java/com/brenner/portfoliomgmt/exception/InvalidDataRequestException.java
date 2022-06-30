package com.brenner.portfoliomgmt.exception;

/**
 * Exception for data persisitence requests that cannot be completed
 * 
 * @author dbrenner
 *
 */
public class InvalidDataRequestException extends RuntimeException {

	private static final long serialVersionUID = 8256078538646661370L;

	public InvalidDataRequestException() {
		super();
	}
	
	public InvalidDataRequestException(String message) {
		super(message);
	}
}
