package org.bz.app.mspeople.exceptions;

import org.bz.app.mspeople.security.exceptions.RoleEmptyException;
import org.bz.app.mspeople.security.exceptions.UsernameEmptyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DefaultException.class)
    public ResponseEntity<CustomErrorResponse> defaultError(Exception ex) {
        CustomErrorResponse errors = new CustomErrorResponse();
        errors.setMessage(ex.getLocalizedMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
