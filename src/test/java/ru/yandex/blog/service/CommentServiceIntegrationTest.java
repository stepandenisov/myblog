package ru.yandex.blog.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.configuration.ServiceConfiguration;
import ru.yandex.blog.model.Comment;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {ServiceConfiguration.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

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
    void insertComment_shouldInsertComment() {

        Optional<Comment> result = commentService.insertComment(1, "test");
        assertTrue(result.isPresent(), "Вставленный пост должен существовать");
        Comment comment = result.get();

        assertEquals("test", comment.getText(), "text поста д.б. равен test");
        assertEquals(1, comment.getPostId(), "postId поста д.б. равен 1");

    }

    @Test
    void updateComment_shouldUpdateComment() {

        Comment testComment = new Comment(1, 1, "test");

        Optional<Comment> result = commentService.updateComment(1, testComment);
        assertTrue(result.isPresent(), "Обновленный комментарий должен существовать");
        Comment comment = result.get();

        assertEquals("test", comment.getText(), "text поста д.б. равен test");
        assertEquals(1, comment.getPostId(), "postId поста д.б. равен 1");
    }

    @Test
    void findCommentById_shouldReturnComment() {
        Optional<Comment> result = commentService.findCommentById(1);
        assertTrue(result.isPresent(), "Комментарий не должен отсутствовать");
        Comment comment = result.get();
        assertEquals(1, comment.getId(), "id комментария должен быть 1");
        assertEquals(1, comment.getPostId(), "id поста должен быть 1");
        assertEquals("First comment", comment.getText(), "Комментарий должен быть First comment");
    }

    @Test
    void delete_shouldDeleteComment() {
        commentService.deleteComment(1);
        Optional<Comment> result = commentService.findCommentById(1);
        assertTrue(result.isEmpty(), "Удаленный комментарий должен отсутствовать");
    }

    @Test
    void deleteCommentByPostId_shouldDeleteComment() {
        commentService.deleteCommentByPostId(1);
        Optional<Comment> result = commentService.findCommentById(1);
        assertTrue(result.isEmpty(), "Удаленный комментарий должен отсутствовать");
    }
}
