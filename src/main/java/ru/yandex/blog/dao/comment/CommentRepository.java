package ru.yandex.blog.dao.comment;

import ru.yandex.blog.model.Comment;

import java.util.Optional;

public interface CommentRepository {
    Optional<Comment> findCommentById(int id);
    Optional<Comment> insertComment(int postId, String commentText);
    Optional<Comment> updateComment(int id, Comment comment);
    void deleteComment(int id);

    void deleteCommentByPostId(int postId);
}
