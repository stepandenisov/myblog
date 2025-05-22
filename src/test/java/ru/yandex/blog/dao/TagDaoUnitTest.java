package ru.yandex.blog.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.yandex.blog.dao.tag.TagRepository;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.model.Tag;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class TagDaoUnitTest {

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    public void setUp() {
        tagRepository.deleteAll();
    }

    @Test
    public void save_shouldSaveTag() {

        Tag tag = new Tag(null, "test");
        Tag insertedTag = tagRepository.save(tag);
        assertEquals("test", insertedTag.getName(), "Имя должно быть test");

    }

    @Test
    public void findById_shouldReturnPost() {

        Tag tagToInsert = new Tag(null, "test");
        Tag insertedTag = tagRepository.save(tagToInsert);

        Optional<Tag> tag = tagRepository.findById(insertedTag.getId());
        assertTrue(tag.isPresent(), "Пост должен быть");
        assertEquals(insertedTag.getId(), tag.get().getId(), "id должны совпадать");

    }
}
