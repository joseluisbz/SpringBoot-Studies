package org.bz.app.mspeople.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bz.app.mspeople.dtos.UserDTO;
import org.bz.app.mspeople.entities.PhoneEntity;
import org.bz.app.mspeople.entities.UserEntity;
import org.bz.app.mspeople.mapper.PeopleMapper;
import org.bz.app.mspeople.repositories.PhoneRepository;
import org.bz.app.mspeople.repositories.UserRepository;
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
        if (userRepository.findById(id).isPresent()) {
            UserEntity userEntity = userRepository.findById(id).get();
            return Optional.of(peopleMapper.userEntityToDTO(userEntity));
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findFirstByEmailIgnoreCase(String email) {
        if (userRepository.findFirstByEmailIgnoreCase(email).isPresent()) {
            UserEntity userEntity = userRepository.findFirstByEmailIgnoreCase(email).get();
            return Optional.of(peopleMapper.userEntityToDTO(userEntity));
        }
        return Optional.empty();
    }

    @Override
    public List<UserDTO> findByEmailIgnoreCaseAndIdNot(String email, UUID id) {
        List<UserEntity> listUserEntity = userRepository.findByEmailIgnoreCaseAndIdNot(email, id);
        return peopleMapper.userEntityToDTO(listUserEntity);
    }

    @Override
    @Transactional
    public UserDTO save(UserDTO userDTO) {
        UserEntity userEntity = peopleMapper.userDTOToEntity(userDTO);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        UserDTO savedUserDTO = peopleMapper.userEntityToDTO(savedUserEntity);
        return savedUserDTO;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
}
