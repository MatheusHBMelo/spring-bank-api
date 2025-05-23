package br.com.springbank.controller.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(@NotBlank(message = "O username não pode estar em branco") String username,
                       @NotBlank(message = "O password não pode estar em branco") String password) {
}
