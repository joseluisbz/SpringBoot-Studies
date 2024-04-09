package org.bz.app.mspeople.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bz.app.mspeople.dtos.*;
import org.bz.app.mspeople.exceptions.*;
import org.bz.app.mspeople.security.exceptions.NonexistentRoleBadRequestException;
import org.bz.app.mspeople.security.exceptions.RoleEmptyBadRequestException;
import org.bz.app.mspeople.security.exceptions.UsernameEmptyBadRequestException;
import org.bz.app.mspeople.services.UserService;
import org.bz.app.mspeople.validations.UserPasswordValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final UserPasswordValidator userPasswordValidator;

    @PreAuthorize("hasAuthority('READ_ALL')")
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body(userService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> view(@PathVariable("id") UUID id) {
        Optional<UserResponseDTO> optionalStoredUser = userService.findById(id);
        if (optionalStoredUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(optionalStoredUser.get());
    }

    @PreAuthorize("!hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UserRequestDTO userRequestDTO, BindingResult result) {

        userPasswordValidator.validate(userRequestDTO, result);
        throwExceptionIfErrors(result);

        Optional<UserResponseDTO> optionalStoredUserWithEmail = userService.findFirstByEmailIgnoreCase(userRequestDTO.getEmail());

        Optional<UserResponseDTO> optionalStoredUserWithUsername = userService.findFirstByUsernameIgnoreCase(userRequestDTO.getUsername());

        throwExceptionIfEmailOrUsernameExists(optionalStoredUserWithEmail, optionalStoredUserWithUsername);

        throwExceptionIfPhoneIssue(userRequestDTO.getPhones(), null);

        Optional<RoleDTO> optionalStoredRole = userService.findRoleByNameIgnoreCase(userRequestDTO.getRole().getName());
        if (optionalStoredRole.isEmpty()) {
            throw new NonexistentRoleBadRequestException(userRequestDTO.getRole().getName());
        }

        userRequestDTO.setCreated(new Date());
        userRequestDTO.setIsactive(true);

        UserResponseDTO createdUser = userService.save(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PreAuthorize("hasAnyAuthority('EDIT_ALL', 'EDIT_SELF')")
    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@RequestHeader(value = "Authorization") String token,
                                  @Valid @RequestBody UserRequestDTO userRequestDTO, BindingResult result, @PathVariable("id") UUID id) {

        userPasswordValidator.validate(userRequestDTO, result);
        throwExceptionIfErrors(result);

        if (!id.equals(userRequestDTO.getId())) {
            throw new InconsistentBodyIdBadRequestException(id, userRequestDTO.getId());
        }

        Optional<UserResponseDTO> optionalStoredUser = userService.findById(id);
        if (optionalStoredUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<UserResponseDTO> optionalStoredUserWithEmail = userService.findFirstByEmailIgnoreCaseAndIdNot(userRequestDTO.getEmail(), id);

        Optional<UserResponseDTO> optionalStoredUserWithUsername = userService.findFirstByUsernameIgnoreCaseAndIdNot(userRequestDTO.getUsername(), id);

        throwExceptionIfEmailOrUsernameExists(optionalStoredUserWithEmail, optionalStoredUserWithUsername);

        throwExceptionIfPhoneIssue(userRequestDTO.getPhones(), id);

        Optional<RoleDTO> optionalStoredRole = userService.findRoleByNameIgnoreCase(userRequestDTO.getRole().getName());
        if (optionalStoredRole.isEmpty()) {
            throw new NonexistentRoleBadRequestException(userRequestDTO.getRole().getName());
        }
        UserResponseDTO editedUser = optionalStoredUser.get();
        userRequestDTO.setModified(new Date());
        userRequestDTO.setCreated(editedUser.getCreated());
        userRequestDTO.setToken(token);

        try {
            UserResponseDTO updatedUser = userService.save(userRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
        } catch (Exception exp) {
            throw new DefaultBadRequestException(exp.getLocalizedMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    private void throwExceptionIfErrors(BindingResult bindingResult) {

        List<FieldError> listFieldErrors = bindingResult
                .getAllErrors()
                .stream()
                .map(e -> (FieldError) e)
                //.filter(distinctByFirstKey(FieldError::getField))
                .toList();

        Map<String, String> mapErrors = listFieldErrors
                .stream()
                .filter(fe -> fe.getDefaultMessage() != null)
                //.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage))
                .collect(Collectors.toMap(
                                FieldError::getField,
                                FieldError::getDefaultMessage,
                                (oldValue, newValue) -> newValue
                        )
                );

        if (mapErrors.containsKey("password")) {
            String defaultMessage = mapErrors.get("password");
            mapErrors.remove("password");
            throw new PatternPasswordBadRequestException(defaultMessage);
        }
        if (mapErrors.containsKey("email")) {
            String defaultMessage = mapErrors.get("email");
            mapErrors.remove("email");
            throw new PatternEmailBadRequestException(defaultMessage);
        }
        if (mapErrors.containsKey("username")) {
            String defaultMessage = mapErrors.get("username");
            mapErrors.remove("username");
            throw new UsernameEmptyBadRequestException(defaultMessage);
        }
        if (mapErrors.containsKey("role")) {
            String defaultMessage = mapErrors.get("role");
            mapErrors.remove("role");
            throw new RoleEmptyBadRequestException(defaultMessage);
        }
    }

    private static <T> Predicate<T> distinctByFirstKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> firstSeen = new ConcurrentHashMap<>();
        return t -> firstSeen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private void throwExceptionIfEmailOrUsernameExists(Optional<UserResponseDTO> optionalStoredUserWithEmail, Optional<UserResponseDTO> optionalStoredUserWithUsername) {
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
            throw new ExistingMailOrUsernameBadRequestException(userEmail, userUsername, uuidAreEquals);
        }
    }

    private void throwExceptionIfPhoneIssue(Set<PhoneRequestDTO> phones, UUID id) {
        phones.forEach(phone -> {
            Optional<PhoneResponseDTO> optionalPhoneDTO;
            if (phone.getId() != null) {
                optionalPhoneDTO = userService.findByIdAndUserEntity_Id(phone.getId(), id);
                if (optionalPhoneDTO.isEmpty()) {
                    throw new NotAssignablePhoneBadRequestException(phone.getId(), id);
                }
            } else {
                optionalPhoneDTO = userService.findByCountryCodeAndCityCodeAndNumber(
                        phone.getCountryCode(),
                        phone.getCityCode(),
                        phone.getNumber());
                if (optionalPhoneDTO.isPresent()) {
                    PhoneResponseDTO phoneResponseDTO = optionalPhoneDTO.get();
                    throw new ExistingPhoneBadRequestException(phoneResponseDTO.getCountryCode(), phoneResponseDTO.getCityCode(), phoneResponseDTO.getNumber());
                }

            }
        });
    }
}
