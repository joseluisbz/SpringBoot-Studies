package org.bz.app.mspeople.generator;

import org.bz.app.mspeople.dtos.UserRequestDTO;

import java.util.Set;

public class UserRequestDTOGenerator {
    private static UserRequestDTO generate() {
        UserRequestDTO userRequestDTO = UserRequestDTO
                .builder()
                .password("rr5y5yrty3")
                .token("token1")
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
        userRequestDTO.setPhones(Set.of(PhoneRequestDTOGenerator.generate()));
        return userRequestDTO;
    }
    public static UserRequestDTO userGenerate() {
        UserRequestDTO userRequestDTO = generate();
        userRequestDTO.setEmail("joseluisBZU@gmail.com");
        userRequestDTO.setUsername("joseluisBZU");
        userRequestDTO.setName("User Jose Luis Bernal Zambrano");
        userRequestDTO.setRole(RoleDTOGenerator.userGenerate());
        return userRequestDTO;
    }
    public static UserRequestDTO adminGenerate() {
        UserRequestDTO userRequestDTO = generate();
        userRequestDTO.setEmail("joseluisBZA@gmail.com");
        userRequestDTO.setUsername("joseluisBZA");
        userRequestDTO.setName("Admin Jose Luis Bernal Zambrano");
        userRequestDTO.setRole(RoleDTOGenerator.adminGenerate());
        return userRequestDTO;
    }
}
