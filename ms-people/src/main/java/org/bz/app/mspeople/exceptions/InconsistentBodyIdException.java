package org.bz.app.mspeople.exceptions;

import java.io.Serial;
import java.util.UUID;

public class InconsistentBodyIdException extends DefaultException {

    @Serial
    private static final long serialVersionUID = 6885236156218013161L;

    public InconsistentBodyIdException(UUID pathId, UUID requestBodyId) {
        super("The path Id: '"
                .concat((pathId != null ? pathId.toString() : "null"))
                .concat("' and the requestBody Id: '")
                .concat((requestBodyId != null ? requestBodyId.toString() : "null"))
                .concat("' are different")
        );
    }
}
