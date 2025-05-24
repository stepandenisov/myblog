package ru.yandex.blog.dao.image;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.blog.model.Image;

public interface ImageRepository extends CrudRepository<Image, Long> {
    Image findImagesByPostId(Long postId);

    @Modifying
    @Query("delete from IMAGES i where i.POST_ID = :post_id")
    void deleteImageByPostId(@Param("post_id") Long postId);
}
