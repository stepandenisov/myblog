package ru.yandex.blog.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.model.Tag;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class TagServiceIntegrationTest {

    @Autowired
    private TagService tagService;

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
    public void getTagsFromString_shouldReturnIdsOfInsertedTags() {
        String[] tags = new String[]{"First tag", "Second tag"};
        List<Integer> tagIds = List.of(1, 2);
        List<Tag> insertedTagIds = tagService.getTagsFromString(String.join(", ", tags));
        assertArrayEquals(tagIds.toArray(), insertedTagIds.stream().map(Tag::getId).map(Long::intValue).toArray(), "все идентификаторы должны совпадать");
    }

    @Test
    public void findTagByName_shouldFindTag() {
        Tag tag = tagService.getTagsFromString("First tag").get(0);
        assertEquals(tag.getName(), "First tag", "id должны быть разными");
    }

    @Test
    public void save_shouldSaveTag(){
        tagService.save("new tag");
        Optional<Tag> tag = tagService.findTagByName("new tag");
        assertTrue(tag.isPresent(), "Тег должен существовать");
        assertEquals("new tag", tag.get().getName(), "Названия должны совпадать");
    }
}
