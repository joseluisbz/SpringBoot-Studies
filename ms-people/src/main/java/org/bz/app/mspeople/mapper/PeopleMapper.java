package org.bz.app.mspeople.mapper;

import org.bz.app.mspeople.dtos.PhoneDTO;
import org.bz.app.mspeople.dtos.UserDTO;
import org.bz.app.mspeople.entities.Phone;
import org.bz.app.mspeople.entities.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.StreamSupport;

//@Mapper(componentModel = "spring")
public interface PeopleMapper {


    //@Mapping(source = "phones", target = "phones", qualifiedByName = "phoneDTOToEntity")
    User userDTOToEntity(UserDTO userDTO);

    //@Named("phoneDTOToEntity")
    //@Mapping(target = "user", ignore = true)
    Phone phoneDTOToEntity(PhoneDTO phoneDTO);

    UserDTO userEntityToDTO(User user);

    //@BeforeMapping
    default <T> List<T> castIterableToList(Iterable<T> iterable) {
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .toList();
    }

    List<UserDTO> userEntityToDTO(List<User> listUser);
}
