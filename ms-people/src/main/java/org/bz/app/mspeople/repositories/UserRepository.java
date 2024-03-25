package org.bz.app.mspeople.repositories;

import org.bz.app.mspeople.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {
    Optional<UserEntity> findFirstByEmailIgnoreCase(String email);

    List<UserEntity> findByEmailIgnoreCaseAndIdNot(String email, UUID id);
}