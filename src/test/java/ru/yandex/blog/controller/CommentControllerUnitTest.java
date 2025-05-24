package ru.yandex.blog.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.service.CommentService;
import ru.yandex.blog.service.PostService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void addComment_shouldAddCommentToPostAndRedirect() throws Exception {

        when(commentService.insertComment(1L, "text"))
                .thenReturn(Optional.of(new Comment(1L, 1L, "text")));

        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/comments")
                        .param("text", "Test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    void updateComment_shouldUpdateCommentAndRedirect() throws Exception {

        Comment newComment = new Comment(1L, 1L, "text");

        when(commentService.findCommentById(1L))
                .thenReturn(Optional.of(newComment));

        when(commentService.updateComment(1L, newComment))
                .thenReturn(Optional.of(new Comment(1L, 1L, "new test text")));

        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/comments/1")
                        .param("text", "new test text"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    void deleteComment_shouldDeleteCommentAndRedirect() throws Exception {

        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/posts/1/comments/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }


}
