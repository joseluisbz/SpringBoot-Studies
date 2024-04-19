package org.bz.app.mspeople.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bz.app.mspeople.dtos.RoleDTO;
import org.bz.app.mspeople.dtos.UserRequestDTO;
import org.bz.app.mspeople.entities.UserEntity;
import org.bz.app.mspeople.exceptions.DefaultInternalServerErrorException;
import org.bz.app.mspeople.mappers.PeopleMapper;
import org.bz.app.mspeople.repositories.UserRepository;
import org.bz.app.mspeople.security.dtos.AuthenticationRequestDTO;
import org.bz.app.mspeople.security.dtos.AuthenticationResponseDTO;
import org.bz.app.mspeople.security.entities.AuthoritySecurity;
import org.bz.app.mspeople.security.entities.RoleSecurity;
import org.bz.app.mspeople.security.entities.UserSecurity;
import org.bz.app.mspeople.security.repositories.AuthoritySecurityRepository;
import org.bz.app.mspeople.security.repositories.RoleSecurityRepository;
import org.bz.app.mspeople.security.repositories.UserSecurityRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.bz.app.mspeople.util.FunctionsUtil.stackFrameFunction;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {

    private final PeopleMapper peopleMapper;

    @Value("${token.minutes.expiration}")
    private Long MINUTES_EXPIRATION;

    @Value("${encoded.secret.key}")
    private String ENCODED_SECRET_KEY;

    @Qualifier("customAuthenticationProvider")
    private final AuthenticationProvider customAuthenticationProvider;

    private final UserSecurityRepository userSecurityRepository;

    private final RoleSecurityRepository roleSecurityRepository;

    private final AuthoritySecurityRepository authoritySecurityRepository;

    private final UserRepository userRepository;

    @Override
    public String generateToken(String subject, String id, Map<String, Object> extraClaims) {
        try {
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
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

    private SecretKey secretKey() {
        byte[] decodedSecretKey = Decoders.BASE64.decode(ENCODED_SECRET_KEY);
        return Keys.hmacShaKeyFor(decodedSecretKey);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception exception) {
            throw new DefaultInternalServerErrorException(exception, this.getClass());
        }
    }

    public AuthenticationResponseDTO login(AuthenticationRequestDTO authenticationRequestDTO) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequestDTO.getUsername(),
                    authenticationRequestDTO.getPassword());
            customAuthenticationProvider.authenticate(authentication);

            Optional<UserSecurity> optionalUserSecurity = userSecurityRepository.findFirstByUsernameIgnoreCase(authenticationRequestDTO.getUsername());

            Optional<UserEntity> optionalUserEntity = userRepository.findFirstByUsernameIgnoreCase(authenticationRequestDTO.getUsername());

            var userRequestDTOBuilder = UserRequestDTO.builder();
            if (optionalUserSecurity.isPresent()) {
                UserSecurity userSecurity = optionalUserSecurity.get();
                userRequestDTOBuilder.role(peopleMapper.roleSecurityToDTO(userSecurity.getRole()));
            }
            if (optionalUserEntity.isPresent()) {
                UserEntity userEntity = optionalUserEntity.get();
                userRequestDTOBuilder.name(userEntity.getName());
            }
            UserRequestDTO userRequestDTO = userRequestDTOBuilder.build();

            Map<String, Object> extraClaims = generateExtraClaims(userRequestDTO);
            String token = generateToken(authenticationRequestDTO.getUsername(), null, extraClaims);

            // Actualizar el Token, y modificar la fecha de loggueo.

            return AuthenticationResponseDTO.builder().token(token).build();
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

    public Map<String, Object> generateExtraClaims(UserRequestDTO userRequestDTO) {
        try {
            Set<AuthoritySecurity> authorities = getAuthoritiesByRole(userRequestDTO.getRole());

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("name", userRequestDTO.getName());
            extraClaims.put("role", userRequestDTO.getRole().getName());
            extraClaims.put("authorities", authorities);
            return extraClaims;
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

    private Set<AuthoritySecurity> getAuthoritiesByRole(RoleDTO role) {
        try {
            Set<AuthoritySecurity> authorities = new HashSet<>();
            Optional<RoleSecurity> optionalRoleSecurity = roleSecurityRepository.findByNameIgnoreCase(role.getName());
            if (optionalRoleSecurity.isPresent()) {
                Set<AuthoritySecurity> setSecurityAuthority = authoritySecurityRepository.findByRoleSecurities_Id(optionalRoleSecurity.get().getId());
                log.trace("setSecurityAuthority: " + setSecurityAuthority);
                authorities = setSecurityAuthority;
            }

            return authorities;
        } catch (Exception exception) {
            log.error("exception: ", exception);
            StackWalker.StackFrame stackFrame = StackWalker.getInstance().walk(stackFrameFunction);
            throw new DefaultInternalServerErrorException(exception, stackFrame);
        }
    }

}
