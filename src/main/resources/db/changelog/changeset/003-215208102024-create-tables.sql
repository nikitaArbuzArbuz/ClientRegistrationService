CREATE TABLE bank.clients
(
    id         BIGINT       NOT NULL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    login      VARCHAR(20)  NOT NULL UNIQUE,
    email      VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(120) NOT NULL,
    name       VARCHAR(20),
    CONSTRAINT chk_role_enum CHECK (name IN ('ROLE_USER', 'ROLE_ADMIN'))
);

CREATE TABLE bank.accounts
(
    id             BIGINT NOT NULL PRIMARY KEY,
    account_number VARCHAR(255),
    client_id      BIGINT,
    account_type   VARCHAR(20),
    balance        DECIMAL(19, 2),
    is_blocked     BOOLEAN,
    FOREIGN KEY (client_id) REFERENCES bank.clients (id) ON DELETE CASCADE,
    CONSTRAINT chk_account_type CHECK (account_type IN ('DEPOSIT', 'CREDIT'))
);

CREATE TABLE bank.transactions
(
    id               BIGINT NOT NULL PRIMARY KEY,
    account_id       BIGINT,
    amount           DECIMAL(19, 2),
    transaction_date TIMESTAMP,
    description      VARCHAR(255),
    FOREIGN KEY (account_id) REFERENCES bank.accounts (id) ON DELETE CASCADE
);