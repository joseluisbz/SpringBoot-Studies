package org.bz.app.mspeople.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public class DefaultInternalServerErrorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8501338416669793371L;

    private final Exception originException;

    private Class<?> catcherClass;

    private StackWalker.StackFrame stackFrame;

    public DefaultInternalServerErrorException(Exception exception, Class<?> catcherClass) {
        super(exception.getLocalizedMessage());
        originException = exception;
        this.catcherClass = catcherClass;
    }

    public DefaultInternalServerErrorException(Exception exception, StackWalker.StackFrame stackFrame) {
        super(exception.getLocalizedMessage());
        originException = exception;
        this.stackFrame = stackFrame;
    }

}
