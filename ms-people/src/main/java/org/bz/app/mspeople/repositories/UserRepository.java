package org.bz.app.mspeople.repositories;

import org.bz.app.mspeople.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {
    Optional<UserEntity> findFirstByEmailIgnoreCase(String email);

    Optional<UserEntity> findFirstByEmailIgnoreCaseAndIdNot(String email, UUID id);

    Optional<UserEntity> findFirstByUsernameIgnoreCaseAndIdNot(String email, UUID id);

    Optional<UserEntity> findFirstByUsernameIgnoreCase(String username);
}