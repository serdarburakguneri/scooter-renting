CREATE TABLE scooter
(
    id            UUID PRIMARY KEY NOT NULL,
    serial_number VARCHAR(255)     NOT NULL,
    brand         VARCHAR(255)     NOT NULL,
    model         VARCHAR(255)     NOT NULL,
    status        VARCHAR(255)     NOT NULL,
    battery_level INTEGER,
    location      VARCHAR(255)     NOT NULL
);

CREATE TABLE rental_history
(
    id         UUID PRIMARY KEY NOT NULL,
    scooter_id UUID             NOT NULL,
    user_id    UUID             NOT NULL,
    start_date TIMESTAMP        NOT NULL,
    end_date   TIMESTAMP,
    total_cost DECIMAL(10, 2),
    FOREIGN KEY (scooter_id) REFERENCES scooter (id)
)