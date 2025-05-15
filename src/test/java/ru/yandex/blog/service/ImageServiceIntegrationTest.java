package ru.yandex.blog.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.configuration.ServiceConfiguration;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {ServiceConfiguration.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class ImageServiceIntegrationTest {

    @Value("${project-path}")
    private String projectPath;

    @Autowired
    private ImageService imageService;

    @Autowired
    private PostService postService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Очистка и заполнение тестовых данных в базе
        jdbcTemplate.execute("TRUNCATE TABLE images RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=0");
        jdbcTemplate.execute("TRUNCATE TABLE posts RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=1");

        jdbcTemplate.execute("insert into posts(title, post_text, likes_count) values ('First post', 'Text of the first post', 0);");
        jdbcTemplate.execute("insert into posts(title, post_text, likes_count) values ('Second post', 'Text of the second post', 0);");
        jdbcTemplate.execute("insert into posts(title, post_text, likes_count) values ('Third post', 'Text of the third post', 0);");

        jdbcTemplate.execute("insert into images(post_id, image) values (1, file_read('" + projectPath + "src/main/resources/assets/1.jpg'));");
        jdbcTemplate.execute("insert into images(post_id, image) values (2, file_read('" + projectPath + "src/main/resources/assets/2.jpg'));");
        jdbcTemplate.execute("insert into images(post_id, image) values (3, file_read('" + projectPath + "src/main/resources/assets/3.jpg'));");
    }

    @Test
    public void getImageByPostId_shouldReturnImageBytes(){
        byte[] result = imageService.getImageByPostId(1);
        assertNotNull(result, "Байты изображения должны быть");
    }

    @Test
    public void addImageByPostId_shouldAddImageAndReturnId(){

        // PostService использует ImageService для сохранения изображения
        int postId = postService.insertPost("test", "text", "First tag", new byte[]{1});
        byte[] image = imageService.getImageByPostId(postId);
        assertEquals(1, image.length, "Количество байт изображения должно быть 1");
        assertEquals(1, image[0], "Изображения должны совпадать");
    }

    @Test
    public void delete_shouldDeleteImage(){
        imageService.deleteImageByPostId(1);
        byte[] image = imageService.getImageByPostId(1);
        assertNull(image, "Изображения не должно быть");
    }

    @Test
    public void update_shouldUpdateImage(){
        byte[] testImage = new byte[]{1};
        imageService.updateImageByPostId(1, testImage);
        byte[] image = imageService.getImageByPostId(1);
        assertArrayEquals(image, testImage, "Изображения должны совпадать");
    }

}
