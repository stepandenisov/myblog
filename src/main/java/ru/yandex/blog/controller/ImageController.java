package ru.yandex.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.blog.service.ImageService;
import ru.yandex.blog.service.PostService;

@Controller
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(path= {"/", "/{postId}"})
    @ResponseBody
    public byte[] image(@PathVariable int postId){
        return imageService.getImageByPostId(postId);
    }


}
