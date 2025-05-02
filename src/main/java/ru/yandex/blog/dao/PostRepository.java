package ru.yandex.blog.dao;

import ru.yandex.blog.model.Paging;
import ru.yandex.blog.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> findById(int id);
    List<Post> searchPaginated(String search, Paging paging);
}
