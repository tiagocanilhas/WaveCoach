-- Drop existing tables if they exist
DROP TABLE IF EXISTS waveCoach.heat;
DROP TABLE IF EXISTS waveCoach.athlete_competition;
DROP TABLE IF EXISTS waveCoach.competition;
DROP TABLE IF EXISTS waveCoach.maneuver;
DROP TABLE IF EXISTS waveCoach.water_maneuvers;
DROP TABLE IF EXISTS waveCoach.questionnaire;
DROP TABLE IF EXISTS waveCoach.water;
DROP TABLE IF EXISTS waveCoach.exercise;
DROP TABLE IF EXISTS waveCoach.gym_exercises;
DROP TABLE IF EXISTS waveCoach.gym;
DROP TABLE IF EXISTS waveCoach.activity;
DROP TABLE IF EXISTS waveCoach.microcycle;
DROP TABLE IF EXISTS waveCoach.mesocycle;
DROP TABLE IF EXISTS waveCoach.characteristics;
DROP TABLE IF EXISTS waveCoach.token;
DROP TABLE IF EXISTS waveCoach.athlete;
DROP TABLE IF EXISTS waveCoach.coach;
DROP TABLE IF EXISTS waveCoach.user;

-- Create schema
CREATE SCHEMA IF NOT EXISTS waveCoach;

-- Create new tables
CREATE TABLE waveCoach.user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password VARCHAR(256) NOT NULL
);

CREATE TABLE waveCoach.coach (
    uid INTEGER PRIMARY KEY,
    FOREIGN KEY (uid) REFERENCES waveCoach.user(id)
);

CREATE TABLE waveCoach.athlete (
    uid INTEGER PRIMARY KEY,
    coach INTEGER,
    name VARCHAR(64) NOT NULL,
    birth_date BIGINT NOT NULL,
    --code VARCHAR(64) UNIQUE NOT NULL,
    FOREIGN KEY (coach) REFERENCES waveCoach.coach(uid),
    FOREIGN KEY (uid) REFERENCES waveCoach.user(id)
);

CREATE TABLE waveCoach.token (
    token VARCHAR(256) PRIMARY KEY,
    uid INTEGER REFERENCES waveCoach.user(id),
    created_time BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL,
    used_time BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL
);

CREATE TABLE waveCoach.characteristics (
    date BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL,
    uid INTEGER REFERENCES waveCoach.athlete(uid),
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
    PRIMARY KEY (date, uid)
);

CREATE TABLE waveCoach.mesocycle(
    id SERIAL PRIMARY KEY,
    uid INTEGER REFERENCES waveCoach.athlete(uid),
    start_time BIGINT NOT NULL,
    end_time BIGINT NOT NULL
);

CREATE TABLE waveCoach.microcycle(
    id SERIAL PRIMARY KEY,
    mesocycle INTEGER REFERENCES waveCoach.mesocycle(id),
    start_time BIGINT NOT NULL,
    end_time BIGINT NOT NULL
);

CREATE TABLE waveCoach.activity(
    id SERIAL PRIMARY KEY,
    uid INTEGER REFERENCES waveCoach.athlete(uid),
    date BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL
);

CREATE TABLE waveCoach.gym(
    activity INTEGER PRIMARY KEY,
    FOREIGN KEY (activity) REFERENCES waveCoach.activity(id)
);

CREATE TABLE waveCoach.gym_exercises(
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    category VARCHAR(64) NOT NULL
);

CREATE TABLE waveCoach.exercise(
    id SERIAL PRIMARY KEY,
    activity INTEGER REFERENCES waveCoach.gym(activity),
    exercise INTEGER REFERENCES waveCoach.gym_exercises(id),
    weight FLOAT,
    series INTEGER,
    repetitions INTEGER,
    rest_time INTEGER,
    order_ INTEGER
);

CREATE TABLE waveCoach.water(
    activity INTEGER PRIMARY KEY,
    pse INTEGER,
    attempts INTEGER,
    waves INTEGER,
    condition VARCHAR(64),
    heart_rate INTEGER,
    time INTEGER,
    FOREIGN KEY (activity) REFERENCES waveCoach.activity(id)
);

CREATE TABLE waveCoach.questionnaire(
    id SERIAL PRIMARY KEY,
    activity INTEGER REFERENCES waveCoach.water(activity),
    sleep INTEGER,
    fatigue INTEGER,
    stress INTEGER,
    muscle_pain INTEGER
);

CREATE TABLE waveCoach.water_maneuvers(
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);

CREATE TABLE waveCoach.maneuver(
    id SERIAL PRIMARY KEY,
    activity INTEGER REFERENCES waveCoach.water(activity),
    maneuver INTEGER REFERENCES waveCoach.water_maneuvers(id),
    side VARCHAR(5) CHECK (side IN ('left', 'right')) NOT NULL,
    success INTEGER,
    failed INTEGER,
    order_ INTEGER
);

CREATE TABLE waveCoach.competition(
    id SERIAL PRIMARY KEY,
    date BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL,
    location VARCHAR(64)
);

CREATE TABLE waveCoach.athlete_competition(
    athlete INTEGER REFERENCES waveCoach.athlete(uid),
    competition INTEGER REFERENCES waveCoach.competition(id),
    score INTEGER,
    PRIMARY KEY (athlete, competition)
);

CREATE TABLE waveCoach.heat(
);



