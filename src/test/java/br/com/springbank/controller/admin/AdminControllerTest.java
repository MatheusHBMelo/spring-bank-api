package br.com.springbank.controller.admin;

import br.com.springbank.controller.admin.dto.AccountResponseDto;
import br.com.springbank.controller.admin.dto.TransactionsResponseDto;
import br.com.springbank.controller.admin.dto.UsersResponseDto;
import br.com.springbank.controller.transaction.dto.AccountSimpleDto;
import br.com.springbank.domain.entities.user.StatusEnum;
import br.com.springbank.domain.enums.TransactionType;
import br.com.springbank.service.admin.AdminService;
import br.com.springbank.service.exceptions.account.AccountNotFoundException;
import br.com.springbank.service.exceptions.user.UserAlreadyInactiveException;
import br.com.springbank.service.exceptions.user.UserNotFoundException;
import br.com.springbank.service.token.TokenService;
import br.com.springbank.utils.DatetimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = true)
class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "Matheus", roles = {"ADMIN"})
    void deveRetornarTodosOsUsuarios() throws Exception {
        List<UsersResponseDto> users = List.of(
                new UsersResponseDto(1L, "Matheus", StatusEnum.ACTIVE, DatetimeFormatter.formatDateTime(LocalDateTime.now())),
                new UsersResponseDto(2L, "Davi", StatusEnum.ACTIVE, DatetimeFormatter.formatDateTime(LocalDateTime.now()))
        );

        when(this.adminService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(users.get(0).id()))
                .andExpect(jsonPath("$[0].username").value(users.get(0).username()))
                .andExpect(jsonPath("$[0].status", containsString("ACTIVE")))
                .andExpect(jsonPath("$[0].createdAt").value(users.get(0).createdAt()))
                .andExpect(jsonPath("$[1].id").value(users.get(1).id()))
                .andExpect(jsonPath("$[1].username").value(users.get(1).username()))
                .andExpect(jsonPath("$[1].status", containsString("ACTIVE")))
                .andExpect(jsonPath("$[1].createdAt").value(users.get(0).createdAt()));

        verify(adminService, times(1)).findAllUsers();
    }

    @Test
    @WithMockUser(username = "Matheus", roles = {"ADMIN"})
    void deveRetornarUmUsuarioDadoSeuUsername() throws Exception {
        UsersResponseDto user = new UsersResponseDto(1L, "Matheus", StatusEnum.ACTIVE, DatetimeFormatter.formatDateTime(LocalDateTime.now()));
        String username = "Matheus";

        when(this.adminService.findUserByUsername(anyString())).thenReturn(user);

        mockMvc.perform(get("/admin/user")
                        .param("username", username)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.id()))
                .andExpect(jsonPath("$.username").value(user.username()))
                .andExpect(jsonPath("$.status", containsString("ACTIVE")))
                .andExpect(jsonPath("$.createdAt").value(user.createdAt()));

        verify(adminService, times(1)).findUserByUsername(username);
    }

    @Test
    @WithMockUser(username = "Matheus", roles = {"ADMIN"})
    void deveRetornarExcecaoSeUsernameNaoForInformado() throws Exception {
        mockMvc.perform(get("/admin/user"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminService);
    }

    @Test
    @WithMockUser(username = "Matheus", roles = {"ADMIN"})
    void deveRetornarExcecaoSeUserNaoExistir() throws Exception {
        String username = "Matheus";

        when(this.adminService.findUserByUsername(anyString())).thenThrow(new UserNotFoundException("Usuário com nome '" + username + "' não foi encontrado."));

        mockMvc.perform(get("/admin/user")
                        .param("username", username))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Usuário com nome '" + username + "' não foi encontrado.")));

        verify(adminService, times(1)).findUserByUsername(username);
    }

    @Test
    @WithMockUser(username = "Matheus", roles = {"ADMIN"})
    void deveRetornarTodasAsTransacoesComSucesso() throws Exception {
        AccountSimpleDto accountSimple = new AccountSimpleDto("12345");

        List<TransactionsResponseDto> transacoes = List.of(
                new TransactionsResponseDto(TransactionType.DEPOSIT, new BigDecimal("50.00"), accountSimple, null, DatetimeFormatter.formatDateTime(LocalDateTime.now())),
                new TransactionsResponseDto(TransactionType.WITHDRAW, new BigDecimal("30.00"), accountSimple, null, DatetimeFormatter.formatDateTime(LocalDateTime.now()))
        );

        when(this.adminService.findAllTransactions()).thenReturn(transacoes);

        mockMvc.perform(get("/admin/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].type", containsString("DEPOSIT")))
                .andExpect(jsonPath("$[0].amount").value(transacoes.get(0).amount().doubleValue()))
                .andExpect(jsonPath("$[1].type", containsString("WITHDRAW")))
                .andExpect(jsonPath("$[1].amount").value(transacoes.get(1).amount().doubleValue()));

        verify(adminService, times(1)).findAllTransactions();
    }

    @Test
    @WithMockUser(username = "Matheus", roles = {"ADMIN"})
    void deveRetornarTodasAsContasComSucesso() throws Exception {
        List<AccountResponseDto> accounts = List.of(
                new AccountResponseDto(1L, "Matheus", "12345678", "001", DatetimeFormatter.formatDateTime(LocalDateTime.now())),
                new AccountResponseDto(2L, "Davi", "87654321", "001", DatetimeFormatter.formatDateTime(LocalDateTime.now()))
        );

        when(this.adminService.findAllAccounts()).thenReturn(accounts);

        mockMvc.perform(get("/admin/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(adminService, times(1)).findAllAccounts();
    }

    @Test
    @WithMockUser(username = "Matheus", roles = {"ADMIN"})
    void deveRetornarUmaContaDadoSeuNumero() throws Exception {
        AccountResponseDto account = new AccountResponseDto(1L, "Matheus", "12345678", "001", DatetimeFormatter.formatDateTime(LocalDateTime.now()));
        String accountNumber = "12345678";

        when(this.adminService.findAccountByAccountNumber(anyString())).thenReturn(account);

        mockMvc.perform(get("/admin/account")
                        .param("numberAccount", accountNumber)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(account.id()))
                .andExpect(jsonPath("$.username").value(account.username()))
                .andExpect(jsonPath("$.accountNumber").value(account.accountNumber()))
                .andExpect(jsonPath("$.agencyNumber").value(account.agencyNumber()))
                .andExpect(jsonPath("$.createdAt").value(account.createdAt()));

        verify(adminService, times(1)).findAccountByAccountNumber(accountNumber);
    }

    @Test
    @WithMockUser(username = "Matheus", roles = {"ADMIN"})
    void deveRetornarExcecaoSeAccountNumberNaoForInformado() throws Exception {
        mockMvc.perform(get("/admin/account"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminService);
    }

    @Test
    @WithMockUser(username = "Matheus", roles = {"ADMIN"})
    void deveRetornarExcecaoSeAccountNaoExistir() throws Exception {
        String accountNumber = "12345678";

        when(this.adminService.findAccountByAccountNumber(anyString())).thenThrow(new AccountNotFoundException("Conta com número '" + accountNumber + "' não encontrada."));

        mockMvc.perform(get("/admin/account")
                        .param("numberAccount", accountNumber))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Conta com número '" + accountNumber + "' não encontrada.")));

        verify(adminService, times(1)).findAccountByAccountNumber(accountNumber);
    }

    @Test
    @WithMockUser(username = "Matheus", roles = {"ADMIN"})
    void deveDesativarUsuarioComSucesso() throws Exception {
        UsersResponseDto user = new UsersResponseDto(1L, "Matheus", StatusEnum.INACTIVE, DatetimeFormatter.formatDateTime(LocalDateTime.now()));
        String username = "Matheus";

        when(this.adminService.disableUser(anyString())).thenReturn(user);

        mockMvc.perform(patch("/admin/user")
                        .param("username", username)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.id()))
                .andExpect(jsonPath("$.username").value(user.username()))
                .andExpect(jsonPath("$.status", containsString("INACTIVE")))
                .andExpect(jsonPath("$.createdAt").value(user.createdAt()));

        verify(adminService, times(1)).disableUser(username);
    }

    @Test
    @WithMockUser(username = "Matheus", roles = {"ADMIN"})
    void deveRetornarExcecaoSeUsernameActiveNaoForInformado() throws Exception {
        mockMvc.perform(patch("/admin/user").with(csrf()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminService);
    }

    @Test
    @WithMockUser(username = "Matheus", roles = {"ADMIN"})
    void deveRetornarExcecaoUsuarioNaoExistir() throws Exception {
        String username = "Matheus";

        when(this.adminService.disableUser(anyString())).thenThrow(new UserNotFoundException("Usuário com nome '" + username + "' não foi encontrado."));

        mockMvc.perform(patch("/admin/user")
                        .param("username", username)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Usuário com nome '" + username + "' não foi encontrado.")));

        verify(adminService, times(1)).disableUser(username);
    }

    @Test
    @WithMockUser(username = "Matheus", roles = {"ADMIN"})
    void deveRetornarExcecaoUsuarioJaEstiverDesativado() throws Exception {
        String username = "Matheus";

        when(this.adminService.disableUser(anyString())).thenThrow(new UserAlreadyInactiveException("Este usuário já está desativado."));

        mockMvc.perform(patch("/admin/user")
                        .param("username", username)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Este usuário já está desativado.")));

        verify(adminService, times(1)).disableUser(username);
    }
}