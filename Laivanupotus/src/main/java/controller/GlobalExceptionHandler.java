package controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRunTimeException(RuntimeException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

		return ResponseEntity.badRequest().body(errorResponse);
	}

	// Inner class for error responses
	@Data
	@AllArgsConstructor
	public static class ErrorResponse {
		public ErrorResponse(int status, String message) {
			this.status = status;
			this.message = message;
		}

		private int status;
		private String message;
	}


}
