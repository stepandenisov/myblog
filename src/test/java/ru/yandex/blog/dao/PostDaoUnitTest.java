package ru.yandex.blog.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.blog.dao.comment.CommentRepository;
import ru.yandex.blog.dao.image.ImageRepository;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.model.Post;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PostDaoUnitTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CommentRepository commentRepository;


    @BeforeEach
    public void setUp() {
        imageRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    public void save_shouldSavePost() {

        Post post = new Post(null, "test", "test", 0, new ArrayList<>(), "First tag");
        Post insertedPost = postRepository.save(post);
        assertEquals("test", insertedPost.getTitle(), "Имя должно быть test");

    }

    @Test
    public void findById_shouldReturnPost() {

        Post postToInsert = new Post(null, "test", "test", 0, new ArrayList<>(), "First tag");
        Post insertedPost = postRepository.save(postToInsert);

        Optional<Post> post = postRepository.findById(insertedPost.getId());
        assertTrue(post.isPresent(), "Пост должен быть");
        assertEquals(insertedPost.getId(), post.get().getId(), "id должны совпадать");

    }
}
