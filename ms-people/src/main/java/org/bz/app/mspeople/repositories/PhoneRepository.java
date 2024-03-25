package org.bz.app.mspeople.repositories;

import org.bz.app.mspeople.entities.Phone;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

public interface PhoneRepository extends CrudRepository<Phone, UUID> {
    Set<Phone> findByUser_Id(UUID id);
}
