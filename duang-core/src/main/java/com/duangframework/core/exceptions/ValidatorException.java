package com.duangframework.core.exceptions;

import java.util.logging.Logger;


public class ValidatorException extends RuntimeException {

	
	private static final long serialVersionUID = 1401593546385403720L;

	public ValidatorException() {
		super();
	}
	
	public ValidatorException(String message) {
		super(message);
	}

	public ValidatorException(String message, Logger logger) {
		super(message);
	}

	public ValidatorException(Throwable cause) {
		super(cause);
	}

	public ValidatorException(String message, Throwable cause) {
		super(message, cause);
	}
	
	
	
}
