CREATE TABLE bank.roles
(
    id   BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(20)
);


CREATE TABLE bank.clients
(
    id         BIGINT       NOT NULL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    login      VARCHAR(20)  NOT NULL UNIQUE,
    email      VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(120) NOT NULL
);

CREATE TABLE bank.accounts
(
    id             BIGINT NOT NULL PRIMARY KEY,
    account_number VARCHAR(255),
    client_id      BIGINT,
    account_type   VARCHAR(20),
    balance        DECIMAL(19, 2),
    is_blocked     BOOLEAN,
    FOREIGN KEY (client_id) REFERENCES bank.clients (id) ON DELETE CASCADE
);

CREATE TABLE bank.transactions
(
    id               BIGINT NOT NULL PRIMARY KEY,
    account_id       BIGINT,
    amount           DECIMAL(19, 2),
    transaction_date TIMESTAMP,
    description      VARCHAR(255),
    is_cancel        BOOLEAN,
    type             VARCHAR(25),
    FOREIGN KEY (account_id) REFERENCES bank.accounts (id) ON DELETE CASCADE
);

CREATE TABLE bank.client_roles
(
    role_id   BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    CONSTRAINT pk_clients_roles PRIMARY KEY (role_id, client_id),
    CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES bank.roles (id),
    CONSTRAINT fk_userol_on_client FOREIGN KEY (client_id) REFERENCES bank.clients (id)
);