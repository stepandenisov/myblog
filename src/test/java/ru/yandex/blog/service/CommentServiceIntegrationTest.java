package ru.yandex.blog.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Post;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
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
    void insertComment_shouldInsertComment() {

        commentService.insertComment(1L, "test");
        Optional<Comment> result = commentService.findCommentById(4L);
        assertTrue(result.isPresent(), "Вставленный пост должен существовать");
        Comment comment = result.get();

        assertEquals("test", comment.getText(), "text поста д.б. равен test");
        assertEquals(1, comment.getPostId(), "postId поста д.б. равен 1");

    }

    @Test
    void updateComment_shouldUpdateComment() {

        Optional<Post> post = postService.findById(1L);
        assertTrue(post.isPresent(), "Пост должен существовать");
        Comment testComment = new Comment(1L, post.get().getId(), "test");

        commentService.updateComment(1L, testComment);
        Optional<Comment> result = commentService.findCommentById(1L);
        assertTrue(result.isPresent(), "Обновленный комментарий должен существовать");
        Comment comment = result.get();

        assertEquals("test", comment.getText(), "text поста д.б. равен test");
        assertEquals(1L, comment.getPostId(), "postId поста д.б. равен 1");
    }

    @Test
    void findCommentById_shouldReturnComment() {
        Optional<Comment> result = commentService.findCommentById(1L);
        assertTrue(result.isPresent(), "Комментарий не должен отсутствовать");
        Comment comment = result.get();
        assertEquals(1, comment.getId(), "id комментария должен быть 1");
        assertEquals(1, comment.getPostId(), "id поста должен быть 1");
        assertEquals("First comment", comment.getText(), "Комментарий должен быть First comment");
    }

    @Test
    void delete_shouldDeleteComment() {
        commentService.deleteComment(1L);
        Optional<Comment> result = commentService.findCommentById(1L);
        assertTrue(result.isEmpty(), "Удаленный комментарий должен отсутствовать");
    }

    @Test
    void deleteCommentByPostId_shouldDeleteComment() {
        commentService.deleteCommentsByPostId(1L);
        Optional<Comment> result = commentService.findCommentById(1L);
        assertTrue(result.isEmpty(), "Удаленный комментарий должен отсутствовать");
    }
}
