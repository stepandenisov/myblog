package ru.yandex.blog.service;

import org.springframework.stereotype.Service;
import ru.yandex.blog.dao.image.ImageRepository;
import ru.yandex.blog.dao.post.PostRepository;
import ru.yandex.blog.model.Image;
import ru.yandex.blog.model.Post;

import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    private final PostRepository postRepository;


    public ImageService(ImageRepository imageRepository, PostRepository postRepository) {
        this.imageRepository = imageRepository;
        this.postRepository = postRepository;
    }


    public Image getImageByPostId(Long postId) {
        return imageRepository.findImagesByPostId(postId);
    }

    public void addImageByPostId(Long postId, byte[] imageBytes){
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) return;
        Image image = new Image(null, post.get(), imageBytes);
        imageRepository.save(image);
    }

    public void deleteImageByPostId(Long postId){
        imageRepository.deleteImageByPostId(postId);
    }

    public void updateImageByPostId(Long postId, byte[] imageBytes){
        Image image = imageRepository.findImagesByPostId(postId);
        image.setImage(imageBytes);
        imageRepository.save(image);
    }

}
