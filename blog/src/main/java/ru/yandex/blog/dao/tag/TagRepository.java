package ru.yandex.blog.dao.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.blog.model.Tag;


public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findTagByName(String name);
}
