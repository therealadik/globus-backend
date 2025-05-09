CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE banks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    transaction_datetime TIMESTAMP NOT NULL,
    person_type VARCHAR(50) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount DECIMAL(15,5) NOT NULL,
    status VARCHAR(50) NOT NULL,
    bank_sender_id BIGINT NOT NULL ,
    bank_receiver_id BIGINT NOT NULL ,
    inn_receiver VARCHAR(50) NOT NULL ,
    account_receiver VARCHAR(50) NOT NULL ,
    account_sender VARCHAR(50) NOT NULL,
    category_id BIGINT NOT NULL,
    phone_receiver VARCHAR(20) NOT NULL ,
    created_by BIGINT NOT NULL,
    updated_by BIGINT,

    CONSTRAINT fk_sender_bank FOREIGN KEY (bank_sender_id) REFERENCES banks (id),
    CONSTRAINT fk_receiver_bank FOREIGN KEY (bank_receiver_id) REFERENCES banks (id),
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users (id),
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
);