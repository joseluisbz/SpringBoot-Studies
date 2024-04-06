package org.bz.app.mspeople.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public class DefaultInternalServerErrorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8501338416669793371L;

    private final Exception originException;

    private final String catcherMethod;

    public DefaultInternalServerErrorException(Exception exception, String catcherMethod) {
        super(exception.getLocalizedMessage());
        originException = exception;
        this.catcherMethod = catcherMethod;
    }

}
