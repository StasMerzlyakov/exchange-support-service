CREATE TABLE message_info (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    message_id VARCHAR(255) NOT NULL,
    process_guid VARCHAR(255) NOT NULL,
    creator_id INTEGER NOT NULL,
    type VARCHAR(255) NOT NULL
);

CREATE TABLE department (
    id  INTEGER NOT NULL,
    code VARCHAR(9) NOT NULL,
    is_acceptable BOOLEAN NOT null,
    PRIMARY KEY(id)
);

ALTER TABLE message_info ADD CONSTRAINT fk_message_info_department FOREIGN KEY (creator_id) REFERENCES department(id);

CREATE UNIQUE INDEX dx_message_info_message_id ON message_info(message_id);

CREATE index if NOT EXISTS idx_message_info_process_guid ON message_info(process_guid);

CREATE index if NOT EXISTS idx_department_code ON department(code);
