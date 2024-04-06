package org.bz.app.mspeople.exceptions;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomExceptionResponse {

    private String message;

    private LocalDateTime dateTime;

    private String throwerMethod;

    private String catcherMethod;

    private String exceptionThrowerClass;

    private Integer lineNumber;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getThrowerMethod() {
        return throwerMethod;
    }

    public void setThrowerMethod(String throwerMethod) {
        this.throwerMethod = throwerMethod;
    }

    public String getCatcherMethod() {
        return catcherMethod;
    }

    public void setCatcherMethod(String catcherMethod) {
        this.catcherMethod = catcherMethod;
    }

    public String getExceptionThrowerClass() {
        return exceptionThrowerClass;
    }

    public void setExceptionThrowerClass(String exceptionThrowerClass) {
        this.exceptionThrowerClass = exceptionThrowerClass;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }
}
