package com.exception;

public class InvalidStockException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidStockException(String message) {
	        super(message);
	    }

}
