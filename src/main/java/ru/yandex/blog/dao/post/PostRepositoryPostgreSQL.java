package ru.yandex.blog.dao.post;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.blog.dto.PostDto;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Paging;
import ru.yandex.blog.model.Post;

import java.util.*;

@Repository
public class PostRepositoryPostgreSQL implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Post> postRowMapper = (rs, rowNum) -> {
        int postId = rs.getInt("id");
        Integer[] commentIds = rs.getObject("comment_id_array", Integer[].class);
        String[] commentTexts = rs.getObject("comment_text_array", String[].class);
        List<Comment> comments = new ArrayList<>();
        if (commentIds != null) {
            for (int i = 0; i < commentIds.length; i++) {
                comments.add(new Comment(commentIds[i], postId, commentTexts[i]));
            }
        }
        return new Post(
                postId,
                rs.getString("title"),
                rs.getString("post_text"),
                rs.getLong("likes_count"),
                comments,
                rs.getObject("tag_array", String[].class));
    };

    public PostRepositoryPostgreSQL(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Post> findById(int id) {
        try {
            Post post = jdbcTemplate.queryForObject(
                    """
                                select distinct posts.id, posts.title, posts.post_text, posts.likes_count, comments_joined.comment_text_array, comments_joined.comment_id_array, posts_tags_joined.tag_array from posts
                                left outer join posts_tags on posts.id = posts_tags.post_id
                                left outer join  (
                                   select posts_tags.post_id as id, array_agg(posts_tags_joined.tag_name) as tag_array
                                   from   posts_tags
                                   join   tags       posts_tags_joined  on posts_tags_joined.id = posts_tags.tag_id
                                   group  by posts_tags.post_id
                                   ) posts_tags_joined using (id)
                                left outer join  (
                                   select posts.id as id, array_agg(post_comments.comment_text) comment_text_array, array_agg(post_comments.id) comment_id_array
                                   from   posts
                                   join post_comments on post_comments.post_id = posts.id
                                   group  by posts.id
                                   ) comments_joined using (id)
                                where posts.id=?
                            """,
                    new Object[]{id},
                    postRowMapper);
            return Optional.of(post);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<Post> searchPaginated(String search, Paging paging) {
        int skip = (paging.getPageNumber() - 1) * paging.getPageSize();
        int limit = paging.getPageSize();


        List<Post> posts = jdbcTemplate.query(
                """
                            select distinct posts.id, posts.title, posts.post_text, posts.likes_count, comments_joined.comment_text_array, comments_joined.comment_id_array, posts_tags_joined.tag_array from posts
                            left outer join posts_tags on posts.id = posts_tags.post_id
                            left outer join  (
                               select posts_tags.post_id as id, array_agg(posts_tags_joined.tag_name) as tag_array
                               from   posts_tags
                               join   tags       posts_tags_joined  on posts_tags_joined.id = posts_tags.tag_id
                               group  by posts_tags.post_id
                               ) posts_tags_joined using (id)
                            left outer join  (
                               select posts.id as id, array_agg(post_comments.comment_text) comment_text_array, array_agg(post_comments.id) comment_id_array
                               from   posts
                               join post_comments on post_comments.post_id = posts.id
                               group  by posts.id
                               ) comments_joined using (id)
                            where ? = any(posts_tags_joined.tag_array) or ?=''
                            order by posts.id
                            limit ? offset ?
                        """,
                new Object[]{search, search, limit, skip},
                postRowMapper);
        if (posts.isEmpty()) return posts;
        Integer firstPostId = jdbcTemplate.queryForObject("""
                        select posts.id from posts
                        left outer join posts_tags on posts.id = posts_tags.post_id
                        left outer join tags on posts_tags.tag_id = tags.id
                        where tags.tag_name= ? or ?=''
                        order by posts.id
                        limit 1
                        """,
                new Object[]{search, search},
                (rs, rowNum) -> rs.getInt("posts.id"));
        Integer lastPostId = jdbcTemplate.queryForObject("""
                        select posts.id from posts
                        left outer join posts_tags on posts.id = posts_tags.post_id
                        left outer join tags on posts_tags.tag_id = tags.id
                        where tags.tag_name= ? or ?=''
                        order by posts.id desc
                        limit 1
                        """,
                new Object[]{search, search},
                (rs, rowNum) -> rs.getInt("id"));
        paging.setHasNext(posts.stream().map(Post::getId).noneMatch(lastPostId::equals));
        paging.setHasPrevious(posts.stream().map(Post::getId).noneMatch(firstPostId::equals));
        return posts;
    }

    @Override
    public Optional<Post> updatePost(int id, Post post) {
        int updateCount = jdbcTemplate.update(
                """
                        update posts
                        set posts.title = ?, posts.post_text = ?, posts.likes_count = ?
                        where id = ?
                        """,
                post.getTitle(), post.getText(), post.getLikesCount(), id);
        return updateCount == 1 ? Optional.of(post) : Optional.empty();
    }

    public int insertPost(PostDto postDto) {
        int insertedPostId = jdbcTemplate.queryForObject(
                """
                        select id
                          from final TABLE (
                            insert into posts (title, post_text, likes_count)
                            VALUES (?, ?, ?)
                          ) posts
                        """,
                new Object[]{postDto.getTitle(), postDto.getText(), 0},
                (rs, rowNum) -> rs.getInt("id"));

        String insertTagsSql = String.join(",", Collections.nCopies(postDto.getTagIds().size(), String.format("(%d, ?)", insertedPostId)));

        jdbcTemplate.update(
                String.format("""
                        insert into posts_tags (post_id, tag_id)
                        values %s
                        """, insertTagsSql),
                postDto.getTagIds().toArray());
        return insertedPostId;
    }

    @Override
    public void deletePost(int id) {
        jdbcTemplate.update("""
                        delete from posts_tags where post_id = ?
                        """,
                id);
        jdbcTemplate.update("""
                        delete from posts where id = ?
                        """,
                id);
    }

    @Override
    public void updatePost(int id, PostDto postDto) {
        jdbcTemplate.update(
                """
                        update posts set posts.title = ?, posts.post_text = ?
                        where id = ?
                        """,
                postDto.getTitle(), postDto.getText(), id);

        String insertTagsSql = String.join(",", Collections.nCopies(postDto.getTagIds().size(), String.format("(%d, ?)", id)));

        jdbcTemplate.update(
                """
                        delete from posts_tags where post_id = ?
                        """,
                id);

        jdbcTemplate.update(
                String.format("""
                        insert into posts_tags (post_id, tag_id)
                        values %s
                        """, insertTagsSql),
                postDto.getTagIds().toArray());
    }
}
