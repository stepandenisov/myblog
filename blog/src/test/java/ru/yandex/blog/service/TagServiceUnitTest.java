package ru.yandex.blog.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.dao.tag.TagRepository;
import ru.yandex.blog.model.Tag;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class TagServiceUnitTest {
    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @Test
    public void getTagsFromString_shouldReturnIdsOfInsertedTags(){
        String[] tags = new String[]{"First tag", "Second tag"};
        List<Long> tagIds = List.of(1L, 2L);
        when(tagRepository.findTagByName("First tag"))
                .thenReturn(new Tag(1L, "First tag"));
        when(tagRepository.findTagByName("Second tag"))
                .thenReturn(new Tag(2L, "Second tag"));
        List<Tag> insertedTags = tagService.getTagsFromString(String.join(", ", tags));
        assertArrayEquals(tagIds.toArray(), insertedTags.stream().map(Tag::getId).toArray(), "все идентификаторы должны совпадать");
    }

    @Test
    public void save_shouldSaveTag(){
        Tag tag = new Tag(null, "First tag");
        doReturn(new Tag(1L, "First tag")).when(tagRepository)
                        .save(tag);
        Tag result = tagService.save("First tag");
        assertEquals(1L, result.getId(), "id тега должен быть 1");
        assertEquals("First tag", result.getName(), "Название долэно быть First tag");
    }

    @Test
    public void findTagByName_shouldReturnTag(){
        Tag tag = new Tag(1L, "First tag");
        doReturn(tag).when(tagRepository)
                .findTagByName("First tag");
        Optional<Tag> result = tagService.findTagByName("First tag");
        assertTrue(result.isPresent(), "Тег должен существовать");
        assertEquals(1L, result.get().getId(), "id тега должен быть 1");
        assertEquals("First tag", result.get().getName(), "Название долэно быть First tag");
    }
}
