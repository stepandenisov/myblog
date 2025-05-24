create table if not exists POSTS
(
    ID          bigserial primary key,
    TITLE       varchar(256) not null,
    POST_TEXT   text         not null,
    POST_TAGS   text         not null,
    LIKES_COUNT integer      not null
);


create table if not exists POST_COMMENTS
(
    ID           bigserial primary key,
    POST_ID      int  not null references POSTS (ID),
    COMMENT_TEXT text not null
);

create table if not exists IMAGES
(
    ID      bigserial unique primary key,
    POST_ID int unique not null references POSTS (ID),
    IMAGE   bytea      not null
);
