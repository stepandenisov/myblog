package ru.yandex.blog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.blog.configuration.DataSourceConfiguration;
import ru.yandex.blog.configuration.WebConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class CommentControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Очистка и заполнение тестовых данных в базе
        jdbcTemplate.execute("TRUNCATE TABLE post_comments RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=0");
        jdbcTemplate.execute("TRUNCATE TABLE posts RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=1");

        jdbcTemplate.execute("insert into posts(title, post_text, likes_count) values ('First post', 'Text of the first post', 0);");
        jdbcTemplate.execute("insert into posts(title, post_text, likes_count) values ('Second post', 'Text of the second post', 0);");
        jdbcTemplate.execute("insert into posts(title, post_text, likes_count) values ('Third post', 'Text of the third post', 0);");

        jdbcTemplate.execute("insert into post_comments(post_id, comment_text) values (1, 'First comment');");
        jdbcTemplate.execute("insert into post_comments(post_id, comment_text) values (2, 'Second comment');");
        jdbcTemplate.execute("insert into post_comments(post_id, comment_text) values (2, 'Third comment');");
    }

    @Test
    void add_shouldAddCommentToPostAndRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/comments")
                        .param("text", "Test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    void update_shouldUpdateCommentAndRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/comments/1")
                        .param("text", "new test text"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    void delete_shouldDeleteCommentAndRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/comments/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }
}
