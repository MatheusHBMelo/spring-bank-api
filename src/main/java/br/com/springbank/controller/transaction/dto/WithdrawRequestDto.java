package br.com.springbank.controller.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WithdrawRequestDto(@NotNull(message = "O campo amount não pode ser nulo")
                                 @DecimalMin(value = "0.00", inclusive = false, message = "O campo amount deve ser maior que 0.00")
                                 @Digits(integer = 19, fraction = 2) BigDecimal amount) {
}
