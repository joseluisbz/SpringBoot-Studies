package org.bz.app.mspeople.exceptions;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(exclude = {"exception"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomExceptionResponse {

    private String message;

    @JsonFormat(pattern = "yyyy/MM/dd-HH:mm:ss")
    private LocalDateTime dateTime;

    private String catcherClass;

    private String throwerMethod;

    private Integer lineNumber;

    private Exception exception;

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

    public String getCatcherClass() {
        return catcherClass;
    }

    public void setCatcherClass(String catcherClass) {
        this.catcherClass = catcherClass;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
