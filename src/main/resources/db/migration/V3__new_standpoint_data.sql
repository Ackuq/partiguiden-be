CREATE TABLE IF NOT EXISTS standpoint_update_events (
    id SERIAL PRIMARY KEY,
    new_link VARCHAR(150) NOT NULL UNIQUE,
    new_title VARCHAR(100) NOT NULL,
    new_content TEXT ARRAY NOT NULL,
    new_paragraph TEXT NULL,
    update_date TIMESTAMP WITH TIME ZONE NOT NULL,
    party INT NOT NULL,
    standpoint INT NULL,
    CONSTRAINT fk_standpoint_update_events_party
        FOREIGN KEY (party) REFERENCES parties(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_standpoint_update_events_standpoints
        FOREIGN KEY (standpoint) REFERENCES standpoints(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS standpoint_delete_events (
    id SERIAL PRIMARY KEY,
    standpoint INT NOT NULL,
    CONSTRAINT fk_standpoint_delete_events_standpoints
            FOREIGN KEY (standpoint) REFERENCES standpoints(id)
            ON DELETE CASCADE
);