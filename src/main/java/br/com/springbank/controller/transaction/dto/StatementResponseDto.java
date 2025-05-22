package br.com.springbank.controller.transaction.dto;

import br.com.springbank.domain.entities.account.TransactionEntity;
import br.com.springbank.domain.enums.TransactionType;
import br.com.springbank.utils.DatetimeFormatter;

import java.math.BigDecimal;

public record StatementResponseDto(TransactionType type, BigDecimal amount, AccountSimpleDto sourceAccount,
                                   AccountSimpleDto destinationAccount, String createdAt) {
    public static StatementResponseDto fromEntity(TransactionEntity transaction) {
        return new StatementResponseDto(
                transaction.getType(),
                transaction.getAmount(),
                AccountSimpleDto.fromEntity(transaction.getSourceAccount()),
                AccountSimpleDto.fromEntity(transaction.getDestinationAccount()),
                DatetimeFormatter.formatDateTime(transaction.getCreatedAt())
        );
    }
}
