package org.bz.app.mspeople.mapper;

import org.bz.app.mspeople.dtos.PhoneDTO;
import org.bz.app.mspeople.dtos.UserDTO;
import org.bz.app.mspeople.entities.Phone;
import org.bz.app.mspeople.entities.User;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.StreamSupport;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface PeopleMapper {


    @Mapping(source = "phones", target = "phones", qualifiedByName = "phoneDTOToEntity")
    User userDTOToEntity(UserDTO userDTO);

    @AfterMapping
    default void afterMappingUserDTOToEntity(@MappingTarget User user) {
        user.getPhones().forEach(phone -> phone.setUser(user));
    }

    @Named("phoneDTOToEntity")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "cityCode", target = "cityCode")
    @Mapping(source = "countryCode", target = "countryCode")
    @Mapping(target = "user", ignore = true)
    Phone phoneDTOToEntity(PhoneDTO phoneDTO);

    @Mapping(source = "phones", target = "phones", qualifiedByName = "phoneEntityToDTO")
    UserDTO userEntityToDTO(User user);

    @AfterMapping
    default void afterMappingUserEntityToDTO(@MappingTarget UserDTO userDTO) {
        userDTO.getPhones().forEach(phone -> phone.setUser(userDTO));
    }

    @Named("phoneEntityToDTO")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "cityCode", target = "cityCode")
    @Mapping(source = "countryCode", target = "countryCode")
    @Mapping(target = "user", ignore = true)
    PhoneDTO phoneEntityToDTO(Phone phone);

    default <T> List<T> castIterableToList(Iterable<T> iterable) {
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .toList();
    }

    List<UserDTO> userEntityToDTO(List<User> listUser);
}
