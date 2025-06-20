-- Drop existing tables if they exist
DROP TABLE IF EXISTS waveCoach.heat;
DROP TABLE IF EXISTS waveCoach.athlete_competition;
DROP TABLE IF EXISTS waveCoach.competition;
DROP TABLE IF EXISTS waveCoach.maneuver;
DROP TABLE IF EXISTS waveCoach.water_maneuver;
DROP TABLE IF EXISTS waveCoach.questionnaire;
DROP TABLE IF EXISTS waveCoach.water;
DROP TABLE IF EXISTS waveCoach.sets;
DROP TABLE IF EXISTS waveCoach.exercise;
DROP TABLE IF EXISTS waveCoach.gym_exercise;
DROP TABLE IF EXISTS waveCoach.gym;
DROP TABLE IF EXISTS waveCoach.activity;
DROP TABLE IF EXISTS waveCoach.microcycle;
DROP TABLE IF EXISTS waveCoach.mesocycle;
DROP TABLE IF EXISTS waveCoach.characteristics;
DROP TABLE IF EXISTS waveCoach.token;
DROP TABLE IF EXISTS waveCoach.code;
DROP TABLE IF EXISTS waveCoach.athlete;
DROP TABLE IF EXISTS waveCoach.coach;
DROP TABLE IF EXISTS waveCoach.user;

-- Create schema
CREATE SCHEMA IF NOT EXISTS waveCoach;

-- Create new tables
CREATE TABLE waveCoach.user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password VARCHAR(256) NOT NULL,
    is_coach BOOLEAN DEFAULT TRUE
);

CREATE TABLE waveCoach.coach (
    uid INTEGER PRIMARY KEY,
    FOREIGN KEY (uid) REFERENCES waveCoach.user(id)
);

CREATE TABLE waveCoach.athlete (
    uid INTEGER PRIMARY KEY,
    coach INTEGER,
    name VARCHAR(64) NOT NULL,
    birthdate BIGINT NOT NULL,
    credentials_changed BOOLEAN DEFAULT FALSE NOT NULL,
    url VARCHAR(256) DEFAULT NULL,
    FOREIGN KEY (coach) REFERENCES waveCoach.coach(uid) ON DELETE CASCADE,
    FOREIGN KEY (uid) REFERENCES waveCoach.user(id) ON DELETE CASCADE
);

CREATE TABLE waveCoach.code (
    code VARCHAR(256) PRIMARY KEY,
    uid INTEGER UNIQUE,
    created_time BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL,
    FOREIGN KEY (uid) REFERENCES waveCoach.athlete(uid) ON DELETE CASCADE
);

CREATE TABLE waveCoach.token (
    token VARCHAR(256) PRIMARY KEY,
    uid INTEGER,
    created_time BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL,
    used_time BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL,
    FOREIGN KEY (uid) REFERENCES waveCoach.user(id) ON DELETE CASCADE
);

CREATE TABLE waveCoach.characteristics (
    date BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL,
    uid INTEGER,
    weight FLOAT,
    height INTEGER,
    bmi FLOAT GENERATED ALWAYS AS (weight / ((height / 100.0) * (height / 100.0))) STORED,
    calories INTEGER,
    body_fat FLOAT,
    waist_size INTEGER,
    arm_size INTEGER,
    thigh_size INTEGER,
    tricep_fat INTEGER,
    abdomen_fat INTEGER,
    thigh_fat INTEGER,
    PRIMARY KEY (date, uid),
    FOREIGN KEY (uid) REFERENCES waveCoach.athlete(uid) ON DELETE CASCADE
);

CREATE TABLE waveCoach.mesocycle(
    id SERIAL PRIMARY KEY,
    uid INTEGER,
    start_time BIGINT NOT NULL,
    end_time BIGINT NOT NULL,
    FOREIGN KEY (uid) REFERENCES waveCoach.athlete(uid) ON DELETE CASCADE
);

CREATE TABLE waveCoach.microcycle(
    id SERIAL PRIMARY KEY,
    mesocycle INTEGER,
    start_time BIGINT NOT NULL,
    end_time BIGINT NOT NULL,
    FOREIGN KEY (mesocycle) REFERENCES waveCoach.mesocycle(id) ON DELETE CASCADE
);

CREATE TABLE waveCoach.activity(
    id SERIAL PRIMARY KEY,
    uid INTEGER,
    microcycle INTEGER,
    date BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL,
    type VARCHAR(64),
    FOREIGN KEY (uid) REFERENCES waveCoach.athlete(uid) ON DELETE CASCADE,
    FOREIGN KEY (microcycle) REFERENCES waveCoach.microcycle(id) ON DELETE CASCADE
);

CREATE TABLE waveCoach.gym(
    activity INTEGER PRIMARY KEY,
    FOREIGN KEY (activity) REFERENCES waveCoach.activity(id) ON DELETE CASCADE
);

CREATE TABLE waveCoach.gym_exercise(
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    category VARCHAR(64) NOT NULL,
    url VARCHAR(256) DEFAULT NULL
);

CREATE TABLE waveCoach.exercise(
    id SERIAL PRIMARY KEY,
    activity INTEGER,
    exercise INTEGER,
    exercise_order INTEGER,
    FOREIGN KEY (activity) REFERENCES waveCoach.gym(activity) ON DELETE CASCADE,
    FOREIGN KEY (exercise) REFERENCES waveCoach.gym_exercise(id) ON DELETE CASCADE
);

CREATE TABLE waveCoach.set(
    id SERIAL PRIMARY KEY,
    exercise_id INTEGER,
    weight FLOAT,
    reps INTEGER,
    rest_time FLOAT,
    set_order INTEGER,
    FOREIGN KEY (exercise_id) REFERENCES waveCoach.exercise(id) ON DELETE CASCADE
);

CREATE TABLE waveCoach.water(
    activity INTEGER PRIMARY KEY,
    rpe INTEGER,
    condition VARCHAR(64),
    trimp INTEGER,
    duration INTEGER,
    FOREIGN KEY (activity) REFERENCES waveCoach.activity(id) ON DELETE CASCADE
);
CREATE TABLE waveCoach.questionnaire(
    id SERIAL PRIMARY KEY,
    activity INTEGER,
    sleep INTEGER,
    fatigue INTEGER,
    stress INTEGER,
    muscle_pain INTEGER,
    FOREIGN KEY (activity) REFERENCES waveCoach.water(activity) ON DELETE CASCADE
);

CREATE TABLE waveCoach.wave(
    id SERIAL PRIMARY KEY,
    activity INTEGER,
    points FLOAT,
    right_side BOOLEAN,
    wave_order INTEGER,
    FOREIGN KEY (activity) REFERENCES waveCoach.water(activity) ON DELETE CASCADE
);

CREATE TABLE waveCoach.water_maneuver(
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    url VARCHAR(256) DEFAULT NULL
);

CREATE TABLE waveCoach.maneuver(
    id SERIAL PRIMARY KEY,
    wave INTEGER,
    maneuver INTEGER,
    success BOOLEAN,
    maneuver_order INTEGER,
    FOREIGN KEY (wave) REFERENCES waveCoach.wave(id) ON DELETE CASCADE,
    FOREIGN KEY (maneuver) REFERENCES waveCoach.water_maneuver(id) ON DELETE CASCADE
);

CREATE TABLE waveCoach.competition(
    id SERIAL PRIMARY KEY,
    uid INTEGER,
    date BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL,
    location VARCHAR(64),
    place INTEGER,
    FOREIGN KEY (uid) REFERENCES waveCoach.athlete(uid) ON DELETE CASCADE
);

CREATE TABLE waveCoach.heat(
    id SERIAL,
    competition INTEGER,
    water_activity INTEGER,
    score INTEGER,
    PRIMARY KEY (id, competition),
    FOREIGN KEY (competition) REFERENCES waveCoach.competition(id) ON DELETE CASCADE,
    FOREIGN KEY (water_activity) REFERENCES waveCoach.water(activity) ON DELETE CASCADE
);



-- Create triggers to set user roles and activity types

CREATE OR REPLACE FUNCTION set_coach()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE waveCoach.user
    SET is_coach = TRUE
    WHERE id = NEW.uid;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_coach
AFTER INSERT ON waveCoach.coach
FOR EACH ROW
EXECUTE FUNCTION set_coach();





CREATE OR REPLACE FUNCTION set_athlete()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE waveCoach.user
    SET is_coach = FALSE
    WHERE id = NEW.uid;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_athlete
AFTER INSERT ON waveCoach.athlete
FOR EACH ROW
EXECUTE FUNCTION set_athlete();





CREATE OR REPLACE FUNCTION set_activity_type_gym()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE waveCoach.activity
    SET type = 'gym'
    WHERE id = NEW.activity;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_activity_type_gym
AFTER INSERT ON waveCoach.gym
FOR EACH ROW
EXECUTE FUNCTION set_activity_type_gym();





CREATE OR REPLACE FUNCTION set_activity_type_water()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE waveCoach.activity
    SET type = 'water'
    WHERE id = NEW.activity;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_activity_type_water
AFTER INSERT ON waveCoach.water
FOR EACH ROW
EXECUTE FUNCTION set_activity_type_water();
