package project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.util.ArrayList;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import project.dto.user.UserResponseDto;
import project.dto.user.UserUpdateRequestDto;
import project.dto.user.UserUpdateRoleDto;
import project.model.Role;
import project.security.WithMockCustomUser;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    protected static MockMvc mockMvc;
    private static final String URL_TEMPLATE = "/users";
    private static final String ME_ENDPOINT = "/me";
    private static final String ROLE_NAME_ENDPOINT = "/roleName";
    private static final String SEPARATOR = "/";
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
    @DisplayName("Update user role.")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateUserRole_ValidParameters_Success() throws Exception {
        UserUpdateRoleDto userUpdateRoleDto = new UserUpdateRoleDto(Role.RoleName.ROLE_MANAGER);
        Long id = 21L;
        UserResponseDto expectedUserDto = new UserResponseDto("testUser", id,
                "testuser@example.com", "Test", "User", new ArrayList<>(List.of("ROLE_MANAGER")));
        String json = objectMapper.writeValueAsString(userUpdateRoleDto);

        MvcResult result = mockMvc.perform(put(URL_TEMPLATE + SEPARATOR + id + ROLE_NAME_ENDPOINT)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        UserResponseDto actualUserResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);

        EqualsBuilder.reflectionEquals(expectedUserDto, actualUserResponseDto);
    }

    @Test
    @DisplayName("Update user role with invalid parameters.")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateUserRole_InValidParameters_ThrowException() throws Exception {
        UserUpdateRoleDto userUpdateRoleDto = new UserUpdateRoleDto(null);
        long id = 21L;
        String json = objectMapper.writeValueAsString(userUpdateRoleDto);
        MvcResult result = mockMvc.perform(put(URL_TEMPLATE + SEPARATOR + id + ROLE_NAME_ENDPOINT)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "roleName must not be null";
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Find and return user info")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void getUserProfileInfo_ReturnCorrectUserInfo() throws Exception {
        Long id = 21L;
        UserResponseDto expectedUserDto = new UserResponseDto("testUser", id,
                "testuser@example.com", "Test", "User", new ArrayList<>(List.of("ROLE_USER")));
        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + ME_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto actualUserResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);

        EqualsBuilder.reflectionEquals(expectedUserDto, actualUserResponseDto);
    }

    @Test
    @DisplayName("Update with valid  parameters and return user info")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void updateUserInfo_ValidParameters_ReturnCorrectUserInfo() throws Exception {
        Long id = 21L;
        UserUpdateRequestDto userUpdateDto = new UserUpdateRequestDto("kokoroko@mail.ua",
                "Taras", null);
        UserResponseDto expectedUserDto = new UserResponseDto("testUser", id,
                "kokoroko@mail.ua", "Taras", "User", new ArrayList<>(List.of("ROLE_USER")));
        String json = objectMapper.writeValueAsString(userUpdateDto);
        MvcResult result = mockMvc.perform(put(URL_TEMPLATE + ME_ENDPOINT)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto actualUserResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);
        assertEquals(expectedUserDto, actualUserResponseDto);
    }

    @Test
    @DisplayName("Update with existing email and return user info.")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void updateUserInfo_InValidParametersExistingEmail_ThrowException() throws Exception {
        Long id = 21L;
        UserUpdateRequestDto userUpdateDto = new UserUpdateRequestDto("testuser2@example.com",
                "Taras", null);
        String json = objectMapper.writeValueAsString(userUpdateDto);
        MvcResult result = mockMvc.perform(put(URL_TEMPLATE + ME_ENDPOINT)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "User with email: testuser2@example.com is existing";
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Update with existing email and return user info.")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void updateUserInfo_InValidParametersNotEmail_ThrowException() throws Exception {
        Long id = 21L;
        UserUpdateRequestDto userUpdateDto = new UserUpdateRequestDto("testuser2",
                "Taras", null);
        String json = objectMapper.writeValueAsString(userUpdateDto);
        MvcResult result = mockMvc.perform(put(URL_TEMPLATE + ME_ENDPOINT)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "email must be a well-formed email address";
        assertTrue(contentAsString.contains(message));
    }
}
