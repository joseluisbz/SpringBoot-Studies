package org.bz.app.mspeople.security.exceptions;

import org.bz.app.mspeople.exceptions.DefaultBadRequestException;

import java.io.Serial;

public class RoleEmptyBadRequestException extends DefaultBadRequestException {

    @Serial
    private static final long serialVersionUID = -8893201302030328801L;

    public RoleEmptyBadRequestException(String message) {
        super("The 'role' must be included appropriately. "
                .concat(message));
    }
}
