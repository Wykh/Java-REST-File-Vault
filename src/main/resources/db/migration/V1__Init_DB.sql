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
    user_id             uuid                     NOT NULL,
    is_public           bool                     NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);

CREATE TABLE user_entity
(
    id         uuid NOT NULL DEFAULT gen_random_uuid(),
    name       VARCHAR(255),
    password   VARCHAR(255),
    role_id    uuid,
    is_blocked bool,
    PRIMARY KEY (id)
);

CREATE TABLE role_entity
(
    id   uuid NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE change_role_history_entity
(
    id             uuid NOT NULL DEFAULT gen_random_uuid(),
    actor_user_id  uuid,
    target_user_id uuid,
    role_id        uuid,
    PRIMARY KEY (id)
);

ALTER TABLE file_entity
    ADD CONSTRAINT FK_user_id FOREIGN KEY (user_id) REFERENCES user_entity;

ALTER TABLE user_entity
    ADD CONSTRAINT FK_role_id FOREIGN KEY (role_id) REFERENCES role_entity;

ALTER TABLE change_role_history_entity
    ADD CONSTRAINT FK_actor_user_id FOREIGN KEY (actor_user_id) REFERENCES user_entity;

ALTER TABLE change_role_history_entity
    ADD CONSTRAINT FK_target_user_id FOREIGN KEY (target_user_id) REFERENCES user_entity;

ALTER TABLE change_role_history_entity
    ADD CONSTRAINT FK_role_id FOREIGN KEY (role_id) REFERENCES role_entity;