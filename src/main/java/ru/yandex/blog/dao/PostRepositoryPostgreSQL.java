package ru.yandex.blog.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.blog.model.Paging;
import ru.yandex.blog.model.Post;

import java.sql.PreparedStatement;
import java.util.*;

@Repository
public class PostRepositoryPostgreSQL implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostRepositoryPostgreSQL(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Post> findById(int id) {
        return Optional.of(jdbcTemplate.queryForObject(
                "select * from posts where 'id'=?",
                Post.class,
                id));
    }

    @Override
    public List<Post> searchPaginated(String search, Paging paging) {
        int skip = (paging.getPageNumber() - 1) * paging.getPageSize();
        int limit = paging.getPageSize();

        RowMapper<Post> postRowMapper = (rs, rowNum) -> new Post(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("post_text"),
                rs.getString("image_path"),
                rs.getLong("likes_count"),
                rs.getObject("post_comments", String[].class),
                rs.getObject("tag_array", String[].class));

        List<Post> posts = jdbcTemplate.query(
                """
                    select distinct posts.id, posts.title, posts.post_text, posts.image_path, posts.likes_count, posts.post_comments, posts_tags_joined.tag_array from posts
                    left outer join posts_tags on posts.id = posts_tags.post_id
                    left outer join  (
                       select posts_tags.post_id as id, array_agg(posts_tags_joined.tag_name) as tag_array
                       from   posts_tags
                       join   tags       posts_tags_joined  on posts_tags_joined.id = posts_tags.tag_id
                       group  by posts_tags.post_id
                       ) posts_tags_joined using (id)
                    where ? = any(posts_tags_joined.tag_array)
                    order by posts.id              
                    limit ? offset ?
                """,
                new Object[]{search, limit, skip},
                postRowMapper);
        Integer firstPostId = jdbcTemplate.queryForObject("""
                select posts.id from posts
                left outer join posts_tags on posts.id = posts_tags.post_id
                left outer join tags on posts_tags.tag_id = tags.id
                where tags.tag_name= ?
                order by posts.id
                limit 1
                """,
                new Object[]{search},
                (rs, rowNum) -> rs.getInt("posts.id"));
        Integer lastPostId = jdbcTemplate.queryForObject("""
                select posts.id from posts
                left outer join posts_tags on posts.id = posts_tags.post_id
                left outer join tags on posts_tags.tag_id = tags.id
                where tags.tag_name= ?
                order by posts.id desc
                limit 1
                """,
                new Object[]{search},
                (rs, rowNum) -> rs.getInt("id"));
        paging.setHasNext(posts.stream().map(Post::getId).noneMatch(lastPostId::equals));
        paging.setHasPrevious(posts.stream().map(Post::getId).noneMatch(firstPostId::equals));
        return posts;
    }
}
