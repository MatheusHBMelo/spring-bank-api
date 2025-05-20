CREATE TABLE IF NOT EXISTS tb_transactions (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) CHECK (type IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER')),
    amount DECIMAL(19,2) NOT NULL,
    source_account_id BIGINT,
    destination_account_id BIGINT,
    created_at TIMESTAMP,

    CONSTRAINT fk_source_account
        FOREIGN KEY (source_account_id) REFERENCES tb_accounts(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_destination_account
        FOREIGN KEY (destination_account_id) REFERENCES tb_accounts(id)
        ON DELETE SET NULL
);