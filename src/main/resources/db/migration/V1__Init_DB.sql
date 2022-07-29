CREATE TABLE file_entity
(
    id                  uuid                     NOT NULL DEFAULT gen_random_uuid(),
    name                VARCHAR(255)             NOT NULL,
    extension           VARCHAR(255)             NOT NULL,
    comment             VARCHAR(255)             NOT NULL DEFAULT '',
    upload_date         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    content_folder_path VARCHAR(255)             NOT NULL DEFAULT 'upload-dir',
    size                int8                     NOT NULL,
    user_id             int8                    NOT NULL,
    is_public            bool                    NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);

CREATE TABLE user_entity
(
    id       int8 GENERATED BY DEFAULT AS IDENTITY,
    name     VARCHAR(255),
    password VARCHAR(255),
    role_id  int8,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX user_entity_id_uindex
    ON user_entity (id);

CREATE TABLE role_entity
(
    id   int8 GENERATED BY DEFAULT AS IDENTITY,
    role VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX role_entity_id_uindex
    ON role_entity (id);

ALTER TABLE file_entity
    ADD CONSTRAINT FK_UserId FOREIGN KEY (user_id) REFERENCES user_entity;

ALTER TABLE user_entity
    ADD CONSTRAINT FK_RoleId FOREIGN KEY (role_id) REFERENCES role_entity;