package ru.yandex.blog.dao.post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.blog.model.Post;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long> {
    Optional<Post> findById(Long id);
    List<Post> findAllByTagsContains(String tag, Pageable pageable);

    List<Post> findAll(Pageable pageable);

    @Modifying
    @Query("delete from POSTS p where p.ID = :id")
    void deletePostById(@Param("id") Long id);

    long countAllByTagsContains(String tag);

}
