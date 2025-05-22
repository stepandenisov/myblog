package ru.yandex.blog.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != Image.class) return false;
        Image image = (Image) obj;
        if (!Arrays.equals(image.getImage(), this.getImage())) return false;
        if (!Objects.equals(image.getPost(), this.getPost())) return false;
        return Objects.equals(image.getId(), this.getId());
    }
}
