package ru.yandex.blog.dao.comment;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.blog.model.Comment;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findCommentById(Long id);
    @Transactional
    void deleteCommentById(Long id);
    @Transactional
    void deleteCommentsByPostId(Long postId);
}
