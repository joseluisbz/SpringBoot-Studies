package org.bz.app.mspeople.services;


import org.bz.app.mspeople.dtos.PhoneResponseDTO;
import org.bz.app.mspeople.dtos.RoleDTO;
import org.bz.app.mspeople.dtos.UserRequestDTO;
import org.bz.app.mspeople.dtos.UserResponseDTO;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Iterable<UserResponseDTO> findAll();

    Optional<UserResponseDTO> findById(UUID id);

    Optional<UserResponseDTO> findFirstByEmailIgnoreCase(String email);

    Optional<UserResponseDTO> findFirstByEmailIgnoreCaseAndIdNot(String email, UUID id);

    UserResponseDTO save(UserRequestDTO userRequestDTO);

    void deleteById(UUID id);

    Optional<UserResponseDTO> findFirstByUsernameIgnoreCase(String username);

    Optional<RoleDTO> findRoleByNameIgnoreCase(String name);

    Optional<UserResponseDTO> findFirstByUsernameIgnoreCaseAndIdNot(String username, UUID id);

    Optional<PhoneResponseDTO> findByCountryCodeAndCityCodeAndNumber(Integer countryCode, Integer cityCode, Long number);

    Optional<PhoneResponseDTO> findByCountryCodeAndCityCodeAndNumberAndUserEntity_IdNot(Integer countryCode, Integer cityCode, Long number, UUID id);

    Optional<PhoneResponseDTO> findByIdAndUserEntity_Id(UUID id, UUID user_id);

}
