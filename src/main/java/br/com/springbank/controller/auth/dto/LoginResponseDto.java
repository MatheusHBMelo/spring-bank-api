package br.com.springbank.controller.auth.dto;

public record LoginResponseDto(String username, String message, String token, Boolean status) {
}
