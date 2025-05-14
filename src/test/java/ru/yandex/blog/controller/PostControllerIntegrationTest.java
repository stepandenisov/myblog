package ru.yandex.blog.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.blog.configuration.WebConfiguration;
import ru.yandex.blog.configuration.DataSourceConfiguration;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostControllerIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Очистка и заполнение тестовых данных в базе
        jdbcTemplate.execute("TRUNCATE TABLE images RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE posts_tags RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE post_comments RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=0");
        jdbcTemplate.execute("TRUNCATE TABLE tags RESTART IDENTITY");
        jdbcTemplate.execute("TRUNCATE TABLE posts RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=1");

        jdbcTemplate.execute("insert into posts(title, post_text, likes_count) values ('First post', 'Text of the first post', 0);");
        jdbcTemplate.execute("insert into posts(title, post_text, likes_count) values ('Second post', 'Text of the second post', 0);");
        jdbcTemplate.execute("insert into posts(title, post_text, likes_count) values ('Third post', 'Text of the third post', 0);");

        jdbcTemplate.execute("insert into tags(tag_name) values ('First tag');");
        jdbcTemplate.execute("insert into tags(tag_name) values ('Second tag');");
        jdbcTemplate.execute("insert into tags(tag_name) values ('Third tag');");

        jdbcTemplate.execute("insert into post_comments(post_id, comment_text) values (1, 'First comment');");
        jdbcTemplate.execute("insert into post_comments(post_id, comment_text) values (2, 'Second comment');");
        jdbcTemplate.execute("insert into post_comments(post_id, comment_text) values (2, 'Third comment');");

        jdbcTemplate.execute("insert into posts_tags(post_id, tag_id) values (1, 1);");
        jdbcTemplate.execute("insert into posts_tags(post_id, tag_id) values (2, 2);");
        jdbcTemplate.execute("insert into posts_tags(post_id, tag_id) values (3, 3);");
        jdbcTemplate.execute("insert into posts_tags(post_id, tag_id) values (1, 3);");

        jdbcTemplate.execute("insert into images(post_id, image) values (1, file_read('D:\\Projects\\myblog\\src\\main\\resources\\assets\\1.jpg'));");
        jdbcTemplate.execute("insert into images(post_id, image) values (2, file_read('D:\\Projects\\myblog\\src\\main\\resources\\assets\\2.jpg'));");
        jdbcTemplate.execute("insert into images(post_id, image) values (3, file_read('D:\\Projects\\myblog\\src\\main\\resources\\assets\\3.jpg'));");
    }

    @Test
    void getPosts_shouldReturnHtmlWithPosts() throws Exception {
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
    void getPost_shouldReturnHtmlWithPost() throws Exception {
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
    void add_shouldAddPostToDatabaseAndRedirect() throws Exception {
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
    void getEditPostPage_shouldRedirectToEditPostPage() throws Exception {
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
    void addPostPage_shouldReturnAddPostView() throws Exception {
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
