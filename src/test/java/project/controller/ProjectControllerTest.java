package project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.time.LocalDate;
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
import project.dto.project.ProjectRequestCreateDto;
import project.dto.project.ProjectResponseDto;
import project.model.Project;
import project.security.WithMockCustomUser;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectControllerTest {
    protected static MockMvc mockMvc;
    private static final String URL_TEMPLATE = "/projects";
    private static final String MANAGER_ENDPOINT = "/manager";
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
                    new ClassPathResource("db/projects/insert_projects_to_db.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/users/insert_users_to_db.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/tasks/insert_tasks_to_db.sql"));
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
                    new ClassPathResource("db/tasks/delete_tasks_from_db.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/projects/delete_projects_from_db.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/users/delete_users_from_db.sql"));
        }
    }

    @Test
    @DisplayName("Create a new project with valid fields, add the project to the db")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void createProject_ValidRequestDto_Success() throws Exception {
        ProjectRequestCreateDto projectRequestCreateDto = new ProjectRequestCreateDto(
                "New projectRequestCreateDto A",
                "Description for projectRequestCreateDto A",
                LocalDate.now(), LocalDate.now().plusMonths(1),
                Project.Status.IN_PROGRESS);

        ProjectResponseDto expectedProjectDto = new ProjectResponseDto(
                4L, projectRequestCreateDto.name(),
                projectRequestCreateDto.description(),
                projectRequestCreateDto.startDate(),
                projectRequestCreateDto.endDate(), projectRequestCreateDto.status());

        String jsonRequest = objectMapper.writeValueAsString(projectRequestCreateDto);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        ProjectResponseDto actualProjectDto = objectMapper
                .readValue(result.getResponse().getContentAsString(), ProjectResponseDto.class);
        assertNotNull(actualProjectDto);
        EqualsBuilder.reflectionEquals(expectedProjectDto, actualProjectDto, "id");
    }

    @Test
    @DisplayName("Create a new project with invalid fields, don't add the project to the db")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void createProject_InValidRequestDto_NotSuccess() throws Exception {
        ProjectRequestCreateDto projectRequestCreateDto = new ProjectRequestCreateDto(
                "          ",
                "Description for project A",
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                Project.Status.IN_PROGRESS);

        String jsonRequest = objectMapper.writeValueAsString(projectRequestCreateDto);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertFalse(responseBody.isEmpty());
        assertTrue(responseBody.contains("name must not be blank"));
    }

    @Test
    @DisplayName("Get all projects from db for user role manager")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void retrieveManagerProjects_ReturnCorrectProjects() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + MANAGER_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn();

        List<ProjectResponseDto> projectList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<ProjectResponseDto>>() {});
        assertFalse(projectList.isEmpty());
        assertEquals(3, projectList.size());
    }

    @Test
    @DisplayName("Get all projects from db for user role user, "
            + "must return only projects which has a task with assignee value - this user")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void retrieveUserProjects_ReturnCorrectProjectsForMockUser() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_TEMPLATE))
                .andExpect(status().isOk())
                .andReturn();
        List<ProjectResponseDto> projectList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<ProjectResponseDto>>() {
                });
        assertFalse(projectList.isEmpty());
        assertEquals(2, projectList.size());
    }

    @Test
    @DisplayName("Get project details. If user have task in this project")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void retrieveProjectDetails_ReturnCorrectProjectForMockUser()
            throws Exception {
        long id = 1;
        ProjectResponseDto expectedProjectResponseDto = new ProjectResponseDto(
                id, "Project Alpha", "First project description",
                LocalDate.parse("2024-11-01"), LocalDate.parse("2024-12-30"),
                Project.Status.INITIATED);

        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + SEPARATOR + id))
                .andExpect(status().isOk())
                .andReturn();
        ProjectResponseDto actualProjectResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), ProjectResponseDto.class);

        EqualsBuilder.reflectionEquals(expectedProjectResponseDto, actualProjectResponseDto, "id");
    }

    @Test
    @DisplayName("Get project details. Must throw exception")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void retrieveProjectDetails_ThrowsExceptionForWrongId() throws Exception {
        long id = 3;

        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + SEPARATOR + id))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = String.format("User with username: %s doesn't have project with id: %d",
                "testUser", id);
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Get any project. Must return correct project")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void retrieveManagerProjectDetails_ReturnCorrectProject() throws Exception {
        long id = 3;
        ProjectResponseDto expectedProjectResponseDto = new ProjectResponseDto(
                id, "Project Gamma", "Third project description",
                LocalDate.parse("2024-11-03"), LocalDate.parse("2024-12-31"),
                Project.Status.INITIATED);

        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + MANAGER_ENDPOINT + SEPARATOR + id))
                .andExpect(status().isOk())
                .andReturn();
        ProjectResponseDto actualProjectResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), ProjectResponseDto.class);

        EqualsBuilder.reflectionEquals(expectedProjectResponseDto, actualProjectResponseDto, "id");
    }

    @Test
    @DisplayName("Update project with valid parameters dto")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void updateProject_ValidParameters_Success() throws Exception {
        Long id = 1L;
        ProjectRequestCreateDto projectRequestDto = new ProjectRequestCreateDto(
                "New projectRequestCreateDto A",
                "Description for projectRequestCreateDto A",
                LocalDate.now(), LocalDate.now().plusMonths(1),
                Project.Status.IN_PROGRESS);

        ProjectResponseDto expectedProjectDto = new ProjectResponseDto(
                id, projectRequestDto.name(),
                projectRequestDto.description(),
                projectRequestDto.startDate(),
                projectRequestDto.endDate(), projectRequestDto.status());

        String json = objectMapper.writeValueAsString(projectRequestDto);

        MvcResult result = mockMvc.perform(put(URL_TEMPLATE + SEPARATOR + id)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ProjectResponseDto actualProjectResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), ProjectResponseDto.class);

        assertNotNull(actualProjectResponseDto);
        EqualsBuilder.reflectionEquals(expectedProjectDto, actualProjectResponseDto, "id");
    }

    @Test
    @DisplayName("Update project with valid parameters dto but not existing id")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void updateProject_NotExistingId_NotSuccess() throws Exception {
        long id = 15L;
        ProjectRequestCreateDto projectRequestDto = new ProjectRequestCreateDto(
                "New projectRequestCreateDto A",
                "Description for projectRequestCreateDto A",
                LocalDate.now(), LocalDate.now().plusMonths(1),
                Project.Status.IN_PROGRESS);

        String json = objectMapper.writeValueAsString(projectRequestDto);

        MvcResult result = mockMvc.perform(put(URL_TEMPLATE + SEPARATOR + id)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "Can't find project with id: " + id;
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Update project with valid parameters dto")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void updateProject_InValidEndDate_NotSuccess() throws Exception {
        long id = 1L;
        ProjectRequestCreateDto projectRequestDto = new ProjectRequestCreateDto(
                "New projectRequestCreateDto A",
                "Description for projectRequestCreateDto A",
                LocalDate.now(), LocalDate.now().minusDays(5),
                Project.Status.IN_PROGRESS);
        String json = objectMapper.writeValueAsString(projectRequestDto);

        MvcResult result = mockMvc.perform(put(URL_TEMPLATE + SEPARATOR + id)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        String message = "End date must be after start date";
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Delete project")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteProject_ExistingId_Success() throws Exception {
        long id = 1;
        mockMvc.perform(delete(URL_TEMPLATE + SEPARATOR + id))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
