package org.bz.app.mspeople.security.configurations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bz.app.mspeople.security.configurations.filter.TokenAuthenticationFilter;
import org.bz.app.mspeople.security.entities.RoleSecurity;
import org.bz.app.mspeople.security.entities.UserSecurity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class HttpSecurityConfiguration {

    @Qualifier("customAuthenticationProvider")
    private final AuthenticationProvider customAuthenticationProvider;

    @Qualifier("customPasswordEncoder")
    private final PasswordEncoder customPasswordEncoder;

    private final TokenAuthenticationFilter tokenAuthenticationFilter;


    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        RoleSecurity adminRoleSecurity = RoleSecurity
                .builder()
                .name("ADMIN")
                .build();
        UserSecurity adminUserSecurity = UserSecurity
                .builder()
                .password(customPasswordEncoder.encode("userPassword"))
                .username("admin")
                .role(adminRoleSecurity)
                .build();

        RoleSecurity userRoleSecurity = RoleSecurity
                .builder()
                .name("USER")
                .build();
        UserSecurity userUserSecurity = UserSecurity
                .builder()
                .password(customPasswordEncoder.encode("userPassword"))
                .username("user")
                .role(userRoleSecurity)
                .build();
        return new InMemoryUserDetailsManager(adminUserSecurity, userUserSecurity);
    }


    @Bean("customSecurityFilterChain")
    SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception {
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
                    .requestMatchers(HttpMethod.DELETE, "/api/users/{id}")
                    .hasAnyRole("ADMIN");

            authorizeHttpRequestsCustomizer
                    .requestMatchers(HttpMethod.PUT, "/api/users/{id}")
                    .hasAnyAuthority("EDIT_ALL", "EDIT_SELF");

            authorizeHttpRequestsCustomizer
                    .requestMatchers(HttpMethod.POST, "/api/authenticate")
                    .permitAll();

            authorizeHttpRequestsCustomizer
                    .anyRequest()
                    .authenticated();
        };
    }
}
