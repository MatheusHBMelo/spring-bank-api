package br.com.springbank.controller.transaction.impl;

import br.com.springbank.controller.transaction.TransactionController;
import br.com.springbank.controller.transaction.dto.DepositRequestDto;
import br.com.springbank.controller.transaction.dto.StatementResponseDto;
import br.com.springbank.controller.transaction.dto.TransferRequestDto;
import br.com.springbank.controller.transaction.dto.WithdrawRequestDto;
import br.com.springbank.service.transaction.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/transaction")
public class TransactionControllerImpl implements TransactionController {
    private final TransactionService transactionService;

    public TransactionControllerImpl(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(path = "/transfer")
    public ResponseEntity<Void> transfer(@RequestBody @Valid TransferRequestDto transferRequestDto) {
        this.transactionService.transfer(transferRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(path = "/deposit")
    public ResponseEntity<Void> deposit(@RequestBody @Valid DepositRequestDto depositRequestDto) {
        this.transactionService.deposit(depositRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(path = "/withdraw")
    public ResponseEntity<Void> withdraw(@RequestBody @Valid WithdrawRequestDto withdrawRequestDto) {
        this.transactionService.withdraw(withdrawRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(path = "/statement")
    public ResponseEntity<List<StatementResponseDto>> statement() {
        return ResponseEntity.ok(this.transactionService.bankStatement());
    }
}
