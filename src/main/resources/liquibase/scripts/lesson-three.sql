--liquibase formatted sql

--changeset piotr:1

CREATE TABLE faculty (
                         id BIGSERIAL PRIMARY KEY,
                         color VARCHAR(255),
                         name VARCHAR(255)
);

CREATE TABLE student (
                         id BIGSERIAL PRIMARY KEY,
                         age INTEGER,
                         name VARCHAR(255),
                         faculty_id BIGINT,
                         CONSTRAINT fk_student_faculty
                             FOREIGN KEY (faculty_id)
                                 REFERENCES faculty (id)
);

CREATE TABLE avatar (
                        id BIGSERIAL PRIMARY KEY,
                        data OID,
                        file_path VARCHAR(255),
                        file_size BIGINT,
                        media_type VARCHAR(255),
                        student_id BIGINT UNIQUE,
                        CONSTRAINT fk_avatar_student
                            FOREIGN KEY (student_id)
                                REFERENCES student (id)
);

--changeset piotr:2

CREATE INDEX student_name_index on student(name);
CREATE INDEX faculty_name_and_color_index on faculty(name, color);
