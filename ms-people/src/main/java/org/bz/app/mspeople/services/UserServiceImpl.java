package org.bz.app.mspeople.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bz.app.mspeople.dtos.UserDTO;
import org.bz.app.mspeople.entities.Phone;
import org.bz.app.mspeople.entities.User;
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
        Iterable<User> iterableUser = userRepository.findAll();
        Stream<User> streamUser =
                StreamSupport.stream(iterableUser.spliterator(), false);

        streamUser.forEach(u -> {
            Set<Phone> setPhones = phoneRepository.findByUser_Id(u.getId());
            log.info("setPhones: " + setPhones);
            u.setPhones(setPhones);
            log.info("u: " + u);
        });

        List<User> listUser = peopleMapper.castIterableToList(iterableUser);
        return peopleMapper.userEntityToDTO(listUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findById(UUID id) {
        if (userRepository.findById(id).isPresent()) {
            User user = userRepository.findById(id).get();
            return Optional.of(peopleMapper.userEntityToDTO(user));
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findByEmail(String email) {
        if (userRepository.findFirstByEmail(email.toLowerCase()).isPresent()) {
            User user = userRepository.findFirstByEmail(email.toLowerCase()).get();
            return Optional.of(peopleMapper.userEntityToDTO(user));
        }
        return Optional.empty();
    }

    @Override
    public List<UserDTO> findByEmailAndIdNot(String email, UUID id) {
        List<User> listUser = userRepository.findByEmailAndIdNot(email.toLowerCase(), id);
        return peopleMapper.userEntityToDTO(listUser);
    }

    @Override
    @Transactional
    public UserDTO save(UserDTO userDTO) {
        User user = peopleMapper.userDTOToEntity(userDTO);
        user.getPhones().forEach(phone -> phone.setUser(user));
        user.setEmail(user.getEmail().toLowerCase());

        User savedUser = userRepository.save(user);

        UserDTO savedUserDTO = peopleMapper.userEntityToDTO(savedUser);
        savedUserDTO.getPhones().forEach(phone -> phone.setUser(savedUserDTO));
        return savedUserDTO;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
}
