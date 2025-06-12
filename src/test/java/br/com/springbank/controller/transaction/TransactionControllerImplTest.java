package br.com.springbank.controller.transaction;

import br.com.springbank.controller.transaction.dto.*;
import br.com.springbank.controller.transaction.impl.TransactionControllerImpl;
import br.com.springbank.domain.entities.account.AccountEntity;
import br.com.springbank.domain.entities.user.StatusEnum;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.enums.TransactionType;
import br.com.springbank.service.token.TokenService;
import br.com.springbank.service.transaction.TransactionService;
import br.com.springbank.utils.DatetimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerImplTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserEntity user1;
    private UserEntity user2;

    private AccountEntity account1;
    private AccountEntity account2;

    @BeforeEach
    void setUp() {
        user1 = UserEntity.builder().id(1L).username("Matheus").password("12345").email("matheus@email.com").role(Set.of()).status(StatusEnum.ACTIVE).createdAt(LocalDateTime.now()).build();
        user2 = UserEntity.builder().id(2L).username("Davi").password("12345").email("davi@gmail.com").role(Set.of()).status(StatusEnum.ACTIVE).createdAt(LocalDateTime.now()).build();

        account1 = AccountEntity.builder().id(1L).accountNumber("12345678").agencyNumber("001").balance(new BigDecimal("50.00")).userEntity(user1).createdAt(LocalDateTime.now()).build();
        account2 = AccountEntity.builder().id(2L).accountNumber("87654321").agencyNumber("001").balance(new BigDecimal("0.00")).userEntity(user2).createdAt(LocalDateTime.now()).build();
    }

    @Test
    void deveTransferirComSucesso() throws Exception {
        TransferRequestDto dto = new TransferRequestDto(new BigDecimal("20.00"), account2.getAccountNumber());

        mockMvc.perform(post("/transaction/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk());

        ArgumentCaptor<TransferRequestDto> captor = ArgumentCaptor.forClass(TransferRequestDto.class);

        verify(transactionService).transfer(captor.capture());

        TransferRequestDto captured = captor.getValue();
        assertEquals(new BigDecimal("20.00"), captured.amount());
        assertEquals("87654321", captured.receiverAccountNumber());
    }

    @Test
    void deveDepositarComSucesso() throws Exception {
        DepositRequestDto dto = new DepositRequestDto(new BigDecimal("20.00"));

        mockMvc.perform(post("/transaction/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk());

        ArgumentCaptor<DepositRequestDto> captor = ArgumentCaptor.forClass(DepositRequestDto.class);

        verify(transactionService).deposit(captor.capture());

        DepositRequestDto captured = captor.getValue();
        assertEquals(new BigDecimal("20.00"), captured.amount());
    }

    @Test
    void deveSacarComSucesso() throws Exception {
        WithdrawRequestDto dto = new WithdrawRequestDto(new BigDecimal("20.00"));

        mockMvc.perform(post("/transaction/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk());

        ArgumentCaptor<WithdrawRequestDto> captor = ArgumentCaptor.forClass(WithdrawRequestDto.class);

        verify(transactionService).withdraw(captor.capture());

        WithdrawRequestDto captured = captor.getValue();
        assertEquals(new BigDecimal("20.00"), captured.amount());
    }

    @Test
    void deveGerarExtratoComSucesso() throws Exception {
        List<StatementResponseDto> extratos = List.of(
                new StatementResponseDto(TransactionType.WITHDRAW, new BigDecimal("20.00"), new AccountSimpleDto("12345678"), null, DatetimeFormatter.formatDateTime(LocalDateTime.now())),
                new StatementResponseDto(TransactionType.DEPOSIT, new BigDecimal("20.00"), new AccountSimpleDto("12345678"), null, DatetimeFormatter.formatDateTime(LocalDateTime.now()))
        );

        when(this.transactionService.bankStatement()).thenReturn(extratos);

        System.out.println("Esperado: " + extratos.get(1).sourceAccount().accountNumber());


        mockMvc.perform(get("/transaction/statement")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("[0].type", containsString("WITHDRAW")))
                .andExpect(jsonPath("[0].amount").value(extratos.get(0).amount().doubleValue()))
                .andExpect(jsonPath("[0].sourceAccount.accountNumber").value(extratos.get(0).sourceAccount().accountNumber()))
                .andExpect(jsonPath("[0].createdAt").value(extratos.get(0).createdAt()))
                .andExpect(jsonPath("[1].type", containsString("DEPOSIT")))
                .andExpect(jsonPath("[1].amount").value(extratos.get(1).amount().doubleValue()))
                .andExpect(jsonPath("[1].sourceAccount.accountNumber").value(extratos.get(1).sourceAccount().accountNumber()))
                .andExpect(jsonPath("[1].createdAt").value(extratos.get(1).createdAt()));

        verify(transactionService, times(1)).bankStatement();
    }
}