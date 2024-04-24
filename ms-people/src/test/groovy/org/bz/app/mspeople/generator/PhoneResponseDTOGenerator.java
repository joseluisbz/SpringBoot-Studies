package org.bz.app.mspeople.generator;

import org.bz.app.mspeople.dtos.PhoneRequestDTO;
import org.bz.app.mspeople.dtos.PhoneResponseDTO;

import java.util.UUID;

public class PhoneResponseDTOGenerator {
    public static PhoneResponseDTO generate() {
        PhoneRequestDTO phoneRequestDTO = PhoneRequestDTOGenerator.generate();
        return PhoneResponseDTO
                .builder()
                .id(phoneRequestDTO.getId() != null ? phoneRequestDTO.getId() : UUID.randomUUID())
                .number(phoneRequestDTO.getNumber())
                .cityCode(phoneRequestDTO.getCityCode())
                .countryCode(phoneRequestDTO.getCountryCode())
                .build();
    }
}
