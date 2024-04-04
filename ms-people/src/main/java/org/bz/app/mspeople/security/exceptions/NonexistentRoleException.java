package org.bz.app.mspeople.security.exceptions;

import java.io.Serial;

public class NonexistentRoleException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4107732096961237185L;

    public NonexistentRoleException(String name) {
        super("The role: '"
                .concat(name)
                .concat("' was not found."));
    }
}
