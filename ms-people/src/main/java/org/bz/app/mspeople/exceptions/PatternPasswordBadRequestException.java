package org.bz.app.mspeople.exceptions;

import java.io.Serial;

public class PatternPasswordBadRequestException extends DefaultBadRequestException {

    @Serial
    private static final long serialVersionUID = -43792510933436020L;

    public PatternPasswordBadRequestException(String message) {
        super("The 'password' does not meet the required pattern. "
                .concat(message));
    }

}
