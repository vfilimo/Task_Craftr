package project.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.sql.Connection;
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
import project.demo.dto.attachment.AttachmentDownloadDto;
import project.demo.dto.attachment.AttachmentDto;
import project.demo.dto.attachment.AttachmentSaveDto;
import project.demo.external.file.sharing.FileSharingService;
import project.demo.security.WithMockCustomUser;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AttachmentControllerTest {
    protected static MockMvc mockMvc;
    private static final String URL_TEMPLATE = "/attachments";
    private static final String MANAGER_ENDPOINT = "/manager";
    private static final String SEPARATOR = "/";
    private static final String DOWNLOADED_FILE_PATH =
            "src/test/resources/db/attachments/test_downloaded_file.txt";
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
                    new ClassPathResource("db/users/insert_users_to_db.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/tasks/insert_tasks_to_db.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/attachments/insert_attachment_to_db.sql"));
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
                    new ClassPathResource("db/attachments/delete_attachments_from_db.sql"));
        }
    }

    @Test
    @DisplayName("Save new attachment for login role USER")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void saveAttachment_ValidParameters_Success() throws Exception {
        AttachmentSaveDto newAttachment = new AttachmentSaveDto(
                "db/attachments/test_upload_file.txt", 1L);
        String dropboxId = "some_id";
        AttachmentDto expectedAttachmentDto = new AttachmentDto(3L, "Task 1", dropboxId,
                "test_upload_file.txt", null);
        String json = objectMapper.writeValueAsString(newAttachment);
        given(fileSharingService.uploadAttachment(Mockito.any())).willReturn(dropboxId);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        AttachmentDto actualAttachmentDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), AttachmentDto.class);

        EqualsBuilder.reflectionEquals(expectedAttachmentDto, actualAttachmentDto, "uploadTime");
    }

    @Test
    @DisplayName("Save new attachment for login role USER, task assignee is not logged-in user")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void saveAttachment_ValidParametersWrongTask_ThrowException() throws Exception {
        Long id = 3L;
        AttachmentSaveDto newAttachment = new AttachmentSaveDto(
                "db/attachments/test_upload_file.txt", id);
        String dropboxId = "some_id";

        String json = objectMapper.writeValueAsString(newAttachment);
        given(fileSharingService.uploadAttachment(Mockito.any())).willReturn(dropboxId);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "User with username: testUser doesn't have task with id: " + id;
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Save new attachment for login role USER with in valid parameters")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void saveAttachment_InValidParameters_ThrowException() throws Exception {
        Long id = 3L;
        AttachmentSaveDto newAttachment = new AttachmentSaveDto("    ", id);
        String dropboxId = "some_id";

        String json = objectMapper.writeValueAsString(newAttachment);
        given(fileSharingService.uploadAttachment(Mockito.any())).willReturn(dropboxId);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "path must not be blank";
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Save new attachment for manager with valid parameters")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void saveAttachmentForManager_ValidParameters_Success() throws Exception {
        AttachmentSaveDto newAttachment = new AttachmentSaveDto(
                "db/attachments/test_upload_file.txt", 3L);
        String dropboxId = "some_id";
        AttachmentDto expectedAttachmentDto = new AttachmentDto(3L, "Task 3", dropboxId,
                "test_upload_file.txt", null);
        String json = objectMapper.writeValueAsString(newAttachment);
        given(fileSharingService.uploadAttachment(Mockito.any())).willReturn(dropboxId);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        AttachmentDto actualAttachmentDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), AttachmentDto.class);

        EqualsBuilder.reflectionEquals(expectedAttachmentDto, actualAttachmentDto, "uploadTime");
    }

    @Test
    @DisplayName("Save new attachment for manager with blank path")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void saveAttachmentForManager_InValidParameters_ThrowException() throws Exception {
        AttachmentSaveDto newAttachment = new AttachmentSaveDto("     ", 3L);
        String dropboxId = "some_id";
        String json = objectMapper.writeValueAsString(newAttachment);
        given(fileSharingService.uploadAttachment(Mockito.any())).willReturn(dropboxId);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();

        String message = "path must not be blank";
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Save new attachment for manager with not existing task id")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void saveAttachmentForManager_ValidParameters_NotExistingTaskId_ThrowException()
            throws Exception {
        AttachmentSaveDto newAttachment = new AttachmentSaveDto(
                "db/attachments/test_upload_file.txt", 9L);
        String dropboxId = "some_id";
        String json = objectMapper.writeValueAsString(newAttachment);
        given(fileSharingService.uploadAttachment(Mockito.any())).willReturn(dropboxId);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();

        String message = "Can't find task with id: 9";
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Find all attachment for task where logged-in user is assignee")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void getAttachmentsForTask_ExistingTaskId_ReturnCorrectTaskList() throws Exception {
        Long id = 1L;
        MvcResult result = mockMvc.perform(get(URL_TEMPLATE)
                        .param("taskId", id.toString()))
                .andExpect(status().isOk())
                .andReturn();
        List<AttachmentDto> attachmentDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<AttachmentDto>>() {});
        assertEquals(1, attachmentDtoList.size());
    }

    @Test
    @DisplayName("Find all attachment for task where logged-in user is not assignee")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void getAttachmentsForTask_ExistingTaskId_EmptyList() throws Exception {
        Long id = 3L;
        MvcResult result = mockMvc.perform(get(URL_TEMPLATE)
                        .param("taskId", id.toString()))
                .andExpect(status().isOk())
                .andReturn();
        List<AttachmentDto> attachmentDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<AttachmentDto>>() {});
        assertTrue(attachmentDtoList.isEmpty());
    }

    @Test
    @DisplayName("Find all attachments for any task")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void getAttachmentsForTaskForManager_ExistingTaskId_ReturnCorrectTaskList() throws Exception {
        Long id = 3L;
        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .param("taskId", id.toString()))
                .andExpect(status().isOk())
                .andReturn();
        List<AttachmentDto> attachmentDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<AttachmentDto>>() {});
        assertEquals(1, attachmentDtoList.size());
    }

    @Test
    @DisplayName("Find all attachments for not existing task id")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void getAttachmentsForTaskForManager_NotExistingTaskId_ReturnEmptyList()
            throws Exception {
        Long id = 50L;
        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + MANAGER_ENDPOINT)
                        .param("taskId", id.toString()))
                .andExpect(status().isOk())
                .andReturn();
        List<AttachmentDto> attachmentDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<AttachmentDto>>() {});
        assertTrue(attachmentDtoList.isEmpty());
    }

    @Test
    @DisplayName("Download file for logged-in user task")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void downloadAttachmentById_ExistingId_Success() throws Exception {
        long id = 1;
        Path mockFilePath = Path.of(DOWNLOADED_FILE_PATH);
        AttachmentDownloadDto expectedDownloadDto =
                new AttachmentDownloadDto("DOWNLOADED", mockFilePath.toString());

        given(fileSharingService.downloadFile(Mockito.any())).willReturn(mockFilePath);
        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + SEPARATOR + id))
                .andExpect(status().isOk())
                .andReturn();
        AttachmentDownloadDto actualDownloadDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), AttachmentDownloadDto.class);
        assertEquals(expectedDownloadDto, actualDownloadDto);
    }

    @Test
    @DisplayName("Try to download file for logged-in user not task assignee")
    @WithMockCustomUser(username = "testUser", roles = "ROLE_USER", userId = 21L)
    void downloadAttachmentById_NotExistingId_ThrowException() throws Exception {
        long id = 2;
        Path mockFilePath = Path.of(DOWNLOADED_FILE_PATH);
        given(fileSharingService.downloadFile(Mockito.any())).willReturn(mockFilePath);

        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + SEPARATOR + id))
                .andExpect(status().isNotFound())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        String message = "Can't find attachment with id: 2 for user: testUser";
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Download attachment for any task")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void downloadAttachmentByIdForManager_ExistingId_Success() throws Exception {
        Long id = 2L;
        Path mockFilePath = Path.of(DOWNLOADED_FILE_PATH);
        AttachmentDownloadDto expectedDownloadDto =
                new AttachmentDownloadDto("DOWNLOADED", mockFilePath.toString());

        given(fileSharingService.downloadFile(Mockito.any())).willReturn(mockFilePath);
        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + MANAGER_ENDPOINT + SEPARATOR + id))
                .andExpect(status().isOk())
                .andReturn();
        AttachmentDownloadDto actualDownloadDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), AttachmentDownloadDto.class);
        assertEquals(expectedDownloadDto, actualDownloadDto);
    }

    @Test
    @DisplayName("Try to download file for manager by not existing attachment id")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void downloadAttachmentByIdForManager_NotExistingId_ThrowException() throws Exception {
        long id = 22L;
        Path mockFilePath = Path.of(DOWNLOADED_FILE_PATH);
        given(fileSharingService.downloadFile(Mockito.any())).willReturn(mockFilePath);

        MvcResult result = mockMvc.perform(get(URL_TEMPLATE + MANAGER_ENDPOINT + SEPARATOR + id))
                .andExpect(status().isNotFound())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        String message = "Can't find attachment with id: " + id;
        assertTrue(contentAsString.contains(message));
    }
}
