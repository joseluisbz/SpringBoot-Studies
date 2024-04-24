package org.bz.app.mspeople.generator;

import org.bz.app.mspeople.dtos.UserRequestDTO;
import org.bz.app.mspeople.dtos.UserResponseDTO;

import java.util.Set;
import java.util.UUID;

public class UserResponseDTOGenerator {
    public static UserResponseDTO userGenerate() {
        UserRequestDTO userRequestDTO = UserRequestDTOGenerator.userGenerate();
        UserResponseDTO userResponseDTO = UserResponseDTO
                .builder()
                .id(userRequestDTO.getId() != null ? userRequestDTO.getId() : UUID.randomUUID())
                .password("encoded_Password")
                .email(userRequestDTO.getEmail())
                .username(userRequestDTO.getUsername())
                .name(userRequestDTO.getName())
                .token(userRequestDTO.getToken())
                .accountNonExpired(userRequestDTO.isAccountNonExpired())
                .accountNonLocked(userRequestDTO.isAccountNonLocked())
                .credentialsNonExpired(userRequestDTO.isCredentialsNonExpired())
                .enabled(userRequestDTO.isEnabled())
                .build();
        userResponseDTO.setPhones(Set.of(PhoneResponseDTOGenerator.generate()));
        userResponseDTO.setRole(RoleDTOGenerator.userWithAuthoritiesGenerate());
        return userResponseDTO;
    }
    public static UserResponseDTO adminGenerate() {
        UserRequestDTO userRequestDTO = UserRequestDTOGenerator.adminGenerate();
        UserResponseDTO userResponseDTO = UserResponseDTO
                .builder()
                .id(userRequestDTO.getId() != null ? userRequestDTO.getId() : UUID.randomUUID())
                .password("encoded_Password")
                .email(userRequestDTO.getEmail())
                .username(userRequestDTO.getUsername())
                .name(userRequestDTO.getName())
                .token(userRequestDTO.getToken())
                .accountNonExpired(userRequestDTO.isAccountNonExpired())
                .accountNonLocked(userRequestDTO.isAccountNonLocked())
                .credentialsNonExpired(userRequestDTO.isCredentialsNonExpired())
                .enabled(userRequestDTO.isEnabled())
                .build();
        userResponseDTO.setPhones(Set.of(PhoneResponseDTOGenerator.generate()));
        userResponseDTO.setRole(RoleDTOGenerator.adminWithAuthoritiesGenerate());
        return userResponseDTO;
    }
}
