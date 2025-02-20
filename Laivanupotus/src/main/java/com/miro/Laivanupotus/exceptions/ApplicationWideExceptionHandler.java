package com.miro.Laivanupotus.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.miro.Laivanupotus.dto.ErrorResponseDto;

@RestControllerAdvice(basePackages = "com.miro.Laivanupotus.controller")
public class ApplicationWideExceptionHandler {
	
	public ApplicationWideExceptionHandler() {
        System.out.println("ApplicationWideErrorHandler initialized");
    }
	
	 @ExceptionHandler(MatchNotFoundException.class)
	    @ResponseStatus(HttpStatus.NOT_FOUND)
	    public ResponseEntity<ErrorResponseDto> handleMatchNotFoundException(
		    MatchNotFoundException ex) {
		ErrorResponseDto errorResponse = new ErrorResponseDto(ex
			.getMessage(), "MATCH_NOT_FOUND",
			HttpStatus.NOT_FOUND
			.value(),
			LocalDateTime
			.now());

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	    };

	
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
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
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

    @ExceptionHandler(OwnGameJoinException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<ErrorResponseDto> handleOwnGameJoinException(
	    OwnGameJoinException ex) {
	ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(),
		"NOT_ACCEPTABLE", HttpStatus.NOT_ACCEPTABLE.value(),
		LocalDateTime.now());
	return new ResponseEntity<ErrorResponseDto>(errorResponse, HttpStatus.UNAUTHORIZED);
    };
    
    @ExceptionHandler(PlayerInActiveMatchException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponseDto> handlePlayerInActiveMatchException(
        PlayerInActiveMatchException ex) {
    	ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(),
    			"PLAYER_IN_ACTIVE_MATCH", HttpStatus.CONFLICT.value(),
    			LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    };
    
    
    // Handle unexpected exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(
	    Exception ex) {
	ErrorResponseDto errorResponse = new ErrorResponseDto(
		"An unexpected error occurred" + ex.getMessage(), "INTERAL_SERVER_ERROR",
		HttpStatus.INTERNAL_SERVER_ERROR
		.value(),
		LocalDateTime
		.now());

	return new ResponseEntity<>(errorResponse,
		HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
