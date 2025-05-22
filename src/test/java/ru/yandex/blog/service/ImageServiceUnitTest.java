package ru.yandex.blog.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.dao.image.ImageRepository;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.model.Image;
import ru.yandex.blog.model.Post;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ImageServiceUnitTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private PostRepository postRepository;

    @Test
    public void getImageByPostId_shouldReturnImageBytes(){

        Image image = new Image(1L, new Post(), new byte[]{1});

        when(imageRepository.findImagesByPostId(1L))
                .thenReturn(image);

        Image result = imageService.getImageByPostId(1L);
        assertArrayEquals(image.getImage(), result.getImage(), "Байты должны совпадать");
    }

    @Test
    public void addImageByPostId_shouldAddImageAndReturnId(){

        Post post = new Post();
        Image image = new Image(null, post, new byte[]{1});
        Image inserted = new Image(1L, post, new byte[]{1});

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));
        when(imageRepository.save(image))
                .thenReturn(inserted);

        Optional<Image> result = imageService.addImageByPostId(1L, new byte[]{1});
        assertTrue(result.isPresent(), "Изображение должно существовать");
        assertEquals(1, result.get().getId(), "id вставленного изображения должен быть 1");
    }
}
