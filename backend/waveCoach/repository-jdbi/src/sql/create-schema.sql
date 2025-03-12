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
DROP TABLE IF EXISTS waveCoach.caracteristics;
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
    code VARCHAR(64) UNIQUE NOT NULL,
    FOREIGN KEY (uid) REFERENCES waveCoach.user(id)
);

CREATE TABLE waveCoach.token (
    token VARCHAR(256) PRIMARY KEY,
    uid INTEGER REFERENCES waveCoach.user(id),
    created_time BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL,
    used_time BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL
);

CREATE TABLE waveCoach.caracteristics (
    date BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) PRIMARY KEY NOT NULL,
    uid INTEGER REFERENCES waveCoach.athlete(id),
    weight FLOAT,
    height INTEGER,
    calories INTEGER,
    %MG FLOAT,
    waist INTEGER,
    arm INTEGER,
    thigh INTEGER,
    tricep FLOAT,
    abdominal FLOAT,
    waist FLOAT,
);

CREATE TABLE waveCoach.mesocycle(
    id SERIAL PRIMARY KEY,
    uid INTEGER REFERENCES waveCoach.athlete(id),
    start_time BIGINT NOT NULL,
    end_time BIGINT NOT NULL,
)

CREATE TABLE waveCoach.microcycle(
    id SERIAL PRIMARY KEY,
    mesocycle INTEGER REFERENCES waveCoach.mesocycle(id),
    start_time BIGINT NOT NULL,
    end_time BIGINT NOT NULL,
)

CREATE TABLE waveCoach.activity(
    id SERIAL PRIMARY KEY,
    microcycle INTEGER REFERENCES waveCoach.microcycle(id),
    date BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL,
)

CREATE TABLE waveCoach.gym(
    activity INTEGER PRIMARY KEY,
    FOREIGN KEY (activity) REFERENCES waveCoach.activity(id),
)

CREATE TABLE waveCoach.gym_exercises(
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    category VARCHAR(64) NOT NULL,
)

CREATE TABLE waveCoach.exercise(
    id SERIAL PRIMARY KEY,
    activity INTEGER REFERENCES waveCoach.gym(id),
    exercise INTEGER REFERENCES waveCoach.gym_exercises(id),
    weight FLOAT,
    series INTEGER,
    repetitions INTEGER,
    rest_time INTEGER,
    order INTEGER,
)

CREATE TABLE waveCoach.water(
    activity INTEGER PRIMARY KEY,
    pse INTEGER,
    attempts INTEGER,
    waves INTEGER,
    condition VARCHAR(64),
    heart_rate INTEGER,
    time INTEGER,
    FOREIGN KEY (activity) REFERENCES waveCoach.activity(id),
)

CREATE TABLE waveCoach.questionnaire(
    id SERIAL PRIMARY KEY,
    activity INTEGER REFERENCES waveCoach.water(id),
    sleep INTEGER,
    fatigue INTEGER,
    stress INTEGER,
    muscle_pain INTEGER,
)

CREATE TABLE waveCoach.water_maneuvers(
    activity INTEGER PRIMARY KEY,
    name VARCHAR(64),
    FOREIGN KEY (activity) REFERENCES waveCoach.activity(id),
)

CREATE TABLE waveCoach.maneuver(
    id SERIAL PRIMARY KEY,
    activity INTEGER REFERENCES waveCoach.water(id),
    maneuver INTEGER REFERENCES waveCoach.water_maneuvers(id),
    side ENUM('left', 'right') NOT NULL,
    success INTEGER,
    failed INTEGER,
    order INTEGER,
)

CREATE TABLE waveCoach.competition(
    id SERIAL PRIMARY KEY,
    date BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) NOT NULL,
    location VARCHAR(64),
)

CREATE TABLE waveCoach.athlete_competition(
    athlete INTEGER REFERENCES waveCoach.athlete(id),
    competition INTEGER REFERENCES waveCoach.competition(id),
    score INTEGER,
    PRIMARY KEY (athlete, competition),
)

CREATE TABLE waveCoach.heat(
)


