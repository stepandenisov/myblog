package ru.yandex.blog.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.blog.model.Paging;
import ru.yandex.blog.model.Post;
import ru.yandex.blog.service.PostService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    @RequestMapping("/{id}")
    public String post(@PathVariable(name = "id") int id,
                       Model model) {
        Optional<Post> post = postService.findById(id);
        if (post.isEmpty()) return "redirect:/posts";
        model.addAttribute("post", post.get());
        return "post";
    }

    @GetMapping({"/", ""})
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

    @PostMapping("/{id}/like")
    public String changeLikes(
            @PathVariable(name = "id") int id,
            @RequestParam(name = "like") boolean like) {
        Optional<Post> post = postService.findById(id);
        if (post.isEmpty()) return "redirect:/posts";
        Post actualPost = post.get();
        if (!like && actualPost.getLikesCount() <= 0) return "redirect:/posts/{id}";
        actualPost.applyLikes(like ? 1 : -1);
        Optional<Post> updatedPost = postService.updatePost(id, actualPost);
        if (updatedPost.isEmpty()) return "redirect:/posts";
        return "redirect:/posts/{id}";
    }

    @GetMapping("/add")
    public String addPost() {
        return "add-post";
    }

    @PostMapping(path = {"/", ""}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String insert(@RequestPart String title, @RequestPart String text, @RequestPart String tags, @RequestPart MultipartFile image) throws IOException {
        int insertedId = postService.insertPost(title, text, tags, image.getBytes());
        return "redirect:/posts/"+insertedId;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable int id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable int id, Model model){
        Optional<Post> post = postService.findById(id);
        if (post.isEmpty()) return "redirect:/posts";
        model.addAttribute("post", post.get());
        return "add-post";
    }

    @PostMapping(path = {"/{id}", ""}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String edit(@PathVariable int id, @RequestPart String title, @RequestPart String text, @RequestPart String tags, @RequestPart MultipartFile image) throws IOException {
        postService.updatePost(id, title, text, tags, image.getBytes());
        return "redirect:/posts/"+id;
    }

}
