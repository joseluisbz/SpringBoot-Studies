package org.bz.app.mspeople.exceptions;

import java.io.Serial;

public class ExistingPhoneException extends DefaultException {
    @Serial
    private static final long serialVersionUID = -2634323841999847616L;

    public ExistingPhoneException(Integer countryCode, Integer cityCode, Long number) {
        super("The phone with countryCode: '"
                .concat(countryCode.toString())
                .concat("', cityCode: '")
                .concat(cityCode.toString())
                .concat("' and number: '")
                .concat(number.toString())
                .concat("' is already registered.")
        );
    }
}
