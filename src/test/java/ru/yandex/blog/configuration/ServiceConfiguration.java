package ru.yandex.blog.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "ru.yandex.blog",
               excludeFilters = @ComponentScan.Filter
                       (type = FilterType.REGEX, pattern = "ru.yandex.blog.*.WebConfiguration"))
public class ServiceConfiguration {
}
