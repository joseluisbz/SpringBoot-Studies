package org.bz.app.mspeople.exceptions;

import java.io.Serial;

public class PatternEmailBadRequestException extends DefaultBadRequestException {

    @Serial
    private static final long serialVersionUID = -4753386595346116814L;

    public PatternEmailBadRequestException(String message) {
        super("The 'email' does not meet the required pattern. "
                .concat(message));
    }


}
