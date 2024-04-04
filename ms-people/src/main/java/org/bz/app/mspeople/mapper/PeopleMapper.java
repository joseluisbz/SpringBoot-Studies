package org.bz.app.mspeople.mapper;

import org.bz.app.mspeople.dtos.PhoneDTO;
import org.bz.app.mspeople.dtos.RoleDTO;
import org.bz.app.mspeople.dtos.UserDTO;
import org.bz.app.mspeople.entities.PhoneEntity;
import org.bz.app.mspeople.entities.UserEntity;
import org.bz.app.mspeople.security.entities.RoleSecurity;
import org.bz.app.mspeople.security.entities.UserSecurity;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.StreamSupport;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface PeopleMapper {


    @Mapping(source = "phones", target = "phoneEntities", qualifiedByName = "phoneDTOToEntity")
    UserEntity userDTOToEntity(UserDTO userDTO);

    //@Mapping(target = "phones", ignore = true)
    UserSecurity userDTOToSecurity(UserDTO userDTO);

    RoleSecurity roleDTOToSecurity(RoleDTO roleDTO);

    @AfterMapping
    default void afterMappingUserDTOToEntity(@MappingTarget UserEntity userEntity) {
        userEntity.getPhoneEntities().forEach(phone -> phone.setUserEntity(userEntity));
    }

    @Named("phoneDTOToEntity")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "cityCode", target = "cityCode")
    @Mapping(source = "countryCode", target = "countryCode")
    @Mapping(target = "userEntity", ignore = true)
    PhoneEntity phoneDTOToEntity(PhoneDTO phoneDTO);

    @Mapping(source = "phoneEntities", target = "phones", qualifiedByName = "phoneEntityToDTO")
    UserDTO userEntityToDTO(UserEntity userEntity);

    UserDTO userSecurityToDTO(UserSecurity userSecurity);

    RoleDTO roleSecurityToDTO(RoleSecurity roleSecurity);

    @AfterMapping
    default void afterMappingUserEntityToDTO(@MappingTarget UserDTO userDTO) {
        userDTO.getPhones().forEach(phone -> phone.setUser(userDTO));
    }

    @Named("phoneEntityToDTO")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "cityCode", target = "cityCode")
    @Mapping(source = "countryCode", target = "countryCode")
    @Mapping(target = "user", ignore = true)
    PhoneDTO phoneEntityToDTO(PhoneEntity phoneEntity);

    default <T> List<T> castIterableToList(Iterable<T> iterable) {
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .toList();
    }

    List<UserDTO> userEntityToDTO(List<UserEntity> listUserEntity);
}
