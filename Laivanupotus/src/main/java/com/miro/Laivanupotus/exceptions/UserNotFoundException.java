package com.miro.Laivanupotus.exceptions;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(String message) {
		super(message);
	}

}
