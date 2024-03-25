package org.bz.app.mspeople.services;


import org.bz.app.mspeople.dtos.UserDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Iterable<UserDTO> findAll();

    Optional<UserDTO> findById(UUID id);

    Optional<UserDTO> findByEmail(String email);

    List<UserDTO> findByEmailAndIdNot(String email, UUID id);

    UserDTO save(UserDTO userDTO);

    void deleteById(UUID id);

}
