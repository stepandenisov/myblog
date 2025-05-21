package ru.yandex.blog.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.yandex.blog.dao.image.ImageRepository;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Image;
import ru.yandex.blog.model.Post;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
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
        Post postToInsert = new Post(null, "text", "text", 0, new ArrayList<>(), new ArrayList<>());
        Post insertedPost = postRepository.save(postToInsert);
        Image image = new Image(null, insertedPost, new byte[]{1});
        Image insertedImage = imageRepository.save(image);
        assertArrayEquals(new byte[]{1}, insertedImage.getImage(), "Изображения должен быть test");

    }

    @Test
    public void findByPostId_shouldReturnImage() {

        Post postToInsert = new Post(null, "text", "text", 0, new ArrayList<>(), new ArrayList<>());
        Post insertedPost = postRepository.save(postToInsert);
        Image imageToInsert = new Image(null, insertedPost, new byte[]{1});
        Image insertedImage = imageRepository.save(imageToInsert);

        Image image = imageRepository.findImagesByPostId(insertedImage.getId());
        assertNotNull(image, "Изображение должен быть");
        assertEquals(insertedImage.getId(), image.getId(), "id должны совпадать");

    }

    @Test
    public void deleteImageByPostId_shouldDeleteImage() {

        Post postToInsert = new Post(null, "text", "text", 0, new ArrayList<>(), new ArrayList<>());
        Post insertedPost = postRepository.save(postToInsert);
        Image imageToInsert = new Image(null, insertedPost, new byte[]{1});
        imageRepository.save(imageToInsert);

        imageRepository.deleteImageByPostId(insertedPost.getId());
        Image image = imageRepository.findImagesByPostId(insertedPost.getId());
        assertNull(image, "Изображение должно отсутствовать");

    }
}
