package ru.yandex.blog.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="images")
@Getter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade=CascadeType.DETACH)
    @JoinColumn(name="post_id", referencedColumnName = "id")
    private Post post;

    @Setter
    @Lob
    private byte[] image;
}
