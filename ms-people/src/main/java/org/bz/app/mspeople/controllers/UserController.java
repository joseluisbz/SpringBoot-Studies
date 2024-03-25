package org.bz.app.mspeople.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bz.app.mspeople.dtos.UserDTO;
import org.bz.app.mspeople.exceptions.DefaultException;
import org.bz.app.mspeople.exceptions.ExistingMailException;
import org.bz.app.mspeople.exceptions.PatternEmailException;
import org.bz.app.mspeople.exceptions.PatternPasswordException;
import org.bz.app.mspeople.services.UserService;
import org.bz.app.mspeople.validations.UserPasswordValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

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
    public ResponseEntity<?> view(@PathVariable("id") Long id) {
        Optional<UserDTO> optionalStoredUser = userService.findById(id);
        if (optionalStoredUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(optionalStoredUser.get());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(value = "Authorization") String token,
                                    @Valid @RequestBody UserDTO userDTO, BindingResult result) {

        userPasswordValidator.validate(userDTO, result);
        throwExceptionIfErrors(result);

        Optional<UserDTO> optionalStoredUser = userService.findByEmail(userDTO.getEmail());
        if (optionalStoredUser.isPresent()) {
            throw new ExistingMailException(userDTO.getEmail());
        }

        userDTO.setIsactive(true);
        userDTO.setToken(token);

        UserDTO createdUser = userService.save(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@RequestHeader(value = "Authorization") String token,
                                  @Valid @RequestBody UserDTO userDTO, BindingResult result, @PathVariable("id") Long id) {

        userPasswordValidator.validate(userDTO, result);
        throwExceptionIfErrors(result);

        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            boolean emailUsed = !userService.findByEmailAndIdNot(userDTO.getEmail(), id).isEmpty();
            if (emailUsed) {
                throw new ExistingMailException(userDTO.getEmail());
            }
        }

        Optional<UserDTO> optionalStoredUser = userService.findById(id);
        if (optionalStoredUser.isEmpty()) {
            return ResponseEntity.notFound().build();
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
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void throwExceptionIfErrors(BindingResult result) {
        if (result.hasErrors()) {
            FieldError passwordFieldError = result.getAllErrors()
                    .stream().map(e -> (FieldError) e)
                    .filter(f -> f.getField().equals("password"))
                    .findFirst().orElse(null);

            if (passwordFieldError != null) {
                throw new PatternPasswordException(passwordFieldError.getDefaultMessage());
            }
            FieldError emailFieldError = result.getAllErrors()
                    .stream().map(e -> (FieldError) e)
                    .filter(f -> f.getField().equals("email"))
                    .findFirst().orElse(null);

            if (emailFieldError != null) {
                throw new PatternEmailException(emailFieldError.getDefaultMessage());
            }
        }
    }
}
