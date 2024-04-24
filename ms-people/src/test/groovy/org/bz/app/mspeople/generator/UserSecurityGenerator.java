package org.bz.app.mspeople.generator;

import org.bz.app.mspeople.dtos.UserResponseDTO;
import org.bz.app.mspeople.security.entities.UserSecurity;

public class UserSecurityGenerator {
    public static UserSecurity userGenerate() {
        UserResponseDTO userResponseDTO = UserResponseDTOGenerator.userGenerate();
        UserSecurity userSecurity = UserSecurity
                .builder()

                .id(userResponseDTO.getId())
                .password(userResponseDTO.getPassword())
                .email(userResponseDTO.getEmail())
                .username(userResponseDTO.getUsername())

                .accountNonExpired(userResponseDTO.isAccountNonExpired())
                .accountNonLocked(userResponseDTO.isAccountNonLocked())
                .credentialsNonExpired(userResponseDTO.isCredentialsNonExpired())
                .enabled(userResponseDTO.isEnabled())

                .build();
        userSecurity.setRole(RoleSecurityGenerator.userWithAuthoritiesGenerate());
        return userSecurity;
    }
    public static UserSecurity adminGenerate() {
        UserResponseDTO userResponseDTO = UserResponseDTOGenerator.adminGenerate();
        UserSecurity userSecurity = UserSecurity
                .builder()

                .id(userResponseDTO.getId())
                .password(userResponseDTO.getPassword())
                .email(userResponseDTO.getEmail())
                .username(userResponseDTO.getUsername())

                .accountNonExpired(userResponseDTO.isAccountNonExpired())
                .accountNonLocked(userResponseDTO.isAccountNonLocked())
                .credentialsNonExpired(userResponseDTO.isCredentialsNonExpired())
                .enabled(userResponseDTO.isEnabled())

                .build();
        userSecurity.setRole(RoleSecurityGenerator.adminWithAuthoritiesGenerate());
        return userSecurity;
    }
}
