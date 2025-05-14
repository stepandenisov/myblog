package ru.yandex.blog.dao.image;

public interface ImageRepository {
    byte[] getImageByPostId(int postId);

    Integer addImageByPostId(int postId, byte[] image);

    void deleteImageByPostId(int postId);

    void updateImageByPostId(int postId, byte[] image);
}
