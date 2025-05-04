package ru.yandex.blog.service;

import org.springframework.stereotype.Service;
import ru.yandex.blog.dao.comment.CommentRepository;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Post;

import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository){
        this.commentRepository = commentRepository;
    }

    public Optional<Comment> insertComment(int postId, String text){
        return commentRepository.insertComment(postId, text);
    }

    public Optional<Comment> updateComment(int id, Comment comment){
        return commentRepository.updateComment(id, comment);
    }

    public Optional<Comment> findCommentById(int id){
        return commentRepository.findCommentById(id);
    }

    public void deleteComment(int id){
        commentRepository.deleteComment(id);
    }
}
