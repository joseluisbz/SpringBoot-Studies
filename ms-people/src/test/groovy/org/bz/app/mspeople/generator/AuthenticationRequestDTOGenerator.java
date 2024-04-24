package org.bz.app.mspeople.generator;

import org.bz.app.mspeople.dtos.UserRequestDTO;
import org.bz.app.mspeople.security.dtos.AuthenticationRequestDTO;

public class AuthenticationRequestDTOGenerator {
    public static AuthenticationRequestDTO adminGenerate() {
        UserRequestDTO userRequestDTO = UserRequestDTOGenerator.adminGenerate();
        return AuthenticationRequestDTO
                .builder()
                .username(userRequestDTO.getUsername())
                .password(userRequestDTO.getPassword())
                .build();
    }
    public static AuthenticationRequestDTO userGenerate() {
        UserRequestDTO userRequestDTO = UserRequestDTOGenerator.userGenerate();
        return AuthenticationRequestDTO
                .builder()
                .username(userRequestDTO.getUsername())
                .password(userRequestDTO.getPassword())
                .build();
    }
}
