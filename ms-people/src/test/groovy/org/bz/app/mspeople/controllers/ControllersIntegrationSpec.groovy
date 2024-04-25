package org.bz.app.mspeople.controllers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.bz.app.mspeople.dtos.PhoneRequestDTO
import org.bz.app.mspeople.dtos.UserResponseDTO
import org.bz.app.mspeople.generator.AuthenticationRequestDTOGenerator
import org.bz.app.mspeople.generator.UserRequestDTOGenerator
import org.bz.app.mspeople.security.dtos.AuthenticationRequestDTO
import org.bz.app.mspeople.security.dtos.AuthenticationResponseDTO
import org.bz.app.mspeople.services.UserService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.spockframework.util.Assert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import java.util.stream.Collectors

import static org.hamcrest.Matchers.containsString
import static org.hamcrest.Matchers.notNullValue
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisplayName("Integration tests for UserController endpoints")
@Tag("integration")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllersIntegrationSpec extends Specification {

    private MockMvc mockMvc

    @Autowired
    private WebApplicationContext context

    @Autowired
    private UserService userService

    private ObjectMapper objectMapper

    void setup() {
        objectMapper = new ObjectMapper()
        objectMapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build()
    }


    @WithMockUser(authorities = ["READ_ALL"])
    def "list_using_Authority"() {
        given:
        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .get("/api/users")

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        then:
        resultActions.andExpect(status().isOk())
    }


    @WithMockUser(username = "user")
    def "list_using_Username_with_wrong_ROLE"() {
        given:
        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .get("/api/users")

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        then:
        resultActions.andExpect(status().isUnauthorized())
    }

    def "list_Forbidden"() {
        given:
        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .get("/api/users")

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        then:
        resultActions.andExpect(status().isUnauthorized())
    }

    @WithMockUser(roles = ["ADMIN"])
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    def "view_using_ROLE"() {
        given:
        def userRequestDTO = UserRequestDTOGenerator.adminGenerate()
        def userResponseDTO = userService.save(userRequestDTO)
        def id = userResponseDTO.getId()

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .get("/api/users/" + id)

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        then:
        resultActions.andExpect(status().isOk())
    }

    @WithMockUser(username = "user")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    def "view_using_Username"() {
        given:
        def userRequestDTO = UserRequestDTOGenerator.userGenerate()
        def userResponseDTO = userService.save(userRequestDTO)
        def id = userResponseDTO.getId()

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .get("/api/users/" + id)

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        then:
        resultActions.andExpect(status().isOk())
    }

    @WithMockUser(roles = ["ADMIN"])
    def "view_NotFound"() {
        given:
        def id = UUID.randomUUID()

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .get("/api/users/" + id)

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        then:
        resultActions.andExpect(status().isNotFound())
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    def "create"() {
        given:
        def userRequestDTO = UserRequestDTOGenerator.userGenerate()

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO))

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        then:
        def mvcResult = resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath('$.id', notNullValue()))
                .andExpect(jsonPath('$.role.authorities[0].authority', containsString("_")))
                .andReturn()

        def mockHttpServletResponse = mvcResult.getResponse()
        def jsonResponse = mockHttpServletResponse.getContentAsString()
        def userResponseDTO = objectMapper.readValue(jsonResponse, UserResponseDTO.class)
        Assert.notNull(userResponseDTO)
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    def "edit_with_Authorization_and_Authority"() {
        given:
        def userResponseDTO = userService.save(UserRequestDTOGenerator.adminGenerate())
        def id = userResponseDTO.getId()
        def phones = userResponseDTO
                .getPhones()
                .stream()
                .map(p -> PhoneRequestDTO
                        .builder()
                        .id(p.getId())
                        .number(p.getNumber())
                        .cityCode(p.getCityCode())
                        .countryCode(p.getCountryCode())
                        .build()
                ).collect(Collectors.toSet())

        def userRequestDTO = UserRequestDTOGenerator.adminGenerate()
        userRequestDTO.setId(id)
        userRequestDTO.setPhones(phones)

        def authenticationRequestDTO = AuthenticationRequestDTOGenerator.adminGenerate()
        def authenticationResponseDTO = authenticate(authenticationRequestDTO)
        def authorization = "Bearer ".concat(authenticationResponseDTO.getToken())

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .put("/api/users/" + id)
        //.with(jwt().authorities(new SimpleGrantedAuthority("DELETE_ALL")))
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO))

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        then:
        resultActions.andExpect(status().isCreated())
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    def "edit_without_Authorization"() {
        given:
        def userResponseDTO = userService.save(UserRequestDTOGenerator.adminGenerate())
        def id = userResponseDTO.getId()
        def phones = userResponseDTO
                .getPhones()
                .stream().
                map(p -> PhoneRequestDTO
                        .builder()
                        .id(p.getId())
                        .number(p.getNumber())
                        .cityCode(p.getCityCode())
                        .countryCode(p.getCountryCode())
                        .build()
                ).collect(Collectors.toSet())

        def userRequestDTO = UserRequestDTOGenerator.adminGenerate()
        userRequestDTO.setId(id)
        userRequestDTO.setPhones(phones)

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .put("/api/users/" + id)
        //.with(jwt().authorities(new SimpleGrantedAuthority("DELETE_ALL")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO))

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        then:
        resultActions.andExpect(status().isUnauthorized())
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    def "delete_with_Authorization_and_ROLE"() {
        given:
        def userResponseDTO = userService.save(UserRequestDTOGenerator.adminGenerate())
        def id = userResponseDTO.getId()

        def authenticationRequestDTO = AuthenticationRequestDTOGenerator.adminGenerate()
        def authenticationResponseDTO = authenticate(authenticationRequestDTO)
        def authorization = "Bearer ".concat(authenticationResponseDTO.getToken())

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .delete("/api/users/" + id)
        //.with(jwt().authorities(new SimpleGrantedAuthority("DELETE_ALL")))
                .header(HttpHeaders.AUTHORIZATION, authorization)

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        then:
        resultActions.andExpect(status().is(HttpStatus.NO_CONTENT.value()))
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    def "delete_with_Authorization_and_Wrong_ROLE"() {
        given:
        def userResponseDTO = userService.save(UserRequestDTOGenerator.userGenerate())
        def id = userResponseDTO.getId()

        def authenticationRequestDTO = AuthenticationRequestDTOGenerator.userGenerate()
        def authenticationResponseDTO = authenticate(authenticationRequestDTO)
        def authorization = "Bearer ".concat(authenticationResponseDTO.getToken())

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .delete("/api/users/" + id)
        //.with(jwt().authorities(new SimpleGrantedAuthority("DELETE_ALL")))
                .header(HttpHeaders.AUTHORIZATION, authorization)

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        then:
        resultActions.andExpect(status().isUnauthorized())
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    def "delete_without_Authorization"() {
        given:
        def userResponseDTO = userService.save(UserRequestDTOGenerator.userGenerate())
        def id = userResponseDTO.getId()

        def param = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .delete("/api/users/" + id)

        when:
        def resultActions = mockMvc.perform(param)
                .andDo(print())

        then:
        resultActions.andExpect(status().isUnauthorized())
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    def "login"() {
        given:
        userService.save(UserRequestDTOGenerator.userGenerate())
        def authenticationRequestDTO = AuthenticationRequestDTOGenerator.userGenerate()
        def loginParam = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequestDTO))

        when:
        def loginResultActions = mockMvc
                .perform(loginParam)
                .andDo(print())

        def loginMockHttpServletResponse = loginResultActions
                .andReturn()
                .getResponse()
        def loginJsonResponse = loginMockHttpServletResponse.getContentAsString()
        def authenticationResponseDTO = objectMapper
                .readValue(loginJsonResponse, AuthenticationResponseDTO.class)

        then:
        authenticationResponseDTO.getToken() != null
        loginResultActions.andExpect(status().isOk())
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    def "login_InternalServerError"() {
        given:
        def authenticationRequestDTO = AuthenticationRequestDTOGenerator.userGenerate()
        def loginParam = (MockHttpServletRequestBuilder) MockMvcRequestBuilders
                .post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequestDTO))

        when:
        def loginResultActions = mockMvc
                .perform(loginParam)
                .andDo(print())

        def loginMockHttpServletResponse = loginResultActions
                .andReturn()
                .getResponse()
        def loginJsonResponse = loginMockHttpServletResponse.getContentAsString()
        def authenticationResponseDTO = objectMapper
                .readValue(loginJsonResponse, AuthenticationResponseDTO.class)

        then:
        authenticationResponseDTO.getToken() == null
        loginResultActions.andExpect(status().isUnauthorized())
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
