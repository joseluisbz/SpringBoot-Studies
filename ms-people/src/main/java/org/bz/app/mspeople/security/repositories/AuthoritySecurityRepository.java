package org.bz.app.mspeople.security.repositories;

import org.bz.app.mspeople.security.entities.AuthoritySecurity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthoritySecurityRepository extends CrudRepository<AuthoritySecurity, UUID> {

}