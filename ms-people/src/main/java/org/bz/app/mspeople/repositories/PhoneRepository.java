package org.bz.app.mspeople.repositories;

import org.bz.app.mspeople.entities.Phone;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface PhoneRepository extends CrudRepository<Phone, Long> {
    List<Phone> deleteByUser_Id(Long id);

    Set<Phone> findByUser_Id(Long id);
}
