CREATE TABLE notification
(
    id bigint AUTO_INCREMENT PRIMARY KEY,
    notifier bigint NOT NULL,
    receiver bigint NOT NULL,
    outerid bigint NOT NULL,
    type int NOT NULL,
    gmt_create bigint NOT NULL,
    status int DEFAULT 0 NOT NULL
);