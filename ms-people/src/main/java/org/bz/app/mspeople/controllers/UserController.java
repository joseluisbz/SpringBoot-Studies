package org.bz.app.mspeople.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bz.app.mspeople.entities.User;
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
    public ResponseEntity<?> view(@PathVariable Long id) {
        Optional<User> optionalStoredUser = userService.findById(id);
        if (!optionalStoredUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(optionalStoredUser.get());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(value = "Authorization") String token,
                                    @Valid @RequestBody User user, BindingResult result) {

        userPasswordValidator.validate(user, result);
        throwExceptionIfErrors(result);

        Optional<User> optionalStoredUser = userService.findByEmail(user.getEmail());
        if (optionalStoredUser.isPresent()) {
            throw new ExistingMailException(user.getEmail());
        }

        user.setIsactive(true);
        user.setToken(token);

        User createdUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@RequestHeader(value = "Authorization") String token,
                                  @Valid @RequestBody User user, BindingResult result, @PathVariable Long id) {

        userPasswordValidator.validate(user, result);
        throwExceptionIfErrors(result);

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            boolean emailUsed = !userService.findByEmailAndIdNot(user.getEmail(), id).isEmpty();
            if (emailUsed) {
                throw new ExistingMailException(user.getEmail());
            }
        }

        Optional<User> optionalStoredUser = userService.findById(id);
        if (optionalStoredUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User editedUser = optionalStoredUser.get();
        editedUser.setName(user.getName());
        editedUser.setEmail(user.getEmail());
        editedUser.setPassword(user.getPassword());
        editedUser.setPhones(user.getPhones());
        editedUser.setModified(new Date());
        editedUser.setIsactive(user.isIsactive());
        editedUser.setToken(token);
        try {
            User updatedUser = userService.save(editedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
        } catch (Exception exp) {
            throw new DefaultException(exp.getLocalizedMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
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
