package br.com.springbank.controller.auth;

import br.com.springbank.controller.auth.dto.LoginDto;
import br.com.springbank.controller.auth.dto.LoginResponseDto;
import br.com.springbank.controller.auth.dto.RegisterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth Controller", description = "Registro e Login de usuários")
public interface AuthController {
    @Operation(summary = "Register User", description = "Registra um novo usuário no sistema", tags = {"auth", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404")
    })
    ResponseEntity<Void> register(@RequestBody @Valid RegisterDto registerDto);

    @Operation(summary = "Login User", description = "Autentica usuário no sistema", tags = {"auth", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401")
    })
    ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginDto loginDto);
}
