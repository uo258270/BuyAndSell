package com.exception;

public class ImageNotFoundException extends RuntimeException {
	public ImageNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}