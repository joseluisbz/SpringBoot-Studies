package org.bz.app.mspeople.exceptions;

import java.io.Serial;

public class DefaultBadRequestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3206918744494302928L;

    public DefaultBadRequestException(String message) {
        super(message);
    }


}
