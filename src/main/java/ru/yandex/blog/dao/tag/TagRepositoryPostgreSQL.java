package ru.yandex.blog.dao.tag;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class TagRepositoryPostgreSQL implements TagRepository {

    private final JdbcTemplate jdbcTemplate;

    public TagRepositoryPostgreSQL(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Integer> addTags(String[] tags) {

        String inSql = String.join(",", Collections.nCopies(tags.length, "?"));

        List<Integer> tagIds = jdbcTemplate.query(String.format("""
                        select id from tags where tag_name in (%s)
                        """, inSql),
                tags,
                (rs, rowNum) -> rs.getInt("id"));


        Function<String, Integer> addTag = tag -> {
            try {
                return jdbcTemplate.queryForObject(
                        """
                                select id
                                  from final TABLE (
                                    insert into tags (tag_name)
                                    values (?)
                                ) tags
                                """,
                        new Object[]{tag},
                        (rs, rowNum) -> rs.getInt("id"));
            } catch (Exception exception) {
                return -1;
            }
        };
        tagIds.addAll(Arrays.stream(tags).map(addTag).filter(id -> id > 0).collect(Collectors.toList()));
        return tagIds;
    }

    @Override
    public void deleteTagsFromDeletedPost() {
        jdbcTemplate.update("""
                delete from tags where id not in (select distinct tag_id from posts_tags)
                """);
    }
}
