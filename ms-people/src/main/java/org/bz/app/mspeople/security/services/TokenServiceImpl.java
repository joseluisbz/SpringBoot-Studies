package org.bz.app.mspeople.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

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

        JwtBuilder builder = Jwts.builder();
        builder.signWith(secretKey())
                .id(id)
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(issuedAt)
                .expiration(expiration)
        //.contentType(Header.JWT_TYPE) //since 0.12.0 - this constant is never used within the JJWT codebase.
        //.header().add(Header.TYPE, Header.JWT_TYPE) //.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
        ;
        String token = builder
                .signWith(secretKey(), secureDigestAlgorithm)
                .compact();
        return token;
    }

    private SecretKey secretKey() {
        byte[] passwordDecoded = Decoders.BASE64.decode(ENCODED_SECRET_KEY);
        return Keys.hmacShaKeyFor(passwordDecoded);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
