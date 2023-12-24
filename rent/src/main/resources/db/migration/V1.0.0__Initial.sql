CREATE TABLE scooter
(
    id                 UUID PRIMARY KEY NOT NULL,
    serial_number      VARCHAR(255)     NOT NULL,
    brand              VARCHAR(255)     NOT NULL,
    model              VARCHAR(255)     NOT NULL,
    status             VARCHAR(255)     NOT NULL,
    battery_level      DECIMAL(3, 3),
    location_latitude  VARCHAR(255),
    location_longitude VARCHAR(255)
);

CREATE TABLE rental_history
(
    id         UUID PRIMARY KEY NOT NULL,
    scooter_id UUID             NOT NULL,
    user_id    UUID             NOT NULL,
    start_date TIMESTAMP        NOT NULL,
    end_date   TIMESTAMP,
    total_cost DECIMAL(10, 5),
    FOREIGN KEY (scooter_id) REFERENCES scooter (id)
)