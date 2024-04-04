package org.bz.app.mspeople.exceptions;

import java.io.Serial;

public class ExistingMailOrUsernameException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7241642475224506375L;

    public ExistingMailOrUsernameException(String email, String username, boolean equals) {
        super("The "
                .concat((email != null ? "email: '".concat(email).concat("' ") : ""))
                .concat((email != null && username != null ? "and the " : ""))
                .concat((username != null ? "username: '".concat(username).concat("' ") : ""))
                .concat((email != null && username != null ? "are " : "is "))
                .concat("already registered to ")
                .concat((equals ? "another record." : "other records."))
        );
    }

}
