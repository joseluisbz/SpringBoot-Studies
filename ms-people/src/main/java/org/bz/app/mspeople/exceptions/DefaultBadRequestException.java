package org.bz.app.mspeople.exceptions;

import java.io.Serial;

public abstract class DefaultBadRequestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3206918744494302928L;

    protected DefaultBadRequestException(String message) {
        super(message);
    }

}
