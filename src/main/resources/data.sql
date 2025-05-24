insert into POSTS(TITLE, POST_TEXT, POST_TAGS, LIKES_COUNT)
values ('First post', 'First tag','Text of the first post', 0);
insert into POSTS(TITLE, POST_TEXT, POST_TAGS, LIKES_COUNT)
values ('Second post', 'Second tag', 'Text of the second post', 0);
insert into POSTS(TITLE, POST_TEXT, POST_TAGS, LIKES_COUNT)
values ('Third post', 'Third tag', 'Text of the third post', 0);


insert into POST_COMMENTS(POST_ID, COMMENT_TEXT)
values (1, 'First comment');
insert into POST_COMMENTS(POST_ID, COMMENT_TEXT)
values (2, 'Second comment');
insert into POST_COMMENTS(POST_ID, COMMENT_TEXT)
values (2, 'Third comment');
-- insert into comments(post_id, comment_text)
-- values (3, 'Fourth comment');


insert into IMAGES(POST_ID, IMAGE) values (1, file_read('D:\Projects\2\blog\src\main\resources\assets\1.jpg'));
insert into IMAGES(POST_ID, IMAGE) values (2, file_read('D:\Projects\2\blog\src\main\resources\assets\2.jpg'));
insert into IMAGES(POST_ID, IMAGE) values (3, file_read('D:\Projects\2\blog\src\main\resources\assets\3.jpg'));