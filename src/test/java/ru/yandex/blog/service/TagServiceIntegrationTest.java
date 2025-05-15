package ru.yandex.blog.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.configuration.ServiceConfiguration;
import ru.yandex.blog.model.Post;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {ServiceConfiguration.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class TagServiceIntegrationTest {

    @Autowired
    private TagService tagService;

    @Autowired
    private PostService postService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Очистка и заполнение тестовых данных в базе
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=0");
        jdbcTemplate.execute("TRUNCATE TABLE tags RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=1");

        jdbcTemplate.execute("insert into tags(tag_name) values ('First tag');");
        jdbcTemplate.execute("insert into tags(tag_name) values ('Second tag');");
        jdbcTemplate.execute("insert into tags(tag_name) values ('Third tag');");
    }

    @Test
    public void addTags_shouldReturnIdsOfInsertedTags() {
        String[] tags = new String[]{"First tag", "Second tag"};
        List<Integer> tagIds = List.of(1, 2);
        List<Integer> insertedTagIds = tagService.addTags(String.join(", ", tags));
        assertEquals(tagIds, insertedTagIds, "все идентификаторы должны совпадать");
    }

    @Test
    public void deleteTagsFromDeletedPost_shouldDeleteUnusedTags() {
        Integer tagId = tagService.addTags("First tag").get(0);
        postService.deletePost(1);
        tagService.deleteTagsFromDeletedPost();
        Integer insertedTagId = tagService.addTags("First tag").get(0);
        assertNotEquals(tagId, insertedTagId, "id должны быть разными");
    }
}
