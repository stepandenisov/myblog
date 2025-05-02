package ru.yandex.blog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class Paging {
    @Setter
    private int pageNumber;
    @Setter
    private int pageSize;
    @Setter
    private boolean hasNext;
    @Setter
    private boolean hasPrevious;
}
