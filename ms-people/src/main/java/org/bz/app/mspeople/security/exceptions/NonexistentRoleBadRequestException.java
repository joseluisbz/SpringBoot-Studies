package org.bz.app.mspeople.security.exceptions;

import org.bz.app.mspeople.exceptions.DefaultBadRequestException;

import java.io.Serial;

public class NonexistentRoleBadRequestException extends DefaultBadRequestException {
    @Serial
    private static final long serialVersionUID = 4107732096961237185L;

    public NonexistentRoleBadRequestException(String name) {
        super("The role: '"
                .concat(name)
                .concat("' was not found."));
    }
}
