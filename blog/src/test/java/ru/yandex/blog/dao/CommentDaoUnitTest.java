package ru.yandex.blog.dao;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.yandex.blog.dao.comment.CommentRepository;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.model.Tag;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CommentDaoUnitTest {

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    public void setUp() {
        commentRepository.deleteAll();
    }

    @Test
    public void save_shouldSaveComment() {
        Post post = new Post(1L, "text", "text", 0, new ArrayList<>(), new ArrayList<>());
        Comment comment = new Comment(null, post, "test");
        Comment insertedComment = commentRepository.save(comment);
        assertEquals("test", insertedComment.getText(), "Текст должен быть test");

    }

    @Test
    public void findById_shouldReturnComment() {

        Post post = new Post(1L, "text", "text", 0, new ArrayList<>(), new ArrayList<>());
        Comment commentToInsert = new Comment(null, post, "test");
        Comment insertedComment = commentRepository.save(commentToInsert);

        Optional<Comment> comment = commentRepository.findById(insertedComment.getId());
        assertTrue(comment.isPresent(), "Комментарий должен быть");
        assertEquals(insertedComment.getId(), comment.get().getId(), "id должны совпадать");

    }

    @Test
    public void deleteCommentById_shouldDeleteById() {

        Post post = new Post(1L, "text", "text", 0, new ArrayList<>(), new ArrayList<>());
        Comment commentToInsert = new Comment(null, post, "test");
        Comment insertedComment = commentRepository.save(commentToInsert);

        commentRepository.deleteCommentById(insertedComment.getId());
        Optional<Comment> comment = commentRepository.findCommentById(insertedComment.getId());
        assertTrue(comment.isEmpty(), "Комментарий должен отсутствовать");

    }

    @Test
    public void deleteCommentsByPostId_shouldDeleteByPostId() {

        Post post = new Post(1L, "text", "text", 0, new ArrayList<>(), new ArrayList<>());
        Comment commentToInsert = new Comment(null, post, "test");
        Comment insertedComment = commentRepository.save(commentToInsert);

        commentRepository.deleteCommentsByPostId(1L);
        Optional<Comment> comment = commentRepository.findCommentById(insertedComment.getId());
        assertTrue(comment.isEmpty(), "Комментарий должен отсутствовать");

    }

}
