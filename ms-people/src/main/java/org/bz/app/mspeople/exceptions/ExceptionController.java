package org.bz.app.mspeople.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

import static org.bz.app.mspeople.utils.FunctionsUtil.getStackTraceElementByExceptionFunction;


@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DefaultBadRequestException.class)
    public ResponseEntity<CustomExceptionResponse> defaultBadRequest(DefaultBadRequestException defaultBadRequestException) {
        CustomExceptionResponse customExceptionResponse = new CustomExceptionResponse();
        customExceptionResponse.setMessage(defaultBadRequestException.getLocalizedMessage());
        customExceptionResponse.setDateTime(LocalDateTime.now());
        customExceptionResponse.setException(defaultBadRequestException);
        return new ResponseEntity<>(customExceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DefaultInternalServerErrorException.class)
    public ResponseEntity<CustomExceptionResponse> defaultInternalServerErrorException(DefaultInternalServerErrorException defaultInternalServerErrorException) {
        CustomExceptionResponse customExceptionResponse = new CustomExceptionResponse();
        customExceptionResponse.setMessage(defaultInternalServerErrorException.getLocalizedMessage());
        customExceptionResponse.setDateTime(LocalDateTime.now());
        customExceptionResponse.setException(defaultInternalServerErrorException);

        StackTraceElement stackTraceElement = getStackTraceElementByExceptionFunction.apply(defaultInternalServerErrorException);

        if (stackTraceElement != null) {
            customExceptionResponse.setLineNumber(stackTraceElement.getLineNumber());
            customExceptionResponse.setCatcherClass(stackTraceElement.getClassName());
            customExceptionResponse.setThrowerMethod(stackTraceElement.getMethodName());
        }
        return new ResponseEntity<>(customExceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomExceptionResponse> accessDeniedException(AccessDeniedException accessDeniedException) {
        CustomExceptionResponse customExceptionResponse = new CustomExceptionResponse();
        customExceptionResponse.setMessage(accessDeniedException.getLocalizedMessage());
        customExceptionResponse.setDateTime(LocalDateTime.now());
        customExceptionResponse.setException(accessDeniedException);
        return new ResponseEntity<>(customExceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomExceptionResponse> badCredentialsException(BadCredentialsException badCredentialsException) {
        CustomExceptionResponse customExceptionResponse = new CustomExceptionResponse();
        customExceptionResponse.setDateTime(LocalDateTime.now());
        customExceptionResponse.setMessage(badCredentialsException.getLocalizedMessage());
        customExceptionResponse.setException(badCredentialsException);
        return new ResponseEntity<>(customExceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomExceptionResponse> exception(Exception exception) {
        CustomExceptionResponse customExceptionResponse = new CustomExceptionResponse();
        customExceptionResponse.setDateTime(LocalDateTime.now());
        customExceptionResponse.setMessage(exception.getLocalizedMessage());
        customExceptionResponse.setException(exception);
        return new ResponseEntity<>(customExceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
