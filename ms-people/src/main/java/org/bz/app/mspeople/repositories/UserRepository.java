package org.bz.app.mspeople.repositories;

import org.bz.app.mspeople.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;
//@Repository
@RepositoryRestResource(path = "company")
public interface UserRepository extends CrudRepository<User, Long> {
	Optional<User> findFirstByEmail(String email);
	List<User> findByEmailAndIdNot(String email, Long id);
}