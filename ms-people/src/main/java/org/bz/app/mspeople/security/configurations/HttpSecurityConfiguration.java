package org.bz.app.mspeople.security.configurations;

import lombok.extern.slf4j.Slf4j;
import org.bz.app.mspeople.security.configurations.filter.TokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class HttpSecurityConfiguration {

    @Autowired
    @Qualifier("customAuthenticationProvider")
    AuthenticationProvider customAuthenticationProvider;

    @Autowired
    TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SecurityFilterChain defaultSecurityFilterChain = http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementCustomizer ->
                        sessionManagementCustomizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(customAuthenticationProvider)
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(getAuthorizationManagerRequestByMethod())
                .build();
        log.info("defaultSecurityFilterChain: " + defaultSecurityFilterChain);
        return defaultSecurityFilterChain;
    }

    private static Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>
    getAuthorizationManagerRequestByCoincidence() {
        return authorizeHttpRequestsCustomizer -> {

            authorizeHttpRequestsCustomizer
                    .requestMatchers(HttpMethod.POST, "/api/users")
                    .permitAll();

/*
            authorizeHttpRequestsCustomizer
                    .requestMatchers(HttpMethod.POST, "/api/users")
                    .anonymous()
                    .anyRequest()
                    .permitAll();
*/
/*
            authorizeHttpRequestsCustomizer
                    .requestMatchers(HttpMethod.POST, "/api/users")
                    .hasAnyRole("ADMIN", "USER")
                    .anyRequest()
                    .denyAll();
*/
            authorizeHttpRequestsCustomizer
                    .requestMatchers(HttpMethod.GET, "/api/users")
                    .hasAnyRole("ADMIN");

/*
            authorizeHttpRequestsCustomizer
                    .requestMatchers("/api/users/{id}")
                    .hasAnyAuthority("READ_ALL", "READ_SELF")
                    .anyRequest()
                    .permitAll();
*/
            authorizeHttpRequestsCustomizer
                    .requestMatchers("/api/users/{id}")
                    .hasAnyAuthority("READ_ALL", "READ_SELF");

            authorizeHttpRequestsCustomizer
                    .requestMatchers(HttpMethod.POST, "/api/authenticate")
                    .permitAll();

            authorizeHttpRequestsCustomizer
                    .anyRequest()
                    .authenticated();
        };
    }

    private static Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>
    getAuthorizationManagerRequestByMethod() {
        return authorizeHttpRequestsCustomizer -> {

            authorizeHttpRequestsCustomizer
                    .requestMatchers(HttpMethod.POST, "/api/users")
                    .permitAll();

            authorizeHttpRequestsCustomizer
                    .requestMatchers(HttpMethod.POST, "/api/authenticate")
                    .permitAll();

            authorizeHttpRequestsCustomizer
                    .anyRequest()
                    .authenticated();
        };
    }
}
