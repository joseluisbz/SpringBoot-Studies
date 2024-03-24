package org.bz.app.mspeople.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bz.app.mspeople.entities.User;
import org.bz.app.mspeople.repositories.PhoneRepository;
import org.bz.app.mspeople.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PhoneRepository phoneRepository;

    @Override
    @Transactional(readOnly = true)
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findFirstByEmail(email.toLowerCase());
    }

    @Override
    public List<User> findByEmailAndIdNot(String email, Long id) {
        return userRepository.findByEmailAndIdNot(email.toLowerCase(), id);
    }

    @Override
    @Transactional
    public User save(User user) {
        user.setEmail(user.getEmail().toLowerCase());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

}
