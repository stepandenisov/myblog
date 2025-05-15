package ru.yandex.blog.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.yandex.blog.configuration.ServiceConfiguration;
import ru.yandex.blog.configuration.WebConfiguration;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.dao.tag.TagRepository;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {ServiceConfiguration.class})
@TestPropertySource(locations = "classpath:test-application.properties")
@ExtendWith(MockitoExtension.class)
public class TagServiceUnitTest {
    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @Test
    public void addTags_shouldReturnIdsOfInsertedTags(){
        String[] tags = new String[]{"First tag", "Second tag"};
        List<Integer> tagIds = List.of(1, 2);
        when(tagRepository.addTags(tags))
                .thenReturn(tagIds);
        List<Integer> insertedTagIds = tagService.addTags(String.join(", ", tags));
        assertEquals(tagIds, insertedTagIds, "все идентификаторы должны совпадать");
    }
}
