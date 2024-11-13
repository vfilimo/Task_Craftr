package project.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.calendar.model.Event;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import project.dto.task.TaskCreateDto;
import project.dto.task.TaskDto;
import project.dto.task.TaskUpdateDto;
import project.external.google.GoogleCalendarService;
import project.model.Task;
import project.security.WithMockCustomUser;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest {
    protected static MockMvc mockMvc;
    private static final String URL_TEMPLATE = "/tasks";
    private static final String MANAGER_ENDPOINT = "/manager";
    private static final String SEPARATOR = "/";
    private static final String ALL_ENDPOINT = "/all";
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleCalendarService googleCalendarService;

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
    @DisplayName("Create and add new task for any user and any project")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void createNewTaskForManager_ValidParameters_Success() throws Exception {
        TaskCreateDto createTaskDto = new TaskCreateDto("new Task", "Description for new task",
                Task.Priority.HIGH, Task.Status.NOT_STARTED, LocalDate.now(), 1L, 21L);
        TaskDto expectedTaskDto = new TaskDto();
        expectedTaskDto.setName(createTaskDto.name());
        expectedTaskDto.setDescription(createTaskDto.description());
        expectedTaskDto.setPriority(createTaskDto.priority());
        expectedTaskDto.setDueDate(createTaskDto.dueDate());
        expectedTaskDto.setStatus(createTaskDto.status());
        expectedTaskDto.setProjectName("Project Alpha");
        expectedTaskDto.setAssigneeUsername("testUser");

        Event mockEvent = new Event();
        mockEvent.setId("mockEventId");
        given(googleCalendarService.createEvent(Mockito.any())).willReturn(mockEvent);
        given(googleCalendarService.insertEventInToCalendar(Mockito.any())).willReturn(mockEvent);

        String jsonRequest = objectMapper.writeValueAsString(createTaskDto);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        TaskDto actualTaskDto = objectMapper
                .readValue(result.getResponse().getContentAsString(), TaskDto.class);
        assertNotNull(actualTaskDto);
        List<String> excludeFields = List.of("id", "labelsId");
        EqualsBuilder.reflectionEquals(expectedTaskDto, actualTaskDto, "id", "labelsId");
    }

    @Test
    @DisplayName("Create task with invalid parameters")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void createNewTaskForManager_InValidParameters_NotSuccess() throws Exception {
        TaskCreateDto createTaskDto = new TaskCreateDto("      ", "            ",
                Task.Priority.HIGH, Task.Status.NOT_STARTED, LocalDate.now(), 1L, 21L);
        Event mockEvent = new Event();
        mockEvent.setId("mockEventId");
        String jsonRequest = objectMapper.writeValueAsString(createTaskDto);
        given(googleCalendarService.createEvent(Mockito.any())).willReturn(mockEvent);
        given(googleCalendarService.insertEventInToCalendar(Mockito.any())).willReturn(mockEvent);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String message = "name must not be blank";
        String contentAsString = result.getResponse().getContentAsString();
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Create task with invalid due date")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void createNewTaskForManager_InValidDueDate_NotSuccess() throws Exception {
        TaskCreateDto createTaskDto = new TaskCreateDto("Some new Task", "Description",
                Task.Priority.HIGH, Task.Status.NOT_STARTED, LocalDate.parse("2026-01-03"),
                1L, 21L);
        Event mockEvent = new Event();
        mockEvent.setId("mockEventId");
        String jsonRequest = objectMapper.writeValueAsString(createTaskDto);
        given(googleCalendarService.createEvent(Mockito.any())).willReturn(mockEvent);
        given(googleCalendarService.insertEventInToCalendar(Mockito.any())).willReturn(mockEvent);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String message = "The due date must be before project end date";
        String contentAsString = result.getResponse().getContentAsString();

        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Create new task for a user which login in app")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void createNewTaskForAssignee_ValidParameters_Success() throws Exception {
        TaskCreateDto createTaskDto = new TaskCreateDto("new Task", "Description for new task",
                Task.Priority.HIGH, Task.Status.NOT_STARTED, LocalDate.now(), 1L, 21L);

        TaskDto expectedTaskDto = new TaskDto();
        expectedTaskDto.setName(createTaskDto.name());
        expectedTaskDto.setDescription(createTaskDto.description());
        expectedTaskDto.setPriority(createTaskDto.priority());
        expectedTaskDto.setDueDate(createTaskDto.dueDate());
        expectedTaskDto.setStatus(createTaskDto.status());
        expectedTaskDto.setProjectName("Project Alpha");
        expectedTaskDto.setAssigneeUsername("testUser");

        Event mockEvent = new Event();
        mockEvent.setId("mockEventId");
        given(googleCalendarService.createEvent(Mockito.any())).willReturn(mockEvent);
        given(googleCalendarService.insertEventInToCalendar(Mockito.any())).willReturn(mockEvent);

        String jsonRequest = objectMapper.writeValueAsString(createTaskDto);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        TaskDto actualTaskDto = objectMapper
                .readValue(result.getResponse().getContentAsString(), TaskDto.class);
        assertNotNull(actualTaskDto);
        EqualsBuilder.reflectionEquals(expectedTaskDto, actualTaskDto, "id", "labelsId");
    }

    @Test
    @DisplayName("Find user task in project where logged-in user is assignee")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void retrieveTasksForProject_ExistingProjectId_ReturnCorrectTask() throws Exception {
        mockMvc.perform(get(URL_TEMPLATE)
                        .param("projectId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName(
            "Return empty list when a logged-in user tries to find a task that is not theirs.")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void retrieveTasksForProject_ExistingProjectIdButNotUserAssignee_ReturnEmptyList()
            throws Exception {
        Long id = 3L;
        mockMvc.perform(get(URL_TEMPLATE)
                        .param("projectId", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("Find all tasks for any project")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void retrieveAllTasksForProject_ExistingProjectId_ReturnCorrectTaskList() throws Exception {
        Long id = 3L;
        mockMvc.perform(get(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .param("projectId", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Try to find all tasks for project which id is not existing")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void retrieveAllTasksForProject_NotExistingProjectId_ReturnEmptyList() throws Exception {
        Long id = 20L;
        mockMvc.perform(get(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .param("projectId", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("Find task by id where logged-in user is assignee")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void retrieveTaskDetails_ExistingTaskId_ReturnCorrectTask() throws Exception {
        Long taskId = 1L;
        TaskDto expectedTaskDto = new TaskDto();
        expectedTaskDto.setName("Task 1");
        expectedTaskDto.setDescription("Description for Task 1");
        expectedTaskDto.setId(taskId);
        expectedTaskDto.setStatus(Task.Status.IN_PROGRESS);
        expectedTaskDto.setPriority(Task.Priority.HIGH);
        expectedTaskDto.setDueDate(LocalDate.parse("2024-11-15"));
        expectedTaskDto.setProjectName("Project Alpha");
        expectedTaskDto.setAssigneeUsername("testUser");

        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + SEPARATOR + taskId))
                .andExpect(status().isOk())
                .andReturn();
        TaskDto actualTaskDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                TaskDto.class);
        EqualsBuilder.reflectionEquals(expectedTaskDto, actualTaskDto, "labelsId");
    }

    @Test
    @DisplayName("Find task by id where logged-in user is not assignee")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void retrieveTaskDetails_ExistingTaskIdButUserIsNotAssignee_ThrowException() throws Exception {
        Long taskId = 3L;

        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + SEPARATOR + taskId))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = String.format(
                "User with username: %s doesn't have task with id: %d",
                "testUser", taskId);
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Find any task details with existing id")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void retrieveAnyTaskDetails_ExistingTAskId_ReturnCorrectTask() throws Exception {
        Long taskId = 3L;
        TaskDto expectedTaskDto = new TaskDto();
        expectedTaskDto.setName("Task 3");
        expectedTaskDto.setDescription("Description for Task 3");
        expectedTaskDto.setId(taskId);
        expectedTaskDto.setStatus(Task.Status.NOT_STARTED);
        expectedTaskDto.setPriority(Task.Priority.MEDIUM);
        expectedTaskDto.setDueDate(LocalDate.parse("2024-11-30"));
        expectedTaskDto.setProjectName("Project Gamma");
        expectedTaskDto.setAssigneeUsername("testUser2");

        MvcResult result = mockMvc.perform(get(
                URL_TEMPLATE + MANAGER_ENDPOINT + SEPARATOR + taskId))
                .andExpect(status().isOk())
                .andReturn();
        TaskDto actualTaskDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                TaskDto.class);
        EqualsBuilder.reflectionEquals(expectedTaskDto, actualTaskDto, "labelsId");
    }

    @Test
    @DisplayName("Find any task details with not existing id")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void retrieveAnyTaskDetails_NotExistingTAskId_ThrowException() throws Exception {
        Long taskId = 7L;

        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + MANAGER_ENDPOINT
                        + SEPARATOR + taskId))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "Can't find task with id: " + taskId;

        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Update task with valid request parameters")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void updateTask_ValidParameters_Success() throws Exception {
        Event mockEvent = new Event();
        mockEvent.setId("mockEventId");
        given(googleCalendarService.createEvent(Mockito.any())).willReturn(mockEvent);
        given(googleCalendarService.insertEventInToCalendar(Mockito.any())).willReturn(mockEvent);
        Long id = 1L;
        TaskDto expectedTaskDto = new TaskDto();
        expectedTaskDto.setId(id);
        expectedTaskDto.setName("New name");
        expectedTaskDto.setDescription("New description");
        expectedTaskDto.setStatus(Task.Status.IN_PROGRESS);
        expectedTaskDto.setPriority(Task.Priority.HIGH);
        expectedTaskDto.setDueDate(LocalDate.parse("2024-11-15"));
        expectedTaskDto.setAssigneeUsername("testUser");
        expectedTaskDto.setAssigneeUsername("Project Alpha");

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto("New name", "New description", null,
                null, null, null, new ArrayList<>());
        String json = objectMapper.writeValueAsString(taskUpdateDto);

        MvcResult result = mockMvc.perform(put(URL_TEMPLATE + SEPARATOR + id)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        TaskDto actualTaskDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                TaskDto.class);
        EqualsBuilder.reflectionEquals(expectedTaskDto, actualTaskDto, "labelsId");
    }

    @Test
    @DisplayName("Update task with valid request parameters but user is not task assignee")
    @WithMockCustomUser(username = "testUser2", roles = "ROLE_USER", userId = 2L)
    void updateTask_ValidParametersButInvalidUser_ThrowException() throws Exception {
        Event mockEvent = new Event();
        mockEvent.setId("mockEventId");
        given(googleCalendarService.createEvent(Mockito.any())).willReturn(mockEvent);
        given(googleCalendarService.insertEventInToCalendar(Mockito.any())).willReturn(mockEvent);
        Long id = 1L;

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto("New name", "New description", null,
                null, null, null, null);
        String json = objectMapper.writeValueAsString(taskUpdateDto);

        MvcResult result = mockMvc.perform(put(URL_TEMPLATE + SEPARATOR + id)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = String.format(
                "User with username: %s doesn't have task with id: %d",
                "testUser2", id);
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Update any task with correct parameters")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void updateTaskForManager_ValidParameters_ReturnCorrectTask() throws Exception {
        Event mockEvent = new Event();
        mockEvent.setId("mockEventId");
        given(googleCalendarService.createEvent(Mockito.any())).willReturn(mockEvent);
        given(googleCalendarService.insertEventInToCalendar(Mockito.any())).willReturn(mockEvent);
        Long id = 3L;
        TaskDto expectedTaskDto = new TaskDto();
        expectedTaskDto.setId(id);
        expectedTaskDto.setName("New name");
        expectedTaskDto.setDescription("New description");
        expectedTaskDto.setStatus(Task.Status.COMPLETED);
        expectedTaskDto.setPriority(Task.Priority.LOW);
        expectedTaskDto.setDueDate(LocalDate.parse("2024-11-15"));
        expectedTaskDto.setAssigneeUsername("testUser");
        expectedTaskDto.setAssigneeUsername("Project Gamma");

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto("New name", "New description",
                Task.Priority.LOW, Task.Status.COMPLETED,
                LocalDate.parse("2024-11-15"), 21L, new ArrayList<>());
        String json = objectMapper.writeValueAsString(taskUpdateDto);

        MvcResult result = mockMvc.perform(put(URL_TEMPLATE + MANAGER_ENDPOINT + SEPARATOR + id)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        TaskDto actualTaskDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                TaskDto.class);
        EqualsBuilder.reflectionEquals(expectedTaskDto, actualTaskDto, "labelsId");
    }

    @Test
    @DisplayName("Delete task with existing id")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void deleteTask() throws Exception {
        long id = 1L;
        mockMvc.perform(delete(URL_TEMPLATE + MANAGER_ENDPOINT + SEPARATOR + id))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Find all task in project where logged-in user has access to")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void retrieveAllTasksForProject_ExistingProjectId_ReturnCorrectTask() throws Exception {
        mockMvc.perform(get(URL_TEMPLATE + ALL_ENDPOINT)
                        .param("projectId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}
