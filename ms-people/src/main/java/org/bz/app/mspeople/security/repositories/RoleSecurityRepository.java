package org.bz.app.mspeople.security.repositories;

import org.bz.app.mspeople.security.entities.RoleSecurity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleSecurityRepository extends CrudRepository<RoleSecurity, UUID> {

    Optional<RoleSecurity> findByNameIgnoreCase(String name);

}