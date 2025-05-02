package ru.yandex.blog.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.blog.model.Paging;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.service.PostService;

import java.io.File;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String posts(@RequestParam(name = "search", required = false, defaultValue = "") String search,
                        @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                        @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                        Model model) {
        Paging paging = new Paging(pageNumber, pageSize, false, false);
        List<Post> posts = postService.searchPaginated(search, paging);
        model.addAttribute("posts", posts);
        model.addAttribute("search", search);
        model.addAttribute("paging", paging);
        return "posts";
    }
}
