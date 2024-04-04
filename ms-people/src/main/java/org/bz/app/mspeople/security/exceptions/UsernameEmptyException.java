package org.bz.app.mspeople.security.exceptions;

import org.bz.app.mspeople.exceptions.DefaultException;

import java.io.Serial;

public class UsernameEmptyException extends DefaultException {
    @Serial
    private static final long serialVersionUID = 9209808288609603251L;

    public UsernameEmptyException(String message) {
        super("The 'username' must be included appropriately. "
                .concat(message));
    }
}
