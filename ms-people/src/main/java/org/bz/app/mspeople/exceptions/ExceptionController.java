package org.bz.app.mspeople.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DefaultBadRequestException.class)
    public ResponseEntity<CustomExceptionResponse> defaultBadRequest(DefaultBadRequestException defaultBadRequestException) {
        CustomExceptionResponse customExceptionResponse = new CustomExceptionResponse();
        customExceptionResponse.setMessage(defaultBadRequestException.getLocalizedMessage());
        customExceptionResponse.setDateTime(LocalDateTime.now());
        return new ResponseEntity<>(customExceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DefaultInternalServerErrorException.class)
    public ResponseEntity<CustomExceptionResponse> defaultInternalServerError(DefaultInternalServerErrorException defaultInternalServerErrorException) {
        StackTraceElement[] arrayStackTraceElement = defaultInternalServerErrorException.getOriginException().getStackTrace();
        CustomExceptionResponse customExceptionResponse = new CustomExceptionResponse();
        customExceptionResponse.setMessage(defaultInternalServerErrorException.getLocalizedMessage());
        if (arrayStackTraceElement != null) {
            customExceptionResponse.setLineNumber(arrayStackTraceElement[1].getLineNumber());
            customExceptionResponse.setExceptionThrowerClass(arrayStackTraceElement[1].getClassName());
            customExceptionResponse.setThrowerMethod(arrayStackTraceElement[1].getMethodName());
            customExceptionResponse.setCatcherMethod(arrayStackTraceElement[2].getMethodName());
        }
        customExceptionResponse.setDateTime(LocalDateTime.now());
        return new ResponseEntity<>(customExceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
