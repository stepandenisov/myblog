package ru.yandex.blog.service;

import org.springframework.stereotype.Service;
import ru.yandex.blog.dao.comment.CommentRepository;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.model.Comment;
import ru.yandex.blog.model.Post;

import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository){
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public Optional<Comment> insertComment(Long postId, String text){
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) return Optional.empty();
        return Optional.of(commentRepository.save(new Comment(null, post.get(), text)));
    }

    public Optional<Comment> updateComment(Long id, Comment newComment){
        Optional<Comment> comment = commentRepository.findCommentById(id);
        if (comment.isEmpty()) return Optional.empty();
        comment.get().setText(newComment.getText());
        return Optional.of(commentRepository.save(comment.get()));
    }

    public Optional<Comment> findCommentById(Long id){
        return commentRepository.findCommentById(id);
    }

    public void deleteComment(Long id){
        commentRepository.deleteCommentById(id);
    }

    public void deleteCommentsByPostId(Long postId) {commentRepository.deleteCommentsByPostId(postId);}
}
