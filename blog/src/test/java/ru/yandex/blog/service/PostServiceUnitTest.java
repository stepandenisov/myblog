package ru.yandex.blog.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class PostServiceUnitTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagService tagService;

    @Mock
    private ImageService imageService;

    @Test
    void insertPost_shouldInsertPost() {

        Post post = new Post(null, "Test", "Test", 0L, new ArrayList<>(), new ArrayList<>());
        Post insertedPost = new Post(1L, "Test", "Test", 0L, new ArrayList<>(), new ArrayList<>());

        doReturn(new Tag()).when(tagService)
                .save("First tag");
        doReturn(insertedPost).when(postRepository)
                .save(post);

        doReturn(Optional.of(new Image())).when(imageService)
                .addImageByPostId(1L, new byte[]{1});

        Long insertedPostId = postService.insertPost("Test", "Test", "First tag", new byte[]{1});
        assertEquals(1, insertedPostId, "Вставленный id должен быть равен 1");
    }

    @Test
    void findById_shouldReturnPostById() {
        Post testPost = new Post(
                1L,
                "title",
                "text",
                0,
                new ArrayList<>(),
                new ArrayList<>());
        doReturn(Optional.of(testPost)).when(postRepository)
                .findById(1L);

        Optional<Post> result = postService.findById(1L);
        assertTrue(result.isPresent(), "Пост должен существовать");
        Post post = result.get();
        assertEquals(1, post.getId(), "id должен быть равен 1");
        assertEquals("title", post.getTitle(), "title должен быть равен title");
        assertEquals("text", post.getText(), "text должен быть равен text");
        assertEquals(0, post.getLikesCount(), "likesCount должен быть равен 0");
    }

    @Test
    void updatePost_shouldUpdatePostAndReturnUpdated() {

        Post updatePost = new Post(1L, "New", "New", 0L, new ArrayList<>(), new ArrayList<>());

        doReturn(Optional.of(updatePost)).when(postRepository)
                .findById(1L);
        doReturn(new Tag()).when(tagService)
                .save("First tag");
        doNothing().when(imageService)
                .updateImageByPostId(1L, new byte[]{1});
        doReturn(updatePost).when(postRepository)
                .save(updatePost);

        Optional<Post> result = postService.updatePost(1L, updatePost);
        assertTrue(result.isPresent(), "Пост должен существовать");
        assertEquals(1L, result.get().getId(), "id должен быть равен 1");
        assertEquals("New", result.get().getTitle(), "title должен быть равен title");
        assertEquals("New", result.get().getText(), "text должен быть равен text");
        assertEquals(0, result.get().getLikesCount(), "likesCount должен быть равен 0");
    }

    @Test
    void searchPaginated_shouldReturnPaginatedPosts() {
        List<Post> postList = List.of(
                new Post(
                        1L,
                        "title",
                        "text",
                        0,
                        new ArrayList<>(),
                        new ArrayList<>())
        );
        Paging paging = new Paging(1, 10, false, false);
        Tag tag = new Tag(1L, "test");
        doReturn(postList).when(postRepository)
                .findAllByTagsContains(PageRequest.of(0, 10), tag);
        doReturn(Optional.of(tag)).when(tagService)
                .findTagByName("test");

        List<Post> result = postService.searchPaginated("test", paging);
        assertEquals(1, result.size(), "Количество постов должно быть 1");
        Post post = result.get(0);
        assertEquals(1, post.getId(), "id должен быть равен 1");
        assertEquals("title", post.getTitle(), "title должен быть равен title");
        assertEquals("text", post.getText(), "text должен быть равен text");
        assertEquals(0, post.getLikesCount(), "likesCount должен быть равен 0");
    }
}
