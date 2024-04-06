package org.bz.app.mspeople.security.services;

import io.jsonwebtoken.Claims;

import java.util.Map;

public interface TokenService {

    String generateToken(String subject, String id, Map<String, Object> extraClaims);

    Claims extractAllClaims(String token);

}
