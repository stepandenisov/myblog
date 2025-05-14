package ru.yandex.blog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

    public String[] getTextParts(){
        return text.split("\n");
    }

    public String getTagsAsText(){
        return String.join(", ", tags);
    }


    public void applyLikes(int value){
        this.likesCount += value ;
    }
}
