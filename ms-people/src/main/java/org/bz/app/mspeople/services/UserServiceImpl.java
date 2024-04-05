package org.bz.app.mspeople.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bz.app.mspeople.dtos.PhoneDTO;
import org.bz.app.mspeople.dtos.RoleDTO;
import org.bz.app.mspeople.dtos.UserDTO;
import org.bz.app.mspeople.entities.PhoneEntity;
import org.bz.app.mspeople.entities.UserEntity;
import org.bz.app.mspeople.mapper.PeopleMapper;
import org.bz.app.mspeople.repositories.PhoneRepository;
import org.bz.app.mspeople.repositories.UserRepository;
import org.bz.app.mspeople.security.entities.RoleSecurity;
import org.bz.app.mspeople.security.entities.UserSecurity;
import org.bz.app.mspeople.security.repositories.RoleSecurityRepository;
import org.bz.app.mspeople.security.repositories.UserSecurityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
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

    @Override
    @Transactional(readOnly = true)
    public Iterable<UserDTO> findAll() {
        Iterable<UserEntity> iterableUser = userRepository.findAll();
        Stream<UserEntity> streamUser =
                StreamSupport.stream(iterableUser.spliterator(), false);

        streamUser.forEach(u -> {
            Set<PhoneEntity> setPhoneEntities = phoneRepository.findByUserEntity_Id(u.getId());
            log.info("setPhones: " + setPhoneEntities);
            u.setPhoneEntities(setPhoneEntities);
            log.info("u: " + u);
        });

        List<UserEntity> listUserEntity = peopleMapper.castIterableToList(iterableUser);
        return peopleMapper.userEntityToDTO(listUserEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findById(UUID id) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(id);
        return optionalUserEntityToDTO(optionalUserEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findFirstByEmailIgnoreCase(String email) {
        Optional<UserEntity> optionalUserEntity = userRepository.findFirstByEmailIgnoreCase(email);
        return optionalUserEntityToDTO(optionalUserEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findFirstByEmailIgnoreCaseAndIdNot(String email, UUID id) {
        Optional<UserEntity> optionalUserEntity = userRepository.findFirstByEmailIgnoreCaseAndIdNot(email, id);
        return optionalUserEntityToDTO(optionalUserEntity);
    }

    @Override
    @Transactional
    public UserDTO save(UserDTO userDTO) {

        UserEntity userEntity = peopleMapper.userDTOToEntity(userDTO);
        UserSecurity userSecurity = peopleMapper.userDTOToSecurity(userDTO);

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

        UserDTO savedUserDTO = peopleMapper.userEntityToDTO(savedUserEntity);
        return savedUserDTO;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
        userSecurityRepository.deleteById(id);
    }

    @Override
    public Optional<UserDTO> findFirstByUsernameIgnoreCase(String username) {
        Optional<UserSecurity> optionalUserSecurity = userSecurityRepository.findFirstByUsernameIgnoreCase(username);
        if (optionalUserSecurity.isPresent()) {
            UserSecurity userSecurity = optionalUserSecurity.get();
            return Optional.of(peopleMapper.userSecurityToDTO(userSecurity));
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findFirstByUsernameIgnoreCaseAndIdNot(String username, UUID id) {
        Optional<UserEntity> optionalUserEntity = userRepository.findFirstByUsernameIgnoreCaseAndIdNot(username, id);
        return optionalUserEntityToDTO(optionalUserEntity);
    }

    @Override
    public Optional<PhoneDTO> findByCountryCodeAndCityCodeAndNumber(Integer countryCode, Integer cityCode, Long number) {
        Optional<PhoneEntity> optionalPhoneEntity = phoneRepository.findByCountryCodeAndCityCodeAndNumber(countryCode, cityCode, number);
        return optionalPhoneEntityToDTO(optionalPhoneEntity);
    }

    @Override
    public Optional<PhoneDTO> findByCountryCodeAndCityCodeAndNumberAndUserEntity_IdNot(Integer countryCode, Integer cityCode, Long number, UUID id) {
        Optional<PhoneEntity> optionalPhoneEntity = phoneRepository.findByCountryCodeAndCityCodeAndNumberAndUserEntity_IdNot(countryCode, cityCode, number, id);
        return optionalPhoneEntityToDTO(optionalPhoneEntity);
    }

    @Override
    public Optional<PhoneDTO> findByIdAndUserEntity_Id(UUID id, UUID user_id) {
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

    private Optional<UserDTO> optionalUserEntityToDTO(Optional<UserEntity> optionalUserEntity) {
        if (optionalUserEntity.isPresent()) {
            UserEntity userEntity = optionalUserEntity.get();
            return Optional.of(peopleMapper.userEntityToDTO(userEntity));
        }
        return Optional.empty();

    }

    private Optional<PhoneDTO> optionalPhoneEntityToDTO(Optional<PhoneEntity> optionalPhoneEntity) {
        if (optionalPhoneEntity.isPresent()) {
            PhoneEntity phoneEntity = optionalPhoneEntity.get();
            return Optional.of(peopleMapper.phoneEntityToDTO(phoneEntity));
        }
        return Optional.empty();
    }
}
