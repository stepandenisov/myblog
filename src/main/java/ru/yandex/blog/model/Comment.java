package ru.yandex.blog.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Entity
@Table(name = "post_comments")
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "post_id")
    private Post post;
    @Setter
    @Column(name = "comment_text")
    private String text;


    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != Comment.class) return false;
        Comment comment = (Comment) obj;
        return Objects.equals(this.id, comment.getId());
    }
}
