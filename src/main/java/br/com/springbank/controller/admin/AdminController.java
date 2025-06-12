package br.com.springbank.controller.admin;

import br.com.springbank.controller.admin.dto.AccountResponseDto;
import br.com.springbank.controller.admin.dto.TransactionsResponseDto;
import br.com.springbank.controller.admin.dto.UsersResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Admin Controller", description = "Funções administrativas")
public interface AdminController {
    @Operation(summary = "Find All Users", description = "Retorna todos os usuários do sistema", tags = {"admin", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200")
    })
    ResponseEntity<List<UsersResponseDto>> findAllUsers();

    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @Operation(summary = "Find User by Username", description = "Retorna um usuario de acordo com seu Username", tags = {"admin", "get"})
    ResponseEntity<UsersResponseDto> findUserByUsername(@Parameter(description = "Username") @RequestParam("username") String username);

    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "409")
    })
    @Operation(summary = "Disable User by Username", description = "Busca usuário com seu username e o desabilita", tags = {"admin", "patch"})
    ResponseEntity<UsersResponseDto> disableUserByUsername(@Parameter(description = "Username") @RequestParam("username") String username);

    @ApiResponses({
            @ApiResponse(responseCode = "200")
    })
    @Operation(summary = "Find All Transactions", description = "Retorna todas as transações do sistema", tags = {"admin", "get"})
    ResponseEntity<List<TransactionsResponseDto>> findAllTransactions();

    @ApiResponses({
            @ApiResponse(responseCode = "200")
    })
    @Operation(summary = "Find All Accounts", description = "Retorna todas as contas bancárias do sistema", tags = {"admin", "get"})
    ResponseEntity<List<AccountResponseDto>> findAllAccounts();

    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404")
    })
    @Operation(summary = "Find Account by Account Number", description = "Retorna uma conta bancária dado seu número", tags = {"admin", "get"})
    ResponseEntity<AccountResponseDto> findAccountByNumber(@Parameter(description = "accountNumber") @RequestParam("numberAccount") String numberAccount);
}
