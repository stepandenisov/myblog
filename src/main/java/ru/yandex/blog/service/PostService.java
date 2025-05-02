package ru.yandex.blog.service;

import org.springframework.stereotype.Service;
import ru.yandex.blog.dao.PostRepository;
import ru.yandex.blog.model.Paging;
import ru.yandex.blog.model.Post;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {


    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }
    public List<Post> searchPaginated(String search, Paging paging) {
        return postRepository.searchPaginated(search, paging);
    }

}
