package org.bz.app.mspeople.security.services

import org.bz.app.mspeople.generator.UserSecurityGenerator
import org.bz.app.mspeople.security.repositories.UserSecurityRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification

class UserDetailsServiceSpec extends Specification {
    private UserDetailsService userDetailsService

    private userSecurityRepository = Mock(UserSecurityRepository)

    void setup() {
        userDetailsService = new UserDetailsServiceImpl(userSecurityRepository)
    }

    def "LoadUserByUsername"() {
        given:
        def userUserSecurity = UserSecurityGenerator.userGenerate()
        userSecurityRepository.findFirstByUsernameIgnoreCase(_ as String) >> Optional.of(userUserSecurity)

        when:
        def userDetails = userDetailsService.loadUserByUsername(userUserSecurity.getUsername())

        then:
        userDetails != null
        userDetails.getUsername() == userUserSecurity.getUsername()
    }

    def "LoadUserByUsername_UsernameNotFoundException"() {
        given:
        def userUserSecurity = UserSecurityGenerator.userGenerate()
        userSecurityRepository.findFirstByUsernameIgnoreCase(_ as String) >> {
            throw new UsernameNotFoundException(userUserSecurity.getUsername())
        }

        when:
        def userDetails = userDetailsService.loadUserByUsername(userUserSecurity.getUsername())

        then:
        thrown UsernameNotFoundException
    }
}
