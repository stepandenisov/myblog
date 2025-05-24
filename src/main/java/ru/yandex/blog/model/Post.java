package ru.yandex.blog.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Getter
@Table(name = "POSTS")
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Post{

    @Id
    @Setter
    private Long id;
    private String title;

    @Column("POST_TEXT")
    private String text;
    private long likesCount;
    @MappedCollection(idColumn = "POST_ID", keyColumn = "ID")
    private List<Comment> comments;

    @Column("POST_TAGS")
    private String tags;


    public String getTextPreview(){
        return text.substring(0, Math.min(50, text.length()));
    }

    public String[] getTextParts(){
        return text.split("\n");
    }

    public String getTagsAsText(){
        return tags;
    }

    public String[] getTags(){
        return tags.split(", ");
    }

    public void applyLikes(int value){
        this.likesCount += value ;
    }
}
