package ru.yandex.blog.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Comment {
    private int id;
    private int postId;
    @Setter
    private String text;
}
