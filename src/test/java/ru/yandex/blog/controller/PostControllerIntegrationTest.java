package ru.yandex.blog.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
public class PostControllerIntegrationTest {

    @Value("${project-path}")
    private String projectPath;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Очистка и заполнение тестовых данных в базе
        jdbcTemplate.execute("TRUNCATE TABLE IMAGES RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE POST_COMMENTS RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=0");
        jdbcTemplate.execute("TRUNCATE TABLE POSTS RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=1");

        jdbcTemplate.execute("insert into POSTS(TITLE, POST_TEXT, POST_TAGS, LIKES_COUNT) values ('First post', 'First tag','Text of the first post', 0);");
        jdbcTemplate.execute("insert into POSTS(TITLE, POST_TEXT, POST_TAGS, LIKES_COUNT) values ('Second post', 'Second tag', 'Text of the second post', 0);");
        jdbcTemplate.execute("insert into POSTS(TITLE, POST_TEXT, POST_TAGS, LIKES_COUNT) values ('Third post', 'Third tag', 'Text of the third post', 0);");

        jdbcTemplate.execute("insert into POST_COMMENTS(POST_ID, COMMENT_TEXT) values (1, 'First comment');");
        jdbcTemplate.execute("insert into POST_COMMENTS(POST_ID, COMMENT_TEXT) values (2, 'Second comment');");
        jdbcTemplate.execute("insert into POST_COMMENTS(POST_ID, COMMENT_TEXT) values (2, 'Third comment');");

        jdbcTemplate.execute("insert into IMAGES(POST_ID, IMAGE) values (1, file_read('" + projectPath + "src/main/resources/assets/1.jpg'));");
        jdbcTemplate.execute("insert into IMAGES(POST_ID, IMAGE) values (2, file_read('" + projectPath + "src/main/resources/assets/2.jpg'));");
        jdbcTemplate.execute("insert into IMAGES(POST_ID, IMAGE) values (3, file_read('" + projectPath + "src/main/resources/assets/3.jpg'));");
    }

    @Test
    void posts_shouldReturnHtmlWithPosts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(xpath("//table/tr").nodeCount(4))
                .andExpect(xpath("//table/tr[2]/td").nodeCount(1))
                .andExpect(xpath("//table/tr[2]/td/h2").nodeCount(1))
                .andExpect(xpath("//table/tr[2]/td/h2").string("First post"));
    }

    @Test
    void post_shouldReturnHtmlWithPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("//table/tr").nodeCount(6))
                .andExpect(xpath("//table/tr[2]/td").nodeCount(1))
                .andExpect(xpath("//table/tr[2]/td/h2").nodeCount(1))
                .andExpect(xpath("//table/tr[2]/td/h2").string("First post"));
    }

    @Test
    void insert_shouldAddPostToDatabaseAndRedirect() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "", "application/json", "{\"image\": \"D:\\Projects\\myblog\\src\\main\\resources\\assets\\1.jpg\"}".getBytes());
        MockPart titlePart = new MockPart("title", "Test".getBytes());
        MockPart textPart = new MockPart("text", "Test".getBytes());
        MockPart tagsPart = new MockPart("tags", "Test".getBytes());
        mockMvc.perform(multipart("/posts/")
                        .file(image)
                        .part(titlePart)
                        .part(textPart)
                        .part(tagsPart))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/4"));
    }

    @Test
    void edit_shouldEditPostToDatabaseAndRedirect() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "", "application/json", "{\"image\": \"D:\\Projects\\myblog\\src\\main\\resources\\assets\\1.jpg\"}".getBytes());
        MockPart titlePart = new MockPart("title", "Test".getBytes());
        MockPart textPart = new MockPart("text", "Test".getBytes());
        MockPart tagsPart = new MockPart("tags", "Test".getBytes());
        mockMvc.perform(multipart("/posts/1")
                        .file(image)
                        .part(titlePart)
                        .part(textPart)
                        .part(tagsPart))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    void edit_shouldRedirectToEditPostPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("add-post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("//table/tr").nodeCount(5))
                .andExpect(xpath("//table/tr[1]/td").nodeCount(1))
                .andExpect(xpath("//table/tr[1]/td/textarea").nodeCount(1))
                .andExpect(xpath("//table/tr[1]/td/textarea").string("First post"));
    }

    @Test
    void delete_shouldDeletePostAndRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    void addPost_shouldReturnAddPostView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-post"));
    }

    @Test
    void changeLikes_shouldRedirectToPostById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/like")
                        .param("like", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }
}
