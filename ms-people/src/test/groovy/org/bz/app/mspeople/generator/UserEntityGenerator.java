package org.bz.app.mspeople.generator;

import org.bz.app.mspeople.dtos.UserResponseDTO;
import org.bz.app.mspeople.entities.UserEntity;

import java.util.Set;

public class UserEntityGenerator {

    public static UserEntity userGenerate() {
        UserResponseDTO userResponseDTO = UserResponseDTOGenerator.userGenerate();
        UserEntity userEntity = UserEntity
                .builder()

                .id(userResponseDTO.getId())
                .password(userResponseDTO.getPassword())
                .email(userResponseDTO.getEmail())
                .username(userResponseDTO.getUsername())

                .name(userResponseDTO.getName())
                .created(userResponseDTO.getCreated())
                .modified(userResponseDTO.getModified())
                .lastLogin(userResponseDTO.getLastLogin())
                .isactive(userResponseDTO.isIsactive())
                .token(userResponseDTO.getToken())
                .build();
        userEntity.setPhoneEntities(Set.of(PhoneEntityGenerator.generate()));
        return userEntity;
    }

    public static UserEntity adminGenerate() {
        UserResponseDTO userResponseDTO = UserResponseDTOGenerator.adminGenerate();
        UserEntity userEntity = UserEntity
                .builder()

                .id(userResponseDTO.getId())
                .password(userResponseDTO.getPassword())
                .email(userResponseDTO.getEmail())
                .username(userResponseDTO.getUsername())

                .name(userResponseDTO.getName())
                .created(userResponseDTO.getCreated())
                .modified(userResponseDTO.getModified())
                .lastLogin(userResponseDTO.getLastLogin())
                .isactive(userResponseDTO.isIsactive())
                .token(userResponseDTO.getToken())
                .build();
        userEntity.setPhoneEntities(Set.of(PhoneEntityGenerator.generate()));
        return userEntity;
    }
}
