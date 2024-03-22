package org.bz.app.mspeople.exceptions;

import java.io.Serial;

public class ExistingMailException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7241642475224506375L;

    public ExistingMailException(String mail) {
        super("The email: '"
                .concat(mail)
                .concat("' is already registered."));
    }

}
