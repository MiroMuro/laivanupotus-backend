package com.miro.Laivanupotus.exceptions;

public class AuthenticationFailedException extends RuntimeException {
	public AuthenticationFailedException(String message) {
		super(message);
	}
}