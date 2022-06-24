package com.brenner.investments.service;

public class InvestmentManagerServiceException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -187117000899649052L;

	public InvestmentManagerServiceException(String message) {
		super(message);
	}
	
	public InvestmentManagerServiceException(String message, Exception e) {
		super(message, e);
	}
}
