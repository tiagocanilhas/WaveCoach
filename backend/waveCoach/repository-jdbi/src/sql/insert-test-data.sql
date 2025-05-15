--Insert gym exercises
insert into waveCoach.gym_exercise (name, category) values ('Deadlift', 'Back');
insert into waveCoach.gym_exercise (name, category) values ('Supine', 'Chest');
insert into waveCoach.gym_exercise (name, category) values ('Pull Up', 'Back');
insert into waveCoach.gym_exercise (name, category) values ('Shoulder Press', 'Shoulders');
insert into waveCoach.gym_exercise (name, category) values ('Lumbar Extension', 'Chest');
insert into waveCoach.gym_exercise (name, category) values ('Dynamic plank with a ball', 'Chest');

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

-- Mesocycles
INSERT INTO waveCoach.mesocycle (uid, start_time, end_time) VALUES (3, 1746057600000, 1748736000000); -- 2025-05-01 to 2025-06-01
INSERT INTO waveCoach.mesocycle (uid, start_time, end_time) VALUES (3, 1748736000000, 1751328000000); -- 2025-06-01 to 2025-07-01
INSERT INTO waveCoach.mesocycle (uid, start_time, end_time) VALUES (3, 1751328000000, 1754006400000); -- 2025-07-01 to 2025-08-01

-- Microcycles
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (1, 1746057600000, 1747339200000); -- 2025-05-01 to 2025-05-15
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (1, 1747339200000, 1748736000000); -- 2025-05-15 to 2025-06-01
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (2, 1748736000000, 1749340800000); -- 2025-06-01 to 2025-06-08
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (2, 1749340800000, 1749945600000); -- 2025-06-08 to 2025-06-15
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (2, 1749945600000, 1751328000000); -- 2025-06-15 to 2025-07-01
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (3, 1751328000000, 1752528000000); -- 2025-07-01 to 2025-07-14
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (3, 1752528000000, 1754006400000); -- 2025-07-14 to 2025-08-01

-- Activities for Microcycle 1 (2025-05-01 to 2025-05-15)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 1, 1746057600000); -- 2025-05-01
INSERT INTO waveCoach.gym (activity) VALUES (1);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 1, 1746144000000); -- 2025-05-02
INSERT INTO waveCoach.water (activity) VALUES (2);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 1, 1746230400000); -- 2025-05-03
INSERT INTO waveCoach.water (activity) VALUES (3);

-- Activities for Microcycle 2 (2025-05-15 to 2025-06-01)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 2, 1747339200000); -- 2025-05-15
INSERT INTO waveCoach.gym (activity) VALUES (4);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 2, 1747425600000); -- 2025-05-16
INSERT INTO waveCoach.water (activity) VALUES (5);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 2, 1747512000000); -- 2025-05-17
INSERT INTO waveCoach.water (activity) VALUES (6);

-- Activities for Microcycle 3 (2025-06-01 to 2025-06-08)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 3, 1748736000000); -- 2025-06-01
INSERT INTO waveCoach.gym (activity) VALUES (7);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 3, 1748822400000); -- 2025-06-02
INSERT INTO waveCoach.water (activity) VALUES (8);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 3, 1748908800000); -- 2025-06-03
INSERT INTO waveCoach.water (activity) VALUES (9);

-- Activities for Microcycle 4 (2025-06-08 to 2025-06-15)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 4, 1749340800000); -- 2025-06-08
INSERT INTO waveCoach.gym (activity) VALUES (10);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 4, 1749427200000); -- 2025-06-09
INSERT INTO waveCoach.water (activity) VALUES (11);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 4, 1749513600000); -- 2025-06-10
INSERT INTO waveCoach.water (activity) VALUES (12);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 4, 1749600000000); -- 2025-06-11
INSERT INTO waveCoach.water (activity) VALUES (13);

-- Activities for Microcycle 5 (2025-06-15 to 2025-07-01)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 5, 1749945600000); -- 2025-06-15
INSERT INTO waveCoach.gym (activity) VALUES (14);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 5, 1750032000000); -- 2025-06-16
INSERT INTO waveCoach.water (activity) VALUES (15);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 5, 1750118400000); -- 2025-06-17
INSERT INTO waveCoach.water (activity) VALUES (16);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 5, 1750204800000); -- 2025-06-18
INSERT INTO waveCoach.water (activity) VALUES (17);

-- Activities for Microcycle 6 (2025-07-01 to 2025-07-14)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 6, 1751328000000); -- 2025-07-01
INSERT INTO waveCoach.gym (activity) VALUES (18);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 6, 1751414400000); -- 2025-07-02
INSERT INTO waveCoach.gym (activity) VALUES (19);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 6, 1751500800000); -- 2025-07-03
INSERT INTO waveCoach.water (activity) VALUES (20);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 6, 1751587200000); -- 2025-07-04
INSERT INTO waveCoach.water (activity) VALUES (21);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 6, 1751673600000); -- 2025-07-05
INSERT INTO waveCoach.water (activity) VALUES (22);

-- Activities for Microcycle 7 (2025-07-14 to 2025-08-01)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 7, 1752528000000); -- 2025-07-14
INSERT INTO waveCoach.gym (activity) VALUES (23);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 7, 17526144000000); -- 2025-07-15
INSERT INTO waveCoach.water (activity) VALUES (24);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 7, 1752700800000); -- 2025-07-16
INSERT INTO waveCoach.water (activity) VALUES (25);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 7, 1752787200000); -- 2025-07-17
INSERT INTO waveCoach.gym (activity) VALUES (26);
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 7, 1752873600000); -- 2025-07-18
INSERT INTO waveCoach.water (activity) VALUES (27);

insert into waveCoach.exercise (activity, exercise, exercise_order) values (1, 1, 1);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (1, 100.0, 10, 60.0, 1);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (1, 120.0, 15, 60.0, 2);
insert into waveCoach.exercise (activity, exercise, exercise_order) values (1, 2, 2);
insert into waveCoach.code (code, uid) values ('mGi_ziXh1QK26wim_2Eq3fTbK-2UYOQwGat8keR69EA=', 3);
-- code: lnAEN21Ohq4cuorzGxMSZMKhCj2mXXSFXCO6UKzSluU=

--Second Athlete
insert into waveCoach.user (username, password) values ('athlete2', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
-- password: Admin123!
insert into waveCoach.athlete (uid, coach, name, birth_date, credentials_changed) values (4, 2, 'Jane Doe', 631152000, true);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (948758400000, 4, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (947462400000, 4, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);

--Third Athlete
insert into waveCoach.user (username, password) values ('athlete3', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
-- password: Admin123!
insert into waveCoach.athlete (uid, coach, name, birth_date) values (5, 2, 'John Smith', 631152000);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (948758400000, 5, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (947462400000, 5, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);
insert into waveCoach.activity (uid, date) values (5, 948758400000);
insert into waveCoach.gym (activity) values (28);

--Fourth Athlete
insert into waveCoach.user (username, password) values ('athlete4', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
-- password: Admin123!
insert into waveCoach.athlete (uid, coach, name, birth_date) values (6, 2, 'Jane Smith', 631152000);

insert into waveCoach.activity (uid, date) values (5, 947462400000);
insert into waveCoach.gym (activity) values (3);
insert into waveCoach.exercise (activity, exercise, exercise_order) values (3, 2, 1);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (3, 100.0, 10, 60.0, 1);

--Second athlete activity
insert into waveCoach.activity (uid, date) values (4, 948758400000);
insert into waveCoach.gym (activity) values (4);



