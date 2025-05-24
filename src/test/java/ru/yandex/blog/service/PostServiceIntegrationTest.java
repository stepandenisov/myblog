package ru.yandex.blog.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.blog.model.Paging;
import ru.yandex.blog.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class PostServiceIntegrationTest {

    @Value("${project-path}")
    private String projectPath;

    @Autowired
    private PostService postService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
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
    void updatePost_shouldUpdatePost() {

        postService.updatePost(1L, "title", "text", "tag", new byte[]{1});
        Optional<Post> result = postService.findById(1L);
        assertTrue(result.isPresent(), "Обновленный пост должен существовать");
        Post post = result.get();

        assertEquals(1, post.getId(), "id поста д.б. 1");
        assertEquals("title", post.getTitle(), "title поста д.б. равен title");
        assertEquals("text", post.getText(), "text поста д.б. равен text");
        assertEquals(1, post.getTags().length, "Количество тегов д.б. 1");
        assertEquals("tag", post.getTags()[0], "Теги поста д.б. равны [\"tag\"]");
    }

    @Test
    void deletePost_shouldDeletePost() {
        postService.deletePost(1L);
        Optional<Post> result = postService.findById(1L);
        assertTrue(result.isEmpty(), "Удаленный пост должен отсутствовать");
    }

    @Test
    void insertPost_shouldInsertPost() {
        Long insertedPostId = postService.insertPost("Test", "Test", "First tag", new byte[]{1});
        assertEquals(4, insertedPostId, "Вставленный id должен быть равен 4");
    }

    @Test
    void findById_shouldReturnPostById() {
        Optional<Post> result = postService.findById(1L);
        assertTrue(result.isPresent(), "Пост должен существовать");
        Post post = result.get();
        assertEquals(1, post.getId(), "id должен быть равен 1");
        assertEquals("First post", post.getTitle(), "title должен быть равен title");
        assertEquals("First tag", post.getText(), "text должен быть равен text");
        assertEquals(0, post.getLikesCount(), "likesCount должен быть равен 0");
        assertEquals(1, post.getTags().length, "Колчичество тегов первого поста д.б равно 1");
        assertEquals("Text of the first post", post.getTags()[0], "Первый тег первого поста должен быть First tag");
    }

    @Test
    void updatePost_shouldUpdatePostAndReturnUpdated() {
        Post testPost = new Post(
                1L,
                "title",
                "text",
                0,
                new ArrayList<>(),
                "First tag");

        Optional<Post> result = postService.updatePost(1L, testPost);
        assertTrue(result.isPresent(), "Пост должен существовать");
        Post post = result.get();
        assertEquals(1, post.getId(), "id должен быть равен 1");
        assertEquals("title", post.getTitle(), "title должен быть равен title");
        assertEquals("text", post.getText(), "text должен быть равен text");
        assertEquals(0, post.getLikesCount(), "likesCount должен быть равен 0");
        assertArrayEquals(new String[]{"First tag"}, post.getTags(), "tags должны совпадать");
    }

    @Test
    void searchPaginated_shouldReturnPaginatedPosts() {
        Paging paging = new Paging(1, 10, false, false);
        List<Post> result = postService.searchPaginated("", paging);
        assertEquals(3, result.size(), "Количество постов должно быть 3");
        Post post = result.get(0);
        assertEquals(1, post.getId(), "id первого поста должен быть равен 1");
        assertEquals("First post", post.getTitle(), "title первого поста должен быть равен First post");
        assertEquals("First tag", post.getText(), "text первого поста должен быть равен First tag");
        assertEquals(0, post.getLikesCount(), "likesCount должен быть равен 0");
    }
}
