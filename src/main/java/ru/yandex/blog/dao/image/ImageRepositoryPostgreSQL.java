package ru.yandex.blog.dao.image;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ImageRepositoryPostgreSQL implements ImageRepository{

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
}
