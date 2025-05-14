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

    public Integer addImageByPostId(int postId, byte[] image){

        return imageRepository.addImageByPostId(postId, image);
    }

    public void deleteImageByPostId(int postId){
        imageRepository.deleteImageByPostId(postId);
    }

    public void updateImageByPostId(int postId, byte[] image){
        imageRepository.updateImageByPostId(postId, image);
    }

}
