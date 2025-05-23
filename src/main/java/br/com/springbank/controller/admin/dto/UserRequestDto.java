package br.com.springbank.controller.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(@NotBlank String username) {
}
