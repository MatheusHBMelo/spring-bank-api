package br.com.springbank.controller.admin;

import br.com.springbank.controller.admin.dto.AccountResponseDto;
import br.com.springbank.controller.admin.dto.TransactionsResponseDto;
import br.com.springbank.controller.admin.dto.UsersResponseDto;
import br.com.springbank.service.admin.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(path = "/users")
    public ResponseEntity<List<UsersResponseDto>> findAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(this.adminService.findAllUsers());
    }

    @GetMapping(path = "/user")
    public ResponseEntity<UsersResponseDto> findUserByUsername(@RequestParam("username") String username) {
        return ResponseEntity.ok(this.adminService.findUserByUsername(username));
    }

    @PatchMapping(path = "/user")
    public ResponseEntity<UsersResponseDto> disableUserByUsername(@RequestParam("username") String username) {
        return ResponseEntity.ok(this.adminService.disableUser(username));
    }

    @GetMapping(path = "/transactions")
    public ResponseEntity<List<TransactionsResponseDto>> findAllTransactions() {
        return ResponseEntity.status(HttpStatus.OK).body(this.adminService.findAllTransactions());
    }

    @GetMapping(path = "/accounts")
    public ResponseEntity<List<AccountResponseDto>> findAllAccounts() {
        return ResponseEntity.status(HttpStatus.OK).body(this.adminService.findAllAccounts());
    }

    @GetMapping(path = "/account")
    public ResponseEntity<AccountResponseDto> findAccountByNumber(@RequestParam("numberAccount") String numberAccount) {
        return ResponseEntity.ok(this.adminService.findAccountByAccountNumber(numberAccount));
    }
}
