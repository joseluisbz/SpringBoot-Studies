package org.bz.app.mspeople.exceptions;

import java.io.Serial;

public class PatternPasswordException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -43792510933436020L;

    public PatternPasswordException(String message) {
        super("The password does not meet the required pattern. "
                .concat(message));
    }

}
