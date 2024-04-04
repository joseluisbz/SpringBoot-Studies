package org.bz.app.mspeople.security.exceptions;

import org.bz.app.mspeople.exceptions.DefaultException;

import java.io.Serial;

public class RoleEmptyException extends DefaultException {

    @Serial
    private static final long serialVersionUID = -8893201302030328801L;

    public RoleEmptyException(String message) {
        super("The 'role' must be included appropriately. "
                .concat(message));
    }
}
