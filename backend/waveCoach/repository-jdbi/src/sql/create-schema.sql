-- Drop existing tables if they exist
DROP TABLE IF EXISTS dbo.message;
DROP VIEW IF EXISTS dbo.channel;
DROP TABLE IF EXISTS dbo.role;
DROP TABLE IF EXISTS dbo.token;
DROP TABLE IF EXISTS dbo.channel_invites;
DROP TABLE IF EXISTS dbo.channel_table;
DROP TABLE IF EXISTS dbo.app_invites;
DROP TABLE IF EXISTS dbo.user;

-- Create schema
CREATE SCHEMA IF NOT EXISTS dbo;

-- Create new tables
CREATE TABLE dbo.user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password VARCHAR(256) NOT NULL
);

CREATE TABLE dbo.token (
    token VARCHAR(256) PRIMARY KEY,
    uid INTEGER REFERENCES dbo.user(id),
    created_at BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT null,
    last_used_at BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL
);

CREATE TABLE dbo.channel_table (
    id SERIAL PRIMARY KEY,
    admin INTEGER REFERENCES dbo.user(id),
    name VARCHAR(64) UNIQUE NOT NULL,
    description VARCHAR(256) NOT NULL,
    isPublic BOOLEAN NOT NULL
);

CREATE TABLE dbo.role (
    uid INTEGER REFERENCES dbo.user(id),
    cid INTEGER REFERENCES dbo.channel_table(id),
    role VARCHAR(10) CHECK (role IN ('admin', 'member', 'observer')) NOT NULL,
    time BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP),
    UNIQUE(uid, cid)
);

CREATE VIEW dbo.channel AS
SELECT c.id, c.admin, c.name, c.description, c.isPublic, COUNT(uid) AS memberCount FROM dbo.channel_table  c
left JOIN dbo.role r ON c.id = r.cid
GROUP BY c.id, c.admin, c.name, c.description, c.isPublic
order by c.id;

CREATE TABLE dbo.message (
    id SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    uid INTEGER REFERENCES dbo.user(id),
    cid INTEGER REFERENCES dbo.channel_table(id),
    time BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)
);

CREATE TABLE dbo.channel_invites (
    uid INTEGER REFERENCES dbo.user(id),
    cid INTEGER REFERENCES dbo.channel_table(id),
    type VARCHAR(10) CHECK (type IN ('member', 'observer')) NOT NULL,
    UNIQUE(uid, cid)
);

CREATE TABLE dbo.app_invites (
    app_invite VARCHAR(256) PRIMARY key,
    inviter INTEGER REFERENCES dbo.user(id)
);