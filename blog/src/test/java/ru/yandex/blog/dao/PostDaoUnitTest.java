package ru.yandex.blog.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.dao.tag.TagRepository;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.model.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PostDaoUnitTest {

    @Autowired
    private PostRepository postRepository;


    @BeforeEach
    public void setUp() {
        postRepository.deleteAll();
    }

    @Test
    public void save_shouldSavePost() {

        Post post = new Post(null, "test", "test", 0, new ArrayList<>(), new ArrayList<>());
        Post insertedPost = postRepository.save(post);
        assertEquals("test", insertedPost.getTitle(), "Имя должно быть test");

    }

    @Test
    public void findById_shouldReturnPost() {

        Post postToInsert = new Post(null, "test", "test", 0, new ArrayList<>(), new ArrayList<>());
        Post insertedPost = postRepository.save(postToInsert);

        Optional<Post> post = postRepository.findById(insertedPost.getId());
        assertTrue(post.isPresent(), "Пост должен быть");
        assertEquals(insertedPost.getId(), post.get().getId(), "id должны совпадать");

    }
}
