package ru.yandex.blog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class Comment {
    private int id;
    private int postId;
    @Setter
    private String text;
}
