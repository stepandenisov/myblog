package ru.yandex.blog.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Getter
@Table(name = "POST_COMMENTS")
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    private Long id;

    private Long postId;
    @Setter
    @Column("COMMENT_TEXT")
    private String text;


    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != Comment.class) return false;
        Comment comment = (Comment) obj;
        return Objects.equals(this.id, comment.getId());
    }
}
