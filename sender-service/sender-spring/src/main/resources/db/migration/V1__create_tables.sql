CREATE TABLE department (
    id  INTEGER NOT NULL,
    code VARCHAR(9) NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE message (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    exchange VARCHAR(255) NOT NULL,
    key VARCHAR(255) NOT NULL,
    discriminator VARCHAR(255) NOT NULL,
    department_id INTEGER NOT NULL,
    sent  BOOLEAN NOT NULL
);

ALTER TABLE message ADD CONSTRAINT fk_message_department FOREIGN KEY (department_id) REFERENCES department(id);

CREATE UNIQUE INDEX dx_message_key_and_department_id ON message(key, department_id);

CREATE index if NOT EXISTS idx_message_sent_and_department_id ON message(sent, department_id);

