-- create posts table
CREATE TABLE IF NOT EXISTS posts (
     id UUID DEFAULT uuid_generate_v4(),
     title VARCHAR(255),
     content VARCHAR(255),
     status VARCHAR(255) default 'DRAFT',
     created_at TIMESTAMP NOT NULL DEFAULT LOCALTIMESTAMP,
     created_by VARCHAR(255),
     moderator UUID,
     PRIMARY KEY (id)
 );

CREATE TABLE IF NOT EXISTS users (
    id UUID DEFAULT uuid_generate_v4(),
    name VARCHAR(255),
    email VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS post_labels (
    label VARCHAR(255),
    posts  UUID NOT NULL
);

ALTER TABLE posts DROP CONSTRAINT IF EXISTS fk_posts_moderator;
ALTER TABLE posts ADD CONSTRAINT fk_posts_moderator FOREIGN KEY (moderator) REFERENCES users (id) MATCH FULL;

ALTER TABLE post_labels DROP CONSTRAINT IF EXISTS fk_post_labels;
ALTER TABLE post_labels ADD CONSTRAINT fk_post_labels FOREIGN KEY (post) REFERENCES posts (id) MATCH FULL;

CREATE TABLE IF NOT EXISTS versioned_posts (
    id UUID DEFAULT uuid_generate_v4(),
    title VARCHAR(255),
    content VARCHAR(255),
    version BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS persistable_posts (
    id UUID DEFAULT uuid_generate_v4(),
    title VARCHAR(255),
    content VARCHAR(255),
    version BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS popular_posts (
    id UUID DEFAULT uuid_generate_v4(),
    title VARCHAR(255),
    content VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT LOCALTIMESTAMP,
    created_by VARCHAR(255),
    version BIGINT,
    PRIMARY KEY (id)
);
