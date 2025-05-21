package br.com.springbank.controller.transaction.dto;

import java.math.BigDecimal;

public record WithdrawRequestDto(BigDecimal amount) {
}
