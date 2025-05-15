package ru.yandex.blog.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.blog.configuration.DataSourceConfiguration;
import ru.yandex.blog.configuration.ServiceConfiguration;
import ru.yandex.blog.configuration.WebConfiguration;
import ru.yandex.blog.dto.PostDto;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Paging;
import ru.yandex.blog.model.Post;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

@SpringJUnitConfig(classes = {ServiceConfiguration.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
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
    void update_shouldUpdatePost() {
        postService.updatePost(1, "title", "text", "tag", new byte[]{1});
        Optional<Post> result = postService.findById(1);
        assertTrue(result.isPresent(), "Обновленный пост должен существовать");
        Post post = result.get();

        assertEquals(1, post.getId(), "id поста д.б. 1");
        assertEquals("title", post.getTitle(), "title поста д.б. равен title");
        assertEquals("text", post.getText(), "text поста д.б. равен text");
        assertEquals(1, post.getTags().length, "Количество тегов д.б. 1");
        assertEquals("tag", post.getTags()[0], "Теги поста д.б. равны [\"tag\"]");
    }

    @Test
    void delete_shouldDeletePost() {
        postService.deletePost(1);
        Optional<Post> result = postService.findById(1);
        assertTrue(result.isEmpty(), "Удаленный пост должен отсутствовать");
    }

    @Test
    void insert_shouldInsertPost() {
        int insertedPostId = postService.insertPost("Test", "Test", "First tag", new byte[]{1});
        assertEquals(4, insertedPostId, "Вставленный id должен быть равен 4");
    }

    @Test
    void getById_shouldReturnPostById() {
        Optional<Post> result = postService.findById(1);
        assertTrue(result.isPresent(), "Пост должен существовать");
        Post post = result.get();
        assertEquals(1, post.getId(), "id должен быть равен 1");
        assertEquals("First post", post.getTitle(), "title должен быть равен title");
        assertEquals("Text of the first post", post.getText(), "text должен быть равен text");
        assertEquals(0, post.getLikesCount(), "likesCount должен быть равен 0");
        assertEquals(
                List.of(new Comment(1, 1, "First comment")),
                post.getComments(),
                "comments должны совпадать");
        assertEquals(2, post.getTags().length, "Колчичество тегов первого поста д.б равно 2");
        assertEquals("First tag", post.getTags()[0], "Первый тег первого поста должен быть First tag");
    }

    @Test
    void update_shouldUpdatePostAndReturnUpdated() {
        List<Comment> commentList = List.of(new Comment(1, 1, "comment"));
        String[] tags = new String[]{"tag"};
        Post testPost = new Post(
                1,
                "title",
                "text",
                0,
                commentList,
                tags);

        Optional<Post> result = postService.updatePost(1, testPost);
        assertTrue(result.isPresent(), "Пост должен существовать");
        Post post = result.get();
        assertEquals(1, post.getId(), "id должен быть равен 1");
        assertEquals("title", post.getTitle(), "title должен быть равен title");
        assertEquals("text", post.getText(), "text должен быть равен text");
        assertEquals(0, post.getLikesCount(), "likesCount должен быть равен 0");
        assertEquals(commentList, post.getComments(), "comments должны совпадать");
        assertEquals(tags, post.getTags(), "tags должны совпадать");
    }

    @Test
    void search_shouldReturnPaginatedPosts() {
        Paging paging = new Paging(1, 10, false, false);
        List<Post> result = postService.searchPaginated("", paging);
        assertEquals(3, result.size(), "Количество постов должно быть 3");
        Post post = result.get(0);
        assertEquals(1, post.getId(), "id первого поста должен быть равен 1");
        assertEquals("First post", post.getTitle(), "title первого поста должен быть равен First post");
        assertEquals("Text of the first post", post.getText(), "text первого поста должен быть равен Text of the first post");
        assertEquals(0, post.getLikesCount(), "likesCount должен быть равен 0");
    }
}
