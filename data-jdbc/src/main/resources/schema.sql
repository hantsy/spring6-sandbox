-- create posts table
CREATE TABLE IF NOT EXISTS posts (
 id UUID DEFAULT uuid_generate_v4(),
 title VARCHAR(255),
 content VARCHAR(255),
 --metadata JSON default '{}',
 status post_status default 'DRAFT',
 created_at TIMESTAMP NOT NULL DEFAULT LOCALTIMESTAMP,
 created_by VARCHAR(255),
 PRIMARY KEY (id)
 );