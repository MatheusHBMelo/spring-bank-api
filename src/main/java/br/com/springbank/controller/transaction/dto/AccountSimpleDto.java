package br.com.springbank.controller.transaction.dto;

import br.com.springbank.domain.entities.account.AccountEntity;

public record AccountSimpleDto(String accountNumber) {
    public static AccountSimpleDto fromEntity(AccountEntity account) {
        if (account == null) {
            return null;
        }
        return new AccountSimpleDto(account.getAccountNumber());
    }
}
