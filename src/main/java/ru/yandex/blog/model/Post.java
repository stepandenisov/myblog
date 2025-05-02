package ru.yandex.blog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class Post {
    private int id;
    private String title;
    private String text;
    private String imagePath;
    private long likesCount;
    private String[] comments;
    private String[] tags;


    public String getTextPreview(){
        return text.substring(0, Math.min(50, text.length()));
    }
}
