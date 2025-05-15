package ru.yandex.blog.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.configuration.ServiceConfiguration;
import ru.yandex.blog.dao.comment.CommentRepository;
import ru.yandex.blog.model.Comment;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {ServiceConfiguration.class})
@TestPropertySource(locations = "classpath:test-application.properties")
@ExtendWith(MockitoExtension.class)
public class CommentServiceUnitTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Test
    public void insert_shouldReturnInsertedComment(){

        when(commentRepository.insertComment(1, "test"))
                .thenReturn(Optional.of(new Comment(1, 1, "test")));

        Optional<Comment> result = commentService.insertComment(1, "test");
        assertTrue(result.isPresent(), "Вставленный комментарий не должен отсутствовать");
        Comment comment = result.get();
        assertEquals(1, comment.getId(), "id комментария должен быть 1");
        assertEquals(1, comment.getPostId(), "id поста должен быть 1");
        assertEquals("test", comment.getText(), "Комментарий должен быть test");
    }

    @Test
    public void update_shouldReturnUpdatedComment(){

        Comment testComment = new Comment(1, 1, "test");

        when(commentRepository.updateComment(1, testComment))
                .thenReturn(Optional.of(testComment));

        Optional<Comment> result = commentService.updateComment(1, testComment);
        assertTrue(result.isPresent(), "Обновленный комментарий не должен отсутствовать");
        Comment comment = result.get();
        assertEquals(1, comment.getId(), "id комментария должен быть 1");
        assertEquals(1, comment.getPostId(), "id поста должен быть 1");
        assertEquals("test", comment.getText(), "Комментарий должен быть test");
    }

    @Test
    public void findCommentById_shouldReturnComment(){

        Comment testComment = new Comment(1, 1, "test");

        when(commentRepository.findCommentById(1))
                .thenReturn(Optional.of(testComment));

        Optional<Comment> result = commentService.findCommentById(1);
        assertTrue(result.isPresent(), "Комментарий не должен отсутствовать");
        Comment comment = result.get();
        assertEquals(1, comment.getId(), "id комментария должен быть 1");
        assertEquals(1, comment.getPostId(), "id поста должен быть 1");
        assertEquals("test", comment.getText(), "Комментарий должен быть test");
    }
}
