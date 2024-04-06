package org.bz.app.mspeople.exceptions;

import java.io.Serial;

public class ExistingPhoneBadRequestException extends DefaultBadRequestException {
    @Serial
    private static final long serialVersionUID = -2634323841999847616L;

    public ExistingPhoneBadRequestException(Integer countryCode, Integer cityCode, Long number) {
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
