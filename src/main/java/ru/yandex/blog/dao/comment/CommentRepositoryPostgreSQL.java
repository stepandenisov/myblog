package ru.yandex.blog.dao.comment;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Post;

import java.util.Optional;

@Repository
public class CommentRepositoryPostgreSQL implements CommentRepository{

    private final JdbcTemplate jdbcTemplate;
    public CommentRepositoryPostgreSQL(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Comment> commentRowMapper = (rs, rowNum) -> new Comment(
            rs.getInt("id"),
            rs.getInt("post_id"),
            rs.getString("comment_text"));

    @Override
    public Optional<Comment> findCommentById(int id) {
        return Optional.of(jdbcTemplate.queryForObject(
                """
                select post_comments.id, post_comments.post_id, post_comments.comment_text
                from post_comments
                where id = ?
                """,
                new Object[]{id},
                commentRowMapper));
    }

    @Override
    public Optional<Comment> insertComment(int postId, String commentText) {
        int insertedId = jdbcTemplate.queryForObject(
                """
                select id
                  from final TABLE (
                    insert into post_comments (post_id, comment_text)
                    VALUES (?, ?)
                  ) comments
                """,
                new Object[]{postId, commentText},
                (rs, rowNum) -> rs.getInt("id"));
        return Optional.of(jdbcTemplate.queryForObject(
                """
                select post_comments.id, post_comments.post_id, post_comments.comment_text
                from post_comments
                where id = ?
                """,
                new Object[]{insertedId},
                commentRowMapper));
    }

    @Override
    public Optional<Comment> updateComment(int id, Comment comment) {
        int updateCount = jdbcTemplate.update(
                """
                update post_comments
                set post_comments.post_id = ?, post_comments.comment_text = ?
                where id = ?
                """,
                comment.getPostId(), comment.getText(), id);
        return updateCount == 1 ? Optional.of(comment) : Optional.empty();
    }

    @Override
    public void deleteComment(int id) {
        jdbcTemplate.update(
                """
                delete from post_comments where id = ?
                """,
                id);
    }
}
