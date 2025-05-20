CREATE TABLE IF NOT EXISTS tb_accounts (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    accountNumber VARCHAR(255) NOT NULL UNIQUE,
    agencyNumber VARCHAR(255) NOT NULL,
    balance NUMERIC(19,2) NOT NULL,
    user_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP,

    CONSTRAINT fk_user_account FOREIGN KEY (user_id) REFERENCES tb_users(id) ON DELETE CASCADE
);