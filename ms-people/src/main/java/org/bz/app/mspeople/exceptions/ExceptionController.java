package org.bz.app.mspeople.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(ExistingMailException.class)
	public ResponseEntity<CustomErrorResponse> existingMailError(Exception ex) {
		CustomErrorResponse errors = new CustomErrorResponse();
		errors.setMessage(ex.getLocalizedMessage());
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PatternPasswordException.class)
	public ResponseEntity<CustomErrorResponse> patternPasswordError(Exception ex) {
		CustomErrorResponse errors = new CustomErrorResponse();
		errors.setMessage(ex.getLocalizedMessage());
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PatternEmailException.class)
	public ResponseEntity<CustomErrorResponse> patternEmailError(Exception ex) {
		CustomErrorResponse errors = new CustomErrorResponse();
		errors.setMessage(ex.getLocalizedMessage());
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DefaultException.class)
	public ResponseEntity<CustomErrorResponse> defaultError(Exception ex) {
		CustomErrorResponse errors = new CustomErrorResponse();
		errors.setMessage(ex.getLocalizedMessage());
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

}
