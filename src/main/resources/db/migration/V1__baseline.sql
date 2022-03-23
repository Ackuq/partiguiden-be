CREATE TABLE IF NOT EXISTS parties (
    id SERIAL PRIMARY KEY ,
    name VARCHAR(255) NOT NULL CONSTRAINT parties_name_unique UNIQUE,
    abbreviation VARCHAR(2) NOT NULL CONSTRAINT parties_abbreviation_unique UNIQUE
);

CREATE TABLE IF NOT EXISTS subjects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL CONSTRAINT subjects_name_unique UNIQUE
);

CREATE TABLE IF NOT EXISTS standpoints (
    id VARCHAR(64) PRIMARY KEY,
    link VARCHAR(150) NOT NULL CONSTRAINT standpoints_link_unique UNIQUE,
    title VARCHAR(100) NOT NULL,
    content TEXT ARRAY NOT NULL,
    update_date TIMESTAMP WITH TIME ZONE NOT NULL,
    party INT NOT NULL,
    subject INT NULL,
    CONSTRAINT fk_standpoints_party FOREIGN KEY (party) REFERENCES parties(id) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT fk_standpoints_subject FOREIGN KEY (subject) REFERENCES subjects(id) ON DELETE SET NULL ON UPDATE RESTRICT
);