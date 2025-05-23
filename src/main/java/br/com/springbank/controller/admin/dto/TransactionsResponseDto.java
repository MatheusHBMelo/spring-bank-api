package br.com.springbank.controller.admin.dto;

import br.com.springbank.controller.transaction.dto.AccountSimpleDto;
import br.com.springbank.domain.entities.account.TransactionEntity;
import br.com.springbank.domain.enums.TransactionType;
import br.com.springbank.utils.DatetimeFormatter;

import java.math.BigDecimal;

public record TransactionsResponseDto(TransactionType type, BigDecimal amount, AccountSimpleDto sourceAccount,
                                      AccountSimpleDto destinationAccount, String createdAt) {
    public static TransactionsResponseDto fromTransactionEntity(TransactionEntity transaction) {
        return new TransactionsResponseDto(
                transaction.getType(),
                transaction.getAmount(),
                AccountSimpleDto.fromEntity(transaction.getSourceAccount()),
                AccountSimpleDto.fromEntity(transaction.getDestinationAccount()),
                DatetimeFormatter.formatDateTime(transaction.getCreatedAt())
        );
    }
}
