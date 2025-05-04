package ru.yandex.blog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Post{
    private int id;
    private String title;
    private String text;
    private long likesCount;
    private List<Comment> comments;
    private String[] tags;


    public String getTextPreview(){
        return text.substring(0, Math.min(50, text.length()));
    }

    public void applyLikes(int value){
        this.likesCount += value ;
    }
}
