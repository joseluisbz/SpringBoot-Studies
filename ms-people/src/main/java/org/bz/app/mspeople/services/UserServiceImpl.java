package org.bz.app.mspeople.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bz.app.mspeople.dtos.PhoneResponseDTO;
import org.bz.app.mspeople.dtos.RoleDTO;
import org.bz.app.mspeople.dtos.UserRequestDTO;
import org.bz.app.mspeople.dtos.UserResponseDTO;
import org.bz.app.mspeople.entities.PhoneEntity;
import org.bz.app.mspeople.entities.UserEntity;
import org.bz.app.mspeople.mapper.PeopleMapper;
import org.bz.app.mspeople.repositories.PhoneRepository;
import org.bz.app.mspeople.repositories.UserRepository;
import org.bz.app.mspeople.security.entities.AuthoritySecurity;
import org.bz.app.mspeople.security.entities.RoleSecurity;
import org.bz.app.mspeople.security.entities.UserSecurity;
import org.bz.app.mspeople.security.repositories.AuthoritySecurityRepository;
import org.bz.app.mspeople.security.repositories.RoleSecurityRepository;
import org.bz.app.mspeople.security.repositories.UserSecurityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final PeopleMapper peopleMapper;

    private final UserRepository userRepository;

    private final PhoneRepository phoneRepository;

    private final UserSecurityRepository userSecurityRepository;

    private final RoleSecurityRepository roleSecurityRepository;

    private final AuthoritySecurityRepository authoritySecurityRepository;

    @Override
    @Transactional(readOnly = true)
    public Iterable<UserResponseDTO> findAll() {
        // Obtain Records Separately
        Iterable<UserEntity> iterableUserEntity = userRepository.findAll();
        Iterable<UserSecurity> iterableUserSecurity = userSecurityRepository.findAll();

        // Convert them to List
        List<UserEntity> listUserEntity = StreamSupport
                .stream(iterableUserEntity.spliterator(), false)
                .toList();

        List<UserSecurity> listUserSecurity = StreamSupport
                .stream(iterableUserSecurity.spliterator(), false)
                .toList();

        // Consolidate UUID
        Set<UUID> setUUID = new HashSet<>();
        listUserEntity.forEach(ue -> setUUID.add(ue.getId()));
        listUserSecurity.forEach(ue -> setUUID.add(ue.getId()));

        // Create new List Mixing
        List<UserResponseDTO> listUserResponseDTO =
                setUUID.stream().map(uuid -> {
                    Optional<UserEntity> optionalUserEntity = listUserEntity
                            .stream()
                            .filter(ue -> ue.getId().equals(uuid))
                            .findFirst();
                    Optional<UserSecurity> optionalUserSecurity = listUserSecurity
                            .stream()
                            .filter(ue -> ue.getId().equals(uuid))
                            .findFirst();
                    Optional<UserResponseDTO> optionalUserResponseDTO = optionalUserEntityAndSecurityToDTO(optionalUserEntity, optionalUserSecurity);
                    return optionalUserResponseDTO.orElseGet(UserResponseDTO::new);
                }).toList();

        return listUserResponseDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findById(UUID id) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(id);
        Optional<UserSecurity> optionalUserSecurity = userSecurityRepository.findById(id);
        return optionalUserEntityAndSecurityToDTO(optionalUserEntity, optionalUserSecurity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findFirstByEmailIgnoreCase(String email) {
        Optional<UserEntity> optionalUserEntity = userRepository.findFirstByEmailIgnoreCase(email);
        return optionalUserEntityToDTO(optionalUserEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findFirstByEmailIgnoreCaseAndIdNot(String email, UUID id) {
        Optional<UserEntity> optionalUserEntity = userRepository.findFirstByEmailIgnoreCaseAndIdNot(email, id);
        return optionalUserEntityToDTO(optionalUserEntity);
    }

    @Override
    @Transactional
    public UserResponseDTO save(UserRequestDTO userRequestDTO) {

        UserEntity userEntity = peopleMapper.userDTOToEntity(userRequestDTO);
        UserSecurity userSecurity = peopleMapper.userDTOToSecurity(userRequestDTO);

        Optional<RoleSecurity> optionalRoleSecurity = roleSecurityRepository.findByNameIgnoreCase(userSecurity.getRole().getName());
        userSecurity.setRole(optionalRoleSecurity.get());

        log.info("userEntity: " + userEntity);
        log.info("userSecurity: " + userSecurity);

        if (userEntity.getId() == null) {
            UUID id = UUID.randomUUID();
            userEntity.setId(id);
            userSecurity.setId(id);
        }

        UserEntity savedUserEntity = userRepository.save(userEntity);
        UserSecurity savedUserSecurity = userSecurityRepository.save(userSecurity);

        Set<AuthoritySecurity> setSecurityAuthorities = authoritySecurityRepository.findByRoleSecurities_Id(savedUserSecurity.getRole().getId());
        savedUserSecurity.getRole().setSecurityAuthorities(setSecurityAuthorities);
        System.out.println("setSecurityAuthorities: " + setSecurityAuthorities);

        UserResponseDTO savedUserResponseDTO = peopleMapper.userEntityAndSecurityToDTO(savedUserEntity, savedUserSecurity);
        return savedUserResponseDTO;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
        userSecurityRepository.deleteById(id);
    }

    @Override
    public Optional<UserResponseDTO> findFirstByUsernameIgnoreCase(String username) {
        Optional<UserSecurity> optionalUserSecurity = userSecurityRepository.findFirstByUsernameIgnoreCase(username);
        if (optionalUserSecurity.isPresent()) {
            UserSecurity userSecurity = optionalUserSecurity.get();
            return Optional.of(peopleMapper.userSecurityToDTO(userSecurity));
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findFirstByUsernameIgnoreCaseAndIdNot(String username, UUID id) {
        Optional<UserEntity> optionalUserEntity = userRepository.findFirstByUsernameIgnoreCaseAndIdNot(username, id);
        return optionalUserEntityToDTO(optionalUserEntity);
    }

    @Override
    public Optional<PhoneResponseDTO> findByCountryCodeAndCityCodeAndNumber(Integer countryCode, Integer cityCode, Long number) {
        Optional<PhoneEntity> optionalPhoneEntity = phoneRepository.findByCountryCodeAndCityCodeAndNumber(countryCode, cityCode, number);
        return optionalPhoneEntityToDTO(optionalPhoneEntity);
    }

    @Override
    public Optional<PhoneResponseDTO> findByCountryCodeAndCityCodeAndNumberAndUserEntity_IdNot(Integer countryCode, Integer cityCode, Long number, UUID id) {
        Optional<PhoneEntity> optionalPhoneEntity = phoneRepository.findByCountryCodeAndCityCodeAndNumberAndUserEntity_IdNot(countryCode, cityCode, number, id);
        return optionalPhoneEntityToDTO(optionalPhoneEntity);
    }

    @Override
    public Optional<PhoneResponseDTO> findByIdAndUserEntity_Id(UUID id, UUID user_id) {
        Optional<PhoneEntity> optionalPhoneEntity =
                (
                        id != null && user_id != null ?
                                phoneRepository.findByIdAndUserEntity_Id(id, user_id) :
                                Optional.empty()
                );
        return optionalPhoneEntityToDTO(optionalPhoneEntity);
    }

    @Override
    public Optional<RoleDTO> findRoleByNameIgnoreCase(String name) {
        Optional<RoleSecurity> optionalRoleSecurity = roleSecurityRepository.findByNameIgnoreCase(name);
        if (optionalRoleSecurity.isPresent()) {
            RoleSecurity roleSecurity = optionalRoleSecurity.get();
            return Optional.of(peopleMapper.roleSecurityToDTO(roleSecurity));
        }
        return Optional.empty();
    }

    private Optional<UserResponseDTO> optionalUserEntityAndSecurityToDTO(Optional<UserEntity> optionalUserEntity, Optional<UserSecurity> optionalUserSecurity) {
        if (optionalUserEntity.isPresent() && optionalUserSecurity.isPresent()) {
            return peopleMapper.wrapOptional(
                    peopleMapper.userEntityAndSecurityToDTO(optionalUserEntity.get(), optionalUserSecurity.get()));
        }
        if (optionalUserEntity.isPresent()) {
            return optionalUserEntityToDTO(optionalUserEntity);
        }
        if (optionalUserSecurity.isPresent()) {
            return peopleMapper.wrapOptional(peopleMapper.userSecurityToDTO(optionalUserSecurity.get()));
        }
        return Optional.empty();
    }

    private Optional<UserResponseDTO> optionalUserEntityToDTO(Optional<UserEntity> optionalUserEntity) {
        if (optionalUserEntity.isPresent()) {
            UserEntity userEntity = optionalUserEntity.get();
            return Optional.of(peopleMapper.userEntityToDTO(userEntity));
        }
        return Optional.empty();

    }


    private Optional<PhoneResponseDTO> optionalPhoneEntityToDTO(Optional<PhoneEntity> optionalPhoneEntity) {
        if (optionalPhoneEntity.isPresent()) {
            PhoneEntity phoneEntity = optionalPhoneEntity.get();
            return Optional.of(peopleMapper.phoneEntityToDTO(phoneEntity));
        }
        return Optional.empty();
    }
}
