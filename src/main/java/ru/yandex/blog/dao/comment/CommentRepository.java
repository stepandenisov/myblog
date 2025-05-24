package ru.yandex.blog.dao.comment;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.blog.model.Comment;

import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    Optional<Comment> findCommentById(Long id);
    @Modifying
    @Query("delete from POST_COMMENTS c where c.ID = :id")
    void deleteCommentById(@Param("id") Long id);
    @Modifying
    @Query("delete from POST_COMMENTS c where c.POST_ID = :post_id")
    void deleteCommentsByPostId(@Param("post_id") Long postId);
}
