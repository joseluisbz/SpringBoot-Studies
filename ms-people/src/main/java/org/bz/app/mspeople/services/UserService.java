package org.bz.app.mspeople.services;


import org.bz.app.mspeople.dtos.RoleDTO;
import org.bz.app.mspeople.dtos.UserDTO;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Iterable<UserDTO> findAll();

    Optional<UserDTO> findById(UUID id);

    Optional<UserDTO> findFirstByEmailIgnoreCase(String email);

    Optional<UserDTO> findFirstByEmailIgnoreCaseAndIdNot(String email, UUID id);

    UserDTO save(UserDTO userDTO);

    void deleteById(UUID id);

    Optional<UserDTO> findFirstByUsernameIgnoreCase(String username);

    Optional<RoleDTO> findRoleByNameIgnoreCase(String name);

    Optional<UserDTO> findFirstByUsernameIgnoreCaseAndIdNot(String username, UUID id);

}
