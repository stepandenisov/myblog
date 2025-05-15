package ru.yandex.blog.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.configuration.ServiceConfiguration;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.dto.PostDto;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Paging;
import ru.yandex.blog.model.Post;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {ServiceConfiguration.class})
@TestPropertySource(locations = "classpath:test-application.properties")
@ExtendWith(MockitoExtension.class)
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
        when(tagService.addTags("First tag"))
                .thenReturn(List.of(1));
        doReturn(1).when(postRepository)
                .insertPost(new PostDto("Test", "Test", List.of(1)));

        doReturn(1).when(imageService)
                .addImageByPostId(1, new byte[]{1});

        int insertedPostId = postService.insertPost("Test", "Test", "First tag", new byte[]{1});
        assertEquals(1, insertedPostId, "Вставленный id должен быть равен 1");
    }

    @Test
    void findById_shouldReturnPostById() {
        List<Comment> commentList = List.of(new Comment(1, 1, "comment"));
        String[] tags = new String[]{"tag"};
        Post testPost = new Post(
                1,
                "title",
                "text",
                0,
                commentList,
                tags);
        doReturn(Optional.of(testPost)).when(postRepository)
                .findById(1);

        Optional<Post> result = postService.findById(1);
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
    void updatePost_shouldUpdatePostAndReturnUpdated() {
        List<Comment> commentList = List.of(new Comment(1, 1, "comment"));
        String[] tags = new String[]{"tag"};
        Post testPost = new Post(
                1,
                "title",
                "text",
                0,
                commentList,
                tags);
        doReturn(Optional.of(testPost)).when(postRepository)
                .updatePost(1, testPost);

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
    void searchPaginated_shouldReturnPaginatedPosts() {
        List<Comment> commentList = List.of(new Comment(1, 1, "comment"));
        String[] tags = new String[]{"tag"};
        List<Post> postList = List.of(
                new Post(
                        1,
                        "title",
                        "text",
                        0,
                        commentList,
                        tags)
        );
        Paging paging = new Paging(1, 10, false, false);
        doReturn(postList).when(postRepository)
                .searchPaginated("test", paging);

        List<Post> result = postService.searchPaginated("test", paging);
        assertEquals(1, result.size(), "Количество постов должно быть 1");
        Post post = result.get(0);
        assertEquals(1, post.getId(), "id должен быть равен 1");
        assertEquals("title", post.getTitle(), "title должен быть равен title");
        assertEquals("text", post.getText(), "text должен быть равен text");
        assertEquals(0, post.getLikesCount(), "likesCount должен быть равен 0");
        assertEquals(commentList, post.getComments(), "comments должны совпадать");
        assertEquals(tags, post.getTags(), "tags должны совпадать");
    }
}
