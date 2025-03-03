insert into dbo.user (username, password) values ('admin', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
 -- password: Admin123!
insert into dbo.user (username, password) values ('user2', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
-- password: Admin123!
insert into dbo.user (username, password) values ('user3', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
-- password: Admin123!
insert into dbo.user (username, password) values ('user4', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
 -- password: Admin123!
insert into dbo.user (username, password) values ('user5', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
 -- password: Admin123!

insert into dbo.channel_table (admin, name, description, isPublic) values (1, 'general', 'General chat', true);
insert into dbo.role (uid, cid, role) values (1, 1, 'admin');
insert into dbo.role (uid, cid, role) values (4, 1, 'member');

insert into dbo.message (content, uid, cid) values ('Message 1!', 1, 1);
insert into dbo.message (content, uid, cid) values ('Message 2!', 1, 1);
insert into dbo.message (content, uid, cid) values ('Message 3!', 1, 1);

insert into dbo.channel_table (admin, name, description, isPublic) values (1, 'private', 'Private chat', false);
insert into dbo.role (uid, cid, role) values (1, 2, 'admin');
insert into dbo.role (uid, cid, role) values (2, 2, 'member');
insert into dbo.role (uid, cid, role) values (3, 2, 'observer');
insert into dbo.channel_invites (uid, cid, type) values (4, 2, 'member');

insert into dbo.channel_table (admin, name, description, isPublic) values (1, 'really private', 'A really private chat', false);
insert into dbo.role (uid, cid, role) values (1, 3, 'admin');
insert into dbo.channel_invites (uid, cid, type) values (5, 3, 'member');

insert into dbo.channel_table (admin, name, description, isPublic) values (1, 'second public channel', 'this is the second public channel', true);
insert into dbo.role (uid, cid, role) values (1, 4, 'admin');

insert into dbo.channel_table (admin, name, description, isPublic) values (1, 'third public channel', 'this is the third public channel', true);
insert into dbo.role (uid, cid, role) values (1, 5, 'admin');

insert into dbo.channel_table (admin, name, description, isPublic) values (2, 'fourth public channel', 'this is the fourth public channel', true);
insert into dbo.role (uid, cid, role) values (2, 6, 'admin');

insert into dbo.channel_table (admin, name, description, isPublic) values (2, 'fifth public channel', 'this is the fifth public channel', true);
insert into dbo.role (uid, cid, role) values (2, 7, 'admin');

insert into dbo.token (token, uid) values ('DOi36t96dyrduP2hLglteGBDCzb0hBvpCvRddTuaDqc=', 1);
-- token: i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ=
insert into dbo.token (token, uid) values ('JXR67wrRv2s2SG5LtZZd5M1BsQQfjKpOby9VnMjztKo=', 2);
-- token: fM5JjtPOUqtnZg1lB7jnJhXBP5gI2WbIIBoO3JhYM5M=
insert into dbo.token (token, uid) values ('k2hUufl0Zt53LREckhLYjD1lfNU4K-xOg0XVTeu26UA=', 4);
-- token: MIxE4a2du18-3Os7K2r0k8Je-KUoEVJjRAX7Hq8Nbyg=