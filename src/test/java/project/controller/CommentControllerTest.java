package project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import project.dto.comment.CommentDto;
import project.dto.comment.CreateCommentDto;
import project.external.file.sharing.FileSharingService;
import project.security.WithMockCustomUser;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentControllerTest {
    protected static MockMvc mockMvc;
    private static final String URL_TEMPLATE = "/comments";
    private static final String MANAGER_ENDPOINT = "/manager";
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private FileSharingService fileSharingService;

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
                    new ClassPathResource(
                            "db/users/insert_users_to_db_for_comment_controller_test.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/tasks/insert_tasks_to_db.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/comments/insert_comments_to_db.sql"));
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
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/comments/delete_comments_from_db.sql"));
        }
    }

    @Test
    @DisplayName("Create new comment for task where logged-in user is manager")
    @WithMockCustomUser(username = "testManager", roles = "ROLE_MANAGER", userId = 5L)
    void createNewComment_ValidParameters_Success() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto(1L, "First comment");
        CommentDto expectedCommentDto = new CommentDto(4L, "Task 1", "testUser",
                createCommentDto.text(), null);
        String json = objectMapper.writeValueAsString(createCommentDto);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CommentDto actualCommentDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), CommentDto.class);

        EqualsBuilder.reflectionEquals(expectedCommentDto, actualCommentDto, "timestamp");
    }

    @Test
    @DisplayName("Create new comment for not existing task where logged-in user is manager")
    @WithMockCustomUser(username = "testManager", roles = "ROLE_MANAGER", userId = 5L)
    void createNewComment_ValidParametersNotExistingTask_ThrowException() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto(10L, "First comment");

        String json = objectMapper.writeValueAsString(createCommentDto);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "Task wit id: 10 is not exist";

        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Create new comment with blank text field task where logged-in user is manager")
    @WithMockCustomUser(username = "testManager", roles = "ROLE_MANAGER", userId = 5L)
    void createNewComment_InValidParameters_ThrowException() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto(1L, "        ");

        String json = objectMapper.writeValueAsString(createCommentDto);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "text must not be blank";

        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Create new comment for task where logged-in user is user")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void createNewCommentForAssignee_ValidParameters_Success() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto(1L, "First comment");
        CommentDto expectedCommentDto = new CommentDto(4L, "Task 1", "testUser",
                createCommentDto.text(), null);
        String json = objectMapper.writeValueAsString(createCommentDto);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CommentDto actualCommentDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), CommentDto.class);

        EqualsBuilder.reflectionEquals(expectedCommentDto, actualCommentDto, "timestamp");
    }

    @Test
    @DisplayName("Create new comment for not task where logged-in user is not assignee")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void createNewCommentForAssignee_ValidParametersNotExistingTask_ThrowException()
            throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto(3L, "First comment");

        String json = objectMapper.writeValueAsString(createCommentDto);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "Can't find task with id: 3 for user: testUser";

        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Create new comment with null taskId field where logged-in user is user")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void createNewCommentForAssignee_InValidParameters_ThrowException() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto(null, "First comment");

        String json = objectMapper.writeValueAsString(createCommentDto);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "taskId must not be null";

        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Find all comments for task where logged-in user is assignee")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void getAllCommentsForTask_ExistingTaskId_ReturnCorrectList() throws Exception {
        Long taskId = 1L;

        MvcResult result = mockMvc.perform(get(URL_TEMPLATE)
                        .param("taskId", taskId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        List<CommentDto> commentDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<CommentDto>>() {});

        assertEquals(1, commentDtoList.size());
    }

    @Test
    @DisplayName("Find all comments for task where logged-in user is not assignee")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void getAllCommentsForTask_NotExistingTaskId_EmptyList() throws Exception {
        Long taskId = 3L;

        MvcResult result = mockMvc.perform(get(URL_TEMPLATE)
                        .param("taskId", taskId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        List<CommentDto> commentDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<CommentDto>>() {});

        assertTrue(commentDtoList.isEmpty());
    }

    @Test
    @DisplayName("Find all comments for any task where logged-in user manager")
    @WithMockCustomUser(username = "testManager", roles = "ROLE_MANAGER", userId = 5L)
    void getAllCommentsForAnyTask_ExistingTaskId_ReturnCorrectList() throws Exception {
        Long taskId = 2L;

        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .param("taskId", taskId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        List<CommentDto> commentDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<CommentDto>>() {});

        assertEquals(1, commentDtoList.size());
    }

    @Test
    @DisplayName("Find all comments for any task where taskId is not existing")
    @WithMockCustomUser(username = "testManager", roles = "ROLE_MANAGER", userId = 5L)
    void getAllCommentsForAnyTask_NotExistingTaskId_EmptyList() throws Exception {
        Long taskId = 30L;

        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .param("taskId", taskId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        List<CommentDto> commentDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<CommentDto>>() {});

        assertTrue(commentDtoList.isEmpty());
    }
}
