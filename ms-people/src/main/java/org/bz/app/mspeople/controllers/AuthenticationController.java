package org.bz.app.mspeople.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bz.app.mspeople.security.dtos.AuthenticationRequestDTO;
import org.bz.app.mspeople.security.dtos.AuthenticationResponseDTO;
import org.bz.app.mspeople.security.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/authenticate")
public class AuthenticationController {

    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequestDTO authenticacationRequestDTO) {
        AuthenticationResponseDTO authenticationResponseDTO = tokenService.login(authenticacationRequestDTO);
        return ResponseEntity.ok().body(authenticationResponseDTO);
    }

}
