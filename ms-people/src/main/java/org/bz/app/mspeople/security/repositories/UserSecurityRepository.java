package org.bz.app.mspeople.security.repositories;

import org.bz.app.mspeople.security.entities.UserSecurity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSecurityRepository extends CrudRepository<UserSecurity, UUID> {

    Optional<UserSecurity> findFirstByUsernameIgnoreCase(String username);

}