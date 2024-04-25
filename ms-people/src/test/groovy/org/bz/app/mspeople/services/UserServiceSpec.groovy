package org.bz.app.mspeople.services

import io.jsonwebtoken.Jwts
import org.bz.app.mspeople.dtos.UserRequestDTO
import org.bz.app.mspeople.entities.UserEntity
import org.bz.app.mspeople.exceptions.DefaultInternalServerErrorException
import org.bz.app.mspeople.generator.*
import org.bz.app.mspeople.mappers.PeopleMapper
import org.bz.app.mspeople.repositories.PhoneRepository
import org.bz.app.mspeople.repositories.UserRepository
import org.bz.app.mspeople.security.entities.UserSecurity
import org.bz.app.mspeople.security.repositories.AuthoritySecurityRepository
import org.bz.app.mspeople.security.repositories.RoleSecurityRepository
import org.bz.app.mspeople.security.repositories.UserSecurityRepository
import org.bz.app.mspeople.security.services.TokenService
import org.mapstruct.factory.Mappers
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

import java.util.stream.StreamSupport

class UserServiceSpec extends Specification {
    private UserService userService

    private PeopleMapper peopleMapper = Mappers.getMapper(PeopleMapper.class)
    private userRepository = Mock(UserRepository)
    private phoneRepository = Stub(PhoneRepository)
    private userSecurityRepository = Mock(UserSecurityRepository)
    private roleSecurityRepository = Stub(RoleSecurityRepository)
    private authoritySecurityRepository = Stub(AuthoritySecurityRepository)
    private tokenService = Stub(TokenService)
    private passwordEncoder = Stub(PasswordEncoder)

    void setup() {
        userService = new UserServiceImpl(peopleMapper, userRepository, phoneRepository, userSecurityRepository,
                roleSecurityRepository, authoritySecurityRepository, tokenService, passwordEncoder)
    }

    def "FindAll"() {
        given:
        def userUserEntity = UserEntityGenerator.userGenerate()
        def userUserSecurity = UserSecurityGenerator.userGenerate()
        def adminUserEntity = UserEntityGenerator.adminGenerate()
        def adminUserSecurity = UserSecurityGenerator.adminGenerate()

        userUserSecurity.setId(userUserEntity.getId())
        adminUserEntity.setId(adminUserSecurity.getId())

        userRepository.findAll() >> [userUserEntity, adminUserEntity]
        userSecurityRepository.findAll() >> [userUserSecurity, adminUserSecurity]

        when:
        def iterableUserResponseDTO = userService.findAll()
        def listUserResponseDTO = StreamSupport
                .stream(iterableUserResponseDTO.spliterator(), false)
                .toList()

        then:
        iterableUserResponseDTO != null
        listUserResponseDTO
                .stream()
                .anyMatch(item -> item.getEmail() == userUserSecurity.getEmail())
        listUserResponseDTO
                .stream()
                .anyMatch(item -> item.getUsername() == adminUserEntity.getUsername())
    }

    def "FindAll_DefaultInternalServerErrorException"() {
        given:
        userRepository.findAll() >> { throw new Exception("oops") }

        when:
        def response = userService.findAll()

        then:
        thrown DefaultInternalServerErrorException
    }

    def "FindById"() {
        given:
        def userUserEntity = UserEntityGenerator.userGenerate()
        def userUserSecurity = UserSecurityGenerator.userGenerate()
        userUserSecurity.setId(userUserEntity.getId())

        userRepository.findById(_ as UUID) >> Optional.of(userUserEntity)
        userSecurityRepository.findById(_ as UUID) >> Optional.of(userUserSecurity)

        when:
        def optionalUserResponseDTO = userService.findById(userUserSecurity.getId())

        then:
        optionalUserResponseDTO != null
        def userResponseDTO = optionalUserResponseDTO.get()
        userResponseDTO.getId() == userUserEntity.getId()
        userResponseDTO.getUsername() == userUserEntity.getUsername()
        userResponseDTO.getEmail() == userUserSecurity.getEmail()
    }

    def "FindById_UserEntity_Empty"() {
        given:
        def userUserEntity = UserEntityGenerator.userGenerate()
        def userUserSecurity = UserSecurityGenerator.userGenerate()
        userUserSecurity.setId(userUserEntity.getId())

        userRepository.findById(_ as UUID) >> Optional.empty()
        userSecurityRepository.findById(_ as UUID) >> Optional.of(userUserSecurity)

        when:
        def optionalUserResponseDTO = userService.findById(userUserSecurity.getId())

        then:
        optionalUserResponseDTO != null
        def userResponseDTO = optionalUserResponseDTO.get()
        userResponseDTO.getId() == userUserEntity.getId()
        userResponseDTO.getUsername() == userUserEntity.getUsername()
        userResponseDTO.getEmail() == userUserSecurity.getEmail()
    }

    def "FindById_UserSecurity_Empty"() {
        given:
        def userUserEntity = UserEntityGenerator.userGenerate()
        def userUserSecurity = UserSecurityGenerator.userGenerate()
        userUserSecurity.setId(userUserEntity.getId())

        userRepository.findById(_ as UUID) >> Optional.of(userUserEntity)
        userSecurityRepository.findById(_ as UUID) >> Optional.empty()

        when:
        def optionalUserResponseDTO = userService.findById(userUserSecurity.getId())

        then:
        optionalUserResponseDTO != null
        def userResponseDTO = optionalUserResponseDTO.get()
        userResponseDTO.getId() == userUserEntity.getId()
        userResponseDTO.getUsername() == userUserEntity.getUsername()
        userResponseDTO.getEmail() == userUserSecurity.getEmail()
    }

    def "FindById_Empty"() {
        given:
        def userUserEntity = UserEntityGenerator.userGenerate()
        def userUserSecurity = UserSecurityGenerator.userGenerate()
        userUserSecurity.setId(userUserEntity.getId())

        userRepository.findById(_ as UUID) >> Optional.empty()
        userSecurityRepository.findById(_ as UUID) >> Optional.empty()

        when:
        def optionalUserResponseDTO = userService.findById(userUserSecurity.getId())

        then:
        optionalUserResponseDTO.isEmpty()
    }

    def "FindById_DefaultInternalServerErrorException"() {
        given:
        def userUserEntity = UserEntityGenerator.userGenerate()
        def userUserSecurity = UserSecurityGenerator.userGenerate()
        userUserSecurity.setId(userUserEntity.getId())

        userRepository.findById(_ as UUID) >> { throw new Exception("oops") }

        when:
        userService.findById(userUserSecurity.getId())

        then:
        thrown DefaultInternalServerErrorException
    }

    def "FindFirstByEmailIgnoreCase"() {
        given:
        userRepository.findFirstByEmailIgnoreCase(_ as String) >> Optional.of(UserEntityGenerator.adminGenerate())

        when:
        def optionalUserResponseDTO = userService.findFirstByEmailIgnoreCase("some@mail.com")

        then:
        optionalUserResponseDTO.isPresent()
    }

    def "FindFirstByEmailIgnoreCase_Empty"() {
        given:
        userRepository.findFirstByEmailIgnoreCase(_ as String) >> Optional.empty()

        when:
        def optionalUserResponseDTO = userService.findFirstByEmailIgnoreCase("some@mail.com")

        then:
        optionalUserResponseDTO.isEmpty()
    }

    def "FindFirstByEmailIgnoreCase_DefaultInternalServerErrorException"() {
        given:
        userRepository.findFirstByEmailIgnoreCase(_ as String) >> { throw new Exception("oops") }

        when:
        userService.findFirstByEmailIgnoreCase("none@mail.com")

        then:
        thrown DefaultInternalServerErrorException
    }

    def "FindFirstByEmailIgnoreCaseAndIdNot"() {
        given:
        userRepository.findFirstByEmailIgnoreCaseAndIdNot(_ as String, _ as UUID) >> Optional.of(UserEntityGenerator.adminGenerate())

        when:
        def optionalUserResponseDTO = userService.findFirstByEmailIgnoreCaseAndIdNot("none@mail.com", UUID.randomUUID())

        then:
        optionalUserResponseDTO != null
    }

    def "FindFirstByEmailIgnoreCaseAndIdNot_DefaultInternalServerErrorException"() {
        given:
        userRepository.findFirstByEmailIgnoreCaseAndIdNot(_ as String, _ as UUID) >> { throw new Exception("oops") }

        when:
        userService.findFirstByEmailIgnoreCaseAndIdNot("none@mail.com", UUID.randomUUID())

        then:
        thrown DefaultInternalServerErrorException
    }

    def "Save_User"() {
        given:
        def userRequestDTO = UserRequestDTOGenerator.userGenerate()
        def roleSecurity = RoleSecurityGenerator
                .userWithAuthoritiesGenerate()
        def authoritySecurities = roleSecurity
                .getAuthoritySecurities()
        def extraClaims = Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("name", userRequestDTO.getName()),
                new AbstractMap.SimpleEntry<String, Object>("role", userRequestDTO.getRole().getName()),
                new AbstractMap.SimpleEntry<String, Object>("authorities", authoritySecurities)
        )
        def claims = Jwts.claims()
                .subject(userRequestDTO.getUsername())
                .add(extraClaims)
                .build()

        tokenService.generateExtraClaims(_ as UserRequestDTO) >> extraClaims
        tokenService.generateToken(_ as String, _ as String, _ as Map<String, Object>) >> "Token1"
        tokenService.extractAllClaims(_ as String) >> claims
        passwordEncoder.encode(_ as CharSequence) >> "password_encoded"
        roleSecurityRepository.findByNameIgnoreCase(_ as String) >> Optional.of(roleSecurity)
        userRepository.save(_ as UserEntity) >> UserEntityGenerator.userGenerate()
        userSecurityRepository.save(_ as UserSecurity) >> UserSecurityGenerator.userGenerate()
        authoritySecurityRepository.findByRoleSecurities_Id(_ as UUID) >> authoritySecurities

        when:
        def userResponseDTO = userService.save(userRequestDTO)

        then:
        userResponseDTO != null
    }

    def "Save_User_DefaultInternalServerErrorException"() {
        given:
        def userRequestDTO = UserRequestDTOGenerator.adminGenerate()

        tokenService.generateExtraClaims(_ as UserRequestDTO) >> { throw new Exception("oops") }

        when:
        def userResponseDTO = userService.save(userRequestDTO)

        then:
        thrown DefaultInternalServerErrorException
    }

    def "DeleteById"() {
        given:
        def userResponseDTO = UserResponseDTOGenerator.adminGenerate()

        when:
        userService.deleteById(userResponseDTO.getId())

        then:
        1 * userRepository.deleteById(_ as UUID)
        1 * userSecurityRepository.deleteById(_ as UUID)
    }

    def "DeleteById_Null"() {
        when:
        userService.deleteById(null)

        then:
        1 * userRepository.deleteById(null)
        1 * userSecurityRepository.deleteById(null)
    }

    def "DeleteById_DefaultInternalServerErrorException"() {
        given:
        userRepository.deleteById(_ as UUID) >> { throw new Exception("oops") }

        when:
        userService.deleteById(UUID.randomUUID())

        then:
        thrown DefaultInternalServerErrorException
    }

    def "FindFirstByUsernameIgnoreCase"() {
        given:
        userSecurityRepository.findFirstByUsernameIgnoreCase(_ as String) >> Optional.of(UserSecurityGenerator.adminGenerate())

        when:
        def optionalUserResponseDTO = userService.findFirstByUsernameIgnoreCase("username")

        then:
        optionalUserResponseDTO.isPresent()
    }

    def "FindFirstByUsernameIgnoreCase_Empty"() {
        given:
        userSecurityRepository.findFirstByUsernameIgnoreCase(_ as String) >> Optional.empty()

        when:
        def optionalUserResponseDTO = userService.findFirstByUsernameIgnoreCase("username")

        then:
        optionalUserResponseDTO.isEmpty()
    }

    def "FindFirstByUsernameIgnoreCase_DefaultInternalServerErrorException"() {
        given:
        userSecurityRepository.findFirstByUsernameIgnoreCase(_ as String) >> { throw new Exception("oops") }

        when:
        userService.findFirstByUsernameIgnoreCase("username")

        then:
        thrown DefaultInternalServerErrorException
    }

    def "FindFirstByUsernameIgnoreCaseAndIdNot"() {
        given:
        userRepository.findFirstByUsernameIgnoreCaseAndIdNot(_ as String, _ as UUID) >> Optional.of(UserEntityGenerator.userGenerate())

        when:
        def optionalUserResponseDTO = userService.findFirstByUsernameIgnoreCaseAndIdNot("username", UUID.randomUUID())

        then:
        optionalUserResponseDTO.isPresent()
    }

    def "FindFirstByUsernameIgnoreCaseAndIdNot_DefaultInternalServerErrorException"() {
        given:
        userRepository.findFirstByUsernameIgnoreCaseAndIdNot(_ as String, _ as UUID) >> { throw new Exception("oops") }

        when:
        userService.findFirstByUsernameIgnoreCaseAndIdNot("username", UUID.randomUUID())

        then:
        thrown DefaultInternalServerErrorException
    }

    def "FindByCountryCodeAndCityCodeAndNumber"() {
        given:
        phoneRepository.findByCountryCodeAndCityCodeAndNumber(_ as Integer, _ as Integer, _ as Long) >> Optional.of(PhoneEntityGenerator.generate())

        when:
        def optionalPhoneResponseDTO = userService.findByCountryCodeAndCityCodeAndNumber(0, 0, 1L)

        then:
        optionalPhoneResponseDTO.isPresent()
    }

    def "FindByCountryCodeAndCityCodeAndNumber_DefaultInternalServerErrorException"() {
        given:
        phoneRepository.findByCountryCodeAndCityCodeAndNumber(_ as Integer, _ as Integer, _ as Long) >> { throw new Exception("oops") }

        when:
        userService.findByCountryCodeAndCityCodeAndNumber(0, 0, 1L)

        then:
        thrown DefaultInternalServerErrorException
    }

    def "FindByIdAndUserEntity_Id_Empty"() {
        given:
        phoneRepository.findByIdAndUserEntity_Id(_ as UUID, null) >> Optional.of(PhoneEntityGenerator.generate())

        when:
        def optionalPhoneResponseDTO = userService.findByIdAndUserEntity_Id(UUID.randomUUID(), null)

        then:
        optionalPhoneResponseDTO.isEmpty()
    }

    def "FindByIdAndUserEntity_Id_DefaultInternalServerErrorException"() {
        given:
        phoneRepository.findByIdAndUserEntity_Id(_ as UUID, _ as UUID) >> { throw new Exception("oops") }

        when:
        userService.findByIdAndUserEntity_Id(UUID.randomUUID(), UUID.randomUUID())

        then:
        thrown DefaultInternalServerErrorException
    }

    def "FindRoleByNameIgnoreCase"() {
        given:
        roleSecurityRepository.findByNameIgnoreCase(_ as String) >> Optional.of(RoleSecurityGenerator.adminWithAuthoritiesGenerate())

        when:
        def optionalRoleDTO = userService.findRoleByNameIgnoreCase("username")

        then:
        optionalRoleDTO.isPresent()
    }

    def "FindRoleByNameIgnoreCase_Empty"() {
        given:
        roleSecurityRepository.findByNameIgnoreCase(_ as String) >> Optional.empty()

        when:
        def optionalRoleDTO = userService.findRoleByNameIgnoreCase("username")

        then:
        optionalRoleDTO.isEmpty()
    }

    def "FindRoleByNameIgnoreCase_DefaultInternalServerErrorException"() {
        given:
        roleSecurityRepository.findByNameIgnoreCase(_ as String) >> { throw new Exception("oops") }

        when:
        userService.findRoleByNameIgnoreCase("username")

        then:
        thrown DefaultInternalServerErrorException
    }
}
