package org.bz.app.mspeople.controllers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.bz.app.mspeople.components.validations.UserPasswordValidator
import org.bz.app.mspeople.controllers.AuthenticationController
import org.bz.app.mspeople.controllers.ExceptionsController
import org.bz.app.mspeople.controllers.UserController
import org.bz.app.mspeople.dtos.PhoneRequestDTO
import org.bz.app.mspeople.dtos.UserRequestDTO
import org.bz.app.mspeople.dtos.UserResponseDTO
import org.bz.app.mspeople.exceptions.ExistingMailOrUsernameBadRequestException
import org.bz.app.mspeople.exceptions.PatternEmailBadRequestException
import org.bz.app.mspeople.exceptions.PatternPasswordBadRequestException
import org.bz.app.mspeople.generator.AuthenticationRequestDTOGenerator
import org.bz.app.mspeople.generator.UserRequestDTOGenerator
import org.bz.app.mspeople.security.dtos.AuthenticationRequestDTO
import org.bz.app.mspeople.security.dtos.AuthenticationResponseDTO
import org.bz.app.mspeople.security.exceptions.RoleEmptyBadRequestException
import org.bz.app.mspeople.security.exceptions.UsernameEmptyBadRequestException
import org.bz.app.mspeople.security.services.TokenService
import org.bz.app.mspeople.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Title

import java.util.stream.Collectors

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Title("Unit Testing of Exceptions for UserController and AuthenticationController")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExceptionsControllerSpec extends Specification {

    private MockMvc mockMvc

    private UserController userController

    @Autowired
    private UserService userService

    @Autowired
    private TokenService tokenService

    private userPasswordValidator = Stub(UserPasswordValidator)

    private AuthenticationController authenticationController

    private ExceptionsController exceptionController

    private ObjectMapper objectMapper

    private UserRequestDTO userRequestDTO
    private AuthenticationRequestDTO authenticationRequestDTO

    void setup() {
        objectMapper = new ObjectMapper()
        objectMapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        userController = new UserController(userService, userPasswordValidator)
        authenticationController = new AuthenticationController(tokenService)
        exceptionController = new ExceptionsController()

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController, authenticationController)
                .setControllerAdvice(exceptionController)
                .build()

        userRequestDTO = UserRequestDTOGenerator.userGenerate()
        authenticationRequestDTO = AuthenticationRequestDTOGenerator.userGenerate()
    }

    def "DefaultBadRequest - PatternPasswordBadRequestException"() {
        given:
        userRequestDTO.setPassword(null) // Force BindingResult error with 'password'

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO))

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        def mockHttpServletResponse = resultActions
                .andReturn()
                .getResponse()

        def jsonResponse = mockHttpServletResponse.getContentAsString()
        def jsonException = objectMapper
                .readTree(jsonResponse)
                .path("exception")
        then:
        resultActions.andExpect(status().isBadRequest())
        jsonException != null
        def exception = objectMapper.treeToValue(jsonException, PatternPasswordBadRequestException.class)
    }

    def "DefaultBadRequest - PatternEmailBadRequestException"() {
        given:
        userRequestDTO.setEmail(null) // Force BindingResult error with 'email'

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO))

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        def mockHttpServletResponse = resultActions
                .andReturn()
                .getResponse()

        def jsonResponse = mockHttpServletResponse.getContentAsString()
        def jsonException = objectMapper
                .readTree(jsonResponse)
                .path("exception")
        then:
        resultActions.andExpect(status().isBadRequest())
        jsonException != null
        def exception = objectMapper.treeToValue(jsonException, PatternEmailBadRequestException.class)
    }

    def "DefaultBadRequest - UsernameEmptyBadRequestException"() {
        given:
        userRequestDTO.setUsername(null) // Force BindingResult error with 'username'

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO))

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        def mockHttpServletResponse = resultActions
                .andReturn()
                .getResponse()

        def jsonResponse = mockHttpServletResponse.getContentAsString()
        def jsonException = objectMapper
                .readTree(jsonResponse)
                .path("exception")
        then:
        resultActions.andExpect(status().isBadRequest())
        jsonException != null
        def exception = objectMapper.treeToValue(jsonException, UsernameEmptyBadRequestException.class)
    }

    def "DefaultBadRequest - RoleEmptyBadRequestException"() {
        given:
        userRequestDTO.setRole(null) // Force BindingResult error with 'role'

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO))

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        def mockHttpServletResponse = resultActions
                .andReturn()
                .getResponse()

        def jsonResponse = mockHttpServletResponse.getContentAsString()
        def jsonException = objectMapper
                .readTree(jsonResponse)
                .path("exception")
        then:
        resultActions.andExpect(status().isBadRequest())
        jsonException != null
        def exception = objectMapper.treeToValue(jsonException, RoleEmptyBadRequestException.class)
    }

    def "DefaultBadRequest - ExistingMailOrUsernameBadRequestException"() {
        given:
        def previousUserRequestDTO = UserRequestDTOGenerator.userGenerate()
        def previousParam = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(previousUserRequestDTO))
        def previousResponse = mockMvc.perform(previousParam).andReturn() // Insert same Username and Email

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO))

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        def mockHttpServletResponse = resultActions
                .andReturn()
                .getResponse()

        def jsonResponse = mockHttpServletResponse.getContentAsString()
        def jsonException = objectMapper
                .readTree(jsonResponse)
                .path("exception")
        then:
        previousResponse != null
        resultActions.andExpect(status().isBadRequest())
        jsonException != null
        def exception = objectMapper.treeToValue(jsonException, ExistingMailOrUsernameBadRequestException.class)
    }

    def "DefaultBadRequest - ExistingPhoneBadRequestException"() {
        given:
        def previousUserRequestDTO = UserRequestDTOGenerator.userGenerate()
        previousUserRequestDTO.setUsername("o" + previousUserRequestDTO.getUsername())
        previousUserRequestDTO.setEmail("o" + previousUserRequestDTO.getEmail())
        def previousParam = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(previousUserRequestDTO))
        def previousResponse = mockMvc.perform(previousParam).andReturn() // Insert same phone

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO))

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        def mockHttpServletResponse = resultActions
                .andReturn()
                .getResponse()

        def jsonResponse = mockHttpServletResponse.getContentAsString()
        def jsonException = objectMapper
                .readTree(jsonResponse)
                .path("exception")
        then:
        previousResponse != null
        resultActions.andExpect(status().isBadRequest())
        jsonException != null
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    def "DefaultBadRequest - NotAssignablePhoneBadRequestException"() {
        given:
        def firstUserRequestDTO = UserRequestDTOGenerator.userGenerate()
        firstUserRequestDTO.setUsername("o" + firstUserRequestDTO.getUsername())
        firstUserRequestDTO.setEmail("o" + firstUserRequestDTO.getEmail())

        // Insert First User
        def firstParam = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstUserRequestDTO))
        def firstResult = mockMvc.perform(firstParam).andReturn()
        def firstJsonResponse = firstResult.getResponse().getContentAsString()
        def firstUserResponseDTO = objectMapper.readValue(firstJsonResponse, UserResponseDTO.class)

        // Insert Second User (With Different Number)
        def secondUserRequestDTO = UserRequestDTOGenerator.userGenerate()
        secondUserRequestDTO.phones.stream().forEach(p -> p.setNumber(p.getNumber() + 1))
        def secondParam = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondUserRequestDTO))
        def secondResult = mockMvc.perform(secondParam).andReturn()
        def secondJsonResponse = secondResult.getResponse().getContentAsString()
        def secondUserResponseDTO = objectMapper.readValue(secondJsonResponse, UserResponseDTO.class)

        // Set phones to secondUser from firstUser
        setPhonesFromResponseToRequest(firstUserResponseDTO, secondUserRequestDTO)

        def authenticationResponseDTO = authenticate(authenticationRequestDTO)
        def authorization = "Bearer ".concat(authenticationResponseDTO.getToken())

        // Edit Second User (using phones of First User)
        def id = secondUserResponseDTO.getId()
        secondUserRequestDTO.setId(id)
        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .put("/api/users/" + id)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondUserRequestDTO))

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        def mockHttpServletResponse = resultActions
                .andReturn()
                .getResponse()

        def jsonResponse = mockHttpServletResponse.getContentAsString()
        def jsonException = objectMapper
                .readTree(jsonResponse)
                .path("exception")
        then:
        firstResult != null
        resultActions.andExpect(status().isBadRequest())
        jsonException != null
    }

    private void setPhonesFromResponseToRequest(UserResponseDTO userResponseDTO, UserRequestDTO userRequestDTO) {
        userRequestDTO.setPhones(userResponseDTO
                .getPhones()
                .stream()
                .map(phoneResponse ->
                        PhoneRequestDTO
                                .builder()
                                .id(phoneResponse.getId())
                                .number(phoneResponse.getNumber())
                                .cityCode(phoneResponse.getCityCode())
                                .countryCode(phoneResponse.getCountryCode())
                                .build()
                )
                .collect(Collectors.toSet())
        )
    }

    private AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) {
        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequestDTO))
        def mockHttpServletResponse = mockMvc
                .perform(param)
                .andDo(print())
                .andReturn()
                .getResponse()
        def loginJsonResponse = mockHttpServletResponse.getContentAsString()
        def authenticationResponseDTO = objectMapper
                .readValue(loginJsonResponse, AuthenticationResponseDTO.class)
        return authenticationResponseDTO
    }
}
