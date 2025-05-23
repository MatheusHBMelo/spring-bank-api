package br.com.springbank.controller.admin.dto;

import br.com.springbank.domain.entities.account.AccountEntity;
import br.com.springbank.utils.DatetimeFormatter;

public record AccountResponseDto(Long id, String username, String accountNumber, String agencyNumber,
                                 String createdAt) {
    public static AccountResponseDto fromAccountEntity(AccountEntity accountEntity) {
        return new AccountResponseDto(
                accountEntity.getId(),
                accountEntity.getUserEntity().getUsername(),
                accountEntity.getAccountNumber(),
                accountEntity.getAgencyNumber(),
                DatetimeFormatter.formatDateTime(accountEntity.getCreatedAt())
        );
    }
}
