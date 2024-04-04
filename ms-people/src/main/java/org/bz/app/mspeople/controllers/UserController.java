package org.bz.app.mspeople.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bz.app.mspeople.dtos.PhoneDTO;
import org.bz.app.mspeople.dtos.RoleDTO;
import org.bz.app.mspeople.dtos.UserDTO;
import org.bz.app.mspeople.exceptions.*;
import org.bz.app.mspeople.security.exceptions.NonexistentRoleException;
import org.bz.app.mspeople.security.exceptions.RoleEmptyException;
import org.bz.app.mspeople.security.exceptions.UsernameEmptyException;
import org.bz.app.mspeople.services.UserService;
import org.bz.app.mspeople.validations.UserPasswordValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final UserPasswordValidator userPasswordValidator;

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> view(@PathVariable("id") UUID id) {
        Optional<UserDTO> optionalStoredUser = userService.findById(id);
        if (optionalStoredUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(optionalStoredUser.get());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UserDTO userDTO, BindingResult result, @RequestHeader(value = "Authorization") String token) {

        userPasswordValidator.validate(userDTO, result);
        throwExceptionIfErrors(result);

        Optional<UserDTO> optionalStoredUserWithEmail = userService.findFirstByEmailIgnoreCase(userDTO.getEmail());

        Optional<UserDTO> optionalStoredUserWithUsername = userService.findFirstByUsernameIgnoreCase(userDTO.getUsername());

        throwExceptionIfEmailOrUsernameExists(optionalStoredUserWithEmail, optionalStoredUserWithUsername);

        userDTO.getPhones().forEach(phone -> {
            Optional<PhoneDTO> optionalPhoneDTO = userService.findByCountryCodeAndCityCodeAndNumber(
                    phone.getCountryCode(),
                    phone.getCityCode(),
                    phone.getNumber());
            if (optionalPhoneDTO.isPresent()) {
                PhoneDTO phoneDTO = optionalPhoneDTO.get();
                throw new ExistingPhoneException(phoneDTO.getCountryCode(), phoneDTO.getCityCode(), phoneDTO.getNumber());
            }
        });

        Optional<RoleDTO> optionalStoredRole = userService.findRoleByNameIgnoreCase(userDTO.getRole().getName());
        if (optionalStoredRole.isEmpty()) {
            throw new NonexistentRoleException(userDTO.getRole().getName());
        }

        userDTO.setCreated(new Date());
        userDTO.setIsactive(true);
        userDTO.setToken(token);

        UserDTO createdUser = userService.save(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@RequestHeader(value = "Authorization") String token,
                                  @Valid @RequestBody UserDTO userDTO, BindingResult result, @PathVariable("id") UUID id) {

        userPasswordValidator.validate(userDTO, result);
        throwExceptionIfErrors(result);

        if (!id.equals(userDTO.getId())) {
            throw new InconsistentBodyIdException(id, userDTO.getId());
        }

        Optional<UserDTO> optionalStoredUser = userService.findById(id);
        if (optionalStoredUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<UserDTO> optionalStoredUserWithEmail = userService.findFirstByEmailIgnoreCaseAndIdNot(userDTO.getEmail(), id);

        Optional<UserDTO> optionalStoredUserWithUsername = userService.findFirstByUsernameIgnoreCaseAndIdNot(userDTO.getUsername(), id);

        throwExceptionIfEmailOrUsernameExists(optionalStoredUserWithEmail, optionalStoredUserWithUsername);

        userDTO.getPhones().forEach(phone -> {
            Optional<PhoneDTO> optionalPhoneDTO = userService.findByCountryCodeAndCityCodeAndNumberAndUserEntity_IdNot(
                    phone.getCountryCode(),
                    phone.getCityCode(),
                    phone.getNumber(),
                    userDTO.getId());
            if (optionalPhoneDTO.isPresent()) {
                PhoneDTO phoneDTO = optionalPhoneDTO.get();
                throw new ExistingPhoneException(phoneDTO.getCountryCode(), phoneDTO.getCityCode(), phoneDTO.getNumber());
            }
        });

        Optional<RoleDTO> optionalStoredRole = userService.findRoleByNameIgnoreCase(userDTO.getRole().getName());
        if (optionalStoredRole.isEmpty()) {
            throw new NonexistentRoleException(userDTO.getRole().getName());
        }

        UserDTO editedUser = optionalStoredUser.get();
        editedUser.setName(userDTO.getName());
        editedUser.setEmail(userDTO.getEmail());
        editedUser.setPassword(userDTO.getPassword());
        editedUser.setPhones(userDTO.getPhones());
        editedUser.setModified(new Date());
        editedUser.setIsactive(userDTO.isIsactive());
        editedUser.setToken(token);
        try {
            UserDTO updatedUser = userService.save(editedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
        } catch (Exception exp) {
            throw new DefaultException(exp.getLocalizedMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void throwExceptionIfErrors(BindingResult bindingResult) {
        Map<String, String> mapErrors = bindingResult
                .getAllErrors()
                .stream()
                .map(e -> (FieldError) e)
                .filter(fe -> fe.getDefaultMessage() != null)
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        if (mapErrors.containsKey("password")) {
            String defaultMessage = mapErrors.get("password");
            mapErrors.remove("password");
            throw new PatternPasswordException(defaultMessage);
        }
        if (mapErrors.containsKey("email")) {
            String defaultMessage = mapErrors.get("email");
            mapErrors.remove("email");
            throw new PatternEmailException(defaultMessage);
        }
        if (mapErrors.containsKey("username")) {
            String defaultMessage = mapErrors.get("username");
            mapErrors.remove("username");
            throw new UsernameEmptyException(defaultMessage);
        }
        if (mapErrors.containsKey("role")) {
            String defaultMessage = mapErrors.get("role");
            mapErrors.remove("role");
            throw new RoleEmptyException(defaultMessage);
        }
    }

    private void throwExceptionIfEmailOrUsernameExists(Optional<UserDTO> optionalStoredUserWithEmail, Optional<UserDTO> optionalStoredUserWithUsername) {
        UUID uuidUserWithEmail = null;
        String userEmail = null;
        if (optionalStoredUserWithEmail.isPresent()) {
            uuidUserWithEmail = optionalStoredUserWithEmail.get().getId();
            userEmail = optionalStoredUserWithEmail.get().getEmail();
        }
        UUID uuidUserWithUsername = null;
        String userUsername = null;
        if (optionalStoredUserWithUsername.isPresent()) {
            uuidUserWithUsername = optionalStoredUserWithUsername.get().getId();
            userUsername = optionalStoredUserWithUsername.get().getUsername();
        }

        if (uuidUserWithEmail != null || uuidUserWithUsername != null) {
            boolean uuidAreEquals = false;
            if (uuidUserWithEmail != null && uuidUserWithUsername != null) {
                uuidAreEquals = uuidUserWithEmail.toString().equals(uuidUserWithUsername.toString());
            }
            throw new ExistingMailOrUsernameException(userEmail, userUsername, uuidAreEquals);
        }
    }
}
