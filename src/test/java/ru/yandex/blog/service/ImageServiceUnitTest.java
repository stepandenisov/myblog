package ru.yandex.blog.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.configuration.ServiceConfiguration;
import ru.yandex.blog.dao.comment.CommentRepository;
import ru.yandex.blog.dao.image.ImageRepository;
import ru.yandex.blog.model.Comment;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {ServiceConfiguration.class})
@TestPropertySource(locations = "classpath:test-application.properties")
@ExtendWith(MockitoExtension.class)
public class ImageServiceUnitTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private ImageRepository imageRepository;

    @Test
    public void getImageByPostId_shouldReturnImageBytes(){

        byte[] image = new byte[]{1};

        when(imageRepository.getImageByPostId(1))
                .thenReturn(image);

        byte[] result = imageService.getImageByPostId(1);
        assertEquals(image, result, "Байты должны совпадать");
    }

    @Test
    public void addImageByPostId_shouldAddImageAndReturnId(){

        byte[] image = new byte[]{1};

        when(imageRepository.addImageByPostId(1, image))
                .thenReturn(1);

        Integer result = imageService.addImageByPostId(1, image);
        assertEquals(1, result, "id вставленного изображения должен быть 1");
    }
}
