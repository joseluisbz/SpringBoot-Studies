package org.bz.app.mspeople.security.exceptions;

import java.io.Serial;

public class UsernameEmptyException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 9209808288609603251L;

    public UsernameEmptyException(String message) {
        super("The 'username' must be included appropriately. "
                .concat(message));
    }
}
