package org.bz.app.mspeople.controllers

import org.bz.app.mspeople.components.validations.UserPasswordValidator
import org.bz.app.mspeople.dtos.UserRequestDTO
import org.bz.app.mspeople.dtos.UserResponseDTO
import org.bz.app.mspeople.exceptions.DefaultInternalServerErrorException
import org.bz.app.mspeople.exceptions.InconsistentBodyIdBadRequestException
import org.bz.app.mspeople.generator.RoleDTOGenerator
import org.bz.app.mspeople.generator.UserRequestDTOGenerator
import org.bz.app.mspeople.generator.UserResponseDTOGenerator
import org.bz.app.mspeople.security.exceptions.NonexistentRoleBadRequestException
import org.bz.app.mspeople.services.UserService
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import spock.lang.Specification

import static org.bz.app.mspeople.utils.FunctionsUtil.stackFrameFunction

@AutoConfigureMockMvc(addFilters = false)
class UserControllerSpec extends Specification {

    private UserController userController

    private userService = Stub(UserService)
    private userServiceMock = Mock(UserService)
    private userPasswordValidator = Stub(UserPasswordValidator)

    void setup() {
        userController = new UserController(userService, userPasswordValidator)
    }

    def "List"() {
        given:
        userService.findAll() >> [UserResponseDTOGenerator.userGenerate(), UserResponseDTOGenerator.adminGenerate()]

        when:
        def responseEntity = userController.list()

        then:
        responseEntity != null
        responseEntity.statusCode == HttpStatus.OK
        def body = (List<UserResponseDTO>) responseEntity.getBody()
        body.size() == 2
    }

    def "List_DefaultInternalServerErrorException"() {
        given:
        def stackFrame = StackWalker.getInstance().walk(stackFrameFunction)
        userService.findAll() >> { throw new DefaultInternalServerErrorException(new Exception("oops"), stackFrame) }

        when:
        def responseEntity = userController.list()

        then:
        thrown DefaultInternalServerErrorException
    }

    def "View"() {
        given:
        def userResponseDTO = UserResponseDTOGenerator.adminGenerate()
        userService.findById(_ as UUID) >> Optional.of(userResponseDTO)

        when:
        def responseEntity = userController.view(userResponseDTO.getId())

        then:
        responseEntity != null
        responseEntity.statusCode == HttpStatus.OK
        responseEntity.getBody() != null
        def body = (UserResponseDTO) responseEntity.getBody()
        body.getId() == userResponseDTO.getId()
    }

    def "View_NotFound"() {
        given:
        userService.findById(_ as UUID) >> Optional.empty()

        when:
        def responseEntity = userController.view(UUID.randomUUID())

        then:
        responseEntity != null
        responseEntity.statusCode == HttpStatus.NOT_FOUND
        responseEntity.getBody() == null
    }

    def "Create"() {
        given:
        def userRequestDTO = UserRequestDTOGenerator.adminGenerate()
        def userResponseDTO = UserResponseDTOGenerator.adminGenerate()
        userService.findRoleByNameIgnoreCase(_ as String) >> Optional.of(RoleDTOGenerator.adminGenerate())
        userService.save(_ as UserRequestDTO) >> userResponseDTO
        def bindingResult = Stub(BindingResult)
        bindingResult.hasErrors() >> false

        when:
        def responseEntity = userController.create(userRequestDTO, bindingResult)

        then:
        responseEntity != null
        responseEntity.statusCode == HttpStatus.CREATED
        def body = (UserResponseDTO) responseEntity.getBody()
        body.getPassword() != userRequestDTO.getPassword()
    }

    def "Create_NonexistentRoleBadRequestException"() {
        given:
        def userRequestDTO = UserRequestDTOGenerator.adminGenerate()
        userService.findRoleByNameIgnoreCase(_ as String) >> Optional.empty()
        def bindingResult = Stub(BindingResult)
        bindingResult.hasErrors() >> false

        when:
        def responseEntity = userController.create(userRequestDTO, bindingResult)

        then:
        thrown NonexistentRoleBadRequestException
        responseEntity == null
    }

    def "Edit"() {
        given:
        def token = "token"
        def userResponseDTO = UserResponseDTOGenerator.userGenerate()
        def userRequestDTO = UserRequestDTOGenerator.userGenerate()
        userRequestDTO.setId(userResponseDTO.getId())
        userService.findById(_ as UUID) >> Optional.of(userResponseDTO)
        userService.findRoleByNameIgnoreCase(_ as String) >> Optional.of(RoleDTOGenerator.userGenerate())
        userService.save(_ as UserRequestDTO) >> userResponseDTO
        def bindingResult = Stub(BindingResult)
        bindingResult.hasErrors() >> false

        when:
        def responseEntity = userController.edit(token, userRequestDTO, bindingResult, userResponseDTO.getId())

        then:
        responseEntity != null
        responseEntity.statusCode == HttpStatus.CREATED
        def body = (UserResponseDTO) responseEntity.getBody()
        body.getPassword() != userRequestDTO.getPassword()

    }

    def "Edit_InconsistentBodyIdBadRequestException"() {
        given:
        def token = "token"
        def userResponseDTO = UserResponseDTOGenerator.userGenerate()
        def userRequestDTO = UserRequestDTOGenerator.userGenerate()
        userRequestDTO.setId(userResponseDTO.getId())
        userService.findRoleByNameIgnoreCase(_ as String) >> Optional.of(RoleDTOGenerator.userGenerate())
        def bindingResult = Stub(BindingResult)
        bindingResult.hasErrors() >> false

        when:
        def responseEntity = userController.edit(token, userRequestDTO, bindingResult, UUID.randomUUID())

        then:
        thrown InconsistentBodyIdBadRequestException

    }

    def "Edit_NonexistentRoleBadRequestException"() {
        given:
        def token = "token"
        def userResponseDTO = UserResponseDTOGenerator.userGenerate()
        def userRequestDTO = UserRequestDTOGenerator.userGenerate()
        userRequestDTO.setId(userResponseDTO.getId())
        userService.findById(_ as UUID) >> Optional.of(userResponseDTO)
        userService.findRoleByNameIgnoreCase(_ as String) >> Optional.empty()
        def bindingResult = Stub(BindingResult)
        bindingResult.hasErrors() >> false

        when:
        def responseEntity = userController.edit(token, userRequestDTO, bindingResult, userResponseDTO.getId())

        then:
        thrown NonexistentRoleBadRequestException
        responseEntity == null

    }

    def "Edit_NotFound"() {
        given:
        def token = "token"
        def userResponseDTO = UserResponseDTOGenerator.userGenerate()
        def userRequestDTO = UserRequestDTOGenerator.userGenerate()
        userRequestDTO.setId(userResponseDTO.getId())
        userService.findRoleByNameIgnoreCase(_ as String) >> Optional.of(RoleDTOGenerator.userGenerate())
        def bindingResult = Stub(BindingResult)
        bindingResult.hasErrors() >> false

        when:
        def responseEntity = userController.edit(token, userRequestDTO, bindingResult, userResponseDTO.getId())

        then:
        responseEntity != null
        responseEntity.statusCode == HttpStatus.NOT_FOUND

    }

    def "Delete"() {
        given:
        userController = new UserController(userServiceMock, userPasswordValidator)
        def userResponseDTO = UserResponseDTOGenerator.adminGenerate()

        when:
        def responseEntity = userController.delete(userResponseDTO.getId())

        then:
        responseEntity != null
        responseEntity.statusCode == HttpStatus.NO_CONTENT
        1 * userServiceMock.deleteById(userResponseDTO.getId())
    }
}
