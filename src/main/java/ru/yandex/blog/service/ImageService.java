package ru.yandex.blog.service;

import org.springframework.stereotype.Service;
import ru.yandex.blog.dao.image.ImageRepository;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }


    public byte[] getImageByPostId(int postId) {
        return imageRepository.getImageByPostId(postId);
    }

}
