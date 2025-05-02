package ru.yandex.blog.dao.image;

public interface ImageRepository {
    byte[] getImageByPostId(int postId);
}
