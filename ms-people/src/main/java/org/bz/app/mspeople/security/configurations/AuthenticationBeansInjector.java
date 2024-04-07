package org.bz.app.mspeople.security.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthenticationBeansInjector {

    @Autowired
    @Qualifier("customUserDetailsService")
    UserDetailsService customUserDetailsService;

    @Bean("customAuthenticationManager")
    public AuthenticationManager customAuthenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean("customAuthenticationProvider")
    public AuthenticationProvider customAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(customPasswordEncoder());
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean("customPasswordEncoder")
    public PasswordEncoder customPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        UserDetails userDetails = User.withUsername("joseluis")
                .password(passwordEncoder().encode("secret"))
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(userDetails);
    }
    */
}
