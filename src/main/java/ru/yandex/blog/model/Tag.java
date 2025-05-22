package ru.yandex.blog.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name="tags")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="tag_name")
    private String name;

    @Override
    public String toString(){
        return this.getName();
    }

    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != Tag.class) return false;
        Tag tag = (Tag) obj;
        if (!Objects.equals(tag.getName(), this.getName())) return false;
        return Objects.equals(tag.getId(), this.getId());
    }
}
