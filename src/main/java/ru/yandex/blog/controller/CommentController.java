package ru.yandex.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.service.CommentService;

import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable("id") Long postId,
                             @RequestParam(name = "text") String text,
                             Model model) {
        commentService.insertComment(postId, text);
        return "redirect:/posts/{id}";
    }

    @PostMapping("/{id}/comments/{commentId}")
    public String updateComment(@PathVariable("id") Long postId,
                                @PathVariable("commentId") Long commentId,
                                @RequestParam(name = "text") String text,
                                Model model) {
        Optional<Comment> comment = commentService.findCommentById(commentId);
        if (comment.isEmpty()) return "redirect:/posts/{id}";
        Comment actualComment = comment.get();
        actualComment.setText(text);
        commentService.updateComment(commentId, actualComment);
        return "redirect:/posts/{id}";
    }

    @PostMapping("/{id}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("id") Long postId,
                                @PathVariable("commentId") Long commentId,
                                Model model) {
        commentService.deleteComment(commentId);
        return "redirect:/posts/{id}";
    }

}
