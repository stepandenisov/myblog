package ru.yandex.blog.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@EqualsAndHashCode
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
}
