package ru.yandex.blog.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.blog.dao.image.ImageRepository;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Image;
import ru.yandex.blog.model.Post;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ImageDaoUnitTest {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    public void setUp() {
        imageRepository.deleteAll();
    }

    @Test
    public void save_shouldSaveImage() {
        Image image = new Image(null, 1L, new byte[]{1});
        Image insertedImage = imageRepository.save(image);
        assertArrayEquals(image.getImage(), insertedImage.getImage(), "Изображения должны совпадать");

    }

    @Test
    public void findByPostId_shouldReturnImage() {

        Image imageToInsert = new Image(null, 1L, new byte[]{1});
        Image insertedImage = imageRepository.save(imageToInsert);

        Image image = imageRepository.findImagesByPostId(1L);
        assertNotNull(image, "Изображение должно быть");
        assertEquals(insertedImage.getId(), image.getId(), "id должны совпадать");

    }

    @Test
    public void deleteImageByPostId_shouldDeleteImage() {

        Image imageToInsert = new Image(null, 1L, new byte[]{1});
        imageRepository.save(imageToInsert);

        imageRepository.deleteImageByPostId(1L);
        Image image = imageRepository.findImagesByPostId(1L);
        assertNull(image, "Изображение должно отсутствовать");

    }
}
