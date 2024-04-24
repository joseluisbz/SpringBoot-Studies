package org.bz.app.mspeople.generator;

import org.bz.app.mspeople.dtos.PhoneResponseDTO;
import org.bz.app.mspeople.entities.PhoneEntity;

public class PhoneEntityGenerator {
    public static PhoneEntity generate() {
        PhoneResponseDTO phoneResponseDTO = PhoneResponseDTOGenerator.generate();
        return PhoneEntity
                .builder()
                .id(phoneResponseDTO.getId())
                .number(phoneResponseDTO.getNumber())
                .cityCode(phoneResponseDTO.getCityCode())
                .countryCode(phoneResponseDTO.getCountryCode())
                .build();
    }
}
