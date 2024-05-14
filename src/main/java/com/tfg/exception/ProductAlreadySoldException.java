package com.tfg.exception;

public class ProductAlreadySoldException extends Exception{
	
	private static final long serialVersionUID = 8970459855932992873L;

	public ProductAlreadySoldException(String message) {
	        super(message);
	    }

}
