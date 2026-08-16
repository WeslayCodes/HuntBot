CREATE TABLE ping_request (
    user_id varchar(32) NOT NULL,
    ping_id varchar(32) NOT NULL,
    last_request_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, ping_id),
    CONSTRAINT fk_user_id
        FOREIGN KEY (user_id)
        REFERENCES hunt_user(user_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);