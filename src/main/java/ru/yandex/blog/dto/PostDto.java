package ru.yandex.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@Getter
public class PostDto {
    private String title;
    private String text;
    private List<Integer> tagIds;
}
