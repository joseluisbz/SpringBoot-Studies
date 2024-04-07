package org.bz.app.mspeople.security.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class HttpSecurityConfiguration {

    @Autowired
    @Qualifier("customAuthenticationProvider")
    AuthenticationProvider customAuthenticationProvider;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementCustomizer -> sessionManagementCustomizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(customAuthenticationProvider)
                .authorizeHttpRequests(authorizeHttpRequestsCustomizer -> {
                    authorizeHttpRequestsCustomizer
                            .requestMatchers(HttpMethod.POST, "/api/users")
                            .permitAll();
                    authorizeHttpRequestsCustomizer
                            .requestMatchers(HttpMethod.POST, "/api/authenticate")
                            .permitAll();
                    authorizeHttpRequestsCustomizer.anyRequest().permitAll();
                })
                .build();
    }
}
