package org.bz.app.mspeople.security.exceptions;

import org.bz.app.mspeople.exceptions.DefaultException;

import java.io.Serial;

public class NonexistentRoleException extends DefaultException {
    @Serial
    private static final long serialVersionUID = 4107732096961237185L;

    public NonexistentRoleException(String name) {
        super("The role: '"
                .concat(name)
                .concat("' was not found."));
    }
}
