package org.bz.app.mspeople.security.services;

import io.jsonwebtoken.Claims;
import org.bz.app.mspeople.dtos.UserRequestDTO;
import org.bz.app.mspeople.security.dtos.AuthenticationRequestDTO;
import org.bz.app.mspeople.security.dtos.AuthenticationResponseDTO;

import java.util.Map;

public interface TokenService {

    String generateToken(String subject, String id, Map<String, Object> extraClaims);

    Map<String, Object> generateExtraClaims(UserRequestDTO userRequestDTO);

    Claims extractAllClaims(String token);

    AuthenticationResponseDTO login(AuthenticationRequestDTO authenticationRequestDTO);

}
