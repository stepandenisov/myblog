package ru.yandex.blog.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.blog.model.Image;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.service.CommentService;
import ru.yandex.blog.service.ImageService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
public class ImageControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;


    @Test
    void image_shouldGetImageForPost() throws Exception {

        Image image = new Image(1L, new Post(), new byte[]{1});

        when(imageService.getImageByPostId(1L)).thenReturn(image);

        mockMvc.perform(MockMvcRequestBuilders.get("/images/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

}
