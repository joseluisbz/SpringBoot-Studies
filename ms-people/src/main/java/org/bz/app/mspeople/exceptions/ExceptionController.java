package org.bz.app.mspeople.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        return new ResponseEntity<>(customExceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DefaultInternalServerErrorException.class)
    public ResponseEntity<CustomExceptionResponse> defaultInternalServerError(DefaultInternalServerErrorException defaultInternalServerErrorException) {
        CustomExceptionResponse customExceptionResponse = new CustomExceptionResponse();
        customExceptionResponse.setDateTime(LocalDateTime.now());
        customExceptionResponse.setMessage(defaultInternalServerErrorException.getLocalizedMessage());

        StackTraceElement stackTraceElement = getStackTraceElementByExceptionFunction.apply(defaultInternalServerErrorException);

        if (stackTraceElement != null) {
            customExceptionResponse.setLineNumber(stackTraceElement.getLineNumber());
            customExceptionResponse.setCatcherClass(stackTraceElement.getClassName());
            customExceptionResponse.setThrowerMethod(stackTraceElement.getMethodName());
        }
        return new ResponseEntity<>(customExceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
