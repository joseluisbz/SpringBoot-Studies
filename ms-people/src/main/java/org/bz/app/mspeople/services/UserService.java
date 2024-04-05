package org.bz.app.mspeople.services;


import org.bz.app.mspeople.dtos.PhoneRequestDTO;
import org.bz.app.mspeople.dtos.RoleDTO;
import org.bz.app.mspeople.dtos.UserRequestDTO;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Iterable<UserRequestDTO> findAll();

    Optional<UserRequestDTO> findById(UUID id);

    Optional<UserRequestDTO> findFirstByEmailIgnoreCase(String email);

    Optional<UserRequestDTO> findFirstByEmailIgnoreCaseAndIdNot(String email, UUID id);

    UserRequestDTO save(UserRequestDTO userRequestDTO);

    void deleteById(UUID id);

    Optional<UserRequestDTO> findFirstByUsernameIgnoreCase(String username);

    Optional<RoleDTO> findRoleByNameIgnoreCase(String name);

    Optional<UserRequestDTO> findFirstByUsernameIgnoreCaseAndIdNot(String username, UUID id);

    Optional<PhoneRequestDTO> findByCountryCodeAndCityCodeAndNumber(Integer countryCode, Integer cityCode, Long number);

    Optional<PhoneRequestDTO> findByCountryCodeAndCityCodeAndNumberAndUserEntity_IdNot(Integer countryCode, Integer cityCode, Long number, UUID id);

    Optional<PhoneRequestDTO> findByIdAndUserEntity_Id(UUID id, UUID user_id);

}
