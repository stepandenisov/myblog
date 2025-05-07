package ru.yandex.blog.dao.tag;

import java.util.List;

public interface TagRepository {

    List<Integer> addTags(String[] tags);

    void deleteTagsFromDeletedPost();
}
