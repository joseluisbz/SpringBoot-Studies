package org.bz.app.mspeople.security.services;

import java.util.Map;

public interface TokenService {

    String generateToken(String userDetails, String id, Map<String, Object> extraClaims);

}
