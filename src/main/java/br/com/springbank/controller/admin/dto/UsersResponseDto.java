package br.com.springbank.controller.admin.dto;

import br.com.springbank.domain.entities.user.StatusEnum;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.utils.DatetimeFormatter;

public record UsersResponseDto(Long id, String username, StatusEnum status, String createdAt) {
    public static UsersResponseDto fromUserEntity(UserEntity user) {
        return new UsersResponseDto(
                user.getId(),
                user.getUsername(),
                user.getStatus(),
                DatetimeFormatter.formatDateTime(user.getCreatedAt())
        );
    }
}
