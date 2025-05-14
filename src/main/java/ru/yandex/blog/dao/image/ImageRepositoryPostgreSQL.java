package ru.yandex.blog.dao.image;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ImageRepositoryPostgreSQL implements ImageRepository {

    private final JdbcTemplate jdbcTemplate;

    public ImageRepositoryPostgreSQL(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public byte[] getImageByPostId(int postId) {
        return jdbcTemplate.queryForObject(
                "select image from images where post_id = ? limit 1",
                new Object[]{postId},
                ((rs, rowNum) -> rs.getBytes("image"))
        );
    }

    @Override
    public Integer addImageByPostId(int postId, byte[] image) {
        return jdbcTemplate.queryForObject(
                """
                        select id
                          from final TABLE (
                            insert into images (post_id, image)
                            VALUES (?, ?)
                          ) images
                        """,
                new Object[]{postId, image},
                (rs, rowNum) -> rs.getInt("id"));
    }

    @Override
    public void deleteImageByPostId(int postId) {
        jdbcTemplate.update("""
                delete from images where post_id = ?
                """,
                postId);
    }

    @Override
    public void updateImageByPostId(int postId, byte[] image) {
        jdbcTemplate.update("""
                update images set image = ? where post_id = ?
                """,
                image, postId);
    }
}
