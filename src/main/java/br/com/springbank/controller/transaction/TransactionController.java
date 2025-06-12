package br.com.springbank.controller.transaction;

import br.com.springbank.controller.transaction.dto.DepositRequestDto;
import br.com.springbank.controller.transaction.dto.StatementResponseDto;
import br.com.springbank.controller.transaction.dto.TransferRequestDto;
import br.com.springbank.controller.transaction.dto.WithdrawRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Transaction Controller", description = "Funções bancárias para usuários")
public interface TransactionController {
    @Operation(summary = "Transfer money", description = "Transfere saldo entre contas ativas", tags = {"transaction", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    ResponseEntity<Void> transfer(@RequestBody @Valid TransferRequestDto transferRequestDto);

    @Operation(summary = "Deposit money", description = "Deposita saldo na conta ativa", tags = {"transaction", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    ResponseEntity<Void> deposit(@RequestBody @Valid DepositRequestDto depositRequestDto);

    @Operation(summary = "Withdraw money", description = "Saca saldo da conta ativa", tags = {"transaction", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    ResponseEntity<Void> withdraw(@RequestBody @Valid WithdrawRequestDto withdrawRequestDto);

    @Operation(summary = "Statement account", description = "Gera extrato de transações", tags = {"transaction", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403")
    })
    ResponseEntity<List<StatementResponseDto>> statement();
}
