package org.bz.app.mspeople.generator;

import org.bz.app.mspeople.dtos.PhoneRequestDTO;

public class PhoneRequestDTOGenerator {
    public static PhoneRequestDTO generate() {
        return PhoneRequestDTO
                .builder()
                .number(2003L)
                .cityCode(20)
                .countryCode(20000)
                .build();
    }
}
