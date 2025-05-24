package ru.yandex.blog.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.model.Paging;
import ru.yandex.blog.model.Post;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {


    private final PostRepository postRepository;

    private final ImageService imageService;

    private final CommentService commentService;


    public PostService(PostRepository postRepository,
                       ImageService imageService,
                       CommentService commentService) {
        this.postRepository = postRepository;
        this.imageService = imageService;
        this.commentService = commentService;
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }


    public List<Post> searchPaginated(String search, Paging paging) {
        List<Post> posts = search.isBlank() ? findAllPaginated(paging) : findAllByTagPaginated(search, paging);

        long count = search.isBlank() ? postRepository.count() : postRepository.countAllByTagsContains(search);

        long totalPageCount = ((count - 1) / paging.getPageSize()) + 1;

        if (paging.getPageNumber() == 1) {
            paging.setHasPrevious(false);
        } else {
            paging.setHasPrevious(totalPageCount != 1);
        }
        paging.setHasNext(totalPageCount > paging.getPageNumber());
        return posts;
    }

    private List<Post> findAllByTagPaginated(String search, Paging paging) {
        return postRepository.findAllByTagsContains(search, PageRequest.of(paging.getPageNumber() - 1, paging.getPageSize()));
    }

    private List<Post> findAllPaginated(Paging paging) {
        return postRepository.findAll(PageRequest.of(paging.getPageNumber() - 1, paging.getPageSize()))
                .stream().toList();
    }

    public Optional<Post> updatePost(Long id, Post newPost) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            return Optional.empty();
        }
        newPost.setId(post.get().getId());
        return Optional.of(postRepository.save(newPost));
    }

    public Long insertPost(String title, String text, String tags, byte[] image) {
        Post post = new Post(null, title, text, 0, new ArrayList<>(), tags);
        Post insertedPost = postRepository.save(post);
        imageService.addImageByPostId(insertedPost.getId(), image);
        return insertedPost.getId();
    }

    public void deletePost(Long id) {
        commentService.deleteCommentsByPostId(id);
        imageService.deleteImageByPostId(id);
        postRepository.deletePostById(id);
    }

    public Optional<Post> updatePost(Long id, String title, String text, String tags, byte[] image) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            return Optional.empty();
        }
        Post newPost = new Post(post.get().getId(), title, text, post.get().getLikesCount(), post.get().getComments(), tags);
        imageService.updateImageByPostId(newPost.getId(), image);
        return Optional.of(postRepository.save(newPost));
    }

}
