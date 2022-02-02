-- initialize the sample data.
DELETE FROM post_labels;
DELETE FROM posts;
DELETE FROM users;
INSERT INTO  posts (title, content) VALUES ('Spring 6 and Jdbc', 'Review the Jdbc features in Spring framework 6.0');
