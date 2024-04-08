package org.bz.app.mspeople.security.configurations.filter;

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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

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

        List<AuthoritySecurity> authorities = new ArrayList<>();
        Object objectList = claims.get("authorities", Object.class);
        if (objectList instanceof List<?>) {
            for (Object object : (List<?>) objectList) {
                if (object instanceof LinkedHashMap<?, ?> lhm) {
                    authorities.add(AuthoritySecurity.builder()
                            .id(UUID.fromString((String) lhm.get("id")))
                            .authority((String) lhm.get("authority"))
                            .build());
                }
            }
        }

        /*
        List<AuthoritySecurity> authorities1 = ((List<Object>)objectList)
                .stream()
                .map(o -> (LinkedHashMap<String, Object>)o)
                .map(l -> AuthoritySecurity.builder()
                        .id(UUID.fromString((String)l.get("id")))
                        .authority((String)l.get("authority"))
                        .build()
                ).toList();
        */
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
