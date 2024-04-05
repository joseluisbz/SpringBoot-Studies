package org.bz.app.mspeople.exceptions;

import java.io.Serial;
import java.util.UUID;

public class NotAssignablePhoneException extends DefaultException {
    @Serial
    private static final long serialVersionUID = -2634323841999847616L;

    public NotAssignablePhoneException(UUID id, UUID user_id) {
        super("The phone with id: '"
                .concat(id.toString())
                .concat("' and userId: '")
                .concat((user_id != null ? user_id.toString() : "null"))
                .concat("' is not assignable to ")
                .concat((user_id != null ? "this user." : "a new user."))
        );
    }
}
