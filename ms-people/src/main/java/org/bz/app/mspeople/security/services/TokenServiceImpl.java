package org.bz.app.mspeople.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    @Value("${token.minutes.expiration}")
    private Long MINUTES_EXPIRATION;

    @Value("${encoded.secret.key}")
    private String ENCODED_SECRET_KEY;

    @Override
    public String generateToken(String subject, String id, Map<String, Object> extraClaims) {

        LocalDateTime localDateTimeNow = LocalDateTime.now();
        LocalDateTime localDateTimeAfter = localDateTimeNow.plusMinutes(MINUTES_EXPIRATION);
        Date issuedAt = Date.from(localDateTimeNow.atZone(ZoneId.systemDefault()).toInstant());
        Date expiration = Date.from(localDateTimeAfter.atZone(ZoneId.systemDefault()).toInstant());

        SecureDigestAlgorithm<SecretKey, SecretKey> secureDigestAlgorithm = Jwts.SIG.HS512;

        return Jwts
                .builder()
                .signWith(secretKey(), secureDigestAlgorithm)
                .id(id)
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .compact();
    }

    private SecretKey secretKey() {
        byte[] decodedSecretKey = Decoders.BASE64.decode(ENCODED_SECRET_KEY);
        return Keys.hmacShaKeyFor(decodedSecretKey);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
