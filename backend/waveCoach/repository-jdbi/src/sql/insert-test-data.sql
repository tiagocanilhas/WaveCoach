--First Coach
insert into waveCoach.user (username, password) values ('admin', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
-- password: Admin123!
insert into waveCoach.coach (uid) values (1);
insert into waveCoach.token (token, uid) values ('DOi36t96dyrduP2hLglteGBDCzb0hBvpCvRddTuaDqc=', 1);
-- token: i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ=

--Second Coach
insert into waveCoach.user (username, password) values ('user2', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
-- password: Admin123!
insert into waveCoach.coach (uid) values (2);
insert into waveCoach.token (token, uid) values ('JXR67wrRv2s2SG5LtZZd5M1BsQQfjKpOby9VnMjztKo=', 2);
-- token: fM5JjtPOUqtnZg1lB7jnJhXBP5gI2WbIIBoO3JhYM5M=

--First Athlete
insert into waveCoach.user (username, password) values ('athlete', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
 -- password: Admin123!
insert into waveCoach.athlete (uid, coach, name, birth_date) values (3, 1, 'John Doe', 631152000);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (948758400000, 3, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (947462400000, 3, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);
insert into waveCoach.activity (uid, date) values (3, 948758400000);
insert into waveCoach.gym (activity) values (1);

--Second Athlete
insert into waveCoach.user (username, password) values ('athlete2', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
-- password: Admin123!
insert into waveCoach.athlete (uid, coach, name, birth_date) values (4, 2, 'Jane Doe', 631152000);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (948758400000, 4, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (947462400000, 4, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);

--Third Athlete
insert into waveCoach.user (username, password) values ('athlete3', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
-- password: Admin123!
insert into waveCoach.athlete (uid, coach, name, birth_date) values (5, 2, 'John Smith', 631152000);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (948758400000, 5, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (947462400000, 5, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);
insert into waveCoach.activity (uid, date) values (5, 948758400000);
insert into waveCoach.gym (activity) values (2);

