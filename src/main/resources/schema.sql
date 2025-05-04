create table if not exists posts
(
    id          bigserial primary key,
    title       varchar(256) not null,
    post_text   text         not null,
    likes_count integer      not null
);

insert into posts(title, post_text, likes_count)
values ('First post', 'Text of the first post', 0);
insert into posts(title, post_text, likes_count)
values ('Second post', 'Text of the second post', 0);
insert into posts(title, post_text, likes_count)
values ('Third post', 'Text of the third post', 0);


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

create table if not exists post_comments
(
    id           bigserial primary key,
    post_id      int  not null references posts(id),
    comment_text text not null
);

insert into post_comments(post_id, comment_text)
values (1, 'First comment');
insert into post_comments(post_id, comment_text)
values (2, 'Second comment');
insert into post_comments(post_id, comment_text)
values (2, 'Third comment');
-- insert into comments(post_id, comment_text)
-- values (3, 'Fourth comment');


create table if not exists posts_tags
(
    post_id int not null references posts (id),
    tag_id  int not null references tags (id)
);

insert into posts_tags(post_id, tag_id)
values (1, 1);
insert into posts_tags(post_id, tag_id)
values (2, 2);
insert into posts_tags(post_id, tag_id)
values (3, 3);
insert into posts_tags(post_id, tag_id)
values (1, 3);

create table if not exists images
(
    post_id int unique not null references posts (id),
    image   bytea      not null
);

insert into images(post_id, image)
values (1, file_read('D:\Projects\myblog\src\main\resources\assets\1.jpg'));
insert into images(post_id, image)
values (2, file_read('D:\Projects\myblog\src\main\resources\assets\2.jpg'));
insert into images(post_id, image)
values (3, file_read('D:\Projects\myblog\src\main\resources\assets\3.jpg'));