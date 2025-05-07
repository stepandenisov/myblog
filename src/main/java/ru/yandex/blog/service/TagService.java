package ru.yandex.blog.service;

import org.springframework.stereotype.Service;
import ru.yandex.blog.dao.tag.TagRepository;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Integer> addTags(String tagsString) {
        return tagRepository.addTags(tagsString.trim().split(", |,"));
    }

    public void deleteTagsFromDeletedPost(){
        tagRepository.deleteTagsFromDeletedPost();
    }
}
