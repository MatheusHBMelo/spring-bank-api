package br.com.springbank.controller.auth;

import br.com.springbank.controller.auth.dto.LoginDto;
import br.com.springbank.controller.auth.dto.LoginResponseDto;
import br.com.springbank.controller.auth.dto.RegisterDto;
import br.com.springbank.service.token.TokenService;
import br.com.springbank.service.user.AuthenticationService;
import br.com.springbank.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenService tokenService;

    @Test
    void deveRegistrarUsuarioComSucesso() throws Exception {
        RegisterDto requestDto = new RegisterDto("Matheus", "12345", "matheus@email.com");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        ).andExpect(status().isCreated());

        ArgumentCaptor<RegisterDto> captor = ArgumentCaptor.forClass(RegisterDto.class);

        verify(userService).registerUser(captor.capture());

        RegisterDto captured = captor.getValue();
        assertEquals("matheus@email.com", captured.email());
        assertEquals("Matheus", captured.username());
        assertEquals("12345", captured.password());
    }

    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("provideInvalidRegisterDtos")
    void deveRetornarExcecaoSeUsernameForNuloOuVazio(String cenario, RegisterDto invalidRegisterDto) throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRegisterDto))
        ).andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void deveLogarUmUsuarioComSucesso() throws Exception {
        LoginDto requestDto = new LoginDto("Matheus", "12345");
        LoginResponseDto responseDto = new LoginResponseDto("Matheus", "Usuario logado", "fake-token-jwt", true);

        when(authenticationService.loginUser(any(LoginDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(responseDto.username()))
                .andExpect(jsonPath("$.message").value(responseDto.message()))
                .andExpect(jsonPath("$.token").value(responseDto.token()))
                .andExpect(jsonPath("$.status").value(responseDto.status()));

        verify(authenticationService, times(1)).loginUser(any(LoginDto.class));
    }

    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("provideInvalidLoginDtos")
    void deveRetornarBadRequestParaLoginInvalido(String nomeCenario, LoginDto invalidLoginDto) throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginDto))
        ).andExpect(status().isBadRequest());

        verifyNoInteractions(authenticationService);
    }

    private static Stream<Arguments> provideInvalidRegisterDtos() {
        return Stream.of(
                Arguments.of("Username vazio", new RegisterDto("", "12345", "matheus@email.com")),
                Arguments.of("Senha vazia", new RegisterDto("Matheus", "", "matheus@email.com")),
                Arguments.of("Email vazio", new RegisterDto("Matheus", "12345", "")),
                Arguments.of("Username, Senha e Email vazios", new RegisterDto("", "", ""))
        );
    }

    private static Stream<Arguments> provideInvalidLoginDtos() {
        return Stream.of(
                Arguments.of("Username vazio", new LoginDto("", "12345")),    // username vazio
                Arguments.of("Senha vazia", new LoginDto("Matheus", "")),
                Arguments.of("Username e senha vazios", new LoginDto("", ""))
        );
    }
}