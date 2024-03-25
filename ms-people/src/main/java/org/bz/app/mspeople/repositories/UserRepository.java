package org.bz.app.mspeople.repositories;

import org.bz.app.mspeople.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findFirstByEmail(String email);

    List<User> findByEmailAndIdNot(String email, Long id);
}