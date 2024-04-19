package org.bz.app.mspeople.security.components.filters;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bz.app.mspeople.security.entities.AuthoritySecurity;
import org.bz.app.mspeople.security.services.TokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        Claims claims = tokenService.extractAllClaims(token);
        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        Object objectList = claims.get("authorities", Object.class);
        List<AuthoritySecurity> authorities = ((List<Object>) objectList)
                .stream()
                .map(o -> (LinkedHashMap<String, Object>) o)
                .map(l -> AuthoritySecurity.builder()
                        .id(UUID.fromString((String) l.get("id")))
                        .authority((String) l.get("authority"))
                        .build()
                ).collect(Collectors.toList());

        AuthoritySecurity roleAuthoritySecurity = AuthoritySecurity
                .builder()
                .id(UUID.randomUUID())
                .authority("ROLE_" + role)
                .build();

        authorities.add(roleAuthoritySecurity);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
