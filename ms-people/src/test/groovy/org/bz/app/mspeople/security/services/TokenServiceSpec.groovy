package org.bz.app.mspeople.security.services

import org.bz.app.mspeople.dtos.UserRequestDTO
import org.bz.app.mspeople.exceptions.DefaultInternalServerErrorException
import org.bz.app.mspeople.generator.RoleSecurityGenerator
import org.bz.app.mspeople.generator.UserEntityGenerator
import org.bz.app.mspeople.generator.UserRequestDTOGenerator
import org.bz.app.mspeople.generator.UserSecurityGenerator
import org.bz.app.mspeople.mappers.PeopleMapper
import org.bz.app.mspeople.repositories.UserRepository
import org.bz.app.mspeople.security.dtos.AuthenticationRequestDTO
import org.bz.app.mspeople.security.entities.AuthoritySecurity
import org.bz.app.mspeople.security.entities.RoleSecurity
import org.bz.app.mspeople.security.repositories.AuthoritySecurityRepository
import org.bz.app.mspeople.security.repositories.RoleSecurityRepository
import org.bz.app.mspeople.security.repositories.UserSecurityRepository
import org.mapstruct.factory.Mappers
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import spock.lang.Specification

import java.lang.reflect.Field
import java.lang.reflect.Method

class TokenServiceSpec extends Specification {
    private TokenService tokenService

    private PeopleMapper peopleMapper = Mappers.getMapper(PeopleMapper.class)

    private customAuthenticationProvider = Mock(AuthenticationProvider)

    private userSecurityRepository = Mock(UserSecurityRepository)
    private roleSecurityRepository = Stub(RoleSecurityRepository)
    private authoritySecurityRepository = Stub(AuthoritySecurityRepository)
    private userRepository = Mock(UserRepository)

    private UserRequestDTO userRequestDTO
    private RoleSecurity roleSecurity
    private Set<AuthoritySecurity> authoritySecurities
    private Map<String, Object> extraClaims

    private def encodedSecretKey =
            "U2Ugc3VtaW5pc3RyYSB1bmEgY2xhdmUgbGFyZ2EgcGFyYSBjdW1wbGlyIGNvbiBsbyBFeGlnaWRvIHBvciBlbCBTdGFuZGFyZA=="

    void setup() {
        tokenService = new TokenServiceImpl(peopleMapper, customAuthenticationProvider,
                userSecurityRepository, roleSecurityRepository, authoritySecurityRepository, userRepository)

        userRequestDTO = UserRequestDTOGenerator.userGenerate()
        roleSecurity = RoleSecurityGenerator
                .userWithAuthoritiesGenerate()
        authoritySecurities = roleSecurity
                .getAuthoritySecurities()
        extraClaims = Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("name", userRequestDTO.getName()),
                new AbstractMap.SimpleEntry<String, Object>("role", userRequestDTO.getRole().getName()),
                new AbstractMap.SimpleEntry<String, Object>("authorities", authoritySecurities)
        )

    }

    def "GenerateToken"() {
        given:
        getFieldByName("MINUTES_EXPIRATION").set(tokenService, 3L)
        getFieldByName("ENCODED_SECRET_KEY").set(tokenService, encodedSecretKey)

        when:
        tokenService.generateToken(userRequestDTO.getUsername(), null, extraClaims)

        then:
        noExceptionThrown()
    }

    def "GenerateToken_DefaultInternalServerErrorException"() {
        when:
        tokenService.generateToken(userRequestDTO.getUsername(), null, extraClaims)

        then:
        thrown DefaultInternalServerErrorException
    }

    def "ExtractAllClaims"() {
        given:
        getFieldByName("MINUTES_EXPIRATION").set(tokenService, 3L)
        getFieldByName("ENCODED_SECRET_KEY").set(tokenService, encodedSecretKey)
        def token = tokenService.generateToken(userRequestDTO.getUsername(), null, extraClaims)

        when:
        def claims = tokenService.extractAllClaims(token)

        then:
        noExceptionThrown()
        claims != null
        claims.get("role", String.class) == userRequestDTO.getRole().getName()
    }

    def "ExtractAllClaims_DefaultInternalServerErrorException"() {
        given:
        getFieldByName("MINUTES_EXPIRATION").set(tokenService, 3L)
        getFieldByName("ENCODED_SECRET_KEY").set(tokenService, encodedSecretKey)
        def token = tokenService.generateToken(userRequestDTO.getUsername(), null, extraClaims)

        when:
        def claims = tokenService.extractAllClaims(token + "Error")

        then:
        thrown DefaultInternalServerErrorException
    }

    def "Login"() {
        given:
        getFieldByName("MINUTES_EXPIRATION").set(tokenService, 3L)
        getFieldByName("ENCODED_SECRET_KEY").set(tokenService, encodedSecretKey)
        def authenticationRequestDTO = AuthenticationRequestDTO
                .builder()
                .username(userRequestDTO.getUsername())
                .password(userRequestDTO.getPassword())
                .build()
        customAuthenticationProvider.authenticate(_ as Authentication)
        userSecurityRepository.findFirstByUsernameIgnoreCase(_ as String) >> Optional.of(UserSecurityGenerator.userGenerate())
        userRepository.findFirstByUsernameIgnoreCase(_ as String) >> Optional.of(UserEntityGenerator.userGenerate())
        def roleSecurity = RoleSecurityGenerator.userWithAuthoritiesGenerate()
        roleSecurityRepository.findByNameIgnoreCase(_ as String) >> Optional.of(roleSecurity)
        authoritySecurityRepository.findByRoleSecurities_Id(_ as UUID) >> roleSecurity.getAuthoritySecurities()
        when:
        def authenticationResponseDTO = tokenService.login(authenticationRequestDTO)

        then:
        authenticationResponseDTO != null
        authenticationResponseDTO.getToken() != null
        println tokenService.extractAllClaims(authenticationResponseDTO.getToken())
    }

    def "Login_DefaultInternalServerErrorException"() {
        given:
        getFieldByName("MINUTES_EXPIRATION").set(tokenService, 3L)
        getFieldByName("ENCODED_SECRET_KEY").set(tokenService, encodedSecretKey)
        def authenticationRequestDTO = AuthenticationRequestDTO
                .builder()
                .username(userRequestDTO.getUsername())
                .password(userRequestDTO.getPassword())
                .build()
        customAuthenticationProvider.authenticate(_ as Authentication) >> { throw new Exception("oops") }
        when:
        tokenService.login(authenticationRequestDTO)

        then:
        thrown DefaultInternalServerErrorException
    }

    def "GenerateExtraClaims"() {
        given:
        def roleSecurity = RoleSecurityGenerator.userWithAuthoritiesGenerate()
        roleSecurityRepository.findByNameIgnoreCase(_ as String) >> Optional.of(roleSecurity)
        authoritySecurityRepository.findByRoleSecurities_Id(_ as UUID) >> roleSecurity.getAuthoritySecurities()

        when:
        def map = tokenService.generateExtraClaims(userRequestDTO)

        then:
        map != null
        println roleSecurity
        println map
    }

    def "GenerateExtraClaims_DefaultInternalServerErrorException"() {
        given:
        roleSecurityRepository.findByNameIgnoreCase(_ as String) >> { throw new Exception("oops") }

        when:
        def map = tokenService.generateExtraClaims(userRequestDTO)

        then:
        thrown DefaultInternalServerErrorException
    }

    private static Field getFieldByName(String name) {
        // This is not needed
        Field[] allFields = TokenServiceImpl.class.getDeclaredFields()
        for (Field field : allFields) {
            String fieldname = field.getName()
            if (fieldname.equalsIgnoreCase(name)) {
                field.setAccessible(true)
            }
        }
        Field field = TokenServiceImpl.class.getDeclaredField(name)
        field.setAccessible(true)
        return field;
    }

    private static Method getMethodByname(String name) {
        // This is not needed
        Method[] allMethods = TokenServiceImpl.class.getDeclaredMethods()
        for (Method method : allMethods) {
            String methodname = method.getName()
            if (methodname.equalsIgnoreCase(name)) {
                println method.getGenericParameterTypes()
            }
        }
        Method method = TokenServiceImpl.class.getDeclaredMethod(name)
        method.setAccessible(true)
        return method
    }
}
