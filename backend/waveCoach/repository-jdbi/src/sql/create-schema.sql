-- Drop existing tables if they exist
DROP TABLE IF EXISTS waveCoach.user;

-- Create schema
CREATE SCHEMA IF NOT EXISTS waveCoach;

-- Create new tables
CREATE TABLE waveCoach.user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password VARCHAR(256) NOT NULL
);
