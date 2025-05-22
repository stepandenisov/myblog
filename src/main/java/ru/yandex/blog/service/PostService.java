package ru.yandex.blog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.model.Paging;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.model.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class PostService {


    private final PostRepository postRepository;

    private final TagService tagService;
    private final ImageService imageService;


    public PostService(PostRepository postRepository,
                       TagService tagService,
                       ImageService imageService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.imageService = imageService;
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }
    public List<Post> searchPaginated(String search, Paging paging) {
        if (search.isBlank()){
            List<Post> posts = postRepository.findAll(PageRequest.of(paging.getPageNumber()-1, paging.getPageSize()))
                    .stream().toList();
            if (paging.getPageNumber() == 1) paging.setHasPrevious(false);
            else {
                List<Post> postsBefore = postRepository.findAll(PageRequest.of(paging.getPageNumber()-2, paging.getPageSize()))
                        .stream().toList();
                paging.setHasPrevious(!postsBefore.isEmpty());
            }
            List<Post> postsAfter = postRepository.findAll(PageRequest.of(paging.getPageNumber(), paging.getPageSize()))
                    .stream().toList();
            paging.setHasNext(!postsAfter.isEmpty());
            return posts;
        }
        Optional<Tag> tag = tagService.findTagByName(search);
        if (tag.isEmpty()) return List.of();
        List<Post> posts = postRepository.findAllByTagsContains(PageRequest.of(paging.getPageNumber()-1, paging.getPageSize()), tag.get());
        if (paging.getPageNumber() == 1) paging.setHasPrevious(false);
        else {
            List<Post> postsBefore = postRepository.findAllByTagsContains(PageRequest.of(paging.getPageNumber()-2, paging.getPageSize()), tag.get());
            paging.setHasPrevious(!postsBefore.isEmpty());
        }
        List<Post> postsAfter = postRepository.findAllByTagsContains(PageRequest.of(paging.getPageNumber(), paging.getPageSize()), tag.get());
        paging.setHasNext(!postsAfter.isEmpty());
        return posts;
    }

    public Optional<Post> updatePost(Long id, Post newPost){
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) return Optional.empty();
        newPost.setId(post.get().getId());
        return Optional.of(postRepository.save(newPost));
    }

    public Long insertPost(String title, String text, String tags, byte[] image){
        List<Tag> tagList = tagService.getTagsFromString(tags);
        Post post = new Post(null, title, text, 0, new ArrayList<>(), tagList);
        Post insertedPost =  postRepository.save(post);
        imageService.addImageByPostId(insertedPost.getId(), image);
        return insertedPost.getId();
    }

    public void deletePost(Long id){
        imageService.deleteImageByPostId(id);
        postRepository.deletePostById(id);
    }

    public Optional<Post> updatePost(Long id, String title, String text, String tags, byte[] image){
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) return Optional.empty();
        List<Tag> tagList = tagService.getTagsFromString(tags);
        Post newPost = new Post(post.get().getId(), title, text, post.get().getLikesCount(), post.get().getComments(), tagList);
        imageService.updateImageByPostId(newPost.getId(), image);
        return Optional.of(postRepository.save(newPost));
    }

}
