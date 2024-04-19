package org.bz.app.mspeople.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bz.app.mspeople.dtos.PhoneResponseDTO;
import org.bz.app.mspeople.dtos.RoleDTO;
import org.bz.app.mspeople.dtos.UserRequestDTO;
import org.bz.app.mspeople.dtos.UserResponseDTO;
import org.bz.app.mspeople.entities.PhoneEntity;
import org.bz.app.mspeople.entities.UserEntity;
import org.bz.app.mspeople.exceptions.DefaultInternalServerErrorException;
import org.bz.app.mspeople.mappers.PeopleMapper;
import org.bz.app.mspeople.repositories.PhoneRepository;
import org.bz.app.mspeople.repositories.UserRepository;
import org.bz.app.mspeople.security.entities.AuthoritySecurity;
import org.bz.app.mspeople.security.entities.RoleSecurity;
import org.bz.app.mspeople.security.entities.UserSecurity;
import org.bz.app.mspeople.security.repositories.AuthoritySecurityRepository;
import org.bz.app.mspeople.security.repositories.RoleSecurityRepository;
import org.bz.app.mspeople.security.repositories.UserSecurityRepository;
import org.bz.app.mspeople.security.services.TokenService;
import org.bz.app.mspeople.util.JsonMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.StreamSupport;

import static org.bz.app.mspeople.util.FunctionsUtil.stackFrameFunction;

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

    private final TokenService tokenService;

    @Autowired
    @Qualifier("customPasswordEncoder")
    private PasswordEncoder customPasswordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Iterable<UserResponseDTO> findAll() {
        try {
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

            log.info("listUserResponseDTO: " + JsonMapperUtil.writeValueAsString(listUserResponseDTO));
            return listUserResponseDTO;
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findById(UUID id) {
        try {
            Optional<UserEntity> optionalUserEntity = userRepository.findById(id);
            Optional<UserSecurity> optionalUserSecurity = userSecurityRepository.findById(id);
            return optionalUserEntityAndSecurityToDTO(optionalUserEntity, optionalUserSecurity);
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findFirstByEmailIgnoreCase(String email) {
        try {
            Optional<UserEntity> optionalUserEntity = userRepository.findFirstByEmailIgnoreCase(email);
            return optionalUserEntityToDTO(optionalUserEntity);
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findFirstByEmailIgnoreCaseAndIdNot(String email, UUID id) {
        try {
            Optional<UserEntity> optionalUserEntity = userRepository.findFirstByEmailIgnoreCaseAndIdNot(email, id);
            return optionalUserEntityToDTO(optionalUserEntity);
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

    @Override
    @Transactional
    public UserResponseDTO save(UserRequestDTO userRequestDTO) {
        try {
            log.info("userRequestDTO: " + JsonMapperUtil.writeValueAsString(userRequestDTO));

            Map<String, Object> extraClaims = tokenService.generateExtraClaims(userRequestDTO);
            String token = tokenService.generateToken(userRequestDTO.getUsername(), null, extraClaims);

            log.info("Claims: " + JsonMapperUtil.writeValueAsString(tokenService.extractAllClaims(token)));
            userRequestDTO.setToken(token);

            String encodedPassword = customPasswordEncoder.encode(userRequestDTO.getPassword());
            userRequestDTO.setPassword(encodedPassword);

            UserEntity userEntity = peopleMapper.userDTOToEntity(userRequestDTO);
            UserSecurity userSecurity = peopleMapper.userDTOToSecurity(userRequestDTO);

            Optional<RoleSecurity> optionalRoleSecurity = roleSecurityRepository.findByNameIgnoreCase(userSecurity.getRole().getName());
            optionalRoleSecurity.ifPresent(userSecurity::setRole);

            if (userEntity.getId() == null) {
                UUID id = UUID.randomUUID();
                userEntity.setId(id);
                userSecurity.setId(id);
            }

            UserEntity savedUserEntity = userRepository.save(userEntity);
            UserSecurity savedUserSecurity = userSecurityRepository.save(userSecurity);

            Set<AuthoritySecurity> setSecurityAuthority = authoritySecurityRepository.findByRoleSecurities_Id(savedUserSecurity.getRole().getId());
            savedUserSecurity.getRole().setAuthoritySecurities(setSecurityAuthority);

            UserResponseDTO userResponseDTO = peopleMapper.userEntityAndSecurityToDTO(savedUserEntity, savedUserSecurity);
            log.info("userResponseDTO: " + JsonMapperUtil.writeValueAsString(userResponseDTO));
            return userResponseDTO;
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        try {
            userRepository.deleteById(id);
            userSecurityRepository.deleteById(id);
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

    @Override
    public Optional<UserResponseDTO> findFirstByUsernameIgnoreCase(String username) {
        try {
            Optional<UserSecurity> optionalUserSecurity = userSecurityRepository.findFirstByUsernameIgnoreCase(username);
            if (optionalUserSecurity.isPresent()) {
                UserSecurity userSecurity = optionalUserSecurity.get();
                return Optional.of(peopleMapper.userSecurityToDTO(userSecurity));
            }
            return Optional.empty();
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findFirstByUsernameIgnoreCaseAndIdNot(String username, UUID id) {
        try {
            Optional<UserEntity> optionalUserEntity = userRepository.findFirstByUsernameIgnoreCaseAndIdNot(username, id);
            return optionalUserEntityToDTO(optionalUserEntity);
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

    @Override
    public Optional<PhoneResponseDTO> findByCountryCodeAndCityCodeAndNumber(Integer countryCode, Integer cityCode, Long number) {
        try {
            Optional<PhoneEntity> optionalPhoneEntity = phoneRepository.findByCountryCodeAndCityCodeAndNumber(countryCode, cityCode, number);
            return optionalPhoneEntityToDTO(optionalPhoneEntity);
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

    @Override
    public Optional<PhoneResponseDTO> findByIdAndUserEntity_Id(UUID id, UUID user_id) {
        try {
            Optional<PhoneEntity> optionalPhoneEntity =
                    (
                            id != null && user_id != null ?
                                    phoneRepository.findByIdAndUserEntity_Id(id, user_id) :
                                    Optional.empty()
                    );
            return optionalPhoneEntityToDTO(optionalPhoneEntity);
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

    @Override
    public Optional<RoleDTO> findRoleByNameIgnoreCase(String name) {
        try {
            Optional<RoleSecurity> optionalRoleSecurity = roleSecurityRepository.findByNameIgnoreCase(name);
            if (optionalRoleSecurity.isPresent()) {
                RoleSecurity roleSecurity = optionalRoleSecurity.get();
                return Optional.of(peopleMapper.roleSecurityToDTO(roleSecurity));
            }
            return Optional.empty();
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
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
        try {
            if (optionalUserEntity.isPresent()) {
                UserEntity userEntity = optionalUserEntity.get();
                return Optional.of(peopleMapper.userEntityToDTO(userEntity));
            }
            return Optional.empty();
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }


    private Optional<PhoneResponseDTO> optionalPhoneEntityToDTO(Optional<PhoneEntity> optionalPhoneEntity) {
        try {
            if (optionalPhoneEntity.isPresent()) {
                PhoneEntity phoneEntity = optionalPhoneEntity.get();
                return Optional.of(peopleMapper.phoneEntityToDTO(phoneEntity));
            }
            return Optional.empty();
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }
}
