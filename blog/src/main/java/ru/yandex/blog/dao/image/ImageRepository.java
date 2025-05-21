package ru.yandex.blog.dao.image;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.blog.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findImagesByPostId(Long postId);

    @Transactional
    void deleteImageByPostId(Long postId);
}
