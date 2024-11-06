package project.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import project.dto.user.UserLoginRequestDto;
import project.dto.user.UserLoginResponseDto;
import project.dto.user.UserRegistrationRequestDto;
import project.dto.user.UserResponseDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
    protected static MockMvc mockMvc;
    private static final String REGISTRATION = "/registration";
    private static final String AUTH = "/auth";
    private static final String LOGIN = "/login";
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws Exception {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/users/insert_roles_to_db.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/users/insert_users_to_db.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/users/insert_users_roles.sql"));
        }
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/users/delete_from_users_roles.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/users/delete_roles_from_db.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/users/delete_users_from_db.sql"));
        }
    }

    @Test
    @DisplayName("Registration new user success")
    void registration_ValidParameters_Success() throws Exception {
        UserRegistrationRequestDto registrationUserDto = new UserRegistrationRequestDto();
        registrationUserDto.setEmail("new_email@email.ua");
        registrationUserDto.setPassword("12345678");
        registrationUserDto.setRepeatPassword("12345678");
        registrationUserDto.setUsername("newUser");
        registrationUserDto.setLastName("Chubenko");
        registrationUserDto.setFirstName("Taras");
        UserResponseDto expectedUserDto = new UserResponseDto(registrationUserDto.getUsername(),
                3L, registrationUserDto.getEmail(), registrationUserDto.getFirstName(),
                registrationUserDto.getLastName(), List.of("ROLE_USER"));
        String json = objectMapper.writeValueAsString(registrationUserDto);
        MvcResult result = mockMvc.perform(post(AUTH + REGISTRATION)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        UserResponseDto actualUserResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);
        EqualsBuilder.reflectionEquals(expectedUserDto, actualUserResponseDto, "id");
    }

    @Test
    @DisplayName("Registration new user with not matching password")
    void registration_InValidParametersNotMatchPassword_ThrowException() throws Exception {
        UserRegistrationRequestDto registrationUserDto = new UserRegistrationRequestDto();
        registrationUserDto.setEmail("new_email@email.ua");
        registrationUserDto.setPassword("12345678");
        registrationUserDto.setRepeatPassword("12345678333");
        registrationUserDto.setUsername("newUser");
        registrationUserDto.setLastName("Chubenko");
        registrationUserDto.setFirstName("Taras");

        String json = objectMapper.writeValueAsString(registrationUserDto);
        MvcResult result = mockMvc.perform(post(AUTH + REGISTRATION)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "Password must match the repeat password";
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Registration new user with blank username field")
    void registration_InValidParametersBlankUsername_ThrowException() throws Exception {
        UserRegistrationRequestDto registrationUserDto = new UserRegistrationRequestDto();
        registrationUserDto.setEmail("new_email@email.ua");
        registrationUserDto.setPassword("12345678");
        registrationUserDto.setRepeatPassword("12345678");
        registrationUserDto.setUsername("          ");
        registrationUserDto.setLastName("Chubenko");
        registrationUserDto.setFirstName("Taras");

        String json = objectMapper.writeValueAsString(registrationUserDto);
        MvcResult result = mockMvc.perform(post(AUTH + REGISTRATION)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "username must not be blank";
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Registration new user with blank username field")
    void registration_InValidParametersExistingEmail_ThrowException() throws Exception {
        UserRegistrationRequestDto registrationUserDto = new UserRegistrationRequestDto();
        registrationUserDto.setEmail("testuser2@example.com");
        registrationUserDto.setPassword("12345678");
        registrationUserDto.setRepeatPassword("12345678");
        registrationUserDto.setUsername("newUsername");
        registrationUserDto.setLastName("Chubenko");
        registrationUserDto.setFirstName("Taras");

        String json = objectMapper.writeValueAsString(registrationUserDto);
        MvcResult result = mockMvc.perform(post(AUTH + REGISTRATION)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message =
                "User with username: newUsername or email: testuser2@example.com is existing";
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Login user which existing in db")
    void login_ExistingUser_ReturnToken() throws Exception {
        UserLoginRequestDto userLogin = new UserLoginRequestDto("testUser2", "12345678");
        String json = objectMapper.writeValueAsString(userLogin);
        MvcResult result = mockMvc.perform(post(AUTH + LOGIN)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserLoginResponseDto userLoginResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserLoginResponseDto.class);
        assertNotNull(userLoginResponseDto.token());
    }

    @Test
    @DisplayName("Login user which not existing in db")
    void login_NotExistingUser_ThrowException() throws Exception {
        UserLoginRequestDto userLogin = new UserLoginRequestDto("user", "12345678");
        String json = objectMapper.writeValueAsString(userLogin);
        MvcResult result = mockMvc.perform(post(AUTH + LOGIN)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "Can't find user by username: user";
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Login user which not existing in db")
    void login_WrongPassword_ReturnEmptyResult() throws Exception {
        UserLoginRequestDto userLogin = new UserLoginRequestDto("testUser2", "123456783");
        String json = objectMapper.writeValueAsString(userLogin);
        MvcResult result = mockMvc.perform(post(AUTH + LOGIN)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();

        assertTrue(contentAsString.isEmpty());
    }
}
