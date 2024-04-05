package org.bz.app.mspeople.mapper;

import org.bz.app.mspeople.dtos.*;
import org.bz.app.mspeople.entities.PhoneEntity;
import org.bz.app.mspeople.entities.UserEntity;
import org.bz.app.mspeople.security.entities.AuthoritySecurity;
import org.bz.app.mspeople.security.entities.RoleSecurity;
import org.bz.app.mspeople.security.entities.UserSecurity;
import org.mapstruct.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface PeopleMapper {


    @Mapping(source = "phones", target = "phoneEntities", qualifiedByName = "phoneDTOToEntity")
    UserEntity userDTOToEntity(UserRequestDTO userRequestDTO);

    UserSecurity userDTOToSecurity(UserRequestDTO userRequestDTO);

    RoleSecurity roleDTOToSecurity(RoleDTO roleDTO);

    @AfterMapping
    default void afterMappingUserDTOToEntity(@MappingTarget UserEntity userEntity) {
        userEntity.getPhoneEntities().forEach(phone -> phone.setUserEntity(userEntity));
    }

    @Named("phoneDTOToEntity")
    @Mapping(target = "userEntity", ignore = true)
    PhoneEntity phoneDTOToEntity(PhoneRequestDTO phoneRequestDTO);

    @Mapping(source = "phoneEntities", target = "phones", qualifiedByName = "phoneEntityToDTO")
    UserResponseDTO userEntityToDTO(UserEntity userEntity);

    UserResponseDTO userSecurityToDTO(UserSecurity userSecurity);

    @Mapping(source = "userSecurity.id", target = "id")
    @Mapping(source = "userEntity.email", target = "email")
    @Mapping(source = "userSecurity.username", target = "username")

    @Mapping(source = "userEntity.name", target = "name")
    @Mapping(source = "userEntity.phoneEntities", target = "phones", qualifiedByName = "phoneEntityToDTO")
    @Mapping(source = "userEntity.created", target = "created")
    @Mapping(source = "userEntity.modified", target = "modified")
    @Mapping(source = "userEntity.lastLogin", target = "lastLogin")
    @Mapping(source = "userEntity.isactive", target = "isactive")
    @Mapping(source = "userEntity.token", target = "token")

    @Mapping(source = "userSecurity.accountNonExpired", target = "accountNonExpired")
    @Mapping(source = "userSecurity.accountNonLocked", target = "accountNonLocked")
    @Mapping(source = "userSecurity.credentialsNonExpired", target = "credentialsNonExpired")
    @Mapping(source = "userSecurity.enabled", target = "enabled")
    @Mapping(source = "userSecurity.role.securityAuthorities", target = "role.authorities", qualifiedByName = "authoritySecurityToDTO")
    UserResponseDTO userEntityAndSecurityToDTO(UserEntity userEntity, UserSecurity userSecurity);

    RoleDTO roleSecurityToDTO(RoleSecurity roleSecurity);

    @Named("authoritySecurityToDTO")
    AuthorityDTO authoritySecurityToDTO(AuthoritySecurity authoritySecurity);

    @AfterMapping
    default void afterMappingUserEntityToDTO(@MappingTarget UserResponseDTO userResponseDTO) {
        userResponseDTO.getPhones().forEach(phone -> phone.setUser(userResponseDTO));
    }

    @Named("phoneEntityToDTO")
    @Mapping(target = "user", ignore = true)
    PhoneResponseDTO phoneEntityToDTO(PhoneEntity phoneEntity);

    default <T> List<T> castIterableToList(Iterable<T> iterable) {
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .toList();
    }

    List<UserResponseDTO> userEntityToDTO(List<UserEntity> listUserEntity);

    default <T> T unwrapOptional(Optional<T> optional) {
        return optional.orElse(null);
    }

    default <T> Optional<T> wrapOptional(T object) {
        return Optional.ofNullable(object);
    }
}
