package org.bz.app.mspeople.repositories;

import org.bz.app.mspeople.entities.PhoneEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

public interface PhoneRepository extends CrudRepository<PhoneEntity, UUID> {
    Set<PhoneEntity> findByUserEntity_Id(UUID id);
}
