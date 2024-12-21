package com.miro.Laivanupotus.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.miro.Laivanupotus.dto.ErrorResponseDto;

@ControllerAdvice
public class ApplicationWideExceptionHandler {
	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(
			UserNotFoundException ex) {
		ErrorResponseDto errorResponse = new ErrorResponseDto(ex
				.getMessage(), "USER_NOT_FOUND",
				HttpStatus.NOT_FOUND
				.value(),
				LocalDateTime
				.now());

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	};

	@ExceptionHandler(InvalidPasswordException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ErrorResponseDto> handleInvalidPasswordException(
			InvalidPasswordException ex) {
		ErrorResponseDto errorResponse = new ErrorResponseDto(ex
				.getMessage(), "USER_NOT_FOUND",
				HttpStatus.UNAUTHORIZED
				.value(),
				LocalDateTime
				.now());
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	};

	@ExceptionHandler(AuthenticationFailedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ErrorResponseDto> handleFailedAuthenticationException(
			AuthenticationFailedException ex) {
		ErrorResponseDto errorResponse = new ErrorResponseDto(ex
				.getMessage(), "AUTHENTICATION_FAILED",
				HttpStatus.UNAUTHORIZED
				.value(),
				LocalDateTime
				.now());
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	};

	// Handle unexpected exceptions
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponseDto> handleGeneralException(
			Exception ex) {
		ErrorResponseDto errorResponse = new ErrorResponseDto(
				"An unexpected error occurred", "INTERAL_SERVER_ERROR",
				HttpStatus.INTERNAL_SERVER_ERROR
				.value(),
				LocalDateTime
				.now());

		return new ResponseEntity<>(errorResponse,
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
