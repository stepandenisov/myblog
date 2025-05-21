package ru.yandex.blog.service;

import org.springframework.stereotype.Service;
import ru.yandex.blog.dao.tag.TagRepository;
import ru.yandex.blog.model.Tag;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Optional<Tag> findTagByName(String name){
        return Optional.ofNullable(tagRepository.findTagByName(name));
    }

    public Tag save(String name){
        return tagRepository.save(new Tag(null, name));
    }

    public List<Tag> getTagsFromString(String tags){
        List<String> tagNames = Arrays.stream(tags.split(", ")).toList();
        return tagNames.stream().map(tagName -> {
            Optional<Tag> tag = findTagByName(tagName);
            return tag.orElseGet(() -> save(tagName));
        }).toList();
    }

}
