package org.bz.app.mspeople.security.exceptions;

import org.bz.app.mspeople.exceptions.DefaultBadRequestException;

import java.io.Serial;

public class UsernameEmptyBadRequestException extends DefaultBadRequestException {
    @Serial
    private static final long serialVersionUID = 9209808288609603251L;

    public UsernameEmptyBadRequestException(String message) {
        super("The 'username' must be included appropriately. "
                .concat(message));
    }
}
