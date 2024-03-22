package org.bz.app.mspeople.services;


import org.bz.app.mspeople.dtos.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
	
	Iterable<UserDTO> findAll();
	Optional<UserDTO> findById(Long id);
	Optional<UserDTO> findByEmail(String email);
	List<UserDTO> findByEmailAndIdNot(String email, Long id);
	UserDTO save(UserDTO userDTO);
	void deleteById(Long id);

}
