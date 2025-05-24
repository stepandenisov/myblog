package ru.yandex.blog.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.blog.model.Paging;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.service.PostService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebMvcTest(PostController.class)
public class PostControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void posts_shouldReturnHtmlWithPosts() throws Exception {

        Paging paging = new Paging(1, 10, false, false);

        when(postService.searchPaginated("", paging))
                .thenReturn(List.of(new Post(1L, "test", "test", 0, new ArrayList<>(), "First tag")));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(xpath("//table/tr").nodeCount(2))
                .andExpect(xpath("//table/tr[2]/td").nodeCount(1))
                .andExpect(xpath("//table/tr[2]/td/h2").nodeCount(1))
                .andExpect(xpath("//table/tr[2]/td/h2").string("test"));
    }

    @Test
    void post_shouldReturnHtmlWithPost() throws Exception {

        when(postService.findById(1L))
                .thenReturn(Optional.of(new Post(1L, "test", "test", 0, new ArrayList<>(), "First tag")));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("//table/tr").nodeCount(5))
                .andExpect(xpath("//table/tr[2]/td").nodeCount(1))
                .andExpect(xpath("//table/tr[2]/td/h2").nodeCount(1))
                .andExpect(xpath("//table/tr[2]/td/h2").string("test"));
    }

    @Test
    void insert_shouldAddPostToDatabaseAndRedirect() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "", "application/json", "{\"image\": \"D:\\Projects\\myblog\\src\\main\\resources\\assets\\1.jpg\"}".getBytes());
        MockPart titlePart = new MockPart("title", "Test".getBytes());
        MockPart textPart = new MockPart("text", "Test".getBytes());
        MockPart tagsPart = new MockPart("tags", "Test".getBytes());

        when(postService.insertPost("title", "text", "tags", image.getBytes()))
                .thenReturn(0L);

        mockMvc.perform(multipart("/posts/")
                        .file(image)
                        .part(titlePart)
                        .part(textPart)
                        .part(tagsPart))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/0"));
    }

    @Test
    void edit_shouldEditPostToDatabaseAndRedirect() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "", "application/json", "{\"image\": \"D:\\Projects\\myblog\\src\\main\\resources\\assets\\1.jpg\"}".getBytes());
        MockPart titlePart = new MockPart("title", "Test".getBytes());
        MockPart textPart = new MockPart("text", "Test".getBytes());
        MockPart tagsPart = new MockPart("tags", "Test".getBytes());

        when(postService.updatePost(1L, "title", "text", "", image.getBytes()))
                .thenReturn(Optional.of(new Post(1L, "title", "text", 0, new ArrayList<>(), "First tag")));

        mockMvc.perform(multipart("/posts/1")
                        .file(image)
                        .part(titlePart)
                        .part(textPart)
                        .part(tagsPart))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    void edit_shouldRedirectToEditPostPage() throws Exception {

        when(postService.findById(1L))
                .thenReturn(Optional.of(new Post(1L, "test", "test", 0, new ArrayList<>(), "First tag")));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/1/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("add-post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(xpath("//table/tr").nodeCount(5))
                .andExpect(xpath("//table/tr[1]/td").nodeCount(1))
                .andExpect(xpath("//table/tr[1]/td/textarea").nodeCount(1))
                .andExpect(xpath("//table/tr[1]/td/textarea").string("test"));
    }

    @Test
    void delete_shouldDeletePostAndRedirect() throws Exception {

        doNothing().when(postService).deletePost(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    void changeLikes_shouldRedirectToPostById() throws Exception {

        Post post = new Post(1L, "test", "test", 0, new ArrayList<>(), "First tag");
        Post liked = new Post(1L, "test", "test", 1, new ArrayList<>(), "First tag");

        when(postService.findById(1L))
                .thenReturn(Optional.of(post));

        when(postService.updatePost(1L, post))
                .thenReturn(Optional.of(liked));

        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/like")
                        .param("like", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

}
