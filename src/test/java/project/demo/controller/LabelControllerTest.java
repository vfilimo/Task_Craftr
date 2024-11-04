package project.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import project.demo.dto.label.LabelSaveDto;
import project.demo.model.Label;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LabelControllerTest {
    protected static MockMvc mockMvc;
    private static final String URL_TEMPLATE = "/labels";
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
                    new ClassPathResource("db/labels/insert_labels_to_db.sql"));
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
                    new ClassPathResource("db/labels/delete_labels_from_db.sql"));
        }
    }

    @Test
    @DisplayName("Create new Label with valid parameters")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void createNewLabel_ValidParameters_Success() throws Exception {
        LabelSaveDto labelSaveDto = new LabelSaveDto("new label", "blue");
        Label expectedLabel = new Label(4L);
        expectedLabel.setName(labelSaveDto.name());
        expectedLabel.setColor(labelSaveDto.color());
        String json = objectMapper.writeValueAsString(labelSaveDto);
        MvcResult result = mockMvc.perform(post(URL_TEMPLATE)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        Label actualLabel = objectMapper.readValue(result.getResponse().getContentAsString(),
                Label.class);
        EqualsBuilder.reflectionEquals(expectedLabel, actualLabel, "id");
    }

    @Test
    @DisplayName("Create new Label with in valid parameters")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void createNewLabel_InValidParameters_BlankName_Success() throws Exception {
        LabelSaveDto labelSaveDto = new LabelSaveDto("      ", "blue");
        String json = objectMapper.writeValueAsString(labelSaveDto);

        MvcResult result = mockMvc.perform(post(URL_TEMPLATE)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "name must not be blank";
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Find all labels")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void getAllLabels_ReturnCorrectListLabels_Success() throws Exception {
        MvcResult result = mockMvc.perform(get(URL_TEMPLATE))
                .andExpect(status().isOk())
                .andReturn();
        List<Label> labels = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<Label>>() {});
        assertEquals(3, labels.size());
    }

    @Test
    @DisplayName("Update label with valid parameter")
    @WithMockUser(username = "user", roles = "USER")
    void updateLabel_ValidParameters_Success() throws Exception {
        long id = 3;
        LabelSaveDto updateLabel = new LabelSaveDto("new label name", "black");
        Label expectedLabel = new Label(id);
        expectedLabel.setName(updateLabel.name());
        expectedLabel.setColor(updateLabel.color());
        String json = objectMapper.writeValueAsString(updateLabel);

        MvcResult result = mockMvc.perform(put(URL_TEMPLATE + SEPARATOR + id)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Label actualLabel = objectMapper.readValue(result.getResponse().getContentAsString(),
                Label.class);
        EqualsBuilder.reflectionEquals(expectedLabel, actualLabel);
    }

    @Test
    @DisplayName("Update label with in valid parameter")
    @WithMockUser(username = "user", roles = "USER")
    void updateLabel_InValidParameters_Success() throws Exception {
        long id = 3;
        LabelSaveDto updateLabel = new LabelSaveDto("new label name", "      ");

        String json = objectMapper.writeValueAsString(updateLabel);

        MvcResult result = mockMvc.perform(put(URL_TEMPLATE + SEPARATOR + id)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String message = "color must not be blank";
        assertTrue(contentAsString.contains(message));
    }

    @Test
    @DisplayName("Delete label")
    @WithMockUser(username = "manager", roles = "MANAGER")
    void deleteLabel_ExistingId_Succes() throws Exception {
        long id = 2;

        mockMvc.perform(delete(URL_TEMPLATE + SEPARATOR + id))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
