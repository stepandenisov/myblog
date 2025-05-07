package ru.yandex.blog.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.dto.PostDto;
import ru.yandex.blog.model.Paging;
import ru.yandex.blog.model.Post;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {


    private final PostRepository postRepository;

    private final TagService tagService;
    private final ImageService imageService;

    private final CommentService commentService;

    public PostService(PostRepository postRepository,
                       TagService tagService,
                       ImageService imageService,
                       CommentService commentService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.imageService = imageService;
        this.commentService = commentService;
    }

    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }
    public List<Post> searchPaginated(String search, Paging paging) {
        return postRepository.searchPaginated(search, paging);
    }

    public Optional<Post> updatePost(int id, Post post){
        return postRepository.updatePost(id, post);
    }

    public int insertPost(String title, String text, String tags, byte[] image){
        List<Integer> tagIds = tagService.addTags(tags);
        PostDto postDto = new PostDto(title, text, tagIds);
        int insertedPostId =  postRepository.insertPost(postDto);
        imageService.addImageByPostId(insertedPostId, image);
        return insertedPostId;
    }

    public void deletePost(int id){
        commentService.deleteCommentByPostId(id);
        imageService.deleteImageByPostId(id);
        postRepository.deletePost(id);
        tagService.deleteTagsFromDeletedPost();
    }

    public void updatePost(int id, String title, String text, String tags, byte[] image){
        List<Integer> tagIds = tagService.addTags(tags);
        PostDto postDto = new PostDto(title, text, tagIds);
        postRepository.updatePost(id, postDto);
        imageService.updateImageByPostId(id, image);
    }

}
