package org.bz.app.mspeople.services;

import org.bz.app.mspeople.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
	
	public Iterable<User> findAll();
	public Optional<User> findById(Long id);
	public Optional<User> findByEmail(String email);
	public List<User> findByEmailAndIdNot(String email, Long id);
	public User save(User user);
	public void deleteById(Long id);

}
