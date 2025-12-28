-- Описание структуры: у каждого человека есть машина.
-- Причем несколько человек могут пользоваться одной машиной.
-- У каждого человека есть имя, возраст и признак того, что у него есть права (или их нет).
-- У каждой машины есть марка, модель и стоимость. Также не забудьте добавить таблицам первичные ключи и связать их.

CREATE TABLE cars
(
    car_id SERIAL PRIMARY KEY,
    brand  VARCHAR(255) NOT NULL,
    model  VARCHAR(255) NOT NULL,
    price  NUMERIC(9, 2) CHECK (price >= 0)
);

CREATE TABLE person
(
    person_id      SERIAL PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    age            INTEGER CHECK (age > 0),
    driver_licence BOOLEAN NOT NULL,
    car_id         INTEGER REFERENCES cars (car_id)
);

