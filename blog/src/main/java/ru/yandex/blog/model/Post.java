package ru.yandex.blog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Entity
@Table(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
public class Post{

    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @Column(name = "post_text")
    private String text;
    private long likesCount;
    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, mappedBy = "post")
    private List<Comment> comments;
    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.DETACH)
    @JoinTable(
            name = "posts_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;


    public String getTextPreview(){
        return text.substring(0, Math.min(50, text.length()));
    }

    public String[] getTextParts(){
        return text.split("\n");
    }

    public String getTagsAsText(){
        return tags.stream().map(Tag::getName).reduce((a, b) -> a + ", " + b).orElse("");
    }

    public void applyLikes(int value){
        this.likesCount += value ;
    }
}
