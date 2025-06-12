package br.com.springbank.controller.auth.impl;

import br.com.springbank.controller.auth.AuthController;
import br.com.springbank.controller.auth.dto.LoginDto;
import br.com.springbank.controller.auth.dto.LoginResponseDto;
import br.com.springbank.controller.auth.dto.RegisterDto;
import br.com.springbank.service.user.AuthenticationService;
import br.com.springbank.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth")
public class AuthControllerImpl implements AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthControllerImpl(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDto registerDto) {
        this.userService.registerUser(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginDto loginDto) {
        return ResponseEntity.ok(this.authenticationService.loginUser(loginDto));
    }
}
