--Insert gym exercises
insert into waveCoach.gym_exercise (name, category) values ('Deadlift', 'legs');
insert into waveCoach.gym_exercise (name, category) values ('Squat', 'legs');
insert into waveCoach.gym_exercise (name, category) values ('Leg Press', 'legs');
insert into waveCoach.gym_exercise (name, category) values ('Bench Press', 'chest');
insert into waveCoach.gym_exercise (name, category) values ('Incline Bench Press', 'chest');
insert into waveCoach.gym_exercise (name, category) values ('Dumbbell Press', 'chest');
insert into waveCoach.gym_exercise (name, category) values ('Incline Dumbbell Press', 'chest');
insert into waveCoach.gym_exercise (name, category) values ('Pull Up', 'back');
insert into waveCoach.gym_exercise (name, category) values ('Lat Pull Down', 'back');
insert into waveCoach.gym_exercise (name, category) values ('Shoulder Press', 'shoulders');
insert into waveCoach.gym_exercise (name, category) values ('Lateral Raises', 'shoulders');
insert into waveCoach.gym_exercise (name, category) values ('Bicep curl', 'arms');
insert into waveCoach.gym_exercise (name, category) values ('Triceps Pushdown', 'arms');

--Insert Water Maneuvers
insert into waveCoach.water_maneuver (name) values ('Roll');
insert into waveCoach.water_maneuver (name) values ('360');
insert into waveCoach.water_maneuver (name) values ('360i');
insert into waveCoach.water_maneuver (name) values ('ARS');
insert into waveCoach.water_maneuver (name) values ('Invert');
insert into waveCoach.water_maneuver (name) values ('Backflip');
insert into waveCoach.water_maneuver (name) values ('Tube Ride');

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
insert into waveCoach.token (token, uid) values ('458Adjq4vJVtK5E-Ue4-nOYA-sZVMBEAbDTtQjtr8JA=', 3);
insert into waveCoach.code (code, uid) values ('mGi_ziXh1QK26wim_2Eq3fTbK-2UYOQwGat8keR69EA=', 3);
-- code: lnAEN21Ohq4cuorzGxMSZMKhCj2mXXSFXCO6UKzSluU=
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (948758400000, 3, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (947462400000, 3, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);

-- Mesocycles
INSERT INTO waveCoach.mesocycle (uid, start_time, end_time) VALUES (3, 1746057600000, 1748736000000); -- 2025-05-01 to 2025-06-01
INSERT INTO waveCoach.mesocycle (uid, start_time, end_time) VALUES (3, 1748736000000, 1751328000000); -- 2025-06-01 to 2025-07-01
INSERT INTO waveCoach.mesocycle (uid, start_time, end_time) VALUES (3, 1751328000000, 1754006400000); -- 2025-07-01 to 2025-08-01
INSERT INTO waveCoach.mesocycle (uid, start_time, end_time) VALUES (3, 1754006400000, 1756684800000); -- 2025-08-01 to 2025-09-01

-- Microcycles
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (1, 1746057600000, 1747339200000); -- 2025-05-01 to 2025-05-15
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (1, 1747339200000, 1748736000000); -- 2025-05-15 to 2025-06-01
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (2, 1748736000000, 1749340800000); -- 2025-06-01 to 2025-06-08
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (2, 1749340800000, 1749945600000); -- 2025-06-08 to 2025-06-15
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (2, 1749945600000, 1751328000000); -- 2025-06-15 to 2025-07-01
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (3, 1751328000000, 1752528000000); -- 2025-07-01 to 2025-07-14
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (3, 1752528000000, 1754006400000); -- 2025-07-14 to 2025-08-01
INSERT INTO waveCoach.microcycle (mesocycle, start_time, end_time) VALUES (4, 1754006400000, 1754613000000); -- 2025-08-01 to 2025-08-08

-- Activities for Microcycle 1 (2025-05-01 to 2025-05-15)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 1, 1746057600000); -- 2025-05-01
INSERT INTO waveCoach.gym (activity) VALUES (1);
insert into waveCoach.exercise (activity, exercise, exercise_order) values (1, 1, 1);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (1, 50.0, 12, 60.0, 1);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (1, 55.0, 10, 60.0, 2);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (1, 60.0, 8, 60.0, 3);
insert into waveCoach.exercise (activity, exercise, exercise_order) values (1, 4, 2);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (2, 40.0, 10, 60.0, 1);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (2, 50.0, 6, 90.0, 2);
insert into waveCoach.exercise (activity, exercise, exercise_order) values (1, 8, 3);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (3, 45.0, 12, 60.0, 1);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (3, 45.0, 12, 60.0, 2);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (3, 45.0, 12, 60.0, 3);
insert into waveCoach.exercise (activity, exercise, exercise_order) values (1, 12, 4);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (4, 10.0, 15, 60.0, 1);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (4, 10.0, 15, 60.0, 2);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (4, 10.0, 15, 60.0, 3);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (4, 10.0, 15, 60.0, 4);
insert into waveCoach.exercise (activity, exercise, exercise_order) values (1, 13, 5);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (5, 10.0, 15, 60.0, 1);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (5, 10.0, 15, 60.0, 2);
insert into waveCoach.exercise (activity, exercise, exercise_order) values (1, 2, 6);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (6, 10.0, 15, 60.0, 1);
insert into waveCoach.sets (exercise_id, weight, reps, rest_time, set_order) values (6, 10.0, 15, 60.0, 2);


INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 1, 1746144000000); -- 2025-05-02
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (2, 5, 'Good', 120, 60);
INSERT INTO waveCoach.wave (activity, points, wave_order) VALUES (2, 5, 1);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (1, 1, true, true, 1);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (1, 2, true, true, 2);
INSERT INTO waveCoach.wave (activity, points, wave_order) VALUES (2, 2, 2);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 1, 1746230400000); -- 2025-05-03
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (3, 6, 'Good', 115, 45);

INSERT INTO waveCoach.questionnaire (activity, sleep, fatigue, stress, muscle_pain) VALUES (3, 7, 3, 2, 4);

-- Activities for Microcycle 2 (2025-05-15 to 2025-06-01)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 2, 1747339200000); -- 2025-05-15
INSERT INTO waveCoach.gym (activity) VALUES (4);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 2, 1747425600000); -- 2025-05-16
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (5, 7, 'Good', 110, 3600);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 2, 1747512000000); -- 2025-05-17
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (6, 6, 'Good', 115, 3600);

-- Activities for Microcycle 3 (2025-06-01 to 2025-06-08)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 3, 1748736000000); -- 2025-06-01
INSERT INTO waveCoach.gym (activity) VALUES (7);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 3, 1748822400000); -- 2025-06-02
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (8, 6, 'Good', 112, 5400);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 3, 1748908800000); -- 2025-06-03
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (9, 5, 'Good', 118, 5400);

-- Activities for Microcycle 4 (2025-06-08 to 2025-06-15)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 4, 1749340800000); -- 2025-06-08
INSERT INTO waveCoach.gym (activity) VALUES (10);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 4, 1749427200000); -- 2025-06-09
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (11, 7, 'Good', 120, 5400);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 4, 1749513600000); -- 2025-06-10
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (12, 8, 'Good', 125, 5400);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 4, 1749600000000); -- 2025-06-11
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (13, 9, 'Good', 130, 5400);

-- Activities for Microcycle 5 (2025-06-15 to 2025-07-01)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 5, 1749945600000); -- 2025-06-15
INSERT INTO waveCoach.gym (activity) VALUES (14);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 5, 1750032000000); -- 2025-06-16
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (15, 7, 'Good', 115, 5400);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 5, 1750118400000); -- 2025-06-17
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (16, 6, 'Good', 120, 5400);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 5, 1750204800000); -- 2025-06-18
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (17, 5, 'Good', 125, 5400);

-- Activities for Microcycle 6 (2025-07-01 to 2025-07-14)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 6, 1751328000000); -- 2025-07-01
INSERT INTO waveCoach.gym (activity) VALUES (18);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 6, 1751414400000); -- 2025-07-02
INSERT INTO waveCoach.gym (activity) VALUES (19);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 6, 1751500800000); -- 2025-07-03
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (20, 7, 'Good', 110, 5400);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 6, 1751587200000); -- 2025-07-04
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (21, 6, 'Good', 115, 5400);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 6, 1751673600000); -- 2025-07-05
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (22, 5, 'Good', 120, 5400);

-- Activities for Microcycle 7 (2025-07-14 to 2025-08-01)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 7, 1752528000000); -- 2025-07-14
INSERT INTO waveCoach.gym (activity) VALUES (23);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 7, 1752614400000); -- 2025-07-15
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (24, 8, 'Good', 115, 5400);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 7, 1752700800000); -- 2025-07-16
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (25, 9, 'Good', 120, 5400);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 7, 1752787200000); -- 2025-07-17
INSERT INTO waveCoach.gym (activity) VALUES (26);

INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 7, 1752873600000); -- 2025-07-18
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (27, 7, 'Good', 125, 5400);

-- Activities for Microcycle 8 (2025-08-01 to 2025-08-08)
INSERT INTO waveCoach.activity (uid, microcycle, date) VALUES (3, 8, 1754006400000); -- 2025-08-01
INSERT INTO waveCoach.water (activity, pse, condition, heart_rate, duration) VALUES (28, 8, 'Good', 107, 5400);

INSERT INTO waveCoach.wave (activity, points, wave_order) VALUES (28, 5, 1);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (3, 1, true, true, 1);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (3, 1, true, true, 2);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (3, 1, true, true, 3);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (3, 1, true, false, 4);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (3, 1, true, false, 5);

INSERT INTO waveCoach.wave (activity, points, wave_order) VALUES (28, 2, 2);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (4, 1, false, true, 1);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (4, 1, false, true, 2);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (4, 1, false, true, 3);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (4, 1, false, true, 4);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (4, 1, false, true, 5);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (4, 1, false, false, 6);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (4, 1, false, false, 7);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (4, 1, false, false, 8);

INSERT INTO waveCoach.wave (activity, points, wave_order) VALUES (28, 2, 3);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (5, 3, false, true, 1);

INSERT INTO waveCoach.wave (activity, points, wave_order) VALUES (28, 2, 4);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (6, 3, true, true, 1);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (6, 3, true, false, 2);
INSERT INTO waveCoach.maneuver (wave, maneuver, right_side, success, maneuver_order) VALUES (6, 3, true, false, 3);

--Second Athlete
insert into waveCoach.user (username, password) values ('athlete2', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
-- password: Admin123!
insert into waveCoach.athlete (uid, coach, name, birth_date, credentials_changed) values (4, 2, 'Jane Doe', 631152000, true);
insert into waveCoach.token (token, uid) values ('ARg4LKqg5rVEhPhPwpwrDM2EF04qVcONyjUEyvc_jhY=', 4);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (948758400000, 4, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (947462400000, 4, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);

--Third Athlete
insert into waveCoach.user (username, password) values ('athlete3', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
-- password: Admin123!
insert into waveCoach.athlete (uid, coach, name, birth_date) values (5, 2, 'John Smith', 631152000);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (948758400000, 5, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);
insert into waveCoach.characteristics (date, uid, weight, height, calories, body_fat, waist_size, arm_size, thigh_size, tricep_fat, abdomen_fat, thigh_fat) values (947462400000, 5, 74.0, 181, 1, 1.0, 1, 1, 1, 1, 1, 1);

--Fourth Athlete
insert into waveCoach.user (username, password) values ('athlete4', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
-- password: Admin123!
insert into waveCoach.athlete (uid, coach, name, birth_date) values (6, 2, 'Jane Smith', 631152000);

--Fifth Athlete
insert into waveCoach.user (username, password) values ('athlete5', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
-- password: Admin123!
insert into waveCoach.athlete (uid, coach, name, birth_date) values (7, 1, 'Alice Johnson', 631152000);





