package ru.yandex.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class PostDto {
    private String title;
    private String text;
    private List<Integer> tagIds;

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != PostDto.class) return false;
        PostDto postDto = (PostDto) obj;
        if (!Objects.equals(title, postDto.getTitle())) return false;
        if (!Objects.equals(text, postDto.getText())) return false;
        return tagIds.equals(postDto.getTagIds());
    }
}
