create table if not exists posts
(
    id          bigserial primary key,
    title       varchar(256) not null,
    post_text   text         not null,
    likes_count integer      not null
);

create table if not exists tags
(
    id       bigserial primary key,
    tag_name varchar(256) unique not null
);

create table if not exists post_comments
(
    id           bigserial primary key,
    post_id      int  not null references posts(id),
    comment_text text not null
);

create table if not exists posts_tags
(
    post_id int not null references posts (id),
    tag_id  int not null references tags (id)
);

create table if not exists images
(
    id bigserial primary key,
    post_id int unique not null references posts (id),
    image   bytea      not null
);
