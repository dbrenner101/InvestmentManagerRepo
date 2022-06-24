package com.brenner.investments.quote.service;

/**
 * Exception to throw when issues are presented trying to retrieve a quote
 * 
 * @author dbrenner
 *
 */
public class QuoteRetrievalException extends Exception {


	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public QuoteRetrievalException() {
		super();
	}


	public QuoteRetrievalException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}


	public QuoteRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}


	public QuoteRetrievalException(String message) {
		super(message);
	}


	public QuoteRetrievalException(Throwable cause) {
		super(cause);
	}

}
