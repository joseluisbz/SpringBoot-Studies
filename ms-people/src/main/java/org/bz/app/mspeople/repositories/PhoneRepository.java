package org.bz.app.mspeople.repositories;

import org.bz.app.mspeople.entities.Phone;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PhoneRepository extends CrudRepository<Phone, UUID> {
    List<Phone> deleteByUser_Id(UUID id);

    Set<Phone> findByUser_Id(UUID id);
}
