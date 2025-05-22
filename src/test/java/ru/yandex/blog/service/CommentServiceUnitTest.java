package ru.yandex.blog.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.dao.comment.CommentRepository;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Post;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class CommentServiceUnitTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Test
    public void insertComment_shouldReturnInsertedComment(){

        Post post = new Post();
        Comment comment = new Comment(null, post, "test");
        Comment insertedComment = new Comment(1L, post, "test");

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));
        when(commentRepository.save(comment))
                .thenReturn(insertedComment);

        Optional<Comment> result = commentService.insertComment(1L, "test");
        assertTrue(result.isPresent(), "Вставленный комментарий не должен отсутствовать");
        assertEquals(1L, insertedComment.getId(), "id комментария должен быть 1");
        assertNotNull(result.get().getPost(), "У комментария должен быть пост");
        assertEquals("test", insertedComment.getText(), "Комментарий должен быть test");
    }

    @Test
    public void updateComment_shouldReturnUpdatedComment(){
        Post post = new Post();
        Comment comment = new Comment(1L, post, "");

        when(commentRepository.findCommentById(1L))
                .thenReturn(Optional.of(comment));

        when(commentRepository.save(comment))
                .thenReturn(comment);

        Comment testComment = new Comment(1L, post, "test");

        Optional<Comment> result = commentService.updateComment(1L, testComment);
        assertTrue(result.isPresent(), "Обновленный комментарий не должен отсутствовать");
        assertEquals(1, result.get().getId(), "id комментария должен быть 1");
        assertNotNull(result.get().getPost(), "Пост должен быть");
        assertEquals("test", comment.getText(), "Комментарий должен быть test");
    }

    @Test
    public void findCommentById_shouldReturnComment(){

        Post post = new Post();
        Comment testComment = new Comment(1L, post, "test");

        when(commentRepository.findCommentById(1L))
                .thenReturn(Optional.of(testComment));

        Optional<Comment> result = commentService.findCommentById(1L);
        assertTrue(result.isPresent(), "Комментарий не должен отсутствовать");
        Comment comment = result.get();
        assertEquals(1, comment.getId(), "id комментария должен быть 1");
        assertNotNull(comment.getPost(), "Пост должен быть");
        assertEquals("test", comment.getText(), "Комментарий должен быть test");
    }
}
