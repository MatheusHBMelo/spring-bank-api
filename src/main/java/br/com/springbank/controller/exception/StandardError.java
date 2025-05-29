package br.com.springbank.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StandardError {
    private String message;
    private Integer statusCode;
    private LocalDateTime timestamp;
    private String path;
}
