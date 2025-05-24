package ru.yandex.blog.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Arrays;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Table("IMAGES")
@Getter
public class Image {

    @Id
    private Long id;

    private Long postId;

    @Setter
    private byte[] image;

    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != Image.class) {
            return false;
        }
        Image image = (Image) obj;
        if (!Arrays.equals(image.getImage(), this.getImage())) {
            return false;
        }
        if (!Objects.equals(image.getPostId(), this.getPostId())) {
            return false;
        }
        return Objects.equals(image.getId(), this.getId());
    }
}
