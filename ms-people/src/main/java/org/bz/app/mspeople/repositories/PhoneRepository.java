package org.bz.app.mspeople.repositories;

import org.bz.app.mspeople.entities.PhoneEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PhoneRepository extends CrudRepository<PhoneEntity, UUID> {
    Set<PhoneEntity> findByUserEntity_Id(UUID id);
    Optional<PhoneEntity> findByCountryCodeAndCityCodeAndNumber(Integer countryCode, Integer cityCode, Long number);
    Optional<PhoneEntity> findByCountryCodeAndCityCodeAndNumberAndUserEntity_IdNot(Integer countryCode, Integer cityCode, Long number, UUID id);
    Optional<PhoneEntity> findByIdAndUserEntity_Id(UUID id, UUID user_id);
    Optional<PhoneEntity> findById(UUID id);
}
