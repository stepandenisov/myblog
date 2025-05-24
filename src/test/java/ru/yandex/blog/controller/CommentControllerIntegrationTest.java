package ru.yandex.blog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
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
    }

    @Test
    void addComment_shouldAddCommentToPostAndRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/comments")
                        .param("text", "Test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    void updateComment_shouldUpdateCommentAndRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/comments/1")
                        .param("text", "new test text"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    void deleteComment_shouldDeleteCommentAndRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/comments/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }
}
