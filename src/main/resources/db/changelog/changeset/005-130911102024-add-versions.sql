ALTER TABLE bank.accounts
    ADD COLUMN version INT DEFAULT 0;

ALTER TABLE bank.transactions
    ADD COLUMN version INT DEFAULT 0;

ALTER TABLE bank.clients
    ADD COLUMN version INT DEFAULT 0;