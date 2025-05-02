create table if not exists posts
(
    id            bigserial primary key,
    title         varchar(256) not null,
    post_text     text         not null,
    image_path    varchar(256) not null,
    likes_count   integer      not null,
    post_comments text ARRAY
);

insert into posts(title, post_text, image_path, likes_count, post_comments)
values ('First post', 'Text of the first post', '/path/to/file', 0, '"Good post", "Bad post"');
insert into posts(title, post_text, image_path, likes_count, post_comments)
values ('Second post', 'Text of the second post', '/path/to/file', 0, '"Best post", "Worst post"');
insert into posts(title, post_text, image_path, likes_count, post_comments)
values ('Third post', 'Text of the third post', '/path/to/file', 0, '"Neutral post", "Ordinary post"');


create table if not exists tags
(
    id       bigserial primary key,
    tag_name varchar(256) not null
);

insert into tags(tag_name)
values ('First tag');
insert into tags(tag_name)
values ('Second tag');
insert into tags(tag_name)
values ('Third tag');

create table if not exists posts_tags
(
    post_id int not null references posts(id),
    tag_id  int not null references tags(id)
);

insert into posts_tags(post_id, tag_id)
values (1, 1);
insert into posts_tags(post_id, tag_id)
values (2, 2);
insert into posts_tags(post_id, tag_id)
values (3, 3);
insert into posts_tags(post_id, tag_id)
values (1, 3);