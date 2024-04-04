package org.bz.app.mspeople.security.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bz.app.mspeople.security.repositories.UserSecurityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserSecurityRepository userSecurityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userSecurityRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(
                () -> new UsernameNotFoundException(username)
        );
    }
}
