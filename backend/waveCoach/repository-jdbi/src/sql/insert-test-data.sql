-- Insert user data
insert into waveCoach.user (username, password) values ('admin', '$2a$10$6wZjBBzU2G1H7rK5VeFNIu.toyRHL.ULP1R2IMjpPy7xB8HhF/h0q');
 -- password: Admin123!
 insert into waveCoach.coach (uid) values (1);
 insert into waveCoach.token (token, uid) values ('DOi36t96dyrduP2hLglteGBDCzb0hBvpCvRddTuaDqc=', 1);
 -- token: i_aY-4lpMqAIMuhkimTbKy4xYEuyvgFPaaTpVS0lctQ=