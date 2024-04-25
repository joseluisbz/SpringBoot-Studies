package org.bz.app.mspeople.controllers

import org.bz.app.mspeople.dtos.UserRequestDTO
import org.bz.app.mspeople.exceptions.DefaultInternalServerErrorException
import org.bz.app.mspeople.generator.UserRequestDTOGenerator
import org.bz.app.mspeople.security.dtos.AuthenticationRequestDTO
import org.bz.app.mspeople.security.dtos.AuthenticationResponseDTO
import org.bz.app.mspeople.security.services.TokenService
import spock.lang.Specification

import static org.bz.app.mspeople.utils.FunctionsUtil.stackFrameFunction

class AuthenticationControllerSpec extends Specification {

    private AuthenticationController authenticationController

    private tokenService = Stub(TokenService)

    private UserRequestDTO userRequestDTO

    void setup() {
        authenticationController = new AuthenticationController(tokenService)
        userRequestDTO = UserRequestDTOGenerator.userGenerate()
    }

    def "Login"() {
        given:
        def token = "token"
        def authenticationResponseDTO = AuthenticationResponseDTO
                .builder()
                .token(token)
                .build()
        tokenService.login(_ as AuthenticationRequestDTO) >> authenticationResponseDTO
        def authenticationRequestDTO = AuthenticationRequestDTO
                .builder()
                .username(userRequestDTO.getUsername())
                .password(userRequestDTO.getPassword())
                .build()

        when:
        def responseEntity = authenticationController.login(authenticationRequestDTO)

        then:
        responseEntity != null
        responseEntity.getBody() != null
        ((AuthenticationResponseDTO)responseEntity.getBody()).getToken() == token
    }

    def "Login_DefaultInternalServerErrorException"() {
        given:
        def stackFrame = StackWalker.getInstance().walk(stackFrameFunction)

        tokenService.login(_ as AuthenticationRequestDTO) >> { throw new DefaultInternalServerErrorException(new Exception("oops"), stackFrame) }
        def authenticationRequestDTO = AuthenticationRequestDTO
                .builder()
                .username(userRequestDTO.getUsername())
                .password(userRequestDTO.getPassword())
                .build()

        when:
        def responseEntity = authenticationController.login(authenticationRequestDTO)

        then:
        thrown DefaultInternalServerErrorException
    }
}
