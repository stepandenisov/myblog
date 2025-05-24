package ru.yandex.blog.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.model.Image;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
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
        jdbcTemplate.execute("TRUNCATE TABLE IMAGES RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=0");
        jdbcTemplate.execute("TRUNCATE TABLE POSTS RESTART IDENTITY");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY=1");

        jdbcTemplate.execute("insert into POSTS(TITLE, POST_TEXT, POST_TAGS, LIKES_COUNT) values ('First post', 'First tag','Text of the first post', 0);");
        jdbcTemplate.execute("insert into POSTS(TITLE, POST_TEXT, POST_TAGS, LIKES_COUNT) values ('Second post', 'Second tag', 'Text of the second post', 0);");
        jdbcTemplate.execute("insert into POSTS(TITLE, POST_TEXT, POST_TAGS, LIKES_COUNT) values ('Third post', 'Third tag', 'Text of the third post', 0);");

        jdbcTemplate.execute("insert into IMAGES(POST_ID, IMAGE) values (1, file_read('" + projectPath + "src/main/resources/assets/1.jpg'));");
        jdbcTemplate.execute("insert into IMAGES(POST_ID, IMAGE) values (2, file_read('" + projectPath + "src/main/resources/assets/2.jpg'));");
        jdbcTemplate.execute("insert into IMAGES(POST_ID, IMAGE) values (3, file_read('" + projectPath + "src/main/resources/assets/3.jpg'));");
    }

    @Test
    public void getImageByPostId_shouldReturnImageBytes(){
        Image result = imageService.getImageByPostId(1L);
        assertNotNull(result.getImage(), "Байты изображения должны быть");
    }

    @Test
    public void addImageByPostId_shouldAddImageAndReturnId(){

        // PostService использует ImageService для сохранения изображения
        Long postId = postService.insertPost("test", "text", "First tag", new byte[]{1});
        Image image = imageService.getImageByPostId(postId);
        assertEquals(1, image.getImage().length, "Количество байт изображения должно быть 1");
        assertEquals(1, image.getImage()[0], "Изображения должны совпадать");
    }

    @Test
    public void deleteImageByPostId_shouldDeleteImage(){
        imageService.deleteImageByPostId(1L);
        Image image = imageService.getImageByPostId(1L);
        assertNull(image, "Изображения не должно быть");
    }

    @Test
    public void updateImageByPostId_shouldUpdateImage(){
        byte[] testImage = new byte[]{1};
        imageService.updateImageByPostId(1L, testImage);
        Image image = imageService.getImageByPostId(1L);
        assertArrayEquals(image.getImage(), testImage, "Изображения должны совпадать");
    }

}
